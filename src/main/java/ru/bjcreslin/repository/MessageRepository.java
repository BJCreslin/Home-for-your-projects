package ru.bjcreslin.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.bjcreslin.domain.Message;

/**
 * Spring Data SQL repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {}
