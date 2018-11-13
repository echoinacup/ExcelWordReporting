package com.echoinacup.service.file;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

@Component
public class FileService {

    private static final String path = "templates/word/word_template_report_1.docx";
    private static final String path2 = "templates/word/word_template_report_2.docx";

    public InputStream readFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(path);
        return is;
    }
}
