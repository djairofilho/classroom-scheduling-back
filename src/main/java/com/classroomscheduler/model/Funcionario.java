package com.classroomscheduler.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Funcionario extends Usuario{

    String numeroCracha;

    String cargo;
}
