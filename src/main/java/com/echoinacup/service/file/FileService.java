package com.echoinacup.service.file;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

@Component
public class FileService {

    public InputStream readFile(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(path);
        return is;
    }
}
