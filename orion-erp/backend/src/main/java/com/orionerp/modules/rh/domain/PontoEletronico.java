package com.orionerp.modules.rh.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "ponto_eletronico")
@Getter
@Setter
public class PontoEletronico extends TenantEntity {

    @Column(name = "funcionario_id", nullable = false)
    private Long funcionarioId;

    @Column(nullable = false)
    private LocalDate data;

    private LocalTime entrada1;
    private LocalTime saida1;
    private LocalTime entrada2;
    private LocalTime saida2;
    private LocalTime entrada3;
    private LocalTime saida3;

    @Column(name = "horas_trabalhadas", precision = 10, scale = 2)
    private BigDecimal horasTrabalhadas = BigDecimal.ZERO;

    @Column(name = "horas_extras", precision = 10, scale = 2)
    private BigDecimal horasExtras = BigDecimal.ZERO;

    @Column(name = "horas_falta", precision = 10, scale = 2)
    private BigDecimal horasFalta = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String tipo = "NORMAL";

    @Column
    private Boolean aprovado = false;
}
