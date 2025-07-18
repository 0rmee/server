package com.ormee.server.memo.service;

import com.ormee.server.memo.domain.Memo;
import com.ormee.server.memo.domain.Message;
import com.ormee.server.memo.dto.MessageListDto;
import com.ormee.server.memo.dto.MessageSubmitDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.memo.repository.MemoRepository;
import com.ormee.server.memo.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemoRepository memoRepository;
    private final MemberRepository memberRepository;

    public MessageService(MessageRepository messageRepository, MemoRepository memoRepository, MemberRepository memberRepository) {
        this.messageRepository = messageRepository;
        this.memoRepository = memoRepository;
        this.memberRepository = memberRepository;
    }

    public void submitMessage(Long memoId, MessageSubmitDto messageSubmitDto, String username) {
        Memo memo = memoRepository.findById(memoId).orElseThrow(() -> new CustomException(ExceptionType.MEMO_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Message message = new Message();
        message.setMemo(memo);
        message.setStudent(student);
        message.setContext(messageSubmitDto.getContext());

        messageRepository.save(message);
    }

    public List<MessageListDto> getMessages(Long memoId) {
        List<Message> messages = messageRepository.findAllByMemo_Id(memoId);

        int totalMessages = messages.stream()
                .mapToInt(message -> message.getContext().split(",").length)
                .sum();

        Map<Integer, Long> frequencyMap = messages.stream()
                .flatMap(message -> {
                    String[] parts = message.getContext().split(",");
                    return Stream.of(parts)
                            .map(String::trim)
                            .map(Integer::valueOf);
                })
                .collect(Collectors.groupingBy(
                        value -> value,
                        Collectors.counting()
                ));

        List<MessageListDto> messageListDtos = frequencyMap.entrySet().stream()
                .map(entry -> {
                    Integer contentDetail = entry.getKey();
                    Long count = entry.getValue();
                    long submitRate = (totalMessages == 0) ? 0 : (count * 100) / totalMessages;

                    return MessageListDto.builder()
                            .contentDetail(contentDetail)
                            .submitRate(submitRate)
                            .submit(count.intValue())
                            .build();
                })
                .sorted(Comparator.comparing(MessageListDto::getSubmitRate).reversed())
                .collect(Collectors.toList());

        long rank = 1;
        long previousRank = 1;
        MessageListDto previousDto = null;

        for (MessageListDto dto : messageListDtos) {
            if (previousDto != null &&
                    (dto.getSubmitRate() != previousDto.getSubmitRate() ||
                            dto.getSubmit() != previousDto.getSubmit())) {
                rank = previousRank;
            }
            dto.setRank(rank);
            previousRank = rank;
            rank++;
            previousDto = dto;
        }

        for (int i = 0; i < messageListDtos.size(); i++) {
            if (i == 0) {
                messageListDtos.get(i).setRank(1);
            } else {
                MessageListDto current = messageListDtos.get(i);
                MessageListDto previous = messageListDtos.get(i - 1);

                if (current.getSubmitRate() == previous.getSubmitRate()) {
                    current.setRank(previous.getRank());
                } else {
                    current.setRank(i + 1);
                }
            }
        }

        return messageListDtos;
    }
}
