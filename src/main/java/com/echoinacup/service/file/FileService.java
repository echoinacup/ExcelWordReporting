package com.echoinacup.service.file;

import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;

@Component
public class FileService {

    public File readFile(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = null;
        try {
            file = new File(classLoader.getResource(path).toURI());
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }

        return file;
    }
}
