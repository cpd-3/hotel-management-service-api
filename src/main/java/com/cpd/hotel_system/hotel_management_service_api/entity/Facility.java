package com.cpd.hotel_system.hotel_management_service_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name="facility")
public class Facility {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(length=100, nullable=false, name = "name")
    private String name;

}
