package com.classroomscheduler.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("FUNCIONARIO")
@Getter
@Setter
public class Funcionario extends Usuario {

    private String numeroCracha;

    private String cargo;

    public Funcionario() {
    }
}
