package ru.bjcreslin.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import ru.bjcreslin.domain.enumeration.UserStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link ru.bjcreslin.domain.UserInfo} entity. This class is used
 * in {@link ru.bjcreslin.web.rest.UserInfoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-infos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserInfoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering UserStatus
     */
    public static class UserStatusFilter extends Filter<UserStatus> {

        public UserStatusFilter() {}

        public UserStatusFilter(UserStatusFilter filter) {
            super(filter);
        }

        @Override
        public UserStatusFilter copy() {
            return new UserStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter email;

    private StringFilter gitHubId;

    private StringFilter name;

    private LongFilter hours;

    private UserStatusFilter status;

    private LocalDateFilter birthday;

    private StringFilter comment;

    private ZonedDateTimeFilter created;

    private ZonedDateTimeFilter edited;

    public UserInfoCriteria() {}

    public UserInfoCriteria(UserInfoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.gitHubId = other.gitHubId == null ? null : other.gitHubId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.hours = other.hours == null ? null : other.hours.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.birthday = other.birthday == null ? null : other.birthday.copy();
        this.comment = other.comment == null ? null : other.comment.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.edited = other.edited == null ? null : other.edited.copy();
    }

    @Override
    public UserInfoCriteria copy() {
        return new UserInfoCriteria(this);
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

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getGitHubId() {
        return gitHubId;
    }

    public StringFilter gitHubId() {
        if (gitHubId == null) {
            gitHubId = new StringFilter();
        }
        return gitHubId;
    }

    public void setGitHubId(StringFilter gitHubId) {
        this.gitHubId = gitHubId;
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

    public LongFilter getHours() {
        return hours;
    }

    public LongFilter hours() {
        if (hours == null) {
            hours = new LongFilter();
        }
        return hours;
    }

    public void setHours(LongFilter hours) {
        this.hours = hours;
    }

    public UserStatusFilter getStatus() {
        return status;
    }

    public UserStatusFilter status() {
        if (status == null) {
            status = new UserStatusFilter();
        }
        return status;
    }

    public void setStatus(UserStatusFilter status) {
        this.status = status;
    }

    public LocalDateFilter getBirthday() {
        return birthday;
    }

    public LocalDateFilter birthday() {
        if (birthday == null) {
            birthday = new LocalDateFilter();
        }
        return birthday;
    }

    public void setBirthday(LocalDateFilter birthday) {
        this.birthday = birthday;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UserInfoCriteria that = (UserInfoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(email, that.email) &&
            Objects.equals(gitHubId, that.gitHubId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(hours, that.hours) &&
            Objects.equals(status, that.status) &&
            Objects.equals(birthday, that.birthday) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(created, that.created) &&
            Objects.equals(edited, that.edited)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, gitHubId, name, hours, status, birthday, comment, created, edited);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserInfoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (gitHubId != null ? "gitHubId=" + gitHubId + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (hours != null ? "hours=" + hours + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (birthday != null ? "birthday=" + birthday + ", " : "") +
            (comment != null ? "comment=" + comment + ", " : "") +
            (created != null ? "created=" + created + ", " : "") +
            (edited != null ? "edited=" + edited + ", " : "") +
            "}";
    }
}
