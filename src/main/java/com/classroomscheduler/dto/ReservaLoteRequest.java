package com.classroomscheduler.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaLoteRequest {
    private Long solicitanteId;
    private Long espacoId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private List<Integer> diasSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String motivo;
    private String statusInicial;
    private Boolean aprovacaoAutomatica;

    public Long getSolicitanteId() {
        return solicitanteId;
    }

    public void setSolicitanteId(Long solicitanteId) {
        this.solicitanteId = solicitanteId;
    }

    public Long getEspacoId() {
        return espacoId;
    }

    public void setEspacoId(Long espacoId) {
        this.espacoId = espacoId;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public List<Integer> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(List<Integer> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getStatusInicial() {
        return statusInicial;
    }

    public void setStatusInicial(String statusInicial) {
        this.statusInicial = statusInicial;
    }

    public Boolean getAprovacaoAutomatica() {
        return aprovacaoAutomatica;
    }

    public void setAprovacaoAutomatica(Boolean aprovacaoAutomatica) {
        this.aprovacaoAutomatica = aprovacaoAutomatica;
    }
}
