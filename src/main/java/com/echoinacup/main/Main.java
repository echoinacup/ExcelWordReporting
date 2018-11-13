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

    @Override
    public void start(Stage primaryStage) {

        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        ExcelHandler excelHandler = context.getBean(ExcelHandler.class);
        WordHandler wordHandler = context.getBean(WordHandler.class);


        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("First Report");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Second Report");
        rb2.setToggleGroup(group);

        Button buttonForReportOne = new Button("Process Report");
        Label chosen = new Label();
        Label resultDir = new Label();

        buttonForReportOne.setOnAction(event -> chooseFileAndSetPath(primaryStage, chosen));
        HBox hBox = new HBox(10, rb1, rb2);
        VBox layout = new VBox(10, hBox, buttonForReportOne, chosen, resultDir);

        //TODO add first by default  and second add click handler

        chosen.textProperty().addListener((ov, t, t1) -> {

            File file = new File(chosen.getText());

            String parentPath = file.getParent();
            //----- separate into two parts
            List<Company> companies = new ArrayList<>();
            List<Project> projects = new ArrayList<>();

            //TODO play with generics

            excelHandler.processExcelBasicInfoSheetPerReport(file, true, companies, projects);
            excelHandler.processExcelBasicInfoSheetPerReport(file, false, companies, projects);

//            List<Company> companies = excelHandler.processExcelBasicInfoSheetIntoCompanies(file, is);
            List<Company> resultCompanies = excelHandler.processExcelTemplateSubsidiaries(companies, file);

            for (Company c : resultCompanies) {
                wordHandler.processWordTemplate(c, parentPath);
            }
            resultDir.setText("Please see directory for results " + parentPath);
        });

        hBox.setAlignment(Pos.CENTER);
        layout.setMinWidth(400);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();

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

