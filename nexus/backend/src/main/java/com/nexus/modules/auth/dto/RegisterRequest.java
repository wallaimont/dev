package com.nexus.modules.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
public class RegisterRequest {
    @NotBlank @Email     public String email;
    @NotBlank @Size(min=8,max=100) public String password;
    @NotBlank            public String firstName;
    @NotBlank            public String lastName;
    public String phone;
}
