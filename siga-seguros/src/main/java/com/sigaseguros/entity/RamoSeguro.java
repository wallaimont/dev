package com.sigaseguros.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ramos_seguro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamoSeguro extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;
}
