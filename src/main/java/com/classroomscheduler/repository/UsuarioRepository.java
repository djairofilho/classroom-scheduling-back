package com.classroomscheduler.repository;

import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByNome(String nome);

    default Optional<Usuario> findByEmailAndPapel(String email, PapelUsuario papel) {
        return findByEmail(email)
                .filter(usuario -> usuario.getPapel() == papel);
    }

    default List<Usuario> findByPapel(PapelUsuario papel) {
        return findAll().stream()
                .filter(usuario -> usuario.getPapel() == papel)
                .toList();
    }
}
