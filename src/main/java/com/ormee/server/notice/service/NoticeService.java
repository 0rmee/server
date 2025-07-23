package com.ormee.server.notice.service;

import com.ormee.server.attachment.repository.AttachmentRepository;
import com.ormee.server.global.response.PageResponseDto;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.dto.AuthorDto;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.notice.domain.Notice;
import com.ormee.server.notice.dto.NoticeDto;
import com.ormee.server.notice.dto.NoticeListDto;
import com.ormee.server.notice.dto.NoticeSaveDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.attachment.service.AttachmentService;
import com.ormee.server.notice.repository.NoticeRepository;
import com.ormee.server.notification.domain.NotificationType;
import com.ormee.server.notification.dto.StudentNotificationRequestDto;
import com.ormee.server.notification.service.StudentNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;
    private final StudentNotificationService studentNotificationService;

    public NoticeService(NoticeRepository noticeRepository, LectureRepository lectureRepository, MemberRepository memberRepository, AttachmentRepository attachmentRepository, AttachmentService attachmentService, StudentNotificationService studentNotificationService) {
        this.noticeRepository = noticeRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
        this.studentNotificationService = studentNotificationService;
    }

    public void saveNotice(Long lectureId, NoticeSaveDto noticeSaveDto, String username) throws Exception {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member author = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Notice notice = Notice.builder()
                .lecture(lecture)
                .author(author)
                .title(noticeSaveDto.getTitle())
                .description(noticeSaveDto.getDescription())
                .isPinned(false)
                .isDraft(noticeSaveDto.getIsDraft())
                .build();

        notice = noticeRepository.save(notice);

        List<Attachment> attachments = noticeSaveDto.getFileIds().stream()
                .map(id -> attachmentRepository.findById(id)
                        .orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION)))
                .collect(Collectors.toList());

        for (Attachment attachment : attachments) {
            attachment.setParentId(notice.getId().toString());
            attachmentRepository.save(attachment);
        }

        notice.setAttachments(attachments);
        notice = noticeRepository.save(notice);

        studentNotificationService.create(lecture.getStudentLectures().stream().map(studentLecture -> studentLecture.getStudent().getId()).toList(),
                StudentNotificationRequestDto.builder()
                        .parentId(notice.getId())
                        .type(NotificationType.NOTICE)
                        .header(lecture.getTitle())
                        .title(notice.getTitle())
                        .body("새 공지가 등록되었어요. 지금 확인해 보세요!")
                        .content(notice.getDescription())
                .build());
    }

    public PageResponseDto<NoticeListDto> findAllByLectureId(Long lectureId, int page) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());

        Page<Notice> noticePage = noticeRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture, pageable);

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

    public List<NoticeListDto> getNotices(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);

        return notices.stream()
                .map(this::convertToDto)
                .toList();
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

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());

        Page<Notice> noticePage = noticeRepository.searchByLectureAndKeyword(lecture, keyword, pageable);

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

    public List<NoticeListDto> searchNotices(Long lectureId, String keyword) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.searchByLectureAndKeyword(lecture, keyword);

        return notices.stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<NoticeListDto> getDraftNotices(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(lecture);

        return notices.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NoticeListDto> loadSavedNotices(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);

        return notices.stream().map(this::convertToDto).toList();
    }

    public NoticeDto findById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        return entityToDto(notice);
    }


    public void modifyNotice(Long noticeId, NoticeSaveDto noticeSaveDto) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));

        if (noticeSaveDto.getTitle() != null) {
            notice.setTitle(noticeSaveDto.getTitle());
        }

        if (noticeSaveDto.getDescription() != null) {
            notice.setDescription(noticeSaveDto.getDescription());
        }

        List<Long> fileIds = noticeSaveDto.getFileIds() != null ? noticeSaveDto.getFileIds() : List.of();

        List<Attachment> existingAttachments = notice.getAttachments();

        List<Attachment> toRemove = existingAttachments.stream()
                .filter(att -> !fileIds.contains(att.getId()))
                .toList();

        for (Attachment att : toRemove) {
            existingAttachments.remove(att);
            attachmentService.delete(att.getId());
        }

        List<Long> existingIds = existingAttachments.stream()
                .map(Attachment::getId)
                .toList();

        for (Long fileId : fileIds) {
            if (!existingIds.contains(fileId)) {
                Attachment attachment = attachmentRepository.findById(fileId)
                        .orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION));
                existingAttachments.add(attachment);
                attachment.setParentId(notice.getId().toString());
                attachmentRepository.save(attachment);
            }
        }

        notice.setIsDraft(noticeSaveDto.getIsDraft());
        noticeRepository.save(notice);
    }

    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));

        for (Attachment attachment : notice.getAttachments()) {
            attachmentService.delete(attachment.getId());
        }

        notice.getLikes().clear();
        noticeRepository.delete(notice);
    }

    public void pin(Long noticeId, boolean isPinned) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));

        if(isPinned && noticeRepository.countAllByLectureAndIsPinnedTrue(notice.getLecture()) >= 3) {
            throw new CustomException(ExceptionType.NOTICE_PIN_FAILED_EXCEPTION);
        }

        notice.setIsPinned(isPinned);
        noticeRepository.save(notice);
    }

    private NoticeListDto convertToDto(Notice notice) {
        NoticeListDto dto = new NoticeListDto();
        dto.setAuthor(Optional.ofNullable(notice.getAuthor())
                .map(Member::getNickname)
                .orElse(notice.getLecture().getTeacher().getNickname()));
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle() != null ? notice.getTitle() : "제목 없음");
        dto.setPostDate(notice.getCreatedAt());
        dto.setIsPinned(notice.getIsPinned() != null ? notice.getIsPinned() : false);
        dto.setLikes(notice.getLikes() != null ? notice.getLikes().size() : 0);
        return dto;
    }

    public NoticeDto entityToDto(Notice notice) {
        return NoticeDto.builder()
                .title(notice.getTitle() != null ? notice.getTitle() : "제목 없음")
                .description(notice.getDescription() != null ? notice.getDescription() : "설명 없음")
                .fileNames(notice.getAttachments().stream()
                        .map(attachment -> Objects.requireNonNullElse(attachment.getOriginalFileName(), attachment.getFileName())).toList())
                .filePaths(notice.getAttachments().stream().map(Attachment::getFilePath).toList())
                .postDate(notice.getCreatedAt())
                .isPinned(notice.getIsPinned() != null ? notice.getIsPinned() : false)
                .likes(notice.getLikes() != null ? notice.getLikes().size() : 0)
                .build();
    }

    public NoticeDto getNotice(Long noticeId, String username) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        return NoticeDto.builder()
                .title(notice.getTitle())
                .description(notice.getDescription())
                .fileNames(notice.getAttachments().stream()
                        .map(attachment -> Objects.requireNonNullElse(attachment.getOriginalFileName(), attachment.getFileName())).toList())
                .filePaths(notice.getAttachments().stream().map(Attachment::getFilePath).toList())
                .postDate(notice.getCreatedAt())
                .isPinned(notice.getIsPinned())
                .likes(notice.getLikes() != null ? notice.getLikes().size() : 0)
                .isLiked(notice.getLikes().contains(student))
                .author(AuthorDto.builder()
                        .name(notice.getAuthor().getNickname())
                        .image(Optional.ofNullable(notice.getAuthor().getImage())
                                .map(Attachment::getFilePath)
                                .orElse(null))
                        .build())
                .build();
    }

    public void like(Long noticeId, String username) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(notice.getLikes().contains(student)) {
            throw new CustomException(ExceptionType.LIKE_ALREADY_EXIST_EXCEPTION);
        }

        notice.addLikes(student);
        noticeRepository.save(notice);
    }

    public void unLike(Long noticeId, String username) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(!notice.getLikes().contains(student)) {
            throw new CustomException(ExceptionType.LIKE_NOT_FOUND_EXCEPTION);
        }

        notice.removeLikes(student);
        noticeRepository.save(notice);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllExpiredDrafts() {
        List<Notice> notices = noticeRepository.findAllByIsDraftTrueAndCreatedAtBefore(LocalDateTime.now().minusDays(30));
        notices.forEach(notice -> deleteNotice(notice.getId()));
    }

    public void deleteByLecture(Lecture lecture) {
        List<Notice> notices = noticeRepository.findAllByLecture(lecture);
        notices.forEach(notice -> deleteNotice(notice.getId()));
    }
}