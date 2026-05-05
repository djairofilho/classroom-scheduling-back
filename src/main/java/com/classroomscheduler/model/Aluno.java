package com.classroomscheduler.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("ALUNO")
@Getter
@Setter
public class Aluno extends Usuario {

    private String numeroMatricula;

    public Aluno() {
    }
}
