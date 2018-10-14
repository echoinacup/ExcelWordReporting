package com.echoinacup.main;

import com.echoinacup.excel.ExcelHandler;
import com.echoinacup.file.FileService;
import com.echoinacup.word.WordHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.echoinacup")
public class MainConfiguration {

    @Bean
    public ExcelHandler excelHandler() {
        return new ExcelHandler();
    }

    @Bean
    public WordHandler wordHandler() {
        return new WordHandler();
    }

    @Bean
    public FileService fileService() {
        return new FileService();
    }
}
