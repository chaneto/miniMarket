package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    CategoryEntity findByName(String name);

    @Override
    List<CategoryEntity> findAll();

    @Query("select c.name from CategoryEntity as c")
    List<String> getAllCategoryName();

    @Transactional
    @Modifying
    void deleteByName(String name);

}
