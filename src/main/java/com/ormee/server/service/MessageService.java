package com.ormee.server.service;

import com.ormee.server.dto.MessageListDto;
import com.ormee.server.dto.MessageSubmitDto;
import com.ormee.server.model.Memo;
import com.ormee.server.model.Message;
import com.ormee.server.repository.MemoRepository;
import com.ormee.server.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemoRepository memoRepository;

    public MessageService(MessageRepository messageRepository, MemoRepository memoRepository) {
        this.messageRepository = messageRepository;
        this.memoRepository = memoRepository;
    }

    public void submitMessage(Long memoId, MessageSubmitDto messageSubmitDto) {
        Memo memo = memoRepository.findById(memoId).orElse(null);

        Message message = new Message();
        message.setMemo(memo);
        message.setContext(messageSubmitDto.getContext());
        message.setAuthor(messageSubmitDto.getAuthor());
        message.setPassword(messageSubmitDto.getPassword());

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
                    return List.of(parts).stream()
                            .map(String::trim)
                            .map(Integer::valueOf);
                })
                .collect(Collectors.groupingBy(
                        value -> value,
                        Collectors.counting()
                ));

        return frequencyMap.entrySet().stream()
                .map(entry->{
                    Integer contentDetail = entry.getKey();
                    Long count = entry.getValue();
                    float submitRate = (totalMessages == 0 ? 0f : (float) count / totalMessages)*100 ;

                    return MessageListDto.builder()
                            .contentDetail(contentDetail)
                            .submitRate(submitRate)
                            .submit(count.intValue())
                            .build();
                })
                .sorted(Comparator.comparing(MessageListDto::getContentDetail))
                .collect(Collectors.toList());
    }
}
