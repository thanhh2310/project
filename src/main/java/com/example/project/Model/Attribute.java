package com.example.project.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attributes")
@SQLDelete(sql = "Update attributes set deleted_at = CURRENT_TIMESTAMP, is_active = false Where id = ?")
public class Attribute extends BaseEntity {

    private String name;
    private String description;

    @OneToMany(mappedBy = "attribute",cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<AttributeValue> values = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void addValue(AttributeValue value) {
        this.values.add(value);
        value.setAttribute(this);
    }
}
