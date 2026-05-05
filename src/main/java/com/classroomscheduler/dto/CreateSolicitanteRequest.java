package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSolicitanteRequest {

    private String nome;

    private String email;

    private String numeroMatricula;

    private String numeroCracha;

    private String cargo;

    public CreateSolicitanteRequest() {
    }
}
