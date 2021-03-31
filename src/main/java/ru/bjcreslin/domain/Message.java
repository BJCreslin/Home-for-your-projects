package ru.bjcreslin.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import ru.bjcreslin.domain.enumeration.MessageStatus;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "author")
    private String author;

    @Column(name = "recepient")
    private String recepient;

    @Column(name = "text")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

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

    public Message id(Long id) {
        this.id = id;
        return this;
    }

    public String getAuthor() {
        return this.author;
    }

    public Message author(String author) {
        this.author = author;
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRecepient() {
        return this.recepient;
    }

    public Message recepient(String recepient) {
        this.recepient = recepient;
        return this;
    }

    public void setRecepient(String recepient) {
        this.recepient = recepient;
    }

    public String getText() {
        return this.text;
    }

    public Message text(String text) {
        this.text = text;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageStatus getStatus() {
        return this.status;
    }

    public Message status(MessageStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Message created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getEdited() {
        return this.edited;
    }

    public Message edited(ZonedDateTime edited) {
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
        if (!(o instanceof Message)) {
            return false;
        }
        return id != null && id.equals(((Message) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", author='" + getAuthor() + "'" +
            ", recepient='" + getRecepient() + "'" +
            ", text='" + getText() + "'" +
            ", status='" + getStatus() + "'" +
            ", created='" + getCreated() + "'" +
            ", edited='" + getEdited() + "'" +
            "}";
    }
}
