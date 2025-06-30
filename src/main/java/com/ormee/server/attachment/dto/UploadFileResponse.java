package com.ormee.server.attachment.dto;

import lombok.Getter;

@Getter
public class UploadFileResponse {
    private final String fileName;
    private final String fileUrl;

    public UploadFileResponse(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}

