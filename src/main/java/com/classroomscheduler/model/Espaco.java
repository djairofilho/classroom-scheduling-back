package com.classroomscheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Espaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoEspaco tipo;

    private Integer capacidade;

    private boolean indisponivel;

    private String motivoIndisponibilidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predio_id")
    private Predio predio;

    @JsonIgnore
    @OneToMany(mappedBy = "espaco", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioFuncionamento> horariosFuncionamento = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "espaco", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Indisponibilidade> indisponibilidades = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "espaco_recurso",
            joinColumns = @JoinColumn(name = "espaco_id"),
            inverseJoinColumns = @JoinColumn(name = "recurso_id")
    )
    private List<RecursoEspaco> recursos = new ArrayList<>();

    public Espaco() {
    }
}
