package com.ormee.server.service.attachment;

import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Attachment;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AttachmentService {
    private final S3Service s3Service;
    private final AttachmentRepository attachmentRepository;

    public AttachmentService(S3Service s3Service, AttachmentRepository attachmentRepository) {
        this.s3Service = s3Service;
        this.attachmentRepository = attachmentRepository;
    }

    public Long saveAttachment(AttachmentType type, Long parentId, MultipartFile multipartFile) throws IOException {
        String filePath = s3Service.uploadFile(multipartFile);

        Attachment attachment = Attachment.builder()
                .type(type)
                .parentId(parentId.toString())
                .filePath(filePath)
                .fileName(LocalDate.now() + multipartFile.getOriginalFilename())
                .fileSize(multipartFile.getSize())
                .dueDate(LocalDateTime.now().plusMonths(1))
                .build();

        return attachmentRepository.save(attachment).getId();
    }

    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION));

        s3Service.deleteFile(attachment.getFileName());
        attachmentRepository.delete(attachment);
    }
}
