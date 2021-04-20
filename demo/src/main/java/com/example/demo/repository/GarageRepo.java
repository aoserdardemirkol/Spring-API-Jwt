package com.example.demo.repository;

import com.example.demo.model.Garage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GarageRepo extends JpaRepository<Garage, Integer> {
    Optional<Garage> findByPlaka(String plaka);
    Optional<Garage> deleteByPlaka(String plaka);

    @Query(value = "SELECT sum(alan) FROM Garage")
    public int sumAlan();
}
