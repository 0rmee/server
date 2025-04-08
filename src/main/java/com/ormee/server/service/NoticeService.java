package com.ormee.server.service;

import com.ormee.server.dto.notice.NoticeDto;
import com.ormee.server.dto.notice.NoticeListDto;
import com.ormee.server.dto.notice.NoticeSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Attachment;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Notice;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.NoticeRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final LectureRepository lectureRepository;
    private AttachmentService attachmentService;

    public NoticeService(NoticeRepository noticeRepository, LectureRepository lectureRepository) {
        this.noticeRepository = noticeRepository;
        this.lectureRepository = lectureRepository;
    }

    public void saveNotice(UUID lectureId, NoticeSaveDto noticeSaveDto) throws IOException {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Notice notice = Notice.builder()
                .lecture(lecture)
                .title(noticeSaveDto.getTitle())
                .description(noticeSaveDto.getDescription())
                .postDate(LocalDateTime.now())
                .isPinned(false)
                .likes(noticeSaveDto.getLikes())
                .build();

        Long parentId = noticeRepository.save(notice).getId();

        List<Attachment> attachments = new ArrayList<>();
        for(MultipartFile multipartFile : noticeSaveDto.getFiles()) {
            attachments.add(attachmentService.save(AttachmentType.notice, parentId, multipartFile));
        }

        notice.setAttachments(attachments);
    }

    public void modifyNotice(Long noticeId, NoticeSaveDto noticeSaveDto) throws IOException {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        saveNotice(notice.getLecture().getId(), noticeSaveDto);
        deleteNotice(noticeId);
    }

    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        noticeRepository.delete(notice);
    }

    public List<NoticeListDto> findAllByLectureId(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.findAllByLectureOrderByCreatedAtDesc(lecture);
        return notices.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public NoticeDto findById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));

        return NoticeDto.builder()
                .title(notice.getTitle() != null ? notice.getTitle() : "제목 없음")
                .description(notice.getDescription() != null ? notice.getDescription() : "설명 없음")
                .filePaths(notice.getAttachments().stream().map(Attachment::getFilePath).toList())
                .postDate(notice.getPostDate() != null ? notice.getPostDate() : LocalDateTime.now())
                .isPinned(notice.getIsPinned() != null ? notice.getIsPinned() : false)
                .likes(notice.getLikes() != null ? notice.getLikes() : 0L)
                .build();
    }

    private NoticeListDto convertToDto(Notice notice) {
        NoticeListDto dto = new NoticeListDto();
        dto.setTitle(notice.getTitle() != null ? notice.getTitle() : "제목 없음");
        dto.setPostDate(notice.getCreatedAt());
        dto.setIsPinned(notice.getIsPinned() != null ? notice.getIsPinned() : false);
        return dto;
    }
}
