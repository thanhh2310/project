package com.example.project.Repository;

import com.example.project.Model.Attribute;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
    boolean existsByName(@NotBlank(message = "Attribute name is required") String name);
}
