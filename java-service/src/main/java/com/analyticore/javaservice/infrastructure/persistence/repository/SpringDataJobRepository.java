package com.analyticore.javaservice.infrastructure.persistence.repository;

import com.analyticore.javaservice.infrastructure.persistence.entity.JobJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface Spring Data JPA para la tabla 'jobs'.
 */
@Repository
public interface SpringDataJobRepository extends JpaRepository<JobJpaEntity, String> {
}
