package com.example.orderhub.config;

import com.example.orderhub.entity.Client;
import com.example.orderhub.entity.Product;
import com.example.orderhub.entity.User;
import com.example.orderhub.enums.Role;
import com.example.orderhub.repository.ClientRepository;
import com.example.orderhub.repository.ProductRepository;
import com.example.orderhub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               ClientRepository clientRepository,
                               ProductRepository productRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@orderhub.com")) {
                userRepository.save(User.builder()
                        .name("Admin Demo")
                        .email("admin@orderhub.com")
                        .password(passwordEncoder.encode("123456"))
                        .role(Role.ADMIN)
                        .build());
            }

            if (clientRepository.count() == 0) {
                clientRepository.save(Client.builder()
                        .name("Cliente Demo 1")
                        .email("cliente1@email.com")
                        .phone("21999990001")
                        .document("12345678901")
                        .active(true)
                        .build());
                clientRepository.save(Client.builder()
                        .name("Cliente Demo 2")
                        .email("cliente2@email.com")
                        .phone("21999990002")
                        .document("98765432100")
                        .active(true)
                        .build());
            }

            if (productRepository.count() == 0) {
                productRepository.save(Product.builder()
                        .name("Notebook Pro")
                        .description("Notebook corporativo")
                        .category("Informática")
                        .price(new BigDecimal("4500.00"))
                        .stock(10)
                        .build());
                productRepository.save(Product.builder()
                        .name("Mouse Sem Fio")
                        .description("Mouse ergonômico")
                        .category("Periféricos")
                        .price(new BigDecimal("120.00"))
                        .stock(50)
                        .build());
                productRepository.save(Product.builder()
                        .name("Monitor 27")
                        .description("Monitor IPS 27 polegadas")
                        .category("Informática")
                        .price(new BigDecimal("1500.00"))
                        .stock(15)
                        .build());
            }
        };
    }
}
