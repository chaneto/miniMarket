package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

    void deleteById(Long id);

    @Query("select a from AddressEntity as a where a.dateTime < :dateTime")
    List<AddressEntity> findAllWithDateIsSmaller(@Param("dateTime") LocalDateTime dateTime);
}
