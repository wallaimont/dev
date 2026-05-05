package com.orbyt.marketplace.shipping.domain;

import com.orbyt.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "shipment_tracking")
@Getter @Setter @NoArgsConstructor
public class ShipmentTracking extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 200)
    private String location;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;
}
