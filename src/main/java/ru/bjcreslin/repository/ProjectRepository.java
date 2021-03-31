package ru.bjcreslin.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.bjcreslin.domain.Project;

/**
 * Spring Data SQL repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {}
