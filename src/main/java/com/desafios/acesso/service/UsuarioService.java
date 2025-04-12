package com.desafios.acesso.service;

import com.desafios.acesso.dtos.UsuarioDTO;
import com.desafios.acesso.model.Usuario;
import com.desafios.acesso.repositorys.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final ClienteMQTT clienteMQTT;


    public void cadastrarUsuario(UsuarioDTO usuarioDTO){
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.nome());
        usuario.setIdAcesso(usuarioDTO.idAcesso());
        usuario.setTelefone(usuarioDTO.telefone());
        usuario.setEmail(usuarioDTO.email());
        usuarioRepository.save(usuario);
    }

    public void atualizarUsuario(UsuarioDTO usuarioDTO, Long id){
        Usuario usuario = buscarUsuarioPeloId(id);
        usuario.setNome(usuarioDTO.nome());
        usuario.setIdAcesso(usuarioDTO.idAcesso());
        usuario.setTelefone(usuarioDTO.telefone());
        usuario.setEmail(usuarioDTO.email());
        usuarioRepository.save(usuario);
    }

    public void cadastrarIdAcesso(UUID idAcesso, Long id){
        Usuario usuario = buscarUsuarioPeloId(id);
        usuario.setIdAcesso(idAcesso);
        usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios(){
        return usuarioRepository.findAll();
    }

    public void deletarUsuario(Long id){
        usuarioRepository.deleteById(id);
    }

    public void aguardarCadastroDoIdAcesso(String topico, String mensagem, long id) {
        clienteMQTT.publicarMensagem(topico, mensagem);
        Usuario usuario = buscarUsuarioPeloId(id);

        CompletableFuture<UUID> future = new CompletableFuture<>();

        clienteMQTT.assinarTopicoEIniciarEscuta(topico, mensagemRecebida -> {
            try {
                System.out.println("Mensagem Recebida via API: " + mensagemRecebida);
                UUID idAcesso = UUID.fromString(mensagemRecebida);
                usuario.setIdAcesso(idAcesso);
                usuarioRepository.save(usuario);
                future.complete(idAcesso);
            } catch (IllegalArgumentException e) {
                System.err.println("Erro: UUID inválido recebido.");
                future.completeExceptionally(new RuntimeException("UUID inválido"));
            }
        });

        try {
            UUID idAcessoRecebido = future.get(30, TimeUnit.SECONDS);
            System.out.println("ID de acesso cadastrado com sucesso: " + idAcessoRecebido);
        } catch (TimeoutException e) {
            System.err.println("Erro: Tempo limite atingido. Nenhum UUID recebido.");
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar ID de acesso: " + e.getMessage());
        }
    }


    public Usuario buscarUsuarioPeloId(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow();
    }
}
