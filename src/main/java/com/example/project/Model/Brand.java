package com.example.project.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bands")
@EqualsAndHashCode(callSuper = true)
public class Brand  extends BaseEntity {
    private String name;
    private String slug;
    @Column(name = "logo_url")
    private String logoUrl;
    private String description;
    @Column(name = "is_active")
    private Boolean isActive;
}
