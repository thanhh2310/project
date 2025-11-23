package com.example.project.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "address_line1")
    private String addressLine1;
    @Column(name = "address_line2")
    private String addressLine2;
    private String city;
    private String state;
    @Column(name = "postal_code")
    private String postalCode;
    private String country;

    @Column(name = "is_default")
    private Boolean idDefault;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
