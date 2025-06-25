package com.ormee.server.service.attachment;

import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Attachment;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.model.Problem;
import com.ormee.server.repository.AttachmentRepository;
import com.ormee.server.repository.ProblemRepository;
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

    public AttachmentService(S3Service s3Service, AttachmentRepository attachmentRepository, ProblemRepository problemRepository) {
        this.s3Service = s3Service;
        this.attachmentRepository = attachmentRepository;
        this.problemRepository = problemRepository;
    }

    public Attachment save(AttachmentType type, Long parentId, MultipartFile multipartFile) throws IOException {
        String filePath = s3Service.uploadFile(multipartFile);

        Attachment attachment = Attachment.builder()
                .type(type)
                .parentId(parentId.toString())
                .filePath(filePath)
                .fileName(LocalDateTime.now() + multipartFile.getOriginalFilename())
                .fileSize(multipartFile.getSize())
                .dueDate(LocalDateTime.now().plusMonths(1))
                .build();

        return attachmentRepository.save(attachment);
    }

    public void delete(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION));

        deleteByType(attachment);

        s3Service.deleteFile(attachment.getFileName());
        attachmentRepository.delete(attachment);
    }

    private void deleteByType(Attachment attachment) {
        if (Objects.requireNonNull(attachment.getType()) == AttachmentType.QUIZ) {
            if (!attachment.getParentId().equals("-1")) {
                Problem problem = problemRepository.findById(Long.valueOf(attachment.getParentId())).orElseThrow(() -> new CustomException(ExceptionType.PROBLEM_NOT_FOUND_EXCEPTION));
                problem.removeAttachment(attachment);
            }
        }
    }
}
