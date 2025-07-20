package com.cpd.hotel_system.hotel_management_service_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

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

    @ManyToOne()
    @JoinColumn(name="branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "room")
    private List<Facility> facilities;

    @OneToMany(mappedBy = "room")
    private List<RoomImage>  roomImages;

}
