package com.classroomscheduler.dto;

import com.classroomscheduler.model.Usuario;
import lombok.Getter;

@Getter
public class UsuarioAuthResponse {

    private Long id;

    private String email;

    public UsuarioAuthResponse() {
    }

    public UsuarioAuthResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
    }

}
