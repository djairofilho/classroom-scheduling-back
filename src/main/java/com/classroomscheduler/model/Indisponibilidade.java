package com.classroomscheduler.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Indisponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espaco_id", nullable = false)
    private Espaco espaco;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criada_por_id")
    private Usuario criadaPor;

    public Indisponibilidade() {
    }

    public Long getId() {
        return id;
    }

    public Espaco getEspaco() {
        return espaco;
    }

    public void setEspaco(Espaco espaco) {
        this.espaco = espaco;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Usuario getCriadaPor() {
        return criadaPor;
    }

    public void setCriadaPor(Usuario criadaPor) {
        this.criadaPor = criadaPor;
    }
}
