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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.List;


public class Main extends Application {

    private static final String pathToTemplateOne = "templates/word/word_template_report_1.docx";
    private static final String pathToTemplateTwo = "templates/word/word_template_report_2.docx";

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
        buttonForReportTwo.setOnAction(event -> chooseFileAndSetPath(primaryStage, chosen2));

        HBox hBox = new HBox(10, buttonForReportOne, buttonForReportTwo);
        VBox layout = new VBox(10, hBox, chosen, resultDir);

        chosen.textProperty().addListener((ov, t, t1) -> {
            System.out.println("report 1 creating");
            File file = new File(chosen.getText());
            String parentPath = file.getParent();
            runReportOneCreating(excelHandler, wordHandler, resultDir, file, parentPath);
        });

        chosen2.textProperty().addListener((ov, t, t1) -> {
            System.out.println("report 2 creating");
            File file = new File(chosen2.getText());
            String parentPath = file.getParent();
            runReportTwoCreating(excelHandler, wordHandler, resultDir, file, parentPath);
        });

        layout.setMinWidth(400);
        hBox.setAlignment(Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();

    }

    private void runReportOneCreating(ExcelHandler excelHandler, WordHandler wordHandler, Label resultDir, File file, String parentPath) {
        System.out.println("report one creating ...");
        List<Company> companies = excelHandler.processExcelBasicInfoSheetIntoCompanies(file);
        List<Company> resultCompanies = excelHandler.processExcelTemplateSubsidiariesForCompanies(companies, file);

        for (Company c : resultCompanies) {
            wordHandler.processWordTemplateForCompanies(c, pathToTemplateOne, parentPath);
        }
        resultDir.setText("Please see directory for results " + parentPath);
    }

    private void runReportTwoCreating(ExcelHandler excelHandler, WordHandler wordHandler, Label resultDir, File file, String parentPath) {
        System.out.println("report two creating ...");
        List<Project> projects = excelHandler.processExcelBasicInfoSheetIntoProjects(file);
        List<Project> resultProjects = excelHandler.processExcelTemplateSubsidiariesForProjects(projects, file);
        for (Project c : resultProjects) {
            System.out.println(projects);
            wordHandler.processWordTemplateForProjects(c, pathToTemplateTwo, parentPath);
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

