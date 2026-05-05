package com.classroomscheduler.dto;

import com.classroomscheduler.model.Usuario;
import lombok.Getter;

@Getter
public class AuthResponse {

    private String token;

    private UsuarioAuthResponse usuario;

    public AuthResponse() {
    }

    public AuthResponse(String token, Usuario usuario) {
        this.token = token;
        this.usuario = new UsuarioAuthResponse(usuario);
    }

}
