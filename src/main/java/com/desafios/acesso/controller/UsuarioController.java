package com.desafios.acesso.controller;

import com.desafios.acesso.dtos.UsuarioDTO;
import com.desafios.acesso.model.Usuario;
import com.desafios.acesso.service.ClienteMQTT;
import com.desafios.acesso.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final ClienteMQTT clienteMQTT;


    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPeloId(@PathVariable long id) {
        Usuario usuario = usuarioService.buscarUsuarioPeloId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> cadastrarUsuario(@RequestBody UsuarioDTO usuario) {
        usuarioService.cadastrarUsuario(usuario);

        return ResponseEntity.status(201).body(usuario);
    }

    @PutMapping("/idAcesso/{id}")
    public ResponseEntity<String> cadastrarIdAcesso(@PathVariable long id,
                                                   @RequestParam String topico,
                                                   @RequestParam String mensagem) {

        usuarioService.aguardarCadastroDoIdAcesso(topico, mensagem, id);
        return ResponseEntity.ok("Aguardando UUID via MQTT...");
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable long id, @RequestBody UsuarioDTO usuario) {
        usuarioService.atualizarUsuario(usuario, id);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
