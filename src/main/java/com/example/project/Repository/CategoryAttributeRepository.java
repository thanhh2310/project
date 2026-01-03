package com.example.project.Repository;

import com.example.project.Model.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Integer> {
    // lay danh sach Attribute cua 1 category
    @Query("SELECT ca FROM CategoryAttribute ca JOIN FETCH ca.attribute WHERE ca.category.id = :categoryId")
    List<CategoryAttribute> findByCategoryId(@Param("categoryId") Integer id);
    // xoa tat ca attribute cu cua category
    void deleteByCategoryId (Integer categoryId);
}
