package ru.bjcreslin.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bjcreslin.domain.UserInfo;
import ru.bjcreslin.repository.UserInfoRepository;
import ru.bjcreslin.service.UserInfoQueryService;
import ru.bjcreslin.service.UserInfoService;
import ru.bjcreslin.service.criteria.UserInfoCriteria;
import ru.bjcreslin.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.bjcreslin.domain.UserInfo}.
 */
@RestController
@RequestMapping("/api")
public class UserInfoResource {

    private final Logger log = LoggerFactory.getLogger(UserInfoResource.class);

    private static final String ENTITY_NAME = "userInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserInfoService userInfoService;

    private final UserInfoRepository userInfoRepository;

    private final UserInfoQueryService userInfoQueryService;

    public UserInfoResource(
        UserInfoService userInfoService,
        UserInfoRepository userInfoRepository,
        UserInfoQueryService userInfoQueryService
    ) {
        this.userInfoService = userInfoService;
        this.userInfoRepository = userInfoRepository;
        this.userInfoQueryService = userInfoQueryService;
    }

    /**
     * {@code POST  /user-infos} : Create a new userInfo.
     *
     * @param userInfo the userInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userInfo, or with status {@code 400 (Bad Request)} if the userInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-infos")
    public ResponseEntity<UserInfo> createUserInfo(@Valid @RequestBody UserInfo userInfo) throws URISyntaxException {
        log.debug("REST request to save UserInfo : {}", userInfo);
        if (userInfo.getId() != null) {
            throw new BadRequestAlertException("A new userInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserInfo result = userInfoService.save(userInfo);
        return ResponseEntity
            .created(new URI("/api/user-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-infos/:id} : Updates an existing userInfo.
     *
     * @param id the id of the userInfo to save.
     * @param userInfo the userInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userInfo,
     * or with status {@code 400 (Bad Request)} if the userInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-infos/{id}")
    public ResponseEntity<UserInfo> updateUserInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserInfo userInfo
    ) throws URISyntaxException {
        log.debug("REST request to update UserInfo : {}, {}", id, userInfo);
        if (userInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserInfo result = userInfoService.save(userInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-infos/:id} : Partial updates given fields of an existing userInfo, field will ignore if it is null
     *
     * @param id the id of the userInfo to save.
     * @param userInfo the userInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userInfo,
     * or with status {@code 400 (Bad Request)} if the userInfo is not valid,
     * or with status {@code 404 (Not Found)} if the userInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the userInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-infos/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<UserInfo> partialUpdateUserInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserInfo userInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserInfo partially : {}, {}", id, userInfo);
        if (userInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserInfo> result = userInfoService.partialUpdate(userInfo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userInfo.getId().toString())
        );
    }

    /**
     * {@code GET  /user-infos} : get all the userInfos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userInfos in body.
     */
    @GetMapping("/user-infos")
    public ResponseEntity<List<UserInfo>> getAllUserInfos(UserInfoCriteria criteria) {
        log.debug("REST request to get UserInfos by criteria: {}", criteria);
        List<UserInfo> entityList = userInfoQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-infos/count} : count all the userInfos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-infos/count")
    public ResponseEntity<Long> countUserInfos(UserInfoCriteria criteria) {
        log.debug("REST request to count UserInfos by criteria: {}", criteria);
        return ResponseEntity.ok().body(userInfoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-infos/:id} : get the "id" userInfo.
     *
     * @param id the id of the userInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-infos/{id}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable Long id) {
        log.debug("REST request to get UserInfo : {}", id);
        Optional<UserInfo> userInfo = userInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userInfo);
    }

    /**
     * {@code DELETE  /user-infos/:id} : delete the "id" userInfo.
     *
     * @param id the id of the userInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-infos/{id}")
    public ResponseEntity<Void> deleteUserInfo(@PathVariable Long id) {
        log.debug("REST request to delete UserInfo : {}", id);
        userInfoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
