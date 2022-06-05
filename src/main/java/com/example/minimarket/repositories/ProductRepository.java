package com.example.minimarket.repositories;

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

    @Modifying
    @Transactional
    @Query("update ProductEntity as p set p.price = :price where p.name = :name")
    void setPrice(@Param("price") BigDecimal price, @Param("name") String productName);

    @Modifying
    @Transactional
    @Query("update ProductEntity as p set p.promotionPrice = :promotionPrice where p.name = :name")
    void setPromotionPrice(@Param("promotionPrice") BigDecimal promotionPrice, @Param("name") String productName);

    @Modifying
    @Transactional
    @Query("update ProductEntity as p set p.discountRate = :discountRate where p.name = :name")
    void setDiscountRate(@Param("discountRate") BigDecimal discountRate, @Param("name") String productName);

    @Modifying
    @Transactional
    @Query("update ProductEntity as p set p.isOnPromotion = :isOnPromotion where p.id = :id")
    void setIsOnPromotion(@Param("isOnPromotion") Boolean isOnPromotion, @Param("id") Long id);

    @Query("select p.name from ProductEntity as p")
    List<String> getAllProductsName();

    @Query(value = "select * from products as p order by p.quantity desc limit 4", nativeQuery = true)
    List<ProductEntity> findTop4ByQuantityDesc();

    @Query(value = "select * from products as p where p.is_on_promotion = true order by p.quantity desc", nativeQuery = true)
    List<ProductEntity> findAllByIsOnPromotionIsTrue();

    @Query("select p from ProductEntity as p where p.id = :id")
    ProductEntity getById(@Param("id") Long id);

    @Query("select p from ProductEntity as p order by p.name")
    List<ProductEntity> findAllOrderByName();

    @Query("select p from ProductEntity as p order by p.quantity")
    List<ProductEntity> findAllOrderByQuantity();

    @Query("select p from ProductEntity as p order by p.quantity desc")
    List<ProductEntity> findAllOrderByQuantityDesc();

    @Query("select p from ProductEntity as p order by p.price")
    List<ProductEntity> findAllOrderByPrice();

    @Query("select p from ProductEntity as p order by p.price desc")
    List<ProductEntity> findAllOrderByPriceDesc();

    List<ProductEntity> findAllByIsLeastInterest(boolean isLeastInterest);

    @Modifying
    @Transactional
    @Query("update ProductEntity as p set p.isLeastInterest = :isLeastInterest where p.id = :id")
    void setIsLeastInterest(@Param("isLeastInterest") Boolean isLeastInterest, @Param("id") Long id);

}
