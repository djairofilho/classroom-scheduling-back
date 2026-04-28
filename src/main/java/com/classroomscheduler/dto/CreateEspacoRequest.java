package com.classroomscheduler.dto;

public class CreateEspacoRequest {

    private String nome;

    private String tipo;

    private Integer capacidade;

    private Long predioId;

    public CreateEspacoRequest() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public Long getPredioId() {
        return predioId;
    }

    public void setPredioId(Long predioId) {
        this.predioId = predioId;
    }
}
