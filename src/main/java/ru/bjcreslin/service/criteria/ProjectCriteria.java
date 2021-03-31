package ru.bjcreslin.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import ru.bjcreslin.domain.enumeration.ProjectStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link ru.bjcreslin.domain.Project} entity. This class is used
 * in {@link ru.bjcreslin.web.rest.ProjectResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /projects?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ProjectStatus
     */
    public static class ProjectStatusFilter extends Filter<ProjectStatus> {

        public ProjectStatusFilter() {}

        public ProjectStatusFilter(ProjectStatusFilter filter) {
            super(filter);
        }

        @Override
        public ProjectStatusFilter copy() {
            return new ProjectStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter projectUrl;

    private StringFilter description;

    private StringFilter projectName;

    private StringFilter comment;

    private ProjectStatusFilter status;

    private ZonedDateTimeFilter created;

    private ZonedDateTimeFilter edited;

    private LongFilter taskId;

    public ProjectCriteria() {}

    public ProjectCriteria(ProjectCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.projectUrl = other.projectUrl == null ? null : other.projectUrl.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.projectName = other.projectName == null ? null : other.projectName.copy();
        this.comment = other.comment == null ? null : other.comment.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.edited = other.edited == null ? null : other.edited.copy();
        this.taskId = other.taskId == null ? null : other.taskId.copy();
    }

    @Override
    public ProjectCriteria copy() {
        return new ProjectCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getProjectUrl() {
        return projectUrl;
    }

    public StringFilter projectUrl() {
        if (projectUrl == null) {
            projectUrl = new StringFilter();
        }
        return projectUrl;
    }

    public void setProjectUrl(StringFilter projectUrl) {
        this.projectUrl = projectUrl;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getProjectName() {
        return projectName;
    }

    public StringFilter projectName() {
        if (projectName == null) {
            projectName = new StringFilter();
        }
        return projectName;
    }

    public void setProjectName(StringFilter projectName) {
        this.projectName = projectName;
    }

    public StringFilter getComment() {
        return comment;
    }

    public StringFilter comment() {
        if (comment == null) {
            comment = new StringFilter();
        }
        return comment;
    }

    public void setComment(StringFilter comment) {
        this.comment = comment;
    }

    public ProjectStatusFilter getStatus() {
        return status;
    }

    public ProjectStatusFilter status() {
        if (status == null) {
            status = new ProjectStatusFilter();
        }
        return status;
    }

    public void setStatus(ProjectStatusFilter status) {
        this.status = status;
    }

    public ZonedDateTimeFilter getCreated() {
        return created;
    }

    public ZonedDateTimeFilter created() {
        if (created == null) {
            created = new ZonedDateTimeFilter();
        }
        return created;
    }

    public void setCreated(ZonedDateTimeFilter created) {
        this.created = created;
    }

    public ZonedDateTimeFilter getEdited() {
        return edited;
    }

    public ZonedDateTimeFilter edited() {
        if (edited == null) {
            edited = new ZonedDateTimeFilter();
        }
        return edited;
    }

    public void setEdited(ZonedDateTimeFilter edited) {
        this.edited = edited;
    }

    public LongFilter getTaskId() {
        return taskId;
    }

    public LongFilter taskId() {
        if (taskId == null) {
            taskId = new LongFilter();
        }
        return taskId;
    }

    public void setTaskId(LongFilter taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProjectCriteria that = (ProjectCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(projectUrl, that.projectUrl) &&
            Objects.equals(description, that.description) &&
            Objects.equals(projectName, that.projectName) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(status, that.status) &&
            Objects.equals(created, that.created) &&
            Objects.equals(edited, that.edited) &&
            Objects.equals(taskId, that.taskId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectUrl, description, projectName, comment, status, created, edited, taskId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (projectUrl != null ? "projectUrl=" + projectUrl + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (projectName != null ? "projectName=" + projectName + ", " : "") +
            (comment != null ? "comment=" + comment + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (created != null ? "created=" + created + ", " : "") +
            (edited != null ? "edited=" + edited + ", " : "") +
            (taskId != null ? "taskId=" + taskId + ", " : "") +
            "}";
    }
}
