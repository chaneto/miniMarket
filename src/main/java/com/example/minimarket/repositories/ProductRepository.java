package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    ProductEntity findByName(String name);

    @Modifying
    @Transactional
    //@Query("delete from ProductEntity as p where p.name = :name")
    void deleteByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("update ProductEntity as p set p.quantity = p.quantity + ?1 where p.name = ?2")
    void addQuantity(BigDecimal quantity, String name);

    @Modifying
    @Transactional
    @Query("update ProductEntity as p set p.quantity = p.quantity - ?1 where p.name = ?2")
    void subtractQuantity(BigDecimal quantity, String name);

    @Modifying
    @Transactional
    @Query("update ProductEntity as p set p.isAvailable = :isAvailable where p.id = :id")
    void setIsAvailable(@Param("isAvailable") Boolean isAvailable, @Param("id") Long id);

    @Query("select p.name from ProductEntity as p")
    List<String> getAllProductsName();

}
