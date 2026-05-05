package com.empresa.sgc.config;

import com.empresa.sgc.entity.Perfil;
import com.empresa.sgc.entity.Usuario;
import com.empresa.sgc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (!usuarioRepository.existsByEmail("admin@sgc.com")) {
                Usuario admin = Usuario.builder()
                        .nome("Administrador")
                        .email("admin@sgc.com")
                        .senha(passwordEncoder.encode("123456"))
                        .perfil(Perfil.ADMIN)
                        .ativo(true)
                        .build();
                usuarioRepository.save(admin);
            }
        };
    }
}
