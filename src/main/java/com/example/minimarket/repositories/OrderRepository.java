package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.CartServiceModel;
import org.hibernate.criterion.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findAllByCartIdAndIsPaid(Long id, Boolean isPaid);

    @Modifying
    @Transactional
    @Query("update OrderEntity as o set o.courier = :courier where o.id = :id")
    void setCourier(@Param("courier") CourierEntity courierEntity, @Param("id")  Long id);

    @Modifying
    @Transactional
    @Query("update OrderEntity as o set o.address = :address where o.id = :id")
    void setAddress(@Param("address") AddressEntity addressEntity, @Param("id")  Long id);

    @Modifying
    @Transactional
    @Query("update OrderEntity as o set o.isPaid = :isPaid where o.id = :id")
    void setIsPaid(@Param("isPaid") Boolean isPaid, @Param("id")  Long id);

    @Modifying
    @Transactional
    @Query("delete from OrderEntity as o where o.id = :id")
    void deleteOrderById(@Param("id") Long id);

    @Query("select o from OrderEntity as o where o.id = :id")
    OrderEntity findOrderById(@Param("id") Long id);

    @Query("select o from OrderEntity as o where o.isPaid = :isPaid and o.cart.id = :id")
    List<OrderEntity> findAllOrderByIsPaid(@Param("isPaid") Boolean isPaid, @Param("id") Long id);

    List<OrderEntity> findAllByProduct(ProductEntity productEntity);
}
