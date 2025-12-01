package com.monssif.ticket_management_service.repository;

import com.monssif.ticket_management_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);


    @Query("SELECT c FROM Category c WHERE c.isActive = true")
    List<Category> findAllActive();


    boolean existsByName(String name);


    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.category.id = :categoryId")
    Long countTicketsByCategoryId(@Param("categoryId") Long categoryId);
}