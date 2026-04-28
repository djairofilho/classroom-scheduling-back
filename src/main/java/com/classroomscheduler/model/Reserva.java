package com.classroomscheduler.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Solicitante solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espaco_id", nullable = false)
    private Espaco espaco;

    @Embedded
    private Horarios horarios;

    private String motivo;

    private boolean cancelada;

    private LocalDateTime criadaEm;

    @PrePersist
    void prePersist() {
        validarReserva();
        if (criadaEm == null) {
            criadaEm = LocalDateTime.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        validarReserva();
    }

    private void validarReserva() {
        if (horarios == null) {
            throw new IllegalStateException("Reserva deve possuir horarios.");
        }

        horarios.validar();
    }
}
