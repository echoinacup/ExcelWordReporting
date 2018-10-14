package com.echoinacup.word;

import com.echoinacup.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;

public class WordHandler {
    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
}
