package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    // Navega pelo relacionamento user e acessa o campo id
    Optional<EmployeeEntity> findByUser_Id(Long userId);
}

