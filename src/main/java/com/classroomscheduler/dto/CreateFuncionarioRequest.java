package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFuncionarioRequest {

    private String nome;

    private String email;

    private String numeroCracha;

    private String cargo;

    public CreateFuncionarioRequest() {
    }

}
