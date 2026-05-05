package com.classroomscheduler.dto;

import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.model.Usuario;
import lombok.Getter;

@Getter
public class UsuarioAuthResponse {

    private Long id;

    private String nome;

    private String email;

    private PapelUsuario papel;

    private TipoSolicitante tipoSolicitante;

    public UsuarioAuthResponse() {
    }

    public UsuarioAuthResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.papel = usuario.getPapel();
        this.tipoSolicitante = usuario.getTipoSolicitante();
    }

}
