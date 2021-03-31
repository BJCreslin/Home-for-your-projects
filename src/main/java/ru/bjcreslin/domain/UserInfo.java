package ru.bjcreslin.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import ru.bjcreslin.domain.enumeration.UserStatus;

/**
 * A UserInfo.
 */
@Entity
@Table(name = "user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "git_hub_id")
    private String gitHubId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Min(value = 0L)
    @Max(value = 169L)
    @Column(name = "hours")
    private Long hours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "edited")
    private ZonedDateTime edited;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo id(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public UserInfo email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGitHubId() {
        return this.gitHubId;
    }

    public UserInfo gitHubId(String gitHubId) {
        this.gitHubId = gitHubId;
        return this;
    }

    public void setGitHubId(String gitHubId) {
        this.gitHubId = gitHubId;
    }

    public String getName() {
        return this.name;
    }

    public UserInfo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getHours() {
        return this.hours;
    }

    public UserInfo hours(Long hours) {
        this.hours = hours;
        return this;
    }

    public void setHours(Long hours) {
        this.hours = hours;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public UserInfo status(UserStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDate getBirthday() {
        return this.birthday;
    }

    public UserInfo birthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getComment() {
        return this.comment;
    }

    public UserInfo comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public UserInfo created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getEdited() {
        return this.edited;
    }

    public UserInfo edited(ZonedDateTime edited) {
        this.edited = edited;
        return this;
    }

    public void setEdited(ZonedDateTime edited) {
        this.edited = edited;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserInfo)) {
            return false;
        }
        return id != null && id.equals(((UserInfo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserInfo{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", gitHubId='" + getGitHubId() + "'" +
            ", name='" + getName() + "'" +
            ", hours=" + getHours() +
            ", status='" + getStatus() + "'" +
            ", birthday='" + getBirthday() + "'" +
            ", comment='" + getComment() + "'" +
            ", created='" + getCreated() + "'" +
            ", edited='" + getEdited() + "'" +
            "}";
    }
}
