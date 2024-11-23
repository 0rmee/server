package com.ormee.server.repository;

import com.ormee.server.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Integer countByMemoId(Long memoId);
}
