package com.example.demo.repository;

import com.example.demo.model.GarageAlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GarageAlanRepo extends JpaRepository<GarageAlan, Integer> {
    Optional<GarageAlan> findByAlan(int alan);
}
