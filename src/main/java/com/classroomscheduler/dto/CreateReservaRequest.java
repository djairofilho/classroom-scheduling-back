package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CreateReservaRequest {

    private Long solicitanteId;

    private Long espacoId;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    private String motivo;

    public CreateReservaRequest() {
    }

}
