package com.echoinacup.service.word;

import com.echoinacup.service.file.FileService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class WordHandler {

    private final static Logger LOGGER = Logger.getLogger(WordHandler.class.getName());

    private static final String pathToWordTemplate = "templates/word/word_template.docx";
    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }


    public void processWordTemplate(Map<String, String> placeHolderMap) {
        File file = fileService.readFile(pathToWordTemplate);
        try {
            for (String placeHolderKey : placeHolderMap.keySet()) {

            }
            XWPFDocument wordTemplate = new XWPFDocument(OPCPackage.open(file));
            replacePlaceHolder(wordTemplate, "Title", "Test title");

        } catch (IOException | InvalidFormatException e) {
            LOGGER.info(e.getMessage());
        }


    }

    private void replacePlaceHolder(XWPFDocument xwpfDocument, String placeHolder, String replacement) throws FileNotFoundException {
        XWPFDocument doc = xwpfDocument;
        FileOutputStream out = new FileOutputStream(new File("output.docx")); //TODO set the name of the file name

        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(placeHolder)) {
                        text = text.replace(placeHolder, replacement);
                        r.setText(text, 0);
                    }
                }
            }
        }
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(placeHolder)) {
                                text = text.replace(placeHolder, replacement);
                                r.setText(text, 0);
                            }
                        }
                    }
                }
            }
        }
        try {

            doc.write(out);
            out.close();

        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
    }


}