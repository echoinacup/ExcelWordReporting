package com.echoinacup.main;


import com.echoinacup.domain.Company;
import com.echoinacup.domain.Project;
import com.echoinacup.service.excel.ExcelHandler;
import com.echoinacup.service.word.WordHandler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;


public class Main extends Application {

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String pathToTemplateOne = "templates/word/word_template_report_1.docx";
    private static final String pathToTemplateTwo = "templates/word/word_template_report_2.docx";

    @Override
    public void start(Stage primaryStage) {

        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        ExcelHandler excelHandler = context.getBean(ExcelHandler.class);
        WordHandler wordHandler = context.getBean(WordHandler.class);

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Look, an Exception Dialog");
        alert.setContentText("Format of the value is incorrect");


        Button buttonForReportOne = new Button("Process Report One");
        Button buttonForReportTwo = new Button("Process Report TwO");
        Label chosen = new Label();
        Label chosen2 = new Label();
        Label resultDirLbl = new Label();

        buttonForReportOne.setOnAction(event -> chooseFileAndSetPath(primaryStage, chosen));
        buttonForReportTwo.setOnAction(event -> chooseFileAndSetPath(primaryStage, chosen2));

        HBox hBox = new HBox(10, buttonForReportOne, buttonForReportTwo);
        VBox layout = new VBox(10, hBox, chosen, resultDirLbl);

        chosen.textProperty().addListener((ov, t, t1) -> {

            LOGGER.info("report 1 creating");
            File file = new File(chosen.getText());
            String parentPath = file.getParent();
            try {
                runReportOneCreating(excelHandler, wordHandler, resultDirLbl, file, parentPath);
            } catch (NumberFormatException nfe) {
                createExceptionForMessage(alert, nfe);
            }
        });

        chosen2.textProperty().addListener((ov, t, t1) -> {
            resultDirLbl.setText("");
            LOGGER.info("report 2 creating");
            File file = new File(chosen2.getText());
            String parentPath = file.getParent();
            try {
                runReportTwoCreating(excelHandler, wordHandler, resultDirLbl, file, parentPath);
            } catch (NumberFormatException nfe) {
                createExceptionForMessage(alert, nfe);
            }
        });

        layout.setMinWidth(400);
        hBox.setAlignment(Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();

    }

    private void runReportOneCreating(ExcelHandler excelHandler, WordHandler wordHandler, Label resultDir, File file, String parentPath) throws NumberFormatException {
        LOGGER.info("report one creating ...");
        List<Company> companies = excelHandler.processExcelBasicInfoSheetIntoCompanies(file);
        List<Company> resultCompanies = excelHandler.processExcelTemplateSubsidiariesForCompanies(companies, file);

        for (Company c : resultCompanies) {
            wordHandler.processWordTemplateForCompanies(c, pathToTemplateOne, parentPath);
        }
        resultDir.setText("Please see directory for results " + parentPath);
    }

    private void runReportTwoCreating(ExcelHandler excelHandler, WordHandler wordHandler, Label resultDir, File file, String parentPath) throws NumberFormatException {
        LOGGER.info("report two creating ...");
        List<Project> projects = excelHandler.processExcelBasicInfoSheetIntoProjects(file);
        List<Project> resultProjects = excelHandler.processExcelTemplateSubsidiariesForProjects(projects, file);
        for (Project c : resultProjects) {
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


    private void createExceptionForMessage(Alert alert, Exception ex) {
        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

