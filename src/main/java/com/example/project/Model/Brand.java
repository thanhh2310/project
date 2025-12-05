package com.example.project.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "brands")
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE brands SET deleted_at = CURRENT_TIMESTAMP, is_active = false WHERE id = ?")
//@Where(clause = "deleted_at IS NULL")
public class Brand  extends BaseEntity {
    private String name;
    private String slug;
    @Column(name = "logo_url")
    private String logoUrl;
    private String description;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
