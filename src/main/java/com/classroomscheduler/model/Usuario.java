package com.classroomscheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("USUARIO")
@Getter
@Setter
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String senhaHash;

    public Usuario() {
    }

    @Transient
    public PapelUsuario getPapel() {
        if (this instanceof Admin) {
            return PapelUsuario.ADMIN;
        }

        return PapelUsuario.SOLICITANTE;
    }

    @Transient
    public TipoSolicitante getTipoSolicitante() {
        if (this instanceof Aluno) {
            return TipoSolicitante.ALUNO;
        }

        if (this instanceof Funcionario) {
            return TipoSolicitante.FUNCIONARIO;
        }

        return null;
    }

}
