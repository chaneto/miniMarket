package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.CourierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, Long> {

    CourierEntity findByName(String name);

    List<CourierEntity> findAll();

    @Modifying
    @Transactional
    void deleteByName(String name);
}
