package com.classroomscheduler.config;

import com.classroomscheduler.model.Admin;
import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    public CommandLineRunner adminBootstrapRunner(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            @Value("${APP_ADMIN_NOME:Administrador Padrao}") String adminNome,
            @Value("${APP_ADMIN_EMAIL:admin@insper.edu.br}") String adminEmail,
            @Value("${APP_ADMIN_PASSWORD:admin1234}") String adminPassword
    ) {
        return args -> {
            if (adminRepository.findByEmail(adminEmail).isPresent()) {
                return;
            }

            Admin admin = new Admin();
            admin.setNome(adminNome);
            admin.setEmail(adminEmail.trim().toLowerCase());
            admin.setPapel(PapelUsuario.ADMIN);
            admin.setSenhaHash(passwordEncoder.encode(adminPassword));
            adminRepository.save(admin);
        };
    }
}
