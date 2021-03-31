package ru.bjcreslin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bjcreslin.web.rest.TestUtil.sameInstant;

import java.time.Instant;
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
import ru.bjcreslin.domain.Project;
import ru.bjcreslin.domain.Task;
import ru.bjcreslin.domain.enumeration.ProjectStatus;
import ru.bjcreslin.repository.ProjectRepository;
import ru.bjcreslin.service.criteria.ProjectCriteria;

/**
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectResourceIT {

    private static final String DEFAULT_PROJECT_URL = "AAAAAAAAAA";
    private static final String UPDATED_PROJECT_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PROJECT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PROJECT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final ProjectStatus DEFAULT_STATUS = ProjectStatus.NEW;
    private static final ProjectStatus UPDATED_STATUS = ProjectStatus.CLOSED;

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_EDITED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_EDITED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_EDITED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMockMvc;

    private Project project;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createEntity(EntityManager em) {
        Project project = new Project()
            .projectUrl(DEFAULT_PROJECT_URL)
            .description(DEFAULT_DESCRIPTION)
            .projectName(DEFAULT_PROJECT_NAME)
            .comment(DEFAULT_COMMENT)
            .status(DEFAULT_STATUS)
            .created(DEFAULT_CREATED)
            .edited(DEFAULT_EDITED);
        return project;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createUpdatedEntity(EntityManager em) {
        Project project = new Project()
            .projectUrl(UPDATED_PROJECT_URL)
            .description(UPDATED_DESCRIPTION)
            .projectName(UPDATED_PROJECT_NAME)
            .comment(UPDATED_COMMENT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);
        return project;
    }

    @BeforeEach
    public void initTest() {
        project = createEntity(em);
    }

    @Test
    @Transactional
    void createProject() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();
        // Create the Project
        restProjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isCreated());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getProjectUrl()).isEqualTo(DEFAULT_PROJECT_URL);
        assertThat(testProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProject.getProjectName()).isEqualTo(DEFAULT_PROJECT_NAME);
        assertThat(testProject.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testProject.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProject.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testProject.getEdited()).isEqualTo(DEFAULT_EDITED);
    }

    @Test
    @Transactional
    void createProjectWithExistingId() throws Exception {
        // Create the Project with an existing ID
        project.setId(1L);

        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkProjectNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set the field null
        project.setProjectName(null);

        // Create the Project, which fails.

        restProjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjects() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectUrl").value(hasItem(DEFAULT_PROJECT_URL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].projectName").value(hasItem(DEFAULT_PROJECT_NAME)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(sameInstant(DEFAULT_EDITED))));
    }

    @Test
    @Transactional
    void getProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get the project
        restProjectMockMvc
            .perform(get(ENTITY_API_URL_ID, project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.projectUrl").value(DEFAULT_PROJECT_URL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.projectName").value(DEFAULT_PROJECT_NAME))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.edited").value(sameInstant(DEFAULT_EDITED)));
    }

    @Test
    @Transactional
    void getProjectsByIdFiltering() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        Long id = project.getId();

        defaultProjectShouldBeFound("id.equals=" + id);
        defaultProjectShouldNotBeFound("id.notEquals=" + id);

        defaultProjectShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectUrl equals to DEFAULT_PROJECT_URL
        defaultProjectShouldBeFound("projectUrl.equals=" + DEFAULT_PROJECT_URL);

        // Get all the projectList where projectUrl equals to UPDATED_PROJECT_URL
        defaultProjectShouldNotBeFound("projectUrl.equals=" + UPDATED_PROJECT_URL);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectUrl not equals to DEFAULT_PROJECT_URL
        defaultProjectShouldNotBeFound("projectUrl.notEquals=" + DEFAULT_PROJECT_URL);

        // Get all the projectList where projectUrl not equals to UPDATED_PROJECT_URL
        defaultProjectShouldBeFound("projectUrl.notEquals=" + UPDATED_PROJECT_URL);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectUrlIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectUrl in DEFAULT_PROJECT_URL or UPDATED_PROJECT_URL
        defaultProjectShouldBeFound("projectUrl.in=" + DEFAULT_PROJECT_URL + "," + UPDATED_PROJECT_URL);

        // Get all the projectList where projectUrl equals to UPDATED_PROJECT_URL
        defaultProjectShouldNotBeFound("projectUrl.in=" + UPDATED_PROJECT_URL);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectUrl is not null
        defaultProjectShouldBeFound("projectUrl.specified=true");

        // Get all the projectList where projectUrl is null
        defaultProjectShouldNotBeFound("projectUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByProjectUrlContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectUrl contains DEFAULT_PROJECT_URL
        defaultProjectShouldBeFound("projectUrl.contains=" + DEFAULT_PROJECT_URL);

        // Get all the projectList where projectUrl contains UPDATED_PROJECT_URL
        defaultProjectShouldNotBeFound("projectUrl.contains=" + UPDATED_PROJECT_URL);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectUrlNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectUrl does not contain DEFAULT_PROJECT_URL
        defaultProjectShouldNotBeFound("projectUrl.doesNotContain=" + DEFAULT_PROJECT_URL);

        // Get all the projectList where projectUrl does not contain UPDATED_PROJECT_URL
        defaultProjectShouldBeFound("projectUrl.doesNotContain=" + UPDATED_PROJECT_URL);
    }

    @Test
    @Transactional
    void getAllProjectsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description equals to DEFAULT_DESCRIPTION
        defaultProjectShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description equals to UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProjectsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description not equals to DEFAULT_DESCRIPTION
        defaultProjectShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description not equals to UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProjectsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the projectList where description equals to UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProjectsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description is not null
        defaultProjectShouldBeFound("description.specified=true");

        // Get all the projectList where description is null
        defaultProjectShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description contains DEFAULT_DESCRIPTION
        defaultProjectShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description contains UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProjectsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description does not contain DEFAULT_DESCRIPTION
        defaultProjectShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description does not contain UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectName equals to DEFAULT_PROJECT_NAME
        defaultProjectShouldBeFound("projectName.equals=" + DEFAULT_PROJECT_NAME);

        // Get all the projectList where projectName equals to UPDATED_PROJECT_NAME
        defaultProjectShouldNotBeFound("projectName.equals=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectName not equals to DEFAULT_PROJECT_NAME
        defaultProjectShouldNotBeFound("projectName.notEquals=" + DEFAULT_PROJECT_NAME);

        // Get all the projectList where projectName not equals to UPDATED_PROJECT_NAME
        defaultProjectShouldBeFound("projectName.notEquals=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectName in DEFAULT_PROJECT_NAME or UPDATED_PROJECT_NAME
        defaultProjectShouldBeFound("projectName.in=" + DEFAULT_PROJECT_NAME + "," + UPDATED_PROJECT_NAME);

        // Get all the projectList where projectName equals to UPDATED_PROJECT_NAME
        defaultProjectShouldNotBeFound("projectName.in=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectName is not null
        defaultProjectShouldBeFound("projectName.specified=true");

        // Get all the projectList where projectName is null
        defaultProjectShouldNotBeFound("projectName.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectName contains DEFAULT_PROJECT_NAME
        defaultProjectShouldBeFound("projectName.contains=" + DEFAULT_PROJECT_NAME);

        // Get all the projectList where projectName contains UPDATED_PROJECT_NAME
        defaultProjectShouldNotBeFound("projectName.contains=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where projectName does not contain DEFAULT_PROJECT_NAME
        defaultProjectShouldNotBeFound("projectName.doesNotContain=" + DEFAULT_PROJECT_NAME);

        // Get all the projectList where projectName does not contain UPDATED_PROJECT_NAME
        defaultProjectShouldBeFound("projectName.doesNotContain=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where comment equals to DEFAULT_COMMENT
        defaultProjectShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the projectList where comment equals to UPDATED_COMMENT
        defaultProjectShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProjectsByCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where comment not equals to DEFAULT_COMMENT
        defaultProjectShouldNotBeFound("comment.notEquals=" + DEFAULT_COMMENT);

        // Get all the projectList where comment not equals to UPDATED_COMMENT
        defaultProjectShouldBeFound("comment.notEquals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProjectsByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultProjectShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the projectList where comment equals to UPDATED_COMMENT
        defaultProjectShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProjectsByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where comment is not null
        defaultProjectShouldBeFound("comment.specified=true");

        // Get all the projectList where comment is null
        defaultProjectShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByCommentContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where comment contains DEFAULT_COMMENT
        defaultProjectShouldBeFound("comment.contains=" + DEFAULT_COMMENT);

        // Get all the projectList where comment contains UPDATED_COMMENT
        defaultProjectShouldNotBeFound("comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProjectsByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where comment does not contain DEFAULT_COMMENT
        defaultProjectShouldNotBeFound("comment.doesNotContain=" + DEFAULT_COMMENT);

        // Get all the projectList where comment does not contain UPDATED_COMMENT
        defaultProjectShouldBeFound("comment.doesNotContain=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProjectsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where status equals to DEFAULT_STATUS
        defaultProjectShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the projectList where status equals to UPDATED_STATUS
        defaultProjectShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProjectsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where status not equals to DEFAULT_STATUS
        defaultProjectShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the projectList where status not equals to UPDATED_STATUS
        defaultProjectShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProjectsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultProjectShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the projectList where status equals to UPDATED_STATUS
        defaultProjectShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProjectsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where status is not null
        defaultProjectShouldBeFound("status.specified=true");

        // Get all the projectList where status is null
        defaultProjectShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where created equals to DEFAULT_CREATED
        defaultProjectShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the projectList where created equals to UPDATED_CREATED
        defaultProjectShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where created not equals to DEFAULT_CREATED
        defaultProjectShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the projectList where created not equals to UPDATED_CREATED
        defaultProjectShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultProjectShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the projectList where created equals to UPDATED_CREATED
        defaultProjectShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where created is not null
        defaultProjectShouldBeFound("created.specified=true");

        // Get all the projectList where created is null
        defaultProjectShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where created is greater than or equal to DEFAULT_CREATED
        defaultProjectShouldBeFound("created.greaterThanOrEqual=" + DEFAULT_CREATED);

        // Get all the projectList where created is greater than or equal to UPDATED_CREATED
        defaultProjectShouldNotBeFound("created.greaterThanOrEqual=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where created is less than or equal to DEFAULT_CREATED
        defaultProjectShouldBeFound("created.lessThanOrEqual=" + DEFAULT_CREATED);

        // Get all the projectList where created is less than or equal to SMALLER_CREATED
        defaultProjectShouldNotBeFound("created.lessThanOrEqual=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where created is less than DEFAULT_CREATED
        defaultProjectShouldNotBeFound("created.lessThan=" + DEFAULT_CREATED);

        // Get all the projectList where created is less than UPDATED_CREATED
        defaultProjectShouldBeFound("created.lessThan=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where created is greater than DEFAULT_CREATED
        defaultProjectShouldNotBeFound("created.greaterThan=" + DEFAULT_CREATED);

        // Get all the projectList where created is greater than SMALLER_CREATED
        defaultProjectShouldBeFound("created.greaterThan=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllProjectsByEditedIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where edited equals to DEFAULT_EDITED
        defaultProjectShouldBeFound("edited.equals=" + DEFAULT_EDITED);

        // Get all the projectList where edited equals to UPDATED_EDITED
        defaultProjectShouldNotBeFound("edited.equals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllProjectsByEditedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where edited not equals to DEFAULT_EDITED
        defaultProjectShouldNotBeFound("edited.notEquals=" + DEFAULT_EDITED);

        // Get all the projectList where edited not equals to UPDATED_EDITED
        defaultProjectShouldBeFound("edited.notEquals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllProjectsByEditedIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where edited in DEFAULT_EDITED or UPDATED_EDITED
        defaultProjectShouldBeFound("edited.in=" + DEFAULT_EDITED + "," + UPDATED_EDITED);

        // Get all the projectList where edited equals to UPDATED_EDITED
        defaultProjectShouldNotBeFound("edited.in=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllProjectsByEditedIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where edited is not null
        defaultProjectShouldBeFound("edited.specified=true");

        // Get all the projectList where edited is null
        defaultProjectShouldNotBeFound("edited.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByEditedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where edited is greater than or equal to DEFAULT_EDITED
        defaultProjectShouldBeFound("edited.greaterThanOrEqual=" + DEFAULT_EDITED);

        // Get all the projectList where edited is greater than or equal to UPDATED_EDITED
        defaultProjectShouldNotBeFound("edited.greaterThanOrEqual=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllProjectsByEditedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where edited is less than or equal to DEFAULT_EDITED
        defaultProjectShouldBeFound("edited.lessThanOrEqual=" + DEFAULT_EDITED);

        // Get all the projectList where edited is less than or equal to SMALLER_EDITED
        defaultProjectShouldNotBeFound("edited.lessThanOrEqual=" + SMALLER_EDITED);
    }

    @Test
    @Transactional
    void getAllProjectsByEditedIsLessThanSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where edited is less than DEFAULT_EDITED
        defaultProjectShouldNotBeFound("edited.lessThan=" + DEFAULT_EDITED);

        // Get all the projectList where edited is less than UPDATED_EDITED
        defaultProjectShouldBeFound("edited.lessThan=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllProjectsByEditedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where edited is greater than DEFAULT_EDITED
        defaultProjectShouldNotBeFound("edited.greaterThan=" + DEFAULT_EDITED);

        // Get all the projectList where edited is greater than SMALLER_EDITED
        defaultProjectShouldBeFound("edited.greaterThan=" + SMALLER_EDITED);
    }

    @Test
    @Transactional
    void getAllProjectsByTaskIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        Task task = TaskResourceIT.createEntity(em);
        em.persist(task);
        em.flush();
        project.addTask(task);
        projectRepository.saveAndFlush(project);
        Long taskId = task.getId();

        // Get all the projectList where task equals to taskId
        defaultProjectShouldBeFound("taskId.equals=" + taskId);

        // Get all the projectList where task equals to (taskId + 1)
        defaultProjectShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectShouldBeFound(String filter) throws Exception {
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectUrl").value(hasItem(DEFAULT_PROJECT_URL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].projectName").value(hasItem(DEFAULT_PROJECT_NAME)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(sameInstant(DEFAULT_EDITED))));

        // Check, that the count call also returns 1
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectShouldNotBeFound(String filter) throws Exception {
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProject() throws Exception {
        // Get the project
        restProjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).get();
        // Disconnect from session so that the updates on updatedProject are not directly saved in db
        em.detach(updatedProject);
        updatedProject
            .projectUrl(UPDATED_PROJECT_URL)
            .description(UPDATED_DESCRIPTION)
            .projectName(UPDATED_PROJECT_NAME)
            .comment(UPDATED_COMMENT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);

        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProject.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getProjectUrl()).isEqualTo(UPDATED_PROJECT_URL);
        assertThat(testProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProject.getProjectName()).isEqualTo(UPDATED_PROJECT_NAME);
        assertThat(testProject.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testProject.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProject.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testProject.getEdited()).isEqualTo(UPDATED_EDITED);
    }

    @Test
    @Transactional
    void putNonExistingProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        project.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, project.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        project.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        project.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject.projectUrl(UPDATED_PROJECT_URL).comment(UPDATED_COMMENT).status(UPDATED_STATUS);

        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getProjectUrl()).isEqualTo(UPDATED_PROJECT_URL);
        assertThat(testProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProject.getProjectName()).isEqualTo(DEFAULT_PROJECT_NAME);
        assertThat(testProject.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testProject.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProject.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testProject.getEdited()).isEqualTo(DEFAULT_EDITED);
    }

    @Test
    @Transactional
    void fullUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .projectUrl(UPDATED_PROJECT_URL)
            .description(UPDATED_DESCRIPTION)
            .projectName(UPDATED_PROJECT_NAME)
            .comment(UPDATED_COMMENT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);

        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getProjectUrl()).isEqualTo(UPDATED_PROJECT_URL);
        assertThat(testProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProject.getProjectName()).isEqualTo(UPDATED_PROJECT_NAME);
        assertThat(testProject.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testProject.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProject.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testProject.getEdited()).isEqualTo(UPDATED_EDITED);
    }

    @Test
    @Transactional
    void patchNonExistingProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        project.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, project.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        project.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        project.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        int databaseSizeBeforeDelete = projectRepository.findAll().size();

        // Delete the project
        restProjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, project.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
