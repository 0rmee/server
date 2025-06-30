package com.ormee.server.memo.service;

import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.memo.domain.Memo;
import com.ormee.server.memo.dto.MemoDto;
import com.ormee.server.memo.dto.MemoListDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.memo.repository.MemoRepository;
import com.ormee.server.memo.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemoService {
    private final MemoRepository memoRepository;
    private final MessageRepository messageRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    public MemoService(MemoRepository memoRepository, MessageRepository messageRepository, LectureRepository lectureRepository, MemberRepository memberRepository) {
        this.memoRepository = memoRepository;
        this.messageRepository = messageRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
    }

    public void createMemo(Long lectureId, MemoDto memoDto, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member author = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Memo memo = new Memo();
        memo.setLecture(lecture);
        memo.setAuthor(author);
        memo.setTitle(memoDto.getTitle());
//        memo.setDescription(memoDto.getDescription());
//        memo.setDueTime(memoDto.getDueTime());
        memo.setDueTime(LocalDateTime.now().plusYears(1));
        memo.setIsOpen(true);
        memo.setNotified(false);

        closeOpenedMemos(lecture);

        memoRepository.save(memo);
    }

    public MemoListDto getAllMemos(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        List<Memo> memoList = memoRepository.findAllByLecture(lecture);
        List<MemoDto> openMemos = new ArrayList<>();
        List<MemoDto> closeMemos = new ArrayList<>();

        memoList.stream()
                .map(memo -> MemoDto.builder()
                        .id(memo.getId())
                        .author(Optional.ofNullable(memo.getAuthor())
                                .map(Member::getNickname)
                                .orElse(memo.getLecture().getTeacher().getNickname()))
                        .title(memo.getTitle())
                        .description(memo.getDescription())
                        .dueTime(memo.getDueTime())
                        .isOpen(memo.getIsOpen())
                        .submitCount(getSubmitCount(memo.getId()))
                        .totalCount(lecture.getStudentLectures().size())
                        .build())
                .forEach(memoDto -> {
                    if (memoDto.getDueTime().isBefore(LocalDateTime.now())) {
                        closeMemos.add(memoDto);
                    } else {
                        openMemos.add(memoDto);
                    }
                });

        return MemoListDto.builder()
                .openMemos(openMemos)
                .closeMemos(closeMemos)
                .build();
    }

    private Integer getSubmitCount(Long memoId) {
        return messageRepository.countByMemoId(memoId);
    }

    private void closeOpenedMemos(Lecture lecture) {
        List<Memo> memos = memoRepository.findAllByLecture(lecture);
        for(Memo memo : memos) {
            memo.setIsOpen(false);
            memo.setDueTime(LocalDateTime.now());
            memoRepository.save(memo);
        }
    }

    public void toggleIsOpen(Long memoId, Boolean isOpen) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new CustomException(ExceptionType.MEMO_NOT_FOUND_EXCEPTION));

        memo.setIsOpen(isOpen);
        if(!isOpen) {
            memo.setDueTime(LocalDateTime.now());
        }
        memoRepository.save(memo);
    }

    public MemoDto getOpenMemo(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Memo memo = memoRepository.findFirstByLectureAndIsOpenOrderByCreatedAtDesc(lecture, true).orElseThrow(() -> new CustomException(ExceptionType.MEMO_NOT_FOUND_EXCEPTION));

        return MemoDto.builder()
                .id(memo.getId())
                .title(memo.getTitle())
                .dueTime(memo.getDueTime())
                .isOpen(memo.getIsOpen())
                .build();
    }
}
