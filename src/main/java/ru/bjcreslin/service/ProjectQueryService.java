package ru.bjcreslin.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bjcreslin.domain.*; // for static metamodels
import ru.bjcreslin.domain.Project;
import ru.bjcreslin.repository.ProjectRepository;
import ru.bjcreslin.service.criteria.ProjectCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Project} entities in the database.
 * The main input is a {@link ProjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Project} or a {@link Page} of {@link Project} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectQueryService extends QueryService<Project> {

    private final Logger log = LoggerFactory.getLogger(ProjectQueryService.class);

    private final ProjectRepository projectRepository;

    public ProjectQueryService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Return a {@link List} of {@link Project} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Project> findByCriteria(ProjectCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Project} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Project> findByCriteria(ProjectCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Project> createSpecification(ProjectCriteria criteria) {
        Specification<Project> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Project_.id));
            }
            if (criteria.getProjectUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getProjectUrl(), Project_.projectUrl));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Project_.description));
            }
            if (criteria.getProjectName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getProjectName(), Project_.projectName));
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComment(), Project_.comment));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Project_.status));
            }
            if (criteria.getCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreated(), Project_.created));
            }
            if (criteria.getEdited() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEdited(), Project_.edited));
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(Project_.tasks, JoinType.LEFT).get(Task_.id))
                    );
            }
        }
        return specification;
    }
}
