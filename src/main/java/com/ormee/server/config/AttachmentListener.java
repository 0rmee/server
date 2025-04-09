package com.ormee.server.config;

import com.ormee.server.model.Attachment;
import com.ormee.server.service.attachment.S3Service;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttachmentListener {

    private S3Service s3Service;

    public AttachmentListener() {
    }

    @Autowired
    public AttachmentListener(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PreRemove
    public void deleteAttachmentFromS3(Attachment attachment) {
        if(attachment.getFilePath() != null) {
            s3Service.deleteFile(attachment.getFileName());
        }
    }
}
