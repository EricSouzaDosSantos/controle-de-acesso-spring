package com.desafios.acesso.controller;

import com.desafios.acesso.service.ClienteMQTT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    private final ClienteMQTT clienteMQTT;

    @Autowired
    public MqttController(ClienteMQTT clienteMQTT) {
        this.clienteMQTT = clienteMQTT;
    }

    @PostMapping("/publicar")
    public String publicar(@RequestParam String topico, @RequestParam String mensagem) {
        clienteMQTT.publicarMensagem(topico, mensagem);
        return "Mensagem publicada no tópico " + topico;
    }

    @GetMapping("/assinar")
    public String assinar(@RequestParam String topico) {
        clienteMQTT.assinarTopicoEIniciarEscuta(topico, mensagem -> {
            System.out.println("Mensagem Recebida via API: " + mensagem);
        });
        return "Inscrito no tópico " + topico;
    }
}