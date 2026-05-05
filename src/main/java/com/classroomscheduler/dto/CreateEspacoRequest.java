package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateEspacoRequest {

    private String nome;

    private String tipo;

    private Integer capacidade;

    private Long predioId;

    public CreateEspacoRequest() {
    }

}
