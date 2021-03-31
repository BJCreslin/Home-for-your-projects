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
import ru.bjcreslin.domain.UserInfo;
import ru.bjcreslin.repository.UserInfoRepository;
import ru.bjcreslin.service.criteria.UserInfoCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UserInfo} entities in the database.
 * The main input is a {@link UserInfoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserInfo} or a {@link Page} of {@link UserInfo} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserInfoQueryService extends QueryService<UserInfo> {

    private final Logger log = LoggerFactory.getLogger(UserInfoQueryService.class);

    private final UserInfoRepository userInfoRepository;

    public UserInfoQueryService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * Return a {@link List} of {@link UserInfo} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserInfo> findByCriteria(UserInfoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserInfo> specification = createSpecification(criteria);
        return userInfoRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link UserInfo} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserInfo> findByCriteria(UserInfoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserInfo> specification = createSpecification(criteria);
        return userInfoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserInfoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserInfo> specification = createSpecification(criteria);
        return userInfoRepository.count(specification);
    }

    /**
     * Function to convert {@link UserInfoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserInfo> createSpecification(UserInfoCriteria criteria) {
        Specification<UserInfo> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserInfo_.id));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), UserInfo_.email));
            }
            if (criteria.getGitHubId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGitHubId(), UserInfo_.gitHubId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), UserInfo_.name));
            }
            if (criteria.getHours() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHours(), UserInfo_.hours));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), UserInfo_.status));
            }
            if (criteria.getBirthday() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBirthday(), UserInfo_.birthday));
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComment(), UserInfo_.comment));
            }
            if (criteria.getCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreated(), UserInfo_.created));
            }
            if (criteria.getEdited() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEdited(), UserInfo_.edited));
            }
        }
        return specification;
    }
}
