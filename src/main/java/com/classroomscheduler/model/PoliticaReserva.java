package com.classroomscheduler.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PoliticaReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer antecedenciaMinimaHoras;

    private Integer duracaoMaximaHoras;

    private boolean permiteFimDeSemana;

    private boolean requerAprovacaoAdmin;

    public PoliticaReserva() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAntecedenciaMinimaHoras() {
        return antecedenciaMinimaHoras;
    }

    public void setAntecedenciaMinimaHoras(Integer antecedenciaMinimaHoras) {
        this.antecedenciaMinimaHoras = antecedenciaMinimaHoras;
    }

    public Integer getDuracaoMaximaHoras() {
        return duracaoMaximaHoras;
    }

    public void setDuracaoMaximaHoras(Integer duracaoMaximaHoras) {
        this.duracaoMaximaHoras = duracaoMaximaHoras;
    }

    public boolean isPermiteFimDeSemana() {
        return permiteFimDeSemana;
    }

    public void setPermiteFimDeSemana(boolean permiteFimDeSemana) {
        this.permiteFimDeSemana = permiteFimDeSemana;
    }

    public boolean isRequerAprovacaoAdmin() {
        return requerAprovacaoAdmin;
    }

    public void setRequerAprovacaoAdmin(boolean requerAprovacaoAdmin) {
        this.requerAprovacaoAdmin = requerAprovacaoAdmin;
    }
}
