package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUsuarioRequest {
    private String nome;
    private String email;
    private String tipoSolicitante;
}
