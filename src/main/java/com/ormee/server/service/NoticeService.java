package com.ormee.server.service;

import com.ormee.server.dto.response.PageResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final LectureRepository lectureRepository;
    private final AttachmentService attachmentService;

    public NoticeService(NoticeRepository noticeRepository, LectureRepository lectureRepository, AttachmentService attachmentService) {
        this.noticeRepository = noticeRepository;
        this.lectureRepository = lectureRepository;
        this.attachmentService = attachmentService;
    }

    public void saveNotice(Long lectureId, NoticeSaveDto noticeSaveDto) throws IOException {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Notice notice = Notice.builder()
                .lecture(lecture)
                .title(noticeSaveDto.getTitle())
                .description(noticeSaveDto.getDescription())
                .isPinned(false)
                .isDraft(noticeSaveDto.getIsDraft())
                .build();

        notice = noticeRepository.save(notice);

        List<Attachment> attachments = new ArrayList<>();
        if (noticeSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : noticeSaveDto.getFiles()) {
                attachments.add(attachmentService.save(AttachmentType.NOTICE, notice.getId(), multipartFile));
            }
        }

        notice.setAttachments(attachments);
        noticeRepository.save(notice);
    }

    public PageResponseDto<NoticeListDto> findAllByLectureId(Long lectureId, int page) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<Notice> noticePage = noticeRepository.findAllByLectureOrderByCreatedAtDesc(lecture, pageable);

        List<NoticeListDto> content = noticePage.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PageResponseDto.<NoticeListDto>builder()
                .content(content)
                .totalPages(noticePage.getTotalPages())
                .totalElements(noticePage.getTotalElements())
                .currentPage(noticePage.getNumber() + 1)
                .build();
    }

    public List<NoticeListDto> getPinnedNotices(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.findAllByLectureAndIsPinnedTrueOrderByCreatedAtDesc(lecture);

        return notices.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PageResponseDto<NoticeListDto> findByKeyword(Long lectureId, String keyword, int page) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<Notice> noticePage = noticeRepository
                .searchByLectureAndKeyword(
                        lecture, keyword, pageable);

        List<NoticeListDto> content = noticePage.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PageResponseDto.<NoticeListDto>builder()
                .content(content)
                .totalPages(noticePage.getTotalPages())
                .totalElements(noticePage.getTotalElements())
                .currentPage(noticePage.getNumber() + 1)
                .build();
    }

    public List<NoticeListDto> getDraftNotices(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(lecture);

        return notices.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public NoticeDto findById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        return entityToDto(notice);
    }


    public void modifyNotice(Long noticeId, NoticeSaveDto noticeSaveDto) throws IOException {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));

        if (noticeSaveDto.getTitle() != null) {
            notice.setTitle(noticeSaveDto.getTitle());
        }
        if (noticeSaveDto.getDescription() != null) {
            notice.setDescription(noticeSaveDto.getDescription());
        }
        List<Attachment> existingAttachments = notice.getAttachments();
        if (existingAttachments != null) {
            existingAttachments.clear();
        }
        if (noticeSaveDto.getFiles() != null) {
            for (MultipartFile file : noticeSaveDto.getFiles()) {
                Attachment newAttachment = attachmentService.save(AttachmentType.NOTICE, notice.getId(), file);
                notice.getAttachments().add(newAttachment);
            }
        }

        notice.setIsDraft(noticeSaveDto.getIsDraft());
        noticeRepository.save(notice);
    }

    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        noticeRepository.delete(notice);
    }

    public void pin(Long noticeId, boolean isPinned) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        notice.setIsPinned(isPinned);
        noticeRepository.save(notice);
    }

    private NoticeListDto convertToDto(Notice notice) {
        NoticeListDto dto = new NoticeListDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle() != null ? notice.getTitle() : "제목 없음");
        dto.setPostDate(notice.getCreatedAt());
        dto.setIsPinned(notice.getIsPinned() != null ? notice.getIsPinned() : false);
        return dto;
    }

    public NoticeDto entityToDto(Notice notice) {
        return NoticeDto.builder()
                .title(notice.getTitle() != null ? notice.getTitle() : "제목 없음")
                .description(notice.getDescription() != null ? notice.getDescription() : "설명 없음")
                .filePaths(notice.getAttachments().stream().map(Attachment::getFilePath).toList())
                .postDate(notice.getPostDate() != null ? notice.getPostDate() : LocalDateTime.now())
                .isPinned(notice.getIsPinned() != null ? notice.getIsPinned() : false)
                .likes(notice.getLikes() != null ? notice.getLikes() : 0L)
                .build();
    }
}
