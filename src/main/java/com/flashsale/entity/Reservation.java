package com.flashsale.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "itemId"}) // Garante que o par seja único
})
public class Reservation {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String itemId;

    private int quantity;

    @Enumerated(EnumType.STRING) // Salva como texto no banco ("PENDING") em vez de número
    private ReservationStatus status;

    private LocalDateTime createdAt;

    public Reservation() {}

    public Reservation(String userId, String itemId, int quantity) {
        this.userId = userId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.status = ReservationStatus.PENDING; // Começa sempre como Pendente
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setter para atualizar status depois
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}