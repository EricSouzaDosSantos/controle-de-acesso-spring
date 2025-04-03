package com.desafios.acesso.service;

import com.desafios.acesso.dtos.UsuarioDTO;
import com.desafios.acesso.model.Usuario;
import com.desafios.acesso.repositorys.UsuarioRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

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

    public Usuario buscarUsuarioPeloId(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow();
    }
}
