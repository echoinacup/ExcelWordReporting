package com.echoinacup.main;

import com.echoinacup.domain.Company;
import com.echoinacup.service.excel.ExcelHandler;
import com.echoinacup.service.word.WordHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.application.Application.launch;


public class Main {// extends Application  {

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//
//        Parent root = FXMLLoader.load(getClass().getResource("/ui_layout.fxml"));
//        primaryStage.setTitle("Excel processor");
//
//
//        FileChooser fileChooser = new FileChooser();
//
//        Button button = new Button("Select File");
//
//        button.setOnAction(e -> {
//            File file = fileChooser.showOpenDialog(primaryStage);
//
//        });
//
//        VBox vBox = new VBox(button);
//        primaryStage.setScene(new Scene(vBox, 200, 200));
//        primaryStage.show();
//    }


    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        ExcelHandler excelHandler = context.getBean(ExcelHandler.class);
        WordHandler wordHandler = context.getBean(WordHandler.class);


//        List<Company> companies = new ArrayList<>();
        List<Company> companies = excelHandler.processExcelBasicInfoSheet();
        List<Company> list = excelHandler.processExcelTemplateSub(companies);

        list.forEach(item -> wordHandler.processWordTemplate(item));

//        launch(args);

    }
}
