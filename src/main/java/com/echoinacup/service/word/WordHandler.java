package com.echoinacup.service.word;

import com.echoinacup.domain.Company;
import com.echoinacup.domain.Project;
import com.echoinacup.service.file.FileService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.echoinacup.service.word.WordUtils.*;
import static com.echoinacup.utils.HelpUtils.formatStringToLatitude;
import static com.echoinacup.utils.HelpUtils.formatStringToNumber;
import static com.echoinacup.utils.HelpUtils.formatToSqrMeters;

public class WordHandler {


    private final static Logger LOGGER = Logger.getLogger(WordHandler.class.getName());

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }


    public void processWordTemplateForCompanies(Company company, String templatePath, String parentPath) {
        LOGGER.info("processWordTemplate started");
        XWPFDocument resultReport;
        Map<String, String> placeholderMap = companyToWordTransformer(company);
        try {
            resultReport = new XWPFDocument(fileService.readFile(templatePath));

            replacePlaceHolderForCompany(resultReport, placeholderMap, company, parentPath);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        LOGGER.info("processWordTemplate finished");
    }

    public void processWordTemplateForProjects(Project project, String templatePath, String parentPath) {
        LOGGER.info("processWordTemplate started");
        XWPFDocument resultReport;
        Map<String, String> placeholderMap = projectToWordTransformer(project);
        try {
            resultReport = new XWPFDocument(fileService.readFile(templatePath));

            replacePlaceHolderForProject(resultReport, placeholderMap, project, parentPath);

        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        LOGGER.info("processWordTemplate finished");
    }


    private XWPFDocument replacePlaceHolderForCompany(XWPFDocument resultReport,
                                                      Map<String, String> placeholderMap,
                                                      Company company,
                                                      String path) {

        String name = company.getCorporateName().isEmpty() ? "" : company.getCorporateName();

        try (FileOutputStream out = new FileOutputStream(new File(path + File.separator + name + " report.docx"))) {

            basicReplacement(resultReport, placeholderMap);

            try {
                XWPFTable tableSubsidiaries = resultReport.getTables().get(1);
                XWPFTable tableActivities = resultReport.getTables().get(2);
                XWPFTable tableDataSource = resultReport.getTables().get(3);
                List<List<String>> subSets = Lists.partition(company.getSubsidiaries(), 4);
                List<List<String>> subSetsActivity = Lists.partition(company.getActivities(), 3);
                List<List<String>> subSetsDataSources = Lists.partition(company.getDataSources(), 1);
                addRowsToTable(tableSubsidiaries, subSets);
                addRowsToTableActivities(tableActivities, subSetsActivity);
                setStyleOfTableBorders(tableActivities);
                addRowsToTableDataSources(tableDataSource, subSetsDataSources);
                setStyleOfTableBorders(tableDataSource);

                resultReport.write(out);
                out.close();

            } catch (IOException e) {
                LOGGER.info(e.getMessage());
            }
        } catch (IOException ioe) {

        }
        return resultReport;
    }

    private XWPFDocument replacePlaceHolderForProject(XWPFDocument resultReport,
                                                      Map<String, String> placeholderMap,
                                                      Project project,
                                                      String path) {

        String name = project.getProjectName().isEmpty() ? "" : project.getProjectName();

        try (FileOutputStream out = new FileOutputStream(new File(path + File.separator + name + " report.docx"))) {

            basicReplacement(resultReport, placeholderMap);
            try {

                XWPFTable tableNews = resultReport.getTables().get(1);
                XWPFTable tablePictures = resultReport.getTables().get(2);
                XWPFTable tableVideos = resultReport.getTables().get(3);
                XWPFTable tableDataSources = resultReport.getTables().get(5);
                List<List<String>> subSetsActivity = Lists.partition(project.getProjectActivities(), 3);
                List<List<String>> subSetsPictures = Lists.partition(project.getProjectPictures(), 1);
                List<List<String>> subSetsVideos = Lists.partition(project.getProjectVideos(), 1);
                List<List<String>> subSetsDataSources = Lists.partition(project.getDataSources(), 1);
                addRowsToTableActivities(tableNews, subSetsActivity);
                setStyleOfTableBorders(tableNews);
                addRowsToTableDataSources(tablePictures, subSetsPictures);
                setStyleOfTableBorders(tablePictures);
                addRowsToTableDataSources(tableVideos, subSetsVideos);
                setStyleOfTableBorders(tableVideos);
                addRowsToTableDataSources(tableDataSources, subSetsDataSources);
                setStyleOfTableBorders(tableDataSources);

                resultReport.write(out);
                out.close();

            } catch (IOException e) {
                LOGGER.info(e.getMessage());
            }
        } catch (IOException ioe) {

        }
        return resultReport;
    }

    private void basicReplacement(XWPFDocument resultReport, Map<String, String> placeholderMap) {
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
    }

    private Map<String, String> companyToWordTransformer(Company company) {
        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("title", company.getCorporateName());
        placeholderMap.put("corpName", company.getCorporateName());
        placeholderMap.put("paidUpCapital", formatStringToNumber(company.getPaidupCapital()));
        placeholderMap.put("shareParValue", company.getShareParValue());
        placeholderMap.put("numberOfShares", formatStringToNumber(company.getNumberOfShares()));
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
                company.getListingDate(),
                company.getSubsidiaries()
        );
        placeholderMap.put("desc", desc);

        return placeholderMap;

    }

    private Map<String, String> projectToWordTransformer(Project project) {
        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("projName", project.getProjectName());
        placeholderMap.put("devCost", project.getDevelopmentConstructionCost());
        placeholderMap.put("ownComp", project.getOwnerCompany());
        placeholderMap.put("parComp", project.getParentCompany());
        placeholderMap.put("projDev", project.getProjectDeveloper());
        placeholderMap.put("projContrct", project.getProjectContractor());
        placeholderMap.put("constrctDate", project.getConstructionDate());
        placeholderMap.put("complitDate", project.getCompletionDate());
        placeholderMap.put("sect", project.getSector());
        placeholderMap.put("projType", project.getProjectType());
        placeholderMap.put("cntr", project.getCountry());
        placeholderMap.put("lanOwn", project.getLandOwnership());
        placeholderMap.put("totalArea", formatToSqrMeters(project.getTotalAreaSize()));
        placeholderMap.put("totalBldArea", formatToSqrMeters(project.getTotalBuiltupArea()));
        placeholderMap.put("totalRentArea", formatToSqrMeters(project.getTotalRentableArea()));
        placeholderMap.put("sts", project.getStatus());
        placeholderMap.put("projAdrs", project.getProjectAddress());
        placeholderMap.put("projLink", project.getProjectWebsite());
        placeholderMap.put("lat", formatStringToLatitude(project.getProjectLatitude()));
        placeholderMap.put("long", formatStringToLatitude(project.getProjectLongitude()));

        String desc = fillDescriptionForProject(
                project.getCity(),
                project.getCountry(),
                project.getProjectName(),
                project.getConstructionComprises(),
                project.getSector(),
                project.getTotalAreaSize(),
                project.getTotalRentableArea(),
                project.getAdditionalArea(),
                project.getCompletionDate());
        placeholderMap.put("tmplDesc", desc);

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
                                   String listingDate,
                                   List<String> subsidiaries) {

        // always have company name, city and country and sector.

        String sentence1 = "Incorporated in " + inceptionDate;
        String sentence11 = " with headquarters in " + city + ", " + country + ". ";
        String sentence2 = corporateName + " is a " + legalStructure + " company " + "operating within the " + sector + " sector.";
        String sentence3 = " The company is engaged in " + productsServicesOffered + ".";
        String sentence4 = " The Company provides " + cutExtraDescForDetails(detailsOfServicesOffered) + ".";
        String sentence5 = " The company has investments and subsidiaries operating in " + insertSubsidiaries(subsidiaries) + ". ";
        String sentencePublic = corporateName + " is a public company listed on the " + stockExchangeName + " since " + listingDate + ".";
        String sentencePrivate = "" + corporateName + " is a private company.";

        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.isNotEmpty(sentence1) ? sentence1 : "");
        sb.append(sentence11);
        sb.append(sentence2);
        sb.append(StringUtils.isNotEmpty(productsServicesOffered) ? sentence3 : "");
        sb.append(StringUtils.isNotEmpty(detailsOfServicesOffered) ? sentence4 : "");
        sb.append(!subsidiaries.isEmpty() ? sentence5 : "");
        sb.append(status == Status.PUBLIC ? sentencePublic : sentencePrivate);


        return sb.toString();
    }
    
    private String fillDescriptionForProject(
            String city,
            String country,
            String projectName,
            String constructionComprises,
            String sector,
            String totalAreaSize,
            String totalRentableArea,
            String additionalArea,
            String completionDate

    ) {
        String sentence1 = "Located in " + city + "," + country + ". ";
        String sentence2 = projectName + "is a " + constructionComprises + ". ";
        String sentence3 = sector + " total area-size is about " + totalAreaSize + " square meters ";
        String sentence31 = "while the leasing area is " + totalRentableArea + " square meter ";
        String sentence32 = "in addition to " + additionalArea + " square meter of open spaces. ";
        String sentence4 = projectName + " was completed on " + completionDate + ".";


        StringBuilder sb = new StringBuilder();
        sb.append(sentence1);
        sb.append(sentence2);
        sb.append(sentence3);
        sb.append(StringUtils.isNotEmpty(totalRentableArea) ? sentence31 : StringUtils.isEmpty(additionalArea) ? "." : " ");
        sb.append(StringUtils.isNotEmpty(additionalArea) ? sentence32 : ".");
        sb.append(sentence4);

        return sb.toString();
    }

    private String cutExtraDescForDetails(String detailsOfServicesOffered) {
        String extraProvides = "provides";
        String resDetailsOfServices;
        if (StringUtils.isNotEmpty(detailsOfServicesOffered)) {
            if (detailsOfServicesOffered.contains(extraProvides)) {
                resDetailsOfServices = detailsOfServicesOffered.replace(extraProvides, "").trim();
                return resDetailsOfServices;
            } else {
                return detailsOfServicesOffered;
            }
        }
        return "";
    }

    private String insertSubsidiaries(List<String> subsidiaries) {
        List<List<String>> subSets = Lists.partition(subsidiaries, 4);
        Set<String> set = new HashSet<>();
        for (List<String> subSet : subSets) {
            if (subSet.size() == 4 && StringUtils.isNotEmpty(subSet.get(3))) {
                set.add(subSet.get(3));
            }
        }
        return String.join(", ", set);
    }


    private void addRowsToTable(XWPFTable tbl, List<List<String>> subSets) {
        for (List<String> subSet : subSets) {
            XWPFTableRow rowTemplate = tbl.getRow(1);
            CTRow ctrow = getCtRowWithStyle(rowTemplate);
            XWPFTableRow newRow = new XWPFTableRow(ctrow, tbl);
            for (int i = 0; i < subSet.size(); i++) {
                newRow.getCell(i).setText(subSet.get(i));
            }
            tbl.addRow(newRow);

        }
        tbl.removeRow(1);
    }

    private CTRow getCtRowWithStyle(XWPFTableRow rowTemplate) {
        XWPFTableRow oldRow = rowTemplate;
        CTRow ctrow = null;
        try {
            ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());

        } catch (XmlException | IOException e) {
            System.out.println(e.getMessage());
        }
        return ctrow;
    }

    private void addRowsToTableDataSources(XWPFTable tbl, List<List<String>> subSets) {
        for (List<String> subSet : subSets) {
            XWPFTableRow rowTemplate = tbl.getRow(0);
            CTRow ctrow = getCtRowWithStyle(rowTemplate);
            XWPFTableRow newRow = new XWPFTableRow(ctrow, tbl);
            for (int i = 0; i < subSet.size(); i++) {
                XWPFParagraph paragraph = newRow.getCell(i).addParagraph();
                paragraph.setAlignment(ParagraphAlignment.LEFT);
                addHyperlink(paragraph, subSet.get(i), "Heading Text");
            }
            tbl.addRow(newRow);

        }
        tbl.removeRow(0);
    }

    private void addRowsToTableActivities(XWPFTable tbl, List<List<String>> subSets) { //By default subset is 3 elements size
        for (List<String> subSet : subSets) {

            for (int i = 0; i < subSet.size(); i++) {
                XWPFTableRow rowTemplate = tbl.getRow(0);
                CTRow ctrow = getCtRowWithStyle(rowTemplate);
                XWPFTableRow newRow = new XWPFTableRow(ctrow, tbl);
                if (i == 0) {
                    XWPFParagraph paragraph = newRow.getCell(0).addParagraph();
                    setRun(paragraph.createRun(), "Arial (Body CS)", 12, subSet.get(i), true, false);
                } else if (i == 2) {
                    XWPFParagraph paragraph = newRow.getCell(0).addParagraph();
                    paragraph.setAlignment(ParagraphAlignment.LEFT);
                    addHyperlink(paragraph, subSet.get(i), "Heading Text");
                } else {
                    newRow.getCell(0).setText(subSet.get(i));
                }
                tbl.addRow(newRow);
            }
        }
        tbl.removeRow(0);

    }
}
