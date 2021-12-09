package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.OrderEntity;
import com.example.minimarket.model.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

    @Transactional
    void deleteById(Long id);

    @Query("select a from AddressEntity as a where a.dateTime < :dateTime")
    List<AddressEntity> findAllWithDateIsSmaller(@Param("dateTime") LocalDateTime dateTime);

    @Modifying
    @Transactional
    @Query("update AddressEntity as a set a.isDelivered = :isDelivered where a.id = :id")
    void setIsDelivered(@Param("isDelivered") boolean isDelivered, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update AddressEntity as a set a.paymentAmount = :paymentAmount where a.id = :id")
    void setPaymentAmount(@Param("paymentAmount")BigDecimal paymentAmount, @Param("id") Long id);

    @Query("select a from AddressEntity as a where a.isDelivered = :isDelivered and a.paymentAmount > 0 order by a.dateTime")
    List<AddressEntity> findAllByIsDeliveredOrderByDateTime(boolean isDelivered);

    List<AddressEntity> findAllByUserId(Long userId);
}
