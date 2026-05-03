package com.classroomscheduler.dto;

import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.model.Usuario;

public class UsuarioAuthResponse {

    private Long id;

    private String email;

    private PapelUsuario papel;

    private TipoSolicitante tipoSolicitante;

    public UsuarioAuthResponse() {
    }

    public UsuarioAuthResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.papel = usuario.getPapel();
        this.tipoSolicitante = usuario.getTipoSolicitante();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public PapelUsuario getPapel() {
        return papel;
    }

    public TipoSolicitante getTipoSolicitante() {
        return tipoSolicitante;
    }
}
