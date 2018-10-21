package com.echoinacup.service.file;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileService {




    public File readFile(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(path).getFile());

        return file;
    }


    public void wroteFile() {
        System.out.println("wrote File");
    }
}
