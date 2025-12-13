package com.example.project.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE categories SET deleted_at = CURRENT_TIMESTAMP, is_active = false WHERE id = ?")
@Table(name = "categories")
public class Category extends BaseEntity{
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
