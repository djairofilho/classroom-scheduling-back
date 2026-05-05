package com.classroomscheduler.model;

import com.classroomscheduler.exception.RegraDeNegocioException;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espaco_id", nullable = false)
    private Espaco espaco;

    @Embedded
    private HorarioReserva horarios;

    private String motivo;

    private boolean cancelada;

    @Enumerated(EnumType.STRING)
    private StatusReserva status;

    private LocalDateTime criadaEm;

    public Reserva() {
    }

    @PrePersist
    void prePersist() {
        validarReserva();
        if (criadaEm == null) {
            criadaEm = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusReserva.PENDENTE;
        }
    }

    @PreUpdate
    void preUpdate() {
        validarReserva();
    }

    private void validarReserva() {
        if (horarios == null) {
            throw new RegraDeNegocioException("Reserva deve possuir horarios.");
        }

        horarios.validar();
    }

}
