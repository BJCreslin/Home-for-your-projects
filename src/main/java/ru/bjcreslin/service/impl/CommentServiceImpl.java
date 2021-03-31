package ru.bjcreslin.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bjcreslin.domain.Comment;
import ru.bjcreslin.repository.CommentRepository;
import ru.bjcreslin.service.CommentService;

/**
 * Service Implementation for managing {@link Comment}.
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment save(Comment comment) {
        log.debug("Request to save Comment : {}", comment);
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> partialUpdate(Comment comment) {
        log.debug("Request to partially update Comment : {}", comment);

        return commentRepository
            .findById(comment.getId())
            .map(
                existingComment -> {
                    if (comment.getAuthor() != null) {
                        existingComment.setAuthor(comment.getAuthor());
                    }
                    if (comment.getText() != null) {
                        existingComment.setText(comment.getText());
                    }
                    if (comment.getStatus() != null) {
                        existingComment.setStatus(comment.getStatus());
                    }
                    if (comment.getCreated() != null) {
                        existingComment.setCreated(comment.getCreated());
                    }
                    if (comment.getEdited() != null) {
                        existingComment.setEdited(comment.getEdited());
                    }

                    return existingComment;
                }
            )
            .map(commentRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> findAll(Pageable pageable) {
        log.debug("Request to get all Comments");
        return commentRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findOne(Long id) {
        log.debug("Request to get Comment : {}", id);
        return commentRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Comment : {}", id);
        commentRepository.deleteById(id);
    }
}
