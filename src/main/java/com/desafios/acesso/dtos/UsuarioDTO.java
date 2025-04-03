package com.desafios.acesso.dtos;

import java.util.UUID;

public record UsuarioDTO(String nome, UUID idAcesso, String telefone, String email) {
}
