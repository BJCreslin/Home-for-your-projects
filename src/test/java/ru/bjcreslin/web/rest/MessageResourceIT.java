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
import ru.bjcreslin.domain.Message;
import ru.bjcreslin.domain.enumeration.MessageStatus;
import ru.bjcreslin.repository.MessageRepository;
import ru.bjcreslin.service.criteria.MessageCriteria;

/**
 * Integration tests for the {@link MessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MessageResourceIT {

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_RECEPIENT = "AAAAAAAAAA";
    private static final String UPDATED_RECEPIENT = "BBBBBBBBBB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final MessageStatus DEFAULT_STATUS = MessageStatus.NEW;
    private static final MessageStatus UPDATED_STATUS = MessageStatus.READED;

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_EDITED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_EDITED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_EDITED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMessageMockMvc;

    private Message message;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createEntity(EntityManager em) {
        Message message = new Message()
            .author(DEFAULT_AUTHOR)
            .recepient(DEFAULT_RECEPIENT)
            .text(DEFAULT_TEXT)
            .status(DEFAULT_STATUS)
            .created(DEFAULT_CREATED)
            .edited(DEFAULT_EDITED);
        return message;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createUpdatedEntity(EntityManager em) {
        Message message = new Message()
            .author(UPDATED_AUTHOR)
            .recepient(UPDATED_RECEPIENT)
            .text(UPDATED_TEXT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);
        return message;
    }

    @BeforeEach
    public void initTest() {
        message = createEntity(em);
    }

    @Test
    @Transactional
    void createMessage() throws Exception {
        int databaseSizeBeforeCreate = messageRepository.findAll().size();
        // Create the Message
        restMessageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(message))
            )
            .andExpect(status().isCreated());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate + 1);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testMessage.getRecepient()).isEqualTo(DEFAULT_RECEPIENT);
        assertThat(testMessage.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testMessage.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testMessage.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testMessage.getEdited()).isEqualTo(DEFAULT_EDITED);
    }

    @Test
    @Transactional
    void createMessageWithExistingId() throws Exception {
        // Create the Message with an existing ID
        message.setId(1L);

        int databaseSizeBeforeCreate = messageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMessageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(message))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMessages() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].recepient").value(hasItem(DEFAULT_RECEPIENT)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(sameInstant(DEFAULT_EDITED))));
    }

    @Test
    @Transactional
    void getMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get the message
        restMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(message.getId().intValue()))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.recepient").value(DEFAULT_RECEPIENT))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.edited").value(sameInstant(DEFAULT_EDITED)));
    }

    @Test
    @Transactional
    void getMessagesByIdFiltering() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        Long id = message.getId();

        defaultMessageShouldBeFound("id.equals=" + id);
        defaultMessageShouldNotBeFound("id.notEquals=" + id);

        defaultMessageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMessageShouldNotBeFound("id.greaterThan=" + id);

        defaultMessageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMessageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMessagesByAuthorIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where author equals to DEFAULT_AUTHOR
        defaultMessageShouldBeFound("author.equals=" + DEFAULT_AUTHOR);

        // Get all the messageList where author equals to UPDATED_AUTHOR
        defaultMessageShouldNotBeFound("author.equals=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllMessagesByAuthorIsNotEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where author not equals to DEFAULT_AUTHOR
        defaultMessageShouldNotBeFound("author.notEquals=" + DEFAULT_AUTHOR);

        // Get all the messageList where author not equals to UPDATED_AUTHOR
        defaultMessageShouldBeFound("author.notEquals=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllMessagesByAuthorIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where author in DEFAULT_AUTHOR or UPDATED_AUTHOR
        defaultMessageShouldBeFound("author.in=" + DEFAULT_AUTHOR + "," + UPDATED_AUTHOR);

        // Get all the messageList where author equals to UPDATED_AUTHOR
        defaultMessageShouldNotBeFound("author.in=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllMessagesByAuthorIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where author is not null
        defaultMessageShouldBeFound("author.specified=true");

        // Get all the messageList where author is null
        defaultMessageShouldNotBeFound("author.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByAuthorContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where author contains DEFAULT_AUTHOR
        defaultMessageShouldBeFound("author.contains=" + DEFAULT_AUTHOR);

        // Get all the messageList where author contains UPDATED_AUTHOR
        defaultMessageShouldNotBeFound("author.contains=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllMessagesByAuthorNotContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where author does not contain DEFAULT_AUTHOR
        defaultMessageShouldNotBeFound("author.doesNotContain=" + DEFAULT_AUTHOR);

        // Get all the messageList where author does not contain UPDATED_AUTHOR
        defaultMessageShouldBeFound("author.doesNotContain=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllMessagesByRecepientIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where recepient equals to DEFAULT_RECEPIENT
        defaultMessageShouldBeFound("recepient.equals=" + DEFAULT_RECEPIENT);

        // Get all the messageList where recepient equals to UPDATED_RECEPIENT
        defaultMessageShouldNotBeFound("recepient.equals=" + UPDATED_RECEPIENT);
    }

    @Test
    @Transactional
    void getAllMessagesByRecepientIsNotEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where recepient not equals to DEFAULT_RECEPIENT
        defaultMessageShouldNotBeFound("recepient.notEquals=" + DEFAULT_RECEPIENT);

        // Get all the messageList where recepient not equals to UPDATED_RECEPIENT
        defaultMessageShouldBeFound("recepient.notEquals=" + UPDATED_RECEPIENT);
    }

    @Test
    @Transactional
    void getAllMessagesByRecepientIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where recepient in DEFAULT_RECEPIENT or UPDATED_RECEPIENT
        defaultMessageShouldBeFound("recepient.in=" + DEFAULT_RECEPIENT + "," + UPDATED_RECEPIENT);

        // Get all the messageList where recepient equals to UPDATED_RECEPIENT
        defaultMessageShouldNotBeFound("recepient.in=" + UPDATED_RECEPIENT);
    }

    @Test
    @Transactional
    void getAllMessagesByRecepientIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where recepient is not null
        defaultMessageShouldBeFound("recepient.specified=true");

        // Get all the messageList where recepient is null
        defaultMessageShouldNotBeFound("recepient.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByRecepientContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where recepient contains DEFAULT_RECEPIENT
        defaultMessageShouldBeFound("recepient.contains=" + DEFAULT_RECEPIENT);

        // Get all the messageList where recepient contains UPDATED_RECEPIENT
        defaultMessageShouldNotBeFound("recepient.contains=" + UPDATED_RECEPIENT);
    }

    @Test
    @Transactional
    void getAllMessagesByRecepientNotContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where recepient does not contain DEFAULT_RECEPIENT
        defaultMessageShouldNotBeFound("recepient.doesNotContain=" + DEFAULT_RECEPIENT);

        // Get all the messageList where recepient does not contain UPDATED_RECEPIENT
        defaultMessageShouldBeFound("recepient.doesNotContain=" + UPDATED_RECEPIENT);
    }

    @Test
    @Transactional
    void getAllMessagesByTextIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where text equals to DEFAULT_TEXT
        defaultMessageShouldBeFound("text.equals=" + DEFAULT_TEXT);

        // Get all the messageList where text equals to UPDATED_TEXT
        defaultMessageShouldNotBeFound("text.equals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByTextIsNotEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where text not equals to DEFAULT_TEXT
        defaultMessageShouldNotBeFound("text.notEquals=" + DEFAULT_TEXT);

        // Get all the messageList where text not equals to UPDATED_TEXT
        defaultMessageShouldBeFound("text.notEquals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByTextIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where text in DEFAULT_TEXT or UPDATED_TEXT
        defaultMessageShouldBeFound("text.in=" + DEFAULT_TEXT + "," + UPDATED_TEXT);

        // Get all the messageList where text equals to UPDATED_TEXT
        defaultMessageShouldNotBeFound("text.in=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where text is not null
        defaultMessageShouldBeFound("text.specified=true");

        // Get all the messageList where text is null
        defaultMessageShouldNotBeFound("text.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByTextContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where text contains DEFAULT_TEXT
        defaultMessageShouldBeFound("text.contains=" + DEFAULT_TEXT);

        // Get all the messageList where text contains UPDATED_TEXT
        defaultMessageShouldNotBeFound("text.contains=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByTextNotContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where text does not contain DEFAULT_TEXT
        defaultMessageShouldNotBeFound("text.doesNotContain=" + DEFAULT_TEXT);

        // Get all the messageList where text does not contain UPDATED_TEXT
        defaultMessageShouldBeFound("text.doesNotContain=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where status equals to DEFAULT_STATUS
        defaultMessageShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the messageList where status equals to UPDATED_STATUS
        defaultMessageShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllMessagesByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where status not equals to DEFAULT_STATUS
        defaultMessageShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the messageList where status not equals to UPDATED_STATUS
        defaultMessageShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllMessagesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultMessageShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the messageList where status equals to UPDATED_STATUS
        defaultMessageShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllMessagesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where status is not null
        defaultMessageShouldBeFound("status.specified=true");

        // Get all the messageList where status is null
        defaultMessageShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where created equals to DEFAULT_CREATED
        defaultMessageShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the messageList where created equals to UPDATED_CREATED
        defaultMessageShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where created not equals to DEFAULT_CREATED
        defaultMessageShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the messageList where created not equals to UPDATED_CREATED
        defaultMessageShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultMessageShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the messageList where created equals to UPDATED_CREATED
        defaultMessageShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where created is not null
        defaultMessageShouldBeFound("created.specified=true");

        // Get all the messageList where created is null
        defaultMessageShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where created is greater than or equal to DEFAULT_CREATED
        defaultMessageShouldBeFound("created.greaterThanOrEqual=" + DEFAULT_CREATED);

        // Get all the messageList where created is greater than or equal to UPDATED_CREATED
        defaultMessageShouldNotBeFound("created.greaterThanOrEqual=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where created is less than or equal to DEFAULT_CREATED
        defaultMessageShouldBeFound("created.lessThanOrEqual=" + DEFAULT_CREATED);

        // Get all the messageList where created is less than or equal to SMALLER_CREATED
        defaultMessageShouldNotBeFound("created.lessThanOrEqual=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where created is less than DEFAULT_CREATED
        defaultMessageShouldNotBeFound("created.lessThan=" + DEFAULT_CREATED);

        // Get all the messageList where created is less than UPDATED_CREATED
        defaultMessageShouldBeFound("created.lessThan=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where created is greater than DEFAULT_CREATED
        defaultMessageShouldNotBeFound("created.greaterThan=" + DEFAULT_CREATED);

        // Get all the messageList where created is greater than SMALLER_CREATED
        defaultMessageShouldBeFound("created.greaterThan=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllMessagesByEditedIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where edited equals to DEFAULT_EDITED
        defaultMessageShouldBeFound("edited.equals=" + DEFAULT_EDITED);

        // Get all the messageList where edited equals to UPDATED_EDITED
        defaultMessageShouldNotBeFound("edited.equals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllMessagesByEditedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where edited not equals to DEFAULT_EDITED
        defaultMessageShouldNotBeFound("edited.notEquals=" + DEFAULT_EDITED);

        // Get all the messageList where edited not equals to UPDATED_EDITED
        defaultMessageShouldBeFound("edited.notEquals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllMessagesByEditedIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where edited in DEFAULT_EDITED or UPDATED_EDITED
        defaultMessageShouldBeFound("edited.in=" + DEFAULT_EDITED + "," + UPDATED_EDITED);

        // Get all the messageList where edited equals to UPDATED_EDITED
        defaultMessageShouldNotBeFound("edited.in=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllMessagesByEditedIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where edited is not null
        defaultMessageShouldBeFound("edited.specified=true");

        // Get all the messageList where edited is null
        defaultMessageShouldNotBeFound("edited.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByEditedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where edited is greater than or equal to DEFAULT_EDITED
        defaultMessageShouldBeFound("edited.greaterThanOrEqual=" + DEFAULT_EDITED);

        // Get all the messageList where edited is greater than or equal to UPDATED_EDITED
        defaultMessageShouldNotBeFound("edited.greaterThanOrEqual=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllMessagesByEditedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where edited is less than or equal to DEFAULT_EDITED
        defaultMessageShouldBeFound("edited.lessThanOrEqual=" + DEFAULT_EDITED);

        // Get all the messageList where edited is less than or equal to SMALLER_EDITED
        defaultMessageShouldNotBeFound("edited.lessThanOrEqual=" + SMALLER_EDITED);
    }

    @Test
    @Transactional
    void getAllMessagesByEditedIsLessThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where edited is less than DEFAULT_EDITED
        defaultMessageShouldNotBeFound("edited.lessThan=" + DEFAULT_EDITED);

        // Get all the messageList where edited is less than UPDATED_EDITED
        defaultMessageShouldBeFound("edited.lessThan=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    void getAllMessagesByEditedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where edited is greater than DEFAULT_EDITED
        defaultMessageShouldNotBeFound("edited.greaterThan=" + DEFAULT_EDITED);

        // Get all the messageList where edited is greater than SMALLER_EDITED
        defaultMessageShouldBeFound("edited.greaterThan=" + SMALLER_EDITED);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMessageShouldBeFound(String filter) throws Exception {
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].recepient").value(hasItem(DEFAULT_RECEPIENT)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(sameInstant(DEFAULT_EDITED))));

        // Check, that the count call also returns 1
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMessageShouldNotBeFound(String filter) throws Exception {
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMessage() throws Exception {
        // Get the message
        restMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message
        Message updatedMessage = messageRepository.findById(message.getId()).get();
        // Disconnect from session so that the updates on updatedMessage are not directly saved in db
        em.detach(updatedMessage);
        updatedMessage
            .author(UPDATED_AUTHOR)
            .recepient(UPDATED_RECEPIENT)
            .text(UPDATED_TEXT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);

        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMessage.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testMessage.getRecepient()).isEqualTo(UPDATED_RECEPIENT);
        assertThat(testMessage.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testMessage.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testMessage.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testMessage.getEdited()).isEqualTo(UPDATED_EDITED);
    }

    @Test
    @Transactional
    void putNonExistingMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, message.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(message))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(message))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(message))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage.recepient(UPDATED_RECEPIENT);

        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testMessage.getRecepient()).isEqualTo(UPDATED_RECEPIENT);
        assertThat(testMessage.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testMessage.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testMessage.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testMessage.getEdited()).isEqualTo(DEFAULT_EDITED);
    }

    @Test
    @Transactional
    void fullUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage
            .author(UPDATED_AUTHOR)
            .recepient(UPDATED_RECEPIENT)
            .text(UPDATED_TEXT)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .edited(UPDATED_EDITED);

        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testMessage.getRecepient()).isEqualTo(UPDATED_RECEPIENT);
        assertThat(testMessage.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testMessage.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testMessage.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testMessage.getEdited()).isEqualTo(UPDATED_EDITED);
    }

    @Test
    @Transactional
    void patchNonExistingMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, message.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(message))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(message))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(message))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeDelete = messageRepository.findAll().size();

        // Delete the message
        restMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, message.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
