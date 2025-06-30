package com.ormee.server.memo.repository;

import com.ormee.server.memo.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Integer countByMemoId(Long memoId);

    List<Message> findAllByMemo_Id(Long memoId);
}
