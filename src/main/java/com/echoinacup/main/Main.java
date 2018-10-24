package com.echoinacup.main;

import com.echoinacup.domain.Company;


import java.io.File;

import com.echoinacup.service.excel.ExcelHandler;
import com.echoinacup.service.word.WordHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        ExcelHandler excelHandler = context.getBean(ExcelHandler.class);
        WordHandler wordHandler = context.getBean(WordHandler.class);

        Button button = new Button("Choose");
        Label chosen = new Label();
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

        VBox layout = new VBox(10, button, chosen);
        chosen.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                System.out.println(chosen.getText());
            }
        });
        System.out.println(chosen.getText());
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

