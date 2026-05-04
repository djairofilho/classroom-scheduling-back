package com.classroomscheduler.model;

import com.classroomscheduler.exception.RegraDeNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class HorarioReserva {

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fim", nullable = false)
    private LocalDateTime fim;

    public HorarioReserva() {
    }

    public void validar() {
        if (inicio == null || fim == null) {
            throw new RegraDeNegocioException("Horario deve possuir inicio e fim.");
        }

        if (!fim.isAfter(inicio)) {
            throw new RegraDeNegocioException("Fim deve ser posterior ao inicio.");
        }
    }

}
