package com.ormee.server.service;

import com.ormee.server.dto.MemoDto;
import com.ormee.server.dto.MemoListDto;
import com.ormee.server.model.Memo;
import com.ormee.server.repository.MemoRepository;
import com.ormee.server.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoService {
    private final MemoRepository memoRepository;
    private final MessageRepository messageRepository;

    public MemoService(MemoRepository memoRepository, MessageRepository messageRepository) {
        this.memoRepository = memoRepository;
        this.messageRepository = messageRepository;
    }

    public List<MemoListDto> getAllMemos() {
        List<Memo> memoList = memoRepository.findAll();

        return memoList.stream()
                .map(memo -> MemoListDto.builder()
                        .title(memo.getTitle())
                        .description(memo.getDescription())
                        .dueTime(memo.getDueTime())
                        .submit(getSubmitCount(memo.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    private Integer getSubmitCount(Long memoId) {

        return messageRepository.countByMemoId(memoId);
    }

    public Memo createMemo(MemoDto memoDto) {
        Memo memo = new Memo();
        memo.setTitle(memoDto.getTitle());
        memo.setDescription(memoDto.getDescription());
        memo.setDueTime(memoDto.getDueTime());

        return memoRepository.save(memo);
    }
}
