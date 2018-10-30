package com.echoinacup.main;


import com.echoinacup.domain.Company;
import com.echoinacup.service.excel.ExcelHandler;
import com.echoinacup.service.word.WordHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.List;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        ExcelHandler excelHandler = context.getBean(ExcelHandler.class);
        WordHandler wordHandler = context.getBean(WordHandler.class);


        Button button = new Button("Choose and Save");
        Label chosen = new Label();
        Label resultDir = new Label();
        button.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(primaryStage);
            if (file != null) {
                String fileAsString = file.toString();
                chosen.setText(fileAsString);
            } else {
                chosen.setText(null);
            }
        });


        VBox layout = new VBox(10, button, chosen, resultDir);
        chosen.textProperty().addListener((ov, t, t1) -> {

            File file = new File(chosen.getText());
            String parentPath = file.getParent();
            List<Company> companies = excelHandler.processExcelBasicInfoSheet(file);
            List<Company> resultCompanies = excelHandler.processExcelTemplateSub(companies, file);

            for (Company c : resultCompanies) {
                wordHandler.processWordTemplate(c, parentPath);
            }
            resultDir.setText("Please see directory for results " + parentPath);
        });


        layout.setMinWidth(400);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}

