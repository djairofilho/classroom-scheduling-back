package com.classroomscheduler.config;

import com.classroomscheduler.model.Admin;
import com.classroomscheduler.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    public CommandLineRunner adminBootstrapRunner(
            AdminRepository adminRepository,
            @Value("${APP_ADMIN_NOME:Administrador Padrao}") String adminNome,
            @Value("${APP_ADMIN_EMAIL:admin@classroom.local}") String adminEmail
    ) {
        return args -> {
            if (adminRepository.findByEmail(adminEmail).isPresent()) {
                return;
            }

            Admin admin = new Admin();
            admin.setNome(adminNome);
            admin.setEmail(adminEmail);
            adminRepository.save(admin);
        };
    }
}
