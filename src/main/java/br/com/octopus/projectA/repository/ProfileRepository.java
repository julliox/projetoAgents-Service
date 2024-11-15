package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findByName(String name);
}
