package com.echoinacup.main;

import com.echoinacup.excel.ExcelHandler;
import com.echoinacup.file.FileService;
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


public class Main { //} extends Application {

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
//        button.setOnAction(e -> {
//            fileChooser.showOpenDialog(primaryStage);
//        });
//
//        VBox vBox = new VBox(button);
//        primaryStage.setScene(new Scene(vBox, 960, 600));
//        primaryStage.show();
//    }


    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        ExcelHandler excelHandler = context.getBean(ExcelHandler.class);

        excelHandler.readExcelTemplateSub();
//        excelHandler.readExcelTemplate();
//        launch(args);

    }
}
