package ru.bjcreslin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bjcreslin.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.bjcreslin.IntegrationTest;
import ru.bjcreslin.domain.UserInfo;
import ru.bjcreslin.domain.enumeration.UserStatus;
import ru.bjcreslin.repository.UserInfoRepository;
import ru.bjcreslin.service.criteria.UserInfoCriteria;

/**
 * Integration tests for the {@link UserInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserInfoResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_GIT_HUB_ID = "AAAAAAAAAA";
    private static final String UPDATED_GIT_HUB_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_HOURS = 0L;
    private static final Long UPDATED_HOURS = 1L;
    private static final Long SMALLER_HOURS = 0L - 1L;

    private static final UserStatus DEFAULT_STATUS = UserStatus.WAITING_FOR_A_TASK;
    private static final UserStatus UPDATED_STATUS = UserStatus.BUSY;

    private static final LocalDate DEFAULT_BIRTHDAY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTHDAY = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTHDAY = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_EDITED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_EDITED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_EDITED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/user-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserInfoMockMvc;

    private UserInfo userInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserInfo createEntity(EntityManager em) {
        UserInfo userInfo = new UserInfo()
            .email(DEFAULT_EMAIL)
            .gitHubId(DEFAULT_GIT_HUB_ID)
            .name(DEFAULT_NAME)
            .hours(DEFAULT_HOURS)
            .status(DEFAULT_STATUS)
            .birthday(DEFAULT_BIRTHDAY)
            .comment(DEFAULT_COMMENT)
            .created(DEFAULT_CREATED)
            .edited(DEFAULT_EDITED);
        return userInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserInfo createUpdatedEntity(EntityManager em) {
        UserInfo userInfo = new UserInfo()
            .email(UPDATED_EMAIL)
            .gitHubId(UPDATED_GIT_HUB_ID)
            .name(UPDATED_NAME)
            .hours(UPDATED_HOURS)
            .status(UPDATED_STATUS)
            .birthday(UPDATED_BIRTHDAY)
            .comment(UPDATED_COMMENT)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);
        return userInfo;
    }

    @BeforeEach
    public void initTest() {
        userInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createUserInfo() throws Exception {
        int databaseSizeBeforeCreate = userInfoRepository.findAll().size();
        // Create the UserInfo
        restUserInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isCreated());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeCreate + 1);
        UserInfo testUserInfo = userInfoList.get(userInfoList.size() - 1);
        assertThat(testUserInfo.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserInfo.getGitHubId()).isEqualTo(DEFAULT_GIT_HUB_ID);
        assertThat(testUserInfo.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserInfo.getHours()).isEqualTo(DEFAULT_HOURS);
        assertThat(testUserInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testUserInfo.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testUserInfo.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testUserInfo.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testUserInfo.getEdited()).isEqualTo(DEFAULT_EDITED);
    }

    @Test
    @Transactional
    void createUserInfoWithExistingId() throws Exception {
        // Create the UserInfo with an existing ID
        userInfo.setId(1L);

        int databaseSizeBeforeCreate = userInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = userInfoRepository.findAll().size();
        // set the field null
        userInfo.setEmail(null);

        // Create the UserInfo, which fails.

        restUserInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isBadRequest());

        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userInfoRepository.findAll().size();
        // set the field null
        userInfo.setName(null);

        // Create the UserInfo, which fails.

        restUserInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isBadRequest());

        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserInfos() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList
        restUserInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].gitHubId").value(hasItem(DEFAULT_GIT_HUB_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(sameInstant(DEFAULT_EDITED))));
    }

    @Test
    @Transactional
    void getUserInfo() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get the userInfo
        restUserInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, userInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userInfo.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.gitHubId").value(DEFAULT_GIT_HUB_ID))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.birthday").value(DEFAULT_BIRTHDAY.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.edited").value(sameInstant(DEFAULT_EDITED)));
    }

    @Test
    @Transactional
    void getUserInfosByIdFiltering() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        Long id = userInfo.getId();

        defaultUserInfoShouldBeFound("id.equals=" + id);
        defaultUserInfoShouldNotBeFound("id.notEquals=" + id);

        defaultUserInfoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserInfoShouldNotBeFound("id.greaterThan=" + id);

        defaultUserInfoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserInfoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserInfosByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where email equals to DEFAULT_EMAIL
        defaultUserInfoShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the userInfoList where email equals to UPDATED_EMAIL
        defaultUserInfoShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserInfosByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where email not equals to DEFAULT_EMAIL
        defaultUserInfoShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the userInfoList where email not equals to UPDATED_EMAIL
        defaultUserInfoShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserInfosByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultUserInfoShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the userInfoList where email equals to UPDATED_EMAIL
        defaultUserInfoShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserInfosByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where email is not null
        defaultUserInfoShouldBeFound("email.specified=true");

        // Get all the userInfoList where email is null
        defaultUserInfoShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByEmailContainsSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where email contains DEFAULT_EMAIL
        defaultUserInfoShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the userInfoList where email contains UPDATED_EMAIL
        defaultUserInfoShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserInfosByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where email does not contain DEFAULT_EMAIL
        defaultUserInfoShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the userInfoList where email does not contain UPDATED_EMAIL
        defaultUserInfoShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserInfosByGitHubIdIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where gitHubId equals to DEFAULT_GIT_HUB_ID
        defaultUserInfoShouldBeFound("gitHubId.equals=" + DEFAULT_GIT_HUB_ID);

        // Get all the userInfoList where gitHubId equals to UPDATED_GIT_HUB_ID
        defaultUserInfoShouldNotBeFound("gitHubId.equals=" + UPDATED_GIT_HUB_ID);
    }

    @Test
    @Transactional
    void getAllUserInfosByGitHubIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where gitHubId not equals to DEFAULT_GIT_HUB_ID
        defaultUserInfoShouldNotBeFound("gitHubId.notEquals=" + DEFAULT_GIT_HUB_ID);

        // Get all the userInfoList where gitHubId not equals to UPDATED_GIT_HUB_ID
        defaultUserInfoShouldBeFound("gitHubId.notEquals=" + UPDATED_GIT_HUB_ID);
    }

    @Test
    @Transactional
    void getAllUserInfosByGitHubIdIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where gitHubId in DEFAULT_GIT_HUB_ID or UPDATED_GIT_HUB_ID
        defaultUserInfoShouldBeFound("gitHubId.in=" + DEFAULT_GIT_HUB_ID + "," + UPDATED_GIT_HUB_ID);

        // Get all the userInfoList where gitHubId equals to UPDATED_GIT_HUB_ID
        defaultUserInfoShouldNotBeFound("gitHubId.in=" + UPDATED_GIT_HUB_ID);
    }

    @Test
    @Transactional
    void getAllUserInfosByGitHubIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where gitHubId is not null
        defaultUserInfoShouldBeFound("gitHubId.specified=true");

        // Get all the userInfoList where gitHubId is null
        defaultUserInfoShouldNotBeFound("gitHubId.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByGitHubIdContainsSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where gitHubId contains DEFAULT_GIT_HUB_ID
        defaultUserInfoShouldBeFound("gitHubId.contains=" + DEFAULT_GIT_HUB_ID);

        // Get all the userInfoList where gitHubId contains UPDATED_GIT_HUB_ID
        defaultUserInfoShouldNotBeFound("gitHubId.contains=" + UPDATED_GIT_HUB_ID);
    }

    @Test
    @Transactional
    void getAllUserInfosByGitHubIdNotContainsSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where gitHubId does not contain DEFAULT_GIT_HUB_ID
        defaultUserInfoShouldNotBeFound("gitHubId.doesNotContain=" + DEFAULT_GIT_HUB_ID);

        // Get all the userInfoList where gitHubId does not contain UPDATED_GIT_HUB_ID
        defaultUserInfoShouldBeFound("gitHubId.doesNotContain=" + UPDATED_GIT_HUB_ID);
    }

    @Test
    @Transactional
    void getAllUserInfosByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where name equals to DEFAULT_NAME
        defaultUserInfoShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the userInfoList where name equals to UPDATED_NAME
        defaultUserInfoShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserInfosByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where name not equals to DEFAULT_NAME
        defaultUserInfoShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the userInfoList where name not equals to UPDATED_NAME
        defaultUserInfoShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserInfosByNameIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where name in DEFAULT_NAME or UPDATED_NAME
        defaultUserInfoShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the userInfoList where name equals to UPDATED_NAME
        defaultUserInfoShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserInfosByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where name is not null
        defaultUserInfoShouldBeFound("name.specified=true");

        // Get all the userInfoList where name is null
        defaultUserInfoShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByNameContainsSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where name contains DEFAULT_NAME
        defaultUserInfoShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the userInfoList where name contains UPDATED_NAME
        defaultUserInfoShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserInfosByNameNotContainsSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where name does not contain DEFAULT_NAME
        defaultUserInfoShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the userInfoList where name does not contain UPDATED_NAME
        defaultUserInfoShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserInfosByHoursIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where hours equals to DEFAULT_HOURS
        defaultUserInfoShouldBeFound("hours.equals=" + DEFAULT_HOURS);

        // Get all the userInfoList where hours equals to UPDATED_HOURS
        defaultUserInfoShouldNotBeFound("hours.equals=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    void getAllUserInfosByHoursIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where hours not equals to DEFAULT_HOURS
        defaultUserInfoShouldNotBeFound("hours.notEquals=" + DEFAULT_HOURS);

        // Get all the userInfoList where hours not equals to UPDATED_HOURS
        defaultUserInfoShouldBeFound("hours.notEquals=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    void getAllUserInfosByHoursIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where hours in DEFAULT_HOURS or UPDATED_HOURS
        defaultUserInfoShouldBeFound("hours.in=" + DEFAULT_HOURS + "," + UPDATED_HOURS);

        // Get all the userInfoList where hours equals to UPDATED_HOURS
        defaultUserInfoShouldNotBeFound("hours.in=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    void getAllUserInfosByHoursIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where hours is not null
        defaultUserInfoShouldBeFound("hours.specified=true");

        // Get all the userInfoList where hours is null
        defaultUserInfoShouldNotBeFound("hours.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByHoursIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where hours is greater than or equal to DEFAULT_HOURS
        defaultUserInfoShouldBeFound("hours.greaterThanOrEqual=" + DEFAULT_HOURS);

        // Get all the userInfoList where hours is greater than or equal to (DEFAULT_HOURS + 1)
        defaultUserInfoShouldNotBeFound("hours.greaterThanOrEqual=" + (DEFAULT_HOURS + 1));
    }

    @Test
    @Transactional
    void getAllUserInfosByHoursIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where hours is less than or equal to DEFAULT_HOURS
        defaultUserInfoShouldBeFound("hours.lessThanOrEqual=" + DEFAULT_HOURS);

        // Get all the userInfoList where hours is less than or equal to SMALLER_HOURS
        defaultUserInfoShouldNotBeFound("hours.lessThanOrEqual=" + SMALLER_HOURS);
    }

    @Test
    @Transactional
    void getAllUserInfosByHoursIsLessThanSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where hours is less than DEFAULT_HOURS
        defaultUserInfoShouldNotBeFound("hours.lessThan=" + DEFAULT_HOURS);

        // Get all the userInfoList where hours is less than (DEFAULT_HOURS + 1)
        defaultUserInfoShouldBeFound("hours.lessThan=" + (DEFAULT_HOURS + 1));
    }

    @Test
    @Transactional
    void getAllUserInfosByHoursIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where hours is greater than DEFAULT_HOURS
        defaultUserInfoShouldNotBeFound("hours.greaterThan=" + DEFAULT_HOURS);

        // Get all the userInfoList where hours is greater than SMALLER_HOURS
        defaultUserInfoShouldBeFound("hours.greaterThan=" + SMALLER_HOURS);
    }

    @Test
    @Transactional
    void getAllUserInfosByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where status equals to DEFAULT_STATUS
        defaultUserInfoShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the userInfoList where status equals to UPDATED_STATUS
        defaultUserInfoShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllUserInfosByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where status not equals to DEFAULT_STATUS
        defaultUserInfoShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the userInfoList where status not equals to UPDATED_STATUS
        defaultUserInfoShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllUserInfosByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultUserInfoShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the userInfoList where status equals to UPDATED_STATUS
        defaultUserInfoShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllUserInfosByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where status is not null
        defaultUserInfoShouldBeFound("status.specified=true");

        // Get all the userInfoList where status is null
        defaultUserInfoShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByBirthdayIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where birthday equals to DEFAULT_BIRTHDAY
        defaultUserInfoShouldBeFound("birthday.equals=" + DEFAULT_BIRTHDAY);

        // Get all the userInfoList where birthday equals to UPDATED_BIRTHDAY
        defaultUserInfoShouldNotBeFound("birthday.equals=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllUserInfosByBirthdayIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where birthday not equals to DEFAULT_BIRTHDAY
        defaultUserInfoShouldNotBeFound("birthday.notEquals=" + DEFAULT_BIRTHDAY);

        // Get all the userInfoList where birthday not equals to UPDATED_BIRTHDAY
        defaultUserInfoShouldBeFound("birthday.notEquals=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllUserInfosByBirthdayIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where birthday in DEFAULT_BIRTHDAY or UPDATED_BIRTHDAY
        defaultUserInfoShouldBeFound("birthday.in=" + DEFAULT_BIRTHDAY + "," + UPDATED_BIRTHDAY);

        // Get all the userInfoList where birthday equals to UPDATED_BIRTHDAY
        defaultUserInfoShouldNotBeFound("birthday.in=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllUserInfosByBirthdayIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where birthday is not null
        defaultUserInfoShouldBeFound("birthday.specified=true");

        // Get all the userInfoList where birthday is null
        defaultUserInfoShouldNotBeFound("birthday.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByBirthdayIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where birthday is greater than or equal to DEFAULT_BIRTHDAY
        defaultUserInfoShouldBeFound("birthday.greaterThanOrEqual=" + DEFAULT_BIRTHDAY);

        // Get all the userInfoList where birthday is greater than or equal to UPDATED_BIRTHDAY
        defaultUserInfoShouldNotBeFound("birthday.greaterThanOrEqual=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllUserInfosByBirthdayIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where birthday is less than or equal to DEFAULT_BIRTHDAY
        defaultUserInfoShouldBeFound("birthday.lessThanOrEqual=" + DEFAULT_BIRTHDAY);

        // Get all the userInfoList where birthday is less than or equal to SMALLER_BIRTHDAY
        defaultUserInfoShouldNotBeFound("birthday.lessThanOrEqual=" + SMALLER_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllUserInfosByBirthdayIsLessThanSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where birthday is less than DEFAULT_BIRTHDAY
        defaultUserInfoShouldNotBeFound("birthday.lessThan=" + DEFAULT_BIRTHDAY);

        // Get all the userInfoList where birthday is less than UPDATED_BIRTHDAY
        defaultUserInfoShouldBeFound("birthday.lessThan=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllUserInfosByBirthdayIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where birthday is greater than DEFAULT_BIRTHDAY
        defaultUserInfoShouldNotBeFound("birthday.greaterThan=" + DEFAULT_BIRTHDAY);

        // Get all the userInfoList where birthday is greater than SMALLER_BIRTHDAY
        defaultUserInfoShouldBeFound("birthday.greaterThan=" + SMALLER_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllUserInfosByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where comment equals to DEFAULT_COMMENT
        defaultUserInfoShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the userInfoList where comment equals to UPDATED_COMMENT
        defaultUserInfoShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllUserInfosByCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where comment not equals to DEFAULT_COMMENT
        defaultUserInfoShouldNotBeFound("comment.notEquals=" + DEFAULT_COMMENT);

        // Get all the userInfoList where comment not equals to UPDATED_COMMENT
        defaultUserInfoShouldBeFound("comment.notEquals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllUserInfosByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultUserInfoShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the userInfoList where comment equals to UPDATED_COMMENT
        defaultUserInfoShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllUserInfosByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where comment is not null
        defaultUserInfoShouldBeFound("comment.specified=true");

        // Get all the userInfoList where comment is null
        defaultUserInfoShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByCommentContainsSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where comment contains DEFAULT_COMMENT
        defaultUserInfoShouldBeFound("comment.contains=" + DEFAULT_COMMENT);

        // Get all the userInfoList where comment contains UPDATED_COMMENT
        defaultUserInfoShouldNotBeFound("comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllUserInfosByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where comment does not contain DEFAULT_COMMENT
        defaultUserInfoShouldNotBeFound("comment.doesNotContain=" + DEFAULT_COMMENT);

        // Get all the userInfoList where comment does not contain UPDATED_COMMENT
        defaultUserInfoShouldBeFound("comment.doesNotContain=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllUserInfosByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where created equals to DEFAULT_CREATED
        defaultUserInfoShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the userInfoList where created equals to UPDATED_CREATED
        defaultUserInfoShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllUserInfosByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where created not equals to DEFAULT_CREATED
        defaultUserInfoShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the userInfoList where created not equals to UPDATED_CREATED
        defaultUserInfoShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllUserInfosByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultUserInfoShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the userInfoList where created equals to UPDATED_CREATED
        defaultUserInfoShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllUserInfosByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where created is not null
        defaultUserInfoShouldBeFound("created.specified=true");

        // Get all the userInfoList where created is null
        defaultUserInfoShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where created is greater than or equal to DEFAULT_CREATED
        defaultUserInfoShouldBeFound("created.greaterThanOrEqual=" + DEFAULT_CREATED);

        // Get all the userInfoList where created is greater than or equal to UPDATED_CREATED
        defaultUserInfoShouldNotBeFound("created.greaterThanOrEqual=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllUserInfosByCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where created is less than or equal to DEFAULT_CREATED
        defaultUserInfoShouldBeFound("created.lessThanOrEqual=" + DEFAULT_CREATED);

        // Get all the userInfoList where created is less than or equal to SMALLER_CREATED
        defaultUserInfoShouldNotBeFound("created.lessThanOrEqual=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllUserInfosByCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where created is less than DEFAULT_CREATED
        defaultUserInfoShouldNotBeFound("created.lessThan=" + DEFAULT_CREATED);

        // Get all the userInfoList where created is less than UPDATED_CREATED
        defaultUserInfoShouldBeFound("created.lessThan=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllUserInfosByCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where created is greater than DEFAULT_CREATED
        defaultUserInfoShouldNotBeFound("created.greaterThan=" + DEFAULT_CREATED);

        // Get all the userInfoList where created is greater than SMALLER_CREATED
        defaultUserInfoShouldBeFound("created.greaterThan=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllUserInfosByEditedIsEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where edited equals to DEFAULT_EDITED
        defaultUserInfoShouldBeFound("edited.equals=" + DEFAULT_EDITED);

        // Get all the userInfoList where edited equals to UPDATED_EDITED
        defaultUserInfoShouldNotBeFound("edited.equals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllUserInfosByEditedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where edited not equals to DEFAULT_EDITED
        defaultUserInfoShouldNotBeFound("edited.notEquals=" + DEFAULT_EDITED);

        // Get all the userInfoList where edited not equals to UPDATED_EDITED
        defaultUserInfoShouldBeFound("edited.notEquals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllUserInfosByEditedIsInShouldWork() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where edited in DEFAULT_EDITED or UPDATED_EDITED
        defaultUserInfoShouldBeFound("edited.in=" + DEFAULT_EDITED + "," + UPDATED_EDITED);

        // Get all the userInfoList where edited equals to UPDATED_EDITED
        defaultUserInfoShouldNotBeFound("edited.in=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllUserInfosByEditedIsNullOrNotNull() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where edited is not null
        defaultUserInfoShouldBeFound("edited.specified=true");

        // Get all the userInfoList where edited is null
        defaultUserInfoShouldNotBeFound("edited.specified=false");
    }

    @Test
    @Transactional
    void getAllUserInfosByEditedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where edited is greater than or equal to DEFAULT_EDITED
        defaultUserInfoShouldBeFound("edited.greaterThanOrEqual=" + DEFAULT_EDITED);

        // Get all the userInfoList where edited is greater than or equal to UPDATED_EDITED
        defaultUserInfoShouldNotBeFound("edited.greaterThanOrEqual=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllUserInfosByEditedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where edited is less than or equal to DEFAULT_EDITED
        defaultUserInfoShouldBeFound("edited.lessThanOrEqual=" + DEFAULT_EDITED);

        // Get all the userInfoList where edited is less than or equal to SMALLER_EDITED
        defaultUserInfoShouldNotBeFound("edited.lessThanOrEqual=" + SMALLER_EDITED);
    }

    @Test
    @Transactional
    void getAllUserInfosByEditedIsLessThanSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where edited is less than DEFAULT_EDITED
        defaultUserInfoShouldNotBeFound("edited.lessThan=" + DEFAULT_EDITED);

        // Get all the userInfoList where edited is less than UPDATED_EDITED
        defaultUserInfoShouldBeFound("edited.lessThan=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllUserInfosByEditedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList where edited is greater than DEFAULT_EDITED
        defaultUserInfoShouldNotBeFound("edited.greaterThan=" + DEFAULT_EDITED);

        // Get all the userInfoList where edited is greater than SMALLER_EDITED
        defaultUserInfoShouldBeFound("edited.greaterThan=" + SMALLER_EDITED);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserInfoShouldBeFound(String filter) throws Exception {
        restUserInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].gitHubId").value(hasItem(DEFAULT_GIT_HUB_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(sameInstant(DEFAULT_EDITED))));

        // Check, that the count call also returns 1
        restUserInfoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserInfoShouldNotBeFound(String filter) throws Exception {
        restUserInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserInfoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserInfo() throws Exception {
        // Get the userInfo
        restUserInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserInfo() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();

        // Update the userInfo
        UserInfo updatedUserInfo = userInfoRepository.findById(userInfo.getId()).get();
        // Disconnect from session so that the updates on updatedUserInfo are not directly saved in db
        em.detach(updatedUserInfo);
        updatedUserInfo
            .email(UPDATED_EMAIL)
            .gitHubId(UPDATED_GIT_HUB_ID)
            .name(UPDATED_NAME)
            .hours(UPDATED_HOURS)
            .status(UPDATED_STATUS)
            .birthday(UPDATED_BIRTHDAY)
            .comment(UPDATED_COMMENT)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);

        restUserInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserInfo))
            )
            .andExpect(status().isOk());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
        UserInfo testUserInfo = userInfoList.get(userInfoList.size() - 1);
        assertThat(testUserInfo.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserInfo.getGitHubId()).isEqualTo(UPDATED_GIT_HUB_ID);
        assertThat(testUserInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserInfo.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testUserInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testUserInfo.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testUserInfo.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testUserInfo.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testUserInfo.getEdited()).isEqualTo(UPDATED_EDITED);
    }

    @Test
    @Transactional
    void putNonExistingUserInfo() throws Exception {
        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();
        userInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserInfo() throws Exception {
        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();
        userInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserInfo() throws Exception {
        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();
        userInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserInfoMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserInfoWithPatch() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();

        // Update the userInfo using partial update
        UserInfo partialUpdatedUserInfo = new UserInfo();
        partialUpdatedUserInfo.setId(userInfo.getId());

        partialUpdatedUserInfo
            .name(UPDATED_NAME)
            .hours(UPDATED_HOURS)
            .birthday(UPDATED_BIRTHDAY)
            .comment(UPDATED_COMMENT)
            .created(UPDATED_CREATED);

        restUserInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserInfo))
            )
            .andExpect(status().isOk());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
        UserInfo testUserInfo = userInfoList.get(userInfoList.size() - 1);
        assertThat(testUserInfo.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserInfo.getGitHubId()).isEqualTo(DEFAULT_GIT_HUB_ID);
        assertThat(testUserInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserInfo.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testUserInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testUserInfo.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testUserInfo.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testUserInfo.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testUserInfo.getEdited()).isEqualTo(DEFAULT_EDITED);
    }

    @Test
    @Transactional
    void fullUpdateUserInfoWithPatch() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();

        // Update the userInfo using partial update
        UserInfo partialUpdatedUserInfo = new UserInfo();
        partialUpdatedUserInfo.setId(userInfo.getId());

        partialUpdatedUserInfo
            .email(UPDATED_EMAIL)
            .gitHubId(UPDATED_GIT_HUB_ID)
            .name(UPDATED_NAME)
            .hours(UPDATED_HOURS)
            .status(UPDATED_STATUS)
            .birthday(UPDATED_BIRTHDAY)
            .comment(UPDATED_COMMENT)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);

        restUserInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserInfo))
            )
            .andExpect(status().isOk());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
        UserInfo testUserInfo = userInfoList.get(userInfoList.size() - 1);
        assertThat(testUserInfo.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserInfo.getGitHubId()).isEqualTo(UPDATED_GIT_HUB_ID);
        assertThat(testUserInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserInfo.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testUserInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testUserInfo.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testUserInfo.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testUserInfo.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testUserInfo.getEdited()).isEqualTo(UPDATED_EDITED);
    }

    @Test
    @Transactional
    void patchNonExistingUserInfo() throws Exception {
        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();
        userInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserInfo() throws Exception {
        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();
        userInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserInfo() throws Exception {
        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();
        userInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserInfoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserInfo() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        int databaseSizeBeforeDelete = userInfoRepository.findAll().size();

        // Delete the userInfo
        restUserInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, userInfo.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
