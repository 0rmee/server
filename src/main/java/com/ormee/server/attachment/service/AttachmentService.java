package com.ormee.server.attachment.service;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.attachment.domain.AttachmentType;
import com.ormee.server.attachment.dto.UploadFileResponse;
import com.ormee.server.attachment.repository.AttachmentRepository;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.repository.HomeworkRepository;
import com.ormee.server.homework.domain.HomeworkSubmit;
import com.ormee.server.homework.repository.HomeworkSubmitRepository;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.notice.domain.Notice;
import com.ormee.server.notice.repository.NoticeRepository;
import com.ormee.server.question.domain.Answer;
import com.ormee.server.question.repository.AnswerRepository;
import com.ormee.server.question.domain.Question;
import com.ormee.server.question.repository.QuestionRepository;
import com.ormee.server.quiz.domain.Problem;
import com.ormee.server.quiz.repository.ProblemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AttachmentService {
    private final S3Service s3Service;
    private final AttachmentRepository attachmentRepository;
    private final ProblemRepository problemRepository;
    private final NoticeRepository noticeRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkSubmitRepository homeworkSubmitRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;

    public AttachmentService(S3Service s3Service, AttachmentRepository attachmentRepository, ProblemRepository problemRepository, NoticeRepository noticeRepository, HomeworkRepository homeworkRepository, HomeworkSubmitRepository homeworkSubmitRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, MemberRepository memberRepository) {
        this.s3Service = s3Service;
        this.attachmentRepository = attachmentRepository;
        this.problemRepository = problemRepository;
        this.noticeRepository = noticeRepository;
        this.homeworkRepository = homeworkRepository;
        this.homeworkSubmitRepository = homeworkSubmitRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.memberRepository = memberRepository;
    }

    public Attachment save(AttachmentType type, Long parentId, MultipartFile multipartFile) throws IOException {
        UploadFileResponse fileResponse = s3Service.uploadFile(multipartFile);

        Attachment attachment = Attachment.builder()
                .type(type)
                .parentId(parentId.toString())
                .originalFileName(multipartFile.getOriginalFilename())
                .filePath(fileResponse.getFileUrl())
                .fileName(fileResponse.getFileName())
                .fileSize(multipartFile.getSize())
                .dueDate(LocalDateTime.now().plusMonths(1))
                .build();

        return attachmentRepository.save(attachment);
    }

    @Transactional
    public void delete(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION));

        deleteByType(attachment);

        s3Service.deleteFile(attachment.getFileName());
        attachmentRepository.delete(attachment);
    }

    private void deleteByType(Attachment attachment) {
        AttachmentType type = Objects.requireNonNull(attachment.getType());

        switch (type) {
            case QUIZ:
                if (!attachment.getParentId().equals("-1")) {
                    Problem problem = problemRepository.findById(Long.valueOf(attachment.getParentId()))
                            .orElseThrow(() -> new CustomException(ExceptionType.PROBLEM_NOT_FOUND_EXCEPTION));
                    problem.removeAttachment(attachment);
                }
                break;

            case NOTICE:
                if (!attachment.getParentId().equals("-1")) {
                    Notice notice = noticeRepository.findById(Long.valueOf(attachment.getParentId()))
                            .orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
                    notice.removeAttachment(attachment);
                }
                break;

            case HOMEWORK:
                if (!attachment.getParentId().equals("-1")) {
                    Homework homework = homeworkRepository.findById(Long.valueOf(attachment.getParentId()))
                            .orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));
                    homework.removeAttachment(attachment);
                }
                break;

            case HOMEWORK_SUBMIT:
                if (!attachment.getParentId().equals("-1")) {
                    HomeworkSubmit submit = homeworkSubmitRepository.findById(Long.valueOf(attachment.getParentId()))
                            .orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));
                    submit.removeAttachment(attachment);
                }
                break;

            case QUESTION:
                if (!attachment.getParentId().equals("-1")) {
                    Question question = questionRepository.findById(Long.valueOf(attachment.getParentId()))
                            .orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
                    question.removeAttachment(attachment);
                }
                break;

            case ANSWER:
                if (!attachment.getParentId().equals("-1")) {
                    Answer answer = answerRepository.findById(Long.valueOf(attachment.getParentId()))
                            .orElseThrow(() -> new CustomException(ExceptionType.ANSWER_NOT_FOUND_EXCEPTION));
                    answer.removeAttachment(attachment);
                }
                break;

            case TEACHER_IMAGE:
                if (!attachment.getParentId().equals("-1")) {
                    Member teacher = memberRepository.findById(Long.valueOf(attachment.getParentId()))
                            .orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
                    teacher.setImage(attachment);
                }
                break;

            default:
                throw new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION);
        }
    }
}
