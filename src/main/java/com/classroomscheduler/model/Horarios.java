package com.classroomscheduler.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Horarios {

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fim", nullable = false)
    private LocalDateTime fim;

    public boolean conflita(Horarios outro) {
        if (outro == null || inicio == null || fim == null || outro.inicio == null || outro.fim == null) {
            return false;
        }

        return inicio.isBefore(outro.getFim()) && fim.isAfter(outro.getInicio());
    }

    public void validar() {
        if (inicio == null || fim == null) {
            throw new IllegalStateException("Horario deve possuir inicio e fim.");
        }

        if (!fim.isAfter(inicio)) {
            throw new IllegalStateException("Fim deve ser posterior ao inicio.");
        }
    }
}
