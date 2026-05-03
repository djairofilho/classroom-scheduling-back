package com.classroomscheduler.model;

import com.classroomscheduler.exception.RegraDeNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class Horarios {

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fim", nullable = false)
    private LocalDateTime fim;

    public Horarios() {
    }

    public boolean conflita(Horarios outro) {
        if (outro == null || inicio == null || fim == null || outro.inicio == null || outro.fim == null) {
            return false;
        }

        return inicio.isBefore(outro.getFim()) && fim.isAfter(outro.getInicio());
    }

    public void validar() {
        if (inicio == null || fim == null) {
            throw new RegraDeNegocioException("Horario deve possuir inicio e fim.");
        }

        if (!fim.isAfter(inicio)) {
            throw new RegraDeNegocioException("Fim deve ser posterior ao inicio.");
        }
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
}
