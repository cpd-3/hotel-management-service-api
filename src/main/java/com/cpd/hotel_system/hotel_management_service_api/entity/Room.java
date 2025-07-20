package com.cpd.hotel_system.hotel_management_service_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name="room")
public class Room {
    @Id
    @Column(name = "room_id", length = 80, nullable = false)
    private String roomId;

    @Column(name = "room_number", length = 80, nullable = false)
    private String roomNumber;

    @Column(name = "room_type", length = 80, nullable = false)
    private String type;

    @Column(name = "bed_count")
    private int bedCount;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "is_available")
    private boolean isAvailable;
}
