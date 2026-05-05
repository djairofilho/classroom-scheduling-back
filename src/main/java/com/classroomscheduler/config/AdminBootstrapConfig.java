package com.classroomscheduler.config;

import com.classroomscheduler.model.Admin;
import com.classroomscheduler.repository.AdminRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    public CommandLineRunner adminBootstrapRunner(
            UsuarioRepository usuarioRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            @Value("${APP_ADMIN_NOME:Administrador Padrao}") String adminNome,
            @Value("${APP_ADMIN_EMAIL:admin@insper.edu.br}") String adminEmail,
            @Value("${APP_ADMIN_PASSWORD:admin1234}") String adminPassword
    ) {
        return args -> {
            String emailNormalizado = adminEmail.trim().toLowerCase(Locale.ROOT);
            if (usuarioRepository.findByEmail(emailNormalizado).isPresent()) {
                return;
            }

            Admin admin = new Admin();
            admin.setNome(adminNome);
            admin.setEmail(emailNormalizado);
            admin.setSenhaHash(passwordEncoder.encode(adminPassword));
            adminRepository.save(admin);
        };
    }
}
