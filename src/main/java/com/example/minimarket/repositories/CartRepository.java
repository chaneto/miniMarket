package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    @Modifying
    @Transactional
    @Query("update CartEntity as c set c.totalPrice = :totalPrice WHERE c.id = :cartId")
    void setTotalPrice(@Param("totalPrice") BigDecimal totalPrice, @Param("cartId") Long id);

    @Modifying
    @Transactional
    @Query("update CartEntity as c set c.courier = :courier where c.id = :id")
    void setCourier(@Param("courier") CourierEntity courierEntity, @Param("id")  Long id);

    @Modifying
    @Transactional
    @Query("update CartEntity as c set c.address = :address where c.id = :id")
    void setAddress(@Param("address") AddressEntity addressEntity, @Param("id")  Long id);

    //@Modifying
    //@Transactional
    // @Query("update CartEntity as c set c.orders = :orders WHERE c.id = :cartId")
    // void setOrders(@Param("orders") List<OrderEntity> orders, @Param("cartId") Long id);

    @Query("select c.orders from CartEntity as c")
    List<OrderEntity> getOrders();

    @Query("select c from CartEntity as c where c.id = :cartId")
    CartEntity getCartById(@Param("cartId") Long id);
}
