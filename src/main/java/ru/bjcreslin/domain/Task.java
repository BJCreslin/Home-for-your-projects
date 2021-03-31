package ru.bjcreslin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import ru.bjcreslin.domain.enumeration.TaskAndProjectStatus;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "author")
    private String author;

    @Column(name = "implementer")
    private String implementer;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "text")
    private String text;

    @Column(name = "comment")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskAndProjectStatus status;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "edited")
    private ZonedDateTime edited;

    @OneToMany(mappedBy = "task")
    @JsonIgnoreProperties(value = { "task" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "tasks" }, allowSetters = true)
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task id(Long id) {
        this.id = id;
        return this;
    }

    public String getAuthor() {
        return this.author;
    }

    public Task author(String author) {
        this.author = author;
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImplementer() {
        return this.implementer;
    }

    public Task implementer(String implementer) {
        this.implementer = implementer;
        return this;
    }

    public void setImplementer(String implementer) {
        this.implementer = implementer;
    }

    public String getName() {
        return this.name;
    }

    public Task name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return this.text;
    }

    public Task text(String text) {
        this.text = text;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getComment() {
        return this.comment;
    }

    public Task comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public TaskAndProjectStatus getStatus() {
        return this.status;
    }

    public Task status(TaskAndProjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(TaskAndProjectStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Task created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getEdited() {
        return this.edited;
    }

    public Task edited(ZonedDateTime edited) {
        this.edited = edited;
        return this;
    }

    public void setEdited(ZonedDateTime edited) {
        this.edited = edited;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public Task comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Task addComment(Comment comment) {
        this.comments.add(comment);
        comment.setTask(this);
        return this;
    }

    public Task removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setTask(null);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setTask(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setTask(this));
        }
        this.comments = comments;
    }

    public Project getProject() {
        return this.project;
    }

    public Task project(Project project) {
        this.setProject(project);
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return id != null && id.equals(((Task) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", author='" + getAuthor() + "'" +
            ", implementer='" + getImplementer() + "'" +
            ", name='" + getName() + "'" +
            ", text='" + getText() + "'" +
            ", comment='" + getComment() + "'" +
            ", status='" + getStatus() + "'" +
            ", created='" + getCreated() + "'" +
            ", edited='" + getEdited() + "'" +
            "}";
    }
}
