package com.ormee.server.notice.service;

import com.ormee.server.global.response.PageResponseDto;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.notice.domain.Notice;
import com.ormee.server.notice.dto.NoticeDto;
import com.ormee.server.notice.dto.NoticeListDto;
import com.ormee.server.notice.dto.NoticeSaveDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.attachment.domain.AttachmentType;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.attachment.service.AttachmentService;
import com.ormee.server.notice.repository.NoticeRepository;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;

    public NoticeService(NoticeRepository noticeRepository, LectureRepository lectureRepository, MemberRepository memberRepository, AttachmentService attachmentService) {
        this.noticeRepository = noticeRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.attachmentService = attachmentService;
    }

    public void saveNotice(Long lectureId, NoticeSaveDto noticeSaveDto, String username) throws IOException {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member author = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Notice notice = Notice.builder()
                .lecture(lecture)
                .author(author)
                .title(noticeSaveDto.getTitle())
                .description(noticeSaveDto.getDescription())
                .isPinned(false)
                .isDraft(noticeSaveDto.getIsDraft())
                .likes(0L)
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

    public List<NoticeListDto> loadSavedNotices(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notice> notices = noticeRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);

        return notices.stream().map(this::convertToDto).toList();
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
        dto.setLikes(notice.getLikes());
        return dto;
    }

    public NoticeDto entityToDto(Notice notice) {
        return NoticeDto.builder()
                .title(notice.getTitle() != null ? notice.getTitle() : "제목 없음")
                .description(notice.getDescription() != null ? notice.getDescription() : "설명 없음")
                .fileNames(notice.getAttachments().stream()
                        .map(attachment -> Objects.requireNonNullElse(attachment.getOriginalFileName(), attachment.getFileName())).toList())
                .filePaths(notice.getAttachments().stream().map(Attachment::getFilePath).toList())
                .postDate(notice.getPostDate() != null ? notice.getPostDate() : LocalDateTime.now())
                .isPinned(notice.getIsPinned() != null ? notice.getIsPinned() : false)
                .likes(notice.getLikes() != null ? notice.getLikes() : 0L)
                .build();
    }
}