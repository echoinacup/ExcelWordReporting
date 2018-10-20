package com.echoinacup.file;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileService {

    private static final String pathToExcelTemplate = "";
    private static final String pathToWordTemplate = "";


    public File readFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("templates/excel/test_example.xlsx").getFile());

        return file;
    }


    public void wroteFile() {

    }
}
