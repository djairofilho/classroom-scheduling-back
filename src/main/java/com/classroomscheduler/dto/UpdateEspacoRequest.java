package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEspacoRequest {
    private String nome;
    private Integer capacidade;
    private String tipo;
    private Long predioId;
}
