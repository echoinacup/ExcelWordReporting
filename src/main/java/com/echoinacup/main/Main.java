package com.echoinacup.main;

import com.echoinacup.service.excel.ExcelHandler;
import com.echoinacup.service.word.WordHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;


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
        WordHandler wordHandler = context.getBean(WordHandler.class);
//
//        Map<String, String> map = new HashMap<>();
//
//        wordHandler.processWordTemplate(map);

//        excelHandler.processExcelTemplateSub();
        excelHandler.processExcelBasicInfoSheet();
//        launch(args);

    }
}
