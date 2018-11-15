package com.echoinacup.main;


import com.echoinacup.domain.Company;
import com.echoinacup.domain.Project;
import com.echoinacup.service.excel.ExcelHandler;
import com.echoinacup.service.word.WordHandler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    private static final String REPORT_ONE = "First Report";
    private static final String REPORT_TWO = "Second Report";

    @Override
    public void start(Stage primaryStage) {

        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        ExcelHandler excelHandler = context.getBean(ExcelHandler.class);
        WordHandler wordHandler = context.getBean(WordHandler.class);

        Button buttonForReportOne = new Button("Process Report One");
        Button buttonForReportTwo = new Button("Process Report TwO");
        Label chosen = new Label();
        Label chosen2 = new Label();
        Label resultDir = new Label();

        buttonForReportOne.setOnAction(event -> chooseFileAndSetPath(primaryStage, chosen));
//        buttonForReportOne.setOnAction(event -> chooseFileAndSetPath(primaryStage, chosen2));

        HBox hBox = new HBox(10, buttonForReportOne, buttonForReportTwo);
        VBox layout = new VBox(10, hBox, chosen, chosen2, resultDir);

        chosen.textProperty().addListener((ov, t, t1) -> {
            System.out.println("report 1 creating");
            File file = new File(chosen.getText());
            String parentPath = file.getParent();
            runReportOneCreating(excelHandler, wordHandler, resultDir, file, parentPath);
        });

//        chosen2.textProperty().addListener((ov, t, t1) -> {
//            System.out.println("report 2 creating");
//            File file = new File(chosen2.getText());
//            String parentPath = file.getParent();
//        });

        layout.setMinWidth(400);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();

    }

    private void runReportOneCreating(ExcelHandler excelHandler, WordHandler wordHandler, Label resultDir, File file, String parentPath) {
        List<Company> companies = excelHandler.processExcelBasicInfoSheetIntoCompanies(file);
        List<Company> resultCompanies = excelHandler.processExcelTemplateSubsidiaries(companies, file);

        for (Company c : resultCompanies) {
            wordHandler.processWordTemplate(c, parentPath);
        }
        resultDir.setText("Please see directory for results " + parentPath);
    }

    private void chooseFileAndSetPath(Stage primaryStage, Label chosen) {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) {
            String fileAsString = file.toString();
            chosen.setText(fileAsString);
        } else {
            chosen.setText(null);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

