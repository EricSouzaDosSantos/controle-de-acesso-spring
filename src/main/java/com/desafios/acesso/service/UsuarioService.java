package com.desafios.acesso.service;

import com.desafios.acesso.dtos.UsuarioDTO;
import com.desafios.acesso.model.Usuario;
import com.desafios.acesso.repositorys.UsuarioRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void cadastraeUsuario(UsuarioDTO usuarioDTO){
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.nome());
        usuario.setTelefone(usuarioDTO.telefone());
        usuario.setEmail(usuarioDTO.email());
        usuarioRepository.save(usuario);
    }
}
