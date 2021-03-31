package ru.bjcreslin.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import ru.bjcreslin.domain.enumeration.TaskAndProjectStatus;
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
 * Criteria class for the {@link ru.bjcreslin.domain.Task} entity. This class is used
 * in {@link ru.bjcreslin.web.rest.TaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TaskCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TaskAndProjectStatus
     */
    public static class TaskAndProjectStatusFilter extends Filter<TaskAndProjectStatus> {

        public TaskAndProjectStatusFilter() {}

        public TaskAndProjectStatusFilter(TaskAndProjectStatusFilter filter) {
            super(filter);
        }

        @Override
        public TaskAndProjectStatusFilter copy() {
            return new TaskAndProjectStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter author;

    private StringFilter implementer;

    private StringFilter name;

    private StringFilter text;

    private StringFilter comment;

    private TaskAndProjectStatusFilter status;

    private ZonedDateTimeFilter created;

    private ZonedDateTimeFilter edited;

    private LongFilter commentId;

    private LongFilter projectId;

    public TaskCriteria() {}

    public TaskCriteria(TaskCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.author = other.author == null ? null : other.author.copy();
        this.implementer = other.implementer == null ? null : other.implementer.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.text = other.text == null ? null : other.text.copy();
        this.comment = other.comment == null ? null : other.comment.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.edited = other.edited == null ? null : other.edited.copy();
        this.commentId = other.commentId == null ? null : other.commentId.copy();
        this.projectId = other.projectId == null ? null : other.projectId.copy();
    }

    @Override
    public TaskCriteria copy() {
        return new TaskCriteria(this);
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

    public StringFilter getAuthor() {
        return author;
    }

    public StringFilter author() {
        if (author == null) {
            author = new StringFilter();
        }
        return author;
    }

    public void setAuthor(StringFilter author) {
        this.author = author;
    }

    public StringFilter getImplementer() {
        return implementer;
    }

    public StringFilter implementer() {
        if (implementer == null) {
            implementer = new StringFilter();
        }
        return implementer;
    }

    public void setImplementer(StringFilter implementer) {
        this.implementer = implementer;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getText() {
        return text;
    }

    public StringFilter text() {
        if (text == null) {
            text = new StringFilter();
        }
        return text;
    }

    public void setText(StringFilter text) {
        this.text = text;
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

    public TaskAndProjectStatusFilter getStatus() {
        return status;
    }

    public TaskAndProjectStatusFilter status() {
        if (status == null) {
            status = new TaskAndProjectStatusFilter();
        }
        return status;
    }

    public void setStatus(TaskAndProjectStatusFilter status) {
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

    public LongFilter getCommentId() {
        return commentId;
    }

    public LongFilter commentId() {
        if (commentId == null) {
            commentId = new LongFilter();
        }
        return commentId;
    }

    public void setCommentId(LongFilter commentId) {
        this.commentId = commentId;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public LongFilter projectId() {
        if (projectId == null) {
            projectId = new LongFilter();
        }
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TaskCriteria that = (TaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(author, that.author) &&
            Objects.equals(implementer, that.implementer) &&
            Objects.equals(name, that.name) &&
            Objects.equals(text, that.text) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(status, that.status) &&
            Objects.equals(created, that.created) &&
            Objects.equals(edited, that.edited) &&
            Objects.equals(commentId, that.commentId) &&
            Objects.equals(projectId, that.projectId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, implementer, name, text, comment, status, created, edited, commentId, projectId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (author != null ? "author=" + author + ", " : "") +
            (implementer != null ? "implementer=" + implementer + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (text != null ? "text=" + text + ", " : "") +
            (comment != null ? "comment=" + comment + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (created != null ? "created=" + created + ", " : "") +
            (edited != null ? "edited=" + edited + ", " : "") +
            (commentId != null ? "commentId=" + commentId + ", " : "") +
            (projectId != null ? "projectId=" + projectId + ", " : "") +
            "}";
    }
}
