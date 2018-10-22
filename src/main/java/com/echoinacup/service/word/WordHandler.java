package com.echoinacup.service.word;

import com.echoinacup.domain.Company;
import com.echoinacup.service.file.FileService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
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


    public void processWordTemplate(Company company) {

        File file = fileService.readFile(pathToWordTemplate);
        XWPFDocument resultReport;
        Map<String, String> placeholderMap = companyToWordTransformator(company);
        try {
            resultReport = new XWPFDocument(OPCPackage.open(file));

//            List<XWPFParagraph> paragraphs = resultReport.getParagraphs();
//
//            List<XWPFTable> l = resultReport.getTables();
//
//            for (XWPFTable tbl : resultReport.getTables()) {
//                for (XWPFTableRow row : tbl.getRows()) {
//                    for (XWPFTableCell cell : row.getTableCells()) {
//                        for (XWPFParagraph p : cell.getParagraphs()) {
//                            for (XWPFRun r : p.getRuns()) {
//                                String text = r.getText(0);
//                                System.out.println(text);
//                            }
//                        }
//                    }
//                }
//            }


//            for (XWPFParagraph para : paragraphs) {
//                System.out.println(para.getText());
//            }

            replacePlaceHolder(resultReport, placeholderMap);

        } catch (IOException | InvalidFormatException e) {
            LOGGER.info(e.getMessage());
        }


    }

    private void replacePlaceHolder(XWPFDocument xwpfDocument, Map<String, String> placeholderMap) throws
            FileNotFoundException {
        XWPFDocument resultReport = xwpfDocument;
        FileOutputStream out = new FileOutputStream(new File("output.docx")); //TODO set the name of the file name

        for (String placeHolderKey : placeholderMap.keySet()) {

            String placeHolder = placeHolderKey;
            String replacement = placeholderMap.get(placeHolderKey);

            for (XWPFParagraph p : resultReport.getParagraphs()) {
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
            for (XWPFTable tbl : resultReport.getTables()) {
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
        }
        try {
            resultReport.write(out);
            out.close();

        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
    }


    private Map<String, String> companyToWordTransformator(Company company) {
        Map<String, String> placholdeMap = new LinkedHashMap<>();
        placholdeMap.put("title", company.getCorporateName());
        placholdeMap.put("corpName", company.getCorporateName());
        placholdeMap.put("paidUpCapital", company.getPaidupCapital());
        placholdeMap.put("shareParValue", company.getShareParValue());
        placholdeMap.put("numberOfShares", company.getNumberOfShares());
        placholdeMap.put("legalStructure", company.getLegalStructure());
        placholdeMap.put("currency", company.getCurrency());
        placholdeMap.put("inceptionDate", company.getInceptionDate());
        placholdeMap.put("sector", company.getSector());
        placholdeMap.put("country", company.getCountry());
        placholdeMap.put("status", company.getStatus().status());
        placholdeMap.put("numOfEmpl", company.getNumberOfEmployees());
        placholdeMap.put("listingDate", company.getListingDate());
        placholdeMap.put("exchange", company.getStockExchangeName());
        placholdeMap.put("phone", company.getPhone());
        placholdeMap.put("email", company.getContactEmail());
        placholdeMap.put("website", company.getWebsite());
        placholdeMap.put("address", company.getCompanyAddress());
        placholdeMap.put("link", company.getLinkedIn());
        placholdeMap.put("twit", company.getTwitter());
        placholdeMap.put("fb", company.getFacebook());
        placholdeMap.put("inst", company.getInstagram());

        return placholdeMap;

    }


}