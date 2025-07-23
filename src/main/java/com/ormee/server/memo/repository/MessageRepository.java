package com.ormee.server.memo.repository;

import com.ormee.server.member.domain.Member;
import com.ormee.server.memo.domain.Memo;
import com.ormee.server.memo.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Integer countByMemoId(Long memoId);

    List<Message> findAllByMemo_Id(Long memoId);

    Optional<Message> findByMemoAndStudent(Memo memo, Member student);

    void deleteAllByMemo(Memo memo);
}
