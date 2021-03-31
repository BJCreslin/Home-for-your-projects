package ru.bjcreslin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import ru.bjcreslin.domain.enumeration.ProjectStatus;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "project_url")
    private String projectUrl;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "comment")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStatus status;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "edited")
    private ZonedDateTime edited;

    @OneToMany(mappedBy = "project")
    @JsonIgnoreProperties(value = { "comments", "project" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project id(Long id) {
        this.id = id;
        return this;
    }

    public String getProjectUrl() {
        return this.projectUrl;
    }

    public Project projectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
        return this;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public Project description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public Project projectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getComment() {
        return this.comment;
    }

    public Project comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ProjectStatus getStatus() {
        return this.status;
    }

    public Project status(ProjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Project created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getEdited() {
        return this.edited;
    }

    public Project edited(ZonedDateTime edited) {
        this.edited = edited;
        return this;
    }

    public void setEdited(ZonedDateTime edited) {
        this.edited = edited;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public Project tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public Project addTask(Task task) {
        this.tasks.add(task);
        task.setProject(this);
        return this;
    }

    public Project removeTask(Task task) {
        this.tasks.remove(task);
        task.setProject(null);
        return this;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setProject(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setProject(this));
        }
        this.tasks = tasks;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return id != null && id.equals(((Project) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", projectUrl='" + getProjectUrl() + "'" +
            ", description='" + getDescription() + "'" +
            ", projectName='" + getProjectName() + "'" +
            ", comment='" + getComment() + "'" +
            ", status='" + getStatus() + "'" +
            ", created='" + getCreated() + "'" +
            ", edited='" + getEdited() + "'" +
            "}";
    }
}
