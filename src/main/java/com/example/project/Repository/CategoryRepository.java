package com.example.project.Repository;

import com.example.project.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Integer id);

    // Dùng LEFT JOIN FETCH để lấy luôn Children và Parent trong 1 lần
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children LEFT JOIN FETCH c.parent")
    List<Category> findAllWithTree();
}
