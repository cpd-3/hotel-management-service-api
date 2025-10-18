package com.cpd.hotel_system.hotel_management_service_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Embeddable
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileFormatter {
    @Lob
    @Column(name="file_name")
    private byte[] fileName;

    @Column(name="resource_url")
    private byte[] resourceUrl;

    @Column(name="directory")
    private byte[] directory;

    @Column(name="hash")
    private byte[] hash;
}
