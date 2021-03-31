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
import ru.bjcreslin.domain.Comment;
import ru.bjcreslin.domain.Project;
import ru.bjcreslin.domain.Task;
import ru.bjcreslin.domain.enumeration.TaskAndProjectStatus;
import ru.bjcreslin.repository.TaskRepository;
import ru.bjcreslin.service.criteria.TaskCriteria;

/**
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_IMPLEMENTER = "AAAAAAAAAA";
    private static final String UPDATED_IMPLEMENTER = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final TaskAndProjectStatus DEFAULT_STATUS = TaskAndProjectStatus.NEW;
    private static final TaskAndProjectStatus UPDATED_STATUS = TaskAndProjectStatus.CLOSED;

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_EDITED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_EDITED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_EDITED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskMockMvc;

    private Task task;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(EntityManager em) {
        Task task = new Task()
            .author(DEFAULT_AUTHOR)
            .implementer(DEFAULT_IMPLEMENTER)
            .name(DEFAULT_NAME)
            .text(DEFAULT_TEXT)
            .comment(DEFAULT_COMMENT)
            .status(DEFAULT_STATUS)
            .created(DEFAULT_CREATED)
            .edited(DEFAULT_EDITED);
        return task;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity(EntityManager em) {
        Task task = new Task()
            .author(UPDATED_AUTHOR)
            .implementer(UPDATED_IMPLEMENTER)
            .name(UPDATED_NAME)
            .text(UPDATED_TEXT)
            .comment(UPDATED_COMMENT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);
        return task;
    }

    @BeforeEach
    public void initTest() {
        task = createEntity(em);
    }

    @Test
    @Transactional
    void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();
        // Create the Task
        restTaskMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testTask.getImplementer()).isEqualTo(DEFAULT_IMPLEMENTER);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testTask.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTask.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testTask.getEdited()).isEqualTo(DEFAULT_EDITED);
    }

    @Test
    @Transactional
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId(1L);

        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setName(null);

        // Create the Task, which fails.

        restTaskMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].implementer").value(hasItem(DEFAULT_IMPLEMENTER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(sameInstant(DEFAULT_EDITED))));
    }

    @Test
    @Transactional
    void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.implementer").value(DEFAULT_IMPLEMENTER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.edited").value(sameInstant(DEFAULT_EDITED)));
    }

    @Test
    @Transactional
    void getTasksByIdFiltering() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        Long id = task.getId();

        defaultTaskShouldBeFound("id.equals=" + id);
        defaultTaskShouldNotBeFound("id.notEquals=" + id);

        defaultTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTasksByAuthorIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where author equals to DEFAULT_AUTHOR
        defaultTaskShouldBeFound("author.equals=" + DEFAULT_AUTHOR);

        // Get all the taskList where author equals to UPDATED_AUTHOR
        defaultTaskShouldNotBeFound("author.equals=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllTasksByAuthorIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where author not equals to DEFAULT_AUTHOR
        defaultTaskShouldNotBeFound("author.notEquals=" + DEFAULT_AUTHOR);

        // Get all the taskList where author not equals to UPDATED_AUTHOR
        defaultTaskShouldBeFound("author.notEquals=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllTasksByAuthorIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where author in DEFAULT_AUTHOR or UPDATED_AUTHOR
        defaultTaskShouldBeFound("author.in=" + DEFAULT_AUTHOR + "," + UPDATED_AUTHOR);

        // Get all the taskList where author equals to UPDATED_AUTHOR
        defaultTaskShouldNotBeFound("author.in=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllTasksByAuthorIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where author is not null
        defaultTaskShouldBeFound("author.specified=true");

        // Get all the taskList where author is null
        defaultTaskShouldNotBeFound("author.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByAuthorContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where author contains DEFAULT_AUTHOR
        defaultTaskShouldBeFound("author.contains=" + DEFAULT_AUTHOR);

        // Get all the taskList where author contains UPDATED_AUTHOR
        defaultTaskShouldNotBeFound("author.contains=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllTasksByAuthorNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where author does not contain DEFAULT_AUTHOR
        defaultTaskShouldNotBeFound("author.doesNotContain=" + DEFAULT_AUTHOR);

        // Get all the taskList where author does not contain UPDATED_AUTHOR
        defaultTaskShouldBeFound("author.doesNotContain=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllTasksByImplementerIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where implementer equals to DEFAULT_IMPLEMENTER
        defaultTaskShouldBeFound("implementer.equals=" + DEFAULT_IMPLEMENTER);

        // Get all the taskList where implementer equals to UPDATED_IMPLEMENTER
        defaultTaskShouldNotBeFound("implementer.equals=" + UPDATED_IMPLEMENTER);
    }

    @Test
    @Transactional
    void getAllTasksByImplementerIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where implementer not equals to DEFAULT_IMPLEMENTER
        defaultTaskShouldNotBeFound("implementer.notEquals=" + DEFAULT_IMPLEMENTER);

        // Get all the taskList where implementer not equals to UPDATED_IMPLEMENTER
        defaultTaskShouldBeFound("implementer.notEquals=" + UPDATED_IMPLEMENTER);
    }

    @Test
    @Transactional
    void getAllTasksByImplementerIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where implementer in DEFAULT_IMPLEMENTER or UPDATED_IMPLEMENTER
        defaultTaskShouldBeFound("implementer.in=" + DEFAULT_IMPLEMENTER + "," + UPDATED_IMPLEMENTER);

        // Get all the taskList where implementer equals to UPDATED_IMPLEMENTER
        defaultTaskShouldNotBeFound("implementer.in=" + UPDATED_IMPLEMENTER);
    }

    @Test
    @Transactional
    void getAllTasksByImplementerIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where implementer is not null
        defaultTaskShouldBeFound("implementer.specified=true");

        // Get all the taskList where implementer is null
        defaultTaskShouldNotBeFound("implementer.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByImplementerContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where implementer contains DEFAULT_IMPLEMENTER
        defaultTaskShouldBeFound("implementer.contains=" + DEFAULT_IMPLEMENTER);

        // Get all the taskList where implementer contains UPDATED_IMPLEMENTER
        defaultTaskShouldNotBeFound("implementer.contains=" + UPDATED_IMPLEMENTER);
    }

    @Test
    @Transactional
    void getAllTasksByImplementerNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where implementer does not contain DEFAULT_IMPLEMENTER
        defaultTaskShouldNotBeFound("implementer.doesNotContain=" + DEFAULT_IMPLEMENTER);

        // Get all the taskList where implementer does not contain UPDATED_IMPLEMENTER
        defaultTaskShouldBeFound("implementer.doesNotContain=" + UPDATED_IMPLEMENTER);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name equals to DEFAULT_NAME
        defaultTaskShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name not equals to DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the taskList where name not equals to UPDATED_NAME
        defaultTaskShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTaskShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name is not null
        defaultTaskShouldBeFound("name.specified=true");

        // Get all the taskList where name is null
        defaultTaskShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByNameContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name contains DEFAULT_NAME
        defaultTaskShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the taskList where name contains UPDATED_NAME
        defaultTaskShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name does not contain DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the taskList where name does not contain UPDATED_NAME
        defaultTaskShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByTextIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where text equals to DEFAULT_TEXT
        defaultTaskShouldBeFound("text.equals=" + DEFAULT_TEXT);

        // Get all the taskList where text equals to UPDATED_TEXT
        defaultTaskShouldNotBeFound("text.equals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllTasksByTextIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where text not equals to DEFAULT_TEXT
        defaultTaskShouldNotBeFound("text.notEquals=" + DEFAULT_TEXT);

        // Get all the taskList where text not equals to UPDATED_TEXT
        defaultTaskShouldBeFound("text.notEquals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllTasksByTextIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where text in DEFAULT_TEXT or UPDATED_TEXT
        defaultTaskShouldBeFound("text.in=" + DEFAULT_TEXT + "," + UPDATED_TEXT);

        // Get all the taskList where text equals to UPDATED_TEXT
        defaultTaskShouldNotBeFound("text.in=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllTasksByTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where text is not null
        defaultTaskShouldBeFound("text.specified=true");

        // Get all the taskList where text is null
        defaultTaskShouldNotBeFound("text.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByTextContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where text contains DEFAULT_TEXT
        defaultTaskShouldBeFound("text.contains=" + DEFAULT_TEXT);

        // Get all the taskList where text contains UPDATED_TEXT
        defaultTaskShouldNotBeFound("text.contains=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllTasksByTextNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where text does not contain DEFAULT_TEXT
        defaultTaskShouldNotBeFound("text.doesNotContain=" + DEFAULT_TEXT);

        // Get all the taskList where text does not contain UPDATED_TEXT
        defaultTaskShouldBeFound("text.doesNotContain=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllTasksByCommentIsEqualToSomethingTest() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where comment equals to DEFAULT_COMMENT
        defaultTaskShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the taskList where comment equals to UPDATED_COMMENT
        defaultTaskShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllTasksByCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where comment not equals to DEFAULT_COMMENT
        defaultTaskShouldNotBeFound("comment.notEquals=" + DEFAULT_COMMENT);

        // Get all the taskList where comment not equals to UPDATED_COMMENT
        defaultTaskShouldBeFound("comment.notEquals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllTasksByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultTaskShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the taskList where comment equals to UPDATED_COMMENT
        defaultTaskShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllTasksByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where comment is not null
        defaultTaskShouldBeFound("comment.specified=true");

        // Get all the taskList where comment is null
        defaultTaskShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByCommentContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where comment contains DEFAULT_COMMENT
        defaultTaskShouldBeFound("comment.contains=" + DEFAULT_COMMENT);

        // Get all the taskList where comment contains UPDATED_COMMENT
        defaultTaskShouldNotBeFound("comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllTasksByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where comment does not contain DEFAULT_COMMENT
        defaultTaskShouldNotBeFound("comment.doesNotContain=" + DEFAULT_COMMENT);

        // Get all the taskList where comment does not contain UPDATED_COMMENT
        defaultTaskShouldBeFound("comment.doesNotContain=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllTasksByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where status equals to DEFAULT_STATUS
        defaultTaskShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the taskList where status equals to UPDATED_STATUS
        defaultTaskShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTasksByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where status not equals to DEFAULT_STATUS
        defaultTaskShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the taskList where status not equals to UPDATED_STATUS
        defaultTaskShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTasksByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultTaskShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the taskList where status equals to UPDATED_STATUS
        defaultTaskShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTasksByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where status is not null
        defaultTaskShouldBeFound("status.specified=true");

        // Get all the taskList where status is null
        defaultTaskShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where created equals to DEFAULT_CREATED
        defaultTaskShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the taskList where created equals to UPDATED_CREATED
        defaultTaskShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where created not equals to DEFAULT_CREATED
        defaultTaskShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the taskList where created not equals to UPDATED_CREATED
        defaultTaskShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultTaskShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the taskList where created equals to UPDATED_CREATED
        defaultTaskShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where created is not null
        defaultTaskShouldBeFound("created.specified=true");

        // Get all the taskList where created is null
        defaultTaskShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where created is greater than or equal to DEFAULT_CREATED
        defaultTaskShouldBeFound("created.greaterThanOrEqual=" + DEFAULT_CREATED);

        // Get all the taskList where created is greater than or equal to UPDATED_CREATED
        defaultTaskShouldNotBeFound("created.greaterThanOrEqual=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where created is less than or equal to DEFAULT_CREATED
        defaultTaskShouldBeFound("created.lessThanOrEqual=" + DEFAULT_CREATED);

        // Get all the taskList where created is less than or equal to SMALLER_CREATED
        defaultTaskShouldNotBeFound("created.lessThanOrEqual=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where created is less than DEFAULT_CREATED
        defaultTaskShouldNotBeFound("created.lessThan=" + DEFAULT_CREATED);

        // Get all the taskList where created is less than UPDATED_CREATED
        defaultTaskShouldBeFound("created.lessThan=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where created is greater than DEFAULT_CREATED
        defaultTaskShouldNotBeFound("created.greaterThan=" + DEFAULT_CREATED);

        // Get all the taskList where created is greater than SMALLER_CREATED
        defaultTaskShouldBeFound("created.greaterThan=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllTasksByEditedIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where edited equals to DEFAULT_EDITED
        defaultTaskShouldBeFound("edited.equals=" + DEFAULT_EDITED);

        // Get all the taskList where edited equals to UPDATED_EDITED
        defaultTaskShouldNotBeFound("edited.equals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllTasksByEditedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where edited not equals to DEFAULT_EDITED
        defaultTaskShouldNotBeFound("edited.notEquals=" + DEFAULT_EDITED);

        // Get all the taskList where edited not equals to UPDATED_EDITED
        defaultTaskShouldBeFound("edited.notEquals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllTasksByEditedIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where edited in DEFAULT_EDITED or UPDATED_EDITED
        defaultTaskShouldBeFound("edited.in=" + DEFAULT_EDITED + "," + UPDATED_EDITED);

        // Get all the taskList where edited equals to UPDATED_EDITED
        defaultTaskShouldNotBeFound("edited.in=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllTasksByEditedIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where edited is not null
        defaultTaskShouldBeFound("edited.specified=true");

        // Get all the taskList where edited is null
        defaultTaskShouldNotBeFound("edited.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByEditedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where edited is greater than or equal to DEFAULT_EDITED
        defaultTaskShouldBeFound("edited.greaterThanOrEqual=" + DEFAULT_EDITED);

        // Get all the taskList where edited is greater than or equal to UPDATED_EDITED
        defaultTaskShouldNotBeFound("edited.greaterThanOrEqual=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllTasksByEditedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where edited is less than or equal to DEFAULT_EDITED
        defaultTaskShouldBeFound("edited.lessThanOrEqual=" + DEFAULT_EDITED);

        // Get all the taskList where edited is less than or equal to SMALLER_EDITED
        defaultTaskShouldNotBeFound("edited.lessThanOrEqual=" + SMALLER_EDITED);
    }

    @Test
    @Transactional
    void getAllTasksByEditedIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where edited is less than DEFAULT_EDITED
        defaultTaskShouldNotBeFound("edited.lessThan=" + DEFAULT_EDITED);

        // Get all the taskList where edited is less than UPDATED_EDITED
        defaultTaskShouldBeFound("edited.lessThan=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllTasksByEditedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where edited is greater than DEFAULT_EDITED
        defaultTaskShouldNotBeFound("edited.greaterThan=" + DEFAULT_EDITED);

        // Get all the taskList where edited is greater than SMALLER_EDITED
        defaultTaskShouldBeFound("edited.greaterThan=" + SMALLER_EDITED);
    }

    @Test
    @Transactional
    void getAllTasksByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        Comment comment = CommentResourceIT.createEntity(em);
        em.persist(comment);
        em.flush();
        task.addComment(comment);
        taskRepository.saveAndFlush(task);
        Long commentId = comment.getId();

        // Get all the taskList where comment equals to commentId
        defaultTaskShouldBeFound("commentId.equals=" + commentId);

        // Get all the taskList where comment equals to (commentId + 1)
        defaultTaskShouldNotBeFound("commentId.equals=" + (commentId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByProjectIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        Project project = ProjectResourceIT.createEntity(em);
        em.persist(project);
        em.flush();
        task.setProject(project);
        taskRepository.saveAndFlush(task);
        Long projectId = project.getId();

        // Get all the taskList where project equals to projectId
        defaultTaskShouldBeFound("projectId.equals=" + projectId);

        // Get all the taskList where project equals to (projectId + 1)
        defaultTaskShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskShouldBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].implementer").value(hasItem(DEFAULT_IMPLEMENTER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(sameInstant(DEFAULT_EDITED))));

        // Check, that the count call also returns 1
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskShouldNotBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).get();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask
            .author(UPDATED_AUTHOR)
            .implementer(UPDATED_IMPLEMENTER)
            .name(UPDATED_NAME)
            .text(UPDATED_TEXT)
            .comment(UPDATED_COMMENT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);

        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTask.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testTask.getImplementer()).isEqualTo(UPDATED_IMPLEMENTER);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testTask.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTask.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testTask.getEdited()).isEqualTo(UPDATED_EDITED);
    }

    @Test
    @Transactional
    void putNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, task.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask.author(UPDATED_AUTHOR).implementer(UPDATED_IMPLEMENTER).text(UPDATED_TEXT).comment(UPDATED_COMMENT);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testTask.getImplementer()).isEqualTo(UPDATED_IMPLEMENTER);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testTask.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTask.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testTask.getEdited()).isEqualTo(DEFAULT_EDITED);
    }

    @Test
    @Transactional
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .author(UPDATED_AUTHOR)
            .implementer(UPDATED_IMPLEMENTER)
            .name(UPDATED_NAME)
            .text(UPDATED_TEXT)
            .comment(UPDATED_COMMENT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testTask.getImplementer()).isEqualTo(UPDATED_IMPLEMENTER);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testTask.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTask.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testTask.getEdited()).isEqualTo(UPDATED_EDITED);
    }

    @Test
    @Transactional
    void patchNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, task.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Delete the task
        restTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, task.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
