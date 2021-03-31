package ru.bjcreslin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import ru.bjcreslin.domain.enumeration.CommentStatus;

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "author")
    private String author;

    @Column(name = "text")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CommentStatus status;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "edited")
    private ZonedDateTime edited;

    @ManyToOne
    @JsonIgnoreProperties(value = { "comments", "project" }, allowSetters = true)
    private Task task;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Comment id(Long id) {
        this.id = id;
        return this;
    }

    public String getAuthor() {
        return this.author;
    }

    public Comment author(String author) {
        this.author = author;
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return this.text;
    }

    public Comment text(String text) {
        this.text = text;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CommentStatus getStatus() {
        return this.status;
    }

    public Comment status(CommentStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Comment created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getEdited() {
        return this.edited;
    }

    public Comment edited(ZonedDateTime edited) {
        this.edited = edited;
        return this;
    }

    public void setEdited(ZonedDateTime edited) {
        this.edited = edited;
    }

    public Task getTask() {
        return this.task;
    }

    public Comment task(Task task) {
        this.setTask(task);
        return this;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        return id != null && id.equals(((Comment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Comment{" +
            "id=" + getId() +
            ", author='" + getAuthor() + "'" +
            ", text='" + getText() + "'" +
            ", status='" + getStatus() + "'" +
            ", created='" + getCreated() + "'" +
            ", edited='" + getEdited() + "'" +
            "}";
    }
}
