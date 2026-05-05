package com.orionerp.modules.estoque.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventarios")
@Getter
@Setter
public class Inventario extends TenantEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "armazem_id", nullable = false)
    private Armazem armazem;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(name = "data_inventario", nullable = false)
    private LocalDate dataInventario = LocalDate.now();

    @Column(name = "responsavel_id")
    private Long responsavelId;

    @Column(nullable = false, length = 20)
    private String status = "ABERTO";

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<InventarioItem> itens = new ArrayList<>();

    public void addItem(InventarioItem item) {
        item.setInventario(this);
        itens.add(item);
    }
}
