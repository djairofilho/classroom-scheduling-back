package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateAlunoRequest {

    private String nome;

    private String email;

    private String numeroMatricula;

    public CreateAlunoRequest() {
    }

}
