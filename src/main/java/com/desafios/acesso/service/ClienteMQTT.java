package com.desafios.acesso.service;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
public class ClienteMQTT {

    private final MqttClient cliente;
    private String topicoAtual;
    private MensagemListener listenerAtual;

    @Autowired
    public ClienteMQTT(MqttClient mqttClient) {
        this.cliente = mqttClient;
        configurarCallback();
    }

    private void configurarCallback() {
        cliente.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Conexão perdida: " + cause.getMessage());
                reconectar();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String mensagemRecebida = new String(message.getPayload(), StandardCharsets.UTF_8);
                System.out.println("Mensagem recebida no tópico " + topic + ": " + mensagemRecebida);
                if (listenerAtual != null) {
                    listenerAtual.onMensagemRecebida(mensagemRecebida);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Entrega de mensagem concluída.");
            }
        });
    }

    private void reconectar() {
        System.out.println("Tentando reconectar ao broker MQTT...");
        while (!cliente.isConnected()) {
            try {
                TimeUnit.SECONDS.sleep(5);
                cliente.reconnect();
                System.out.println("Reconexão bem-sucedida.");
            } catch (Exception e) {
                System.err.println("Erro ao tentar reconectar: " + e.getMessage());
            }
        }
    }

    public void publicarMensagem(String topico, String mensagem) {
        if (cliente.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage(mensagem.getBytes(StandardCharsets.UTF_8));
                mqttMessage.setQos(1);
                cliente.publish(topico, mqttMessage);
                System.out.println("Mensagem publicada no tópico " + topico + ": " + mensagem);
            } catch (MqttException e) {
                System.err.println("Erro ao publicar mensagem: " + e.getMessage());
            }
        } else {
            System.err.println("Erro: cliente desconectado.");
        }
    }

    public void assinarTopicoEIniciarEscuta(String topico, MensagemListener listener) {
        try {
            topicoAtual = topico;
            listenerAtual = listener;
            cliente.subscribe(topico, (topic, message) -> {
                String mensagemRecebida = new String(message.getPayload(), StandardCharsets.UTF_8).trim();
                System.out.println("Mensagem recebida no tópico " + topic + ": " + mensagemRecebida);
                listener.onMensagemRecebida(mensagemRecebida);
            });
        } catch (MqttException e) {
            System.err.println("Erro ao assinar tópico: " + e.getMessage());
        }
    }

    public interface MensagemListener {
        void onMensagemRecebida(String mensagem);
    }
}
