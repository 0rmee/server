package com.ormee.server.service;

import com.ormee.server.dto.notice.NoticeSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Notice;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final LectureRepository lectureRepository;
    public NoticeService(NoticeRepository noticeRepository, LectureRepository lectureRepository) {
        this.noticeRepository = noticeRepository;
        this.lectureRepository = lectureRepository;
    }

    public void saveNotice(UUID lectureId, NoticeSaveDto noticeSaveDto) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Notice notice = Notice.builder()
                .lecture(lecture)
                .title(noticeSaveDto.getTitle())
                .description(noticeSaveDto.getDescription())
                .postDate(LocalDateTime.now())
                .isPinned(false)
                .likes(noticeSaveDto.getLikes())
                .build();
        noticeRepository.save(notice);
    }

    public void modifyNotice(Long noticeId, NoticeSaveDto noticeSaveDto) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        saveNotice(notice.getLecture().getId(), noticeSaveDto);
        deleteNotice(noticeId);
    }

    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        noticeRepository.delete(notice);
    }

    public List<Notice> findAllByLectureId(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.findAllByLecture(lecture);
        return notices;
    }
}
