package com.example.project.Repository;

import com.example.project.Model.Brand;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByName(String name);

    boolean existsByName(@Size(min = 2, max = 100, message = "Brand name must be between 2 and 100 characters") String name);
}
