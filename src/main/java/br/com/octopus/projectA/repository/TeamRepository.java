package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.TeamEntity;
import br.com.octopus.projectA.entity.enuns.TeamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    Optional<TeamEntity> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT t FROM TeamEntity t WHERE " +
           "(:search IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR t.status = :status)")
    Page<TeamEntity> findByFilters(
            @Param("search") String search,
            @Param("status") TeamStatus status,
            Pageable pageable
    );

    @Query("SELECT COUNT(t) FROM TeamEntity t WHERE t.status = :status")
    long countByStatus(@Param("status") TeamStatus status);

    @Query("SELECT COUNT(DISTINCT a) FROM TeamEntity t JOIN t.agents a WHERE t.status = :status")
    long countAgentsByTeamStatus(@Param("status") TeamStatus status);

    @Query("SELECT COUNT(DISTINCT a) FROM TeamEntity t JOIN t.agents a")
    long countAllDistinctAgents();
}

