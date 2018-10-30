package com.echoinacup.service.word;

import com.echoinacup.domain.Company;
import com.echoinacup.service.file.FileService;
import com.google.common.collect.Lists;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.echoinacup.utils.HelpUtils.formatString;

public class WordHandler {


    private final static Logger LOGGER = Logger.getLogger(WordHandler.class.getName());

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }


    public void processWordTemplate(Company company, String parentPath) {

        XWPFDocument resultReport;
        Map<String, String> placeholderMap = companyToWordTransformator(company);
        try {
            resultReport = new XWPFDocument(fileService.readFile());

            replacePlaceHolder(resultReport, placeholderMap, company, parentPath);

        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
    }


    private void addRowsToTable(XWPFTable tbl, List<List<String>> subSets) {
        for (List<String> subSet : subSets) {
            XWPFTableRow rowTemplate = tbl.getRow(1);
            XWPFTableRow oldRow = rowTemplate;
            CTRow ctrow = null;
            try {
                ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());

            } catch (XmlException | IOException e) {
                System.out.println(e.getMessage());
            }
            XWPFTableRow newRow = new XWPFTableRow(ctrow, tbl);
            for (int i = 0; i < subSet.size(); i++) {
                newRow.getCell(i).setText(subSet.get(i));
            }
            tbl.addRow(newRow);

        }
        tbl.removeRow(1);
    }

    private XWPFDocument replacePlaceHolder(XWPFDocument xwpfDocument,
                                            Map<String, String> placeholderMap,
                                            Company company,
                                            String path) throws
            FileNotFoundException {
        XWPFDocument resultReport = xwpfDocument;
        String name = company.getCorporateName().isEmpty() ? "" : company.getCorporateName();
        FileOutputStream out = new FileOutputStream(new File(path + File.separator + name + " report.docx")); //TODO set the name of the file name

        for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {

            String placeHolder = entry.getKey();
            String replacement = entry.getValue();

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
            XWPFTable tableSubsidiaries = resultReport.getTables().get(1);
            XWPFTable tableActivities = resultReport.getTables().get(2);
            XWPFTable tableDataSource = resultReport.getTables().get(3);
            List<List<String>> subSets = Lists.partition(company.getSubsidiaries(), 4);
            List<List<String>> subSetsActivity = Lists.partition(company.getActivities(), 3);
            List<List<String>> subSetsDataSources = Lists.partition(company.getDataSources(), 1);
            addRowsToTable(tableSubsidiaries, subSets);
            addRowsToTable(tableActivities, subSetsActivity);
            addRowsToTable(tableDataSource, subSetsDataSources);

            resultReport.write(out);
            out.close();

        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return resultReport;
    }

    private Map<String, String> companyToWordTransformator(Company company) {
        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("title", company.getCorporateName());
        placeholderMap.put("corpName", company.getCorporateName());
        placeholderMap.put("paidUpCapital", formatString(company.getPaidupCapital()));
        placeholderMap.put("shareParValue", company.getShareParValue());
        placeholderMap.put("numberOfShares", formatString(company.getNumberOfShares()));
        placeholderMap.put("legalStructure", company.getLegalStructure());
        placeholderMap.put("currency", company.getCurrency());
        placeholderMap.put("inceptionDate", company.getInceptionDate());
        placeholderMap.put("sector", company.getSector());
        placeholderMap.put("country", company.getCountry());
        placeholderMap.put("status", company.getStatus().status());
        placeholderMap.put("numOfEmpl", company.getNumberOfEmployees());
        placeholderMap.put("listingDate", company.getListingDate());
        placeholderMap.put("exchange", company.getStockExchangeName());
        placeholderMap.put("linkToStockProfile", company.getLinkToExchange());
        placeholderMap.put("phone", company.getPhone());
        placeholderMap.put("email", company.getContactEmail());
        placeholderMap.put("website", company.getWebsite());
        placeholderMap.put("address", company.getCompanyAddress());
        placeholderMap.put("link", company.getLinkedIn());
        placeholderMap.put("twit", company.getTwitter());
        placeholderMap.put("fb", company.getFacebook());
        placeholderMap.put("inst", company.getInstagram());

        String desc = fillDescription(company.getStatus(),
                company.getInceptionDate(),
                company.getCity(),
                company.getCountry(),
                company.getCorporateName(),
                company.getLegalStructure(),
                company.getSector(),
                company.getProductsServicesOffered(),
                company.getDetailsOfServicesOffered(),
                company.getStockExchangeName(),
                company.getListingDate());
        placeholderMap.put("desc", desc);

        return placeholderMap;

    }


    private String fillDescription(Status status,
                                   String inceptionDate,
                                   String city,
                                   String country,
                                   String corporateName,
                                   String legalStructure,
                                   String sector,
                                   String productsServicesOffered,
                                   String detailsOfServicesOffered,
                                   String stockExchangeName,
                                   String listingDate) {

        String publicDescription = "Incorporated in " + inceptionDate +
                " with headquarters in " + city + ", " + country + ". " + corporateName + " is a " + legalStructure + " company " +
                "operating within the " + sector + "." +
                " The company is engaged in " + productsServicesOffered + ". The Company provides " + detailsOfServicesOffered + "." +
                " The company has investments and subsidiaries operating in Country, Country, Country and Country." +
                " " + corporateName + " is a public company listed on the " + stockExchangeName + " since " + listingDate + ".";

        String privateDescription = "Incorporated in " + inceptionDate +
                " with headquarters in " + city + ", " + country + ". " + corporateName + " is a " + legalStructure + " company operating" +
                " within the " + sector + "." +
                " The company is engaged in " + productsServicesOffered + ". The Company provides " + detailsOfServicesOffered + " . " +
                " The company has investments and subsidiaries operating in Country, Country, Country and Country." +
                "  " + corporateName + " is a private company.";
        return status == Status.PUBLIC ? publicDescription : privateDescription;

    }


}