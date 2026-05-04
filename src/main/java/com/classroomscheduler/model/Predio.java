package com.classroomscheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Predio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String codigo;

    private String localizacao;

    @JsonIgnore
    @OneToMany(mappedBy = "predio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioFuncionamento> horariosFuncionamento = new ArrayList<>();

    public Predio() {
    }

}
