package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Long> {

    BrandEntity findByName(String name);

    @Query("select b from BrandEntity as b")
    List<BrandEntity> getAllBrands();

    @Modifying
    @Transactional
    void deleteByName(String name);
}
