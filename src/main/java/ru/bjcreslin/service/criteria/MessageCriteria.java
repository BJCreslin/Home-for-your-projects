package ru.bjcreslin.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import ru.bjcreslin.domain.enumeration.MessageStatus;
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
 * Criteria class for the {@link ru.bjcreslin.domain.Message} entity. This class is used
 * in {@link ru.bjcreslin.web.rest.MessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MessageCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MessageStatus
     */
    public static class MessageStatusFilter extends Filter<MessageStatus> {

        public MessageStatusFilter() {}

        public MessageStatusFilter(MessageStatusFilter filter) {
            super(filter);
        }

        @Override
        public MessageStatusFilter copy() {
            return new MessageStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter author;

    private StringFilter recepient;

    private StringFilter text;

    private MessageStatusFilter status;

    private ZonedDateTimeFilter created;

    private ZonedDateTimeFilter edited;

    public MessageCriteria() {}

    public MessageCriteria(MessageCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.author = other.author == null ? null : other.author.copy();
        this.recepient = other.recepient == null ? null : other.recepient.copy();
        this.text = other.text == null ? null : other.text.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.edited = other.edited == null ? null : other.edited.copy();
    }

    @Override
    public MessageCriteria copy() {
        return new MessageCriteria(this);
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

    public StringFilter getRecepient() {
        return recepient;
    }

    public StringFilter recepient() {
        if (recepient == null) {
            recepient = new StringFilter();
        }
        return recepient;
    }

    public void setRecepient(StringFilter recepient) {
        this.recepient = recepient;
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

    public MessageStatusFilter getStatus() {
        return status;
    }

    public MessageStatusFilter status() {
        if (status == null) {
            status = new MessageStatusFilter();
        }
        return status;
    }

    public void setStatus(MessageStatusFilter status) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MessageCriteria that = (MessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(author, that.author) &&
            Objects.equals(recepient, that.recepient) &&
            Objects.equals(text, that.text) &&
            Objects.equals(status, that.status) &&
            Objects.equals(created, that.created) &&
            Objects.equals(edited, that.edited)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, recepient, text, status, created, edited);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (author != null ? "author=" + author + ", " : "") +
            (recepient != null ? "recepient=" + recepient + ", " : "") +
            (text != null ? "text=" + text + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (created != null ? "created=" + created + ", " : "") +
            (edited != null ? "edited=" + edited + ", " : "") +
            "}";
    }
}
