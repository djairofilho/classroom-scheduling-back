package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePredioRequest {
    private String nome;
    private String codigo;
    private String localizacao;
}
