package com.echoinacup.service.excel;

import com.echoinacup.domain.Company;
import com.echoinacup.domain.Project;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static com.echoinacup.utils.ExcelHeader.*;
import static com.echoinacup.utils.HelpUtils.trimWithNonBrackeSpace;

public class ExcelHandler {

    private final static Logger LOGGER = Logger.getLogger(ExcelHandler.class.getName());

    private static MissingCellPolicy xRow;
    private static final int BASIC_INFO_SHEET_FIRST_REPORT = 1;
    private static final int BASIC_INFO_SHEET_SECOND_REPORT = 0;
    private static final int SUBS_SOURCES_SHEET_COMPANIES = 2;
    private static final int SUBS_SOURCES_SHEET_PROJECTS = 1;
    private static final String DATA_SOURCES_HEADER = "DATA SOURCES (COMPANY WEBSITE, COMPANY PROFILE IN STOCK EXCHANGE, NEWS ARTICLES OR OTHER): LINKS (HTTP://…)";
    private static final String SUBSIDIARY_HEADER = "SUBSIDIARY COMPANY (ALL THE ONES YOU CAN FIND)";
    private static final String PROJECT_PICTURES_HEADER = "PROJECT PICTURES (UP TO 10 PICTURES)";
    private static final String PROJECT_VIDEOS_HEADER = "PROJECT VIDEOS (UP TO 10 VIDEOS)";
    private static final String DATE_HEADER = "DATE (DAY FULL MONTH YEAR)";


    public List<Company> processExcelBasicInfoSheetIntoCompanies(File file) {
        System.out.println("Process of Basic info for Companies started...");
        List<Company> companies = new ArrayList<>();
        Map<String, String> headerMap = new LinkedHashMap<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(BASIC_INFO_SHEET_FIRST_REPORT);

            int rowStart = spreadsheet.getFirstRowNum();
            int rowEnd = spreadsheet.getLastRowNum();
            System.out.println("count of rows is " + rowEnd);
            for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                XSSFRow r = spreadsheet.getRow(rowNum);

                if (initHeaderMap(headerMap, r)) continue;

                if (handleEmptyRow(r)) continue;

                Map<String, String> entitiesMap = populateMapForEntityCreation(headerMap, r);

                companies.add(companyCreator(entitiesMap));
            }


        } catch (IOException | InvalidFormatException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Process of Basic info for Companies finished");
        return companies;
    }

    public List<Project> processExcelBasicInfoSheetIntoProjects(File file) {
        List<Project> projects = new ArrayList<>();
        Map<String, String> headerMap = new LinkedHashMap<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(BASIC_INFO_SHEET_SECOND_REPORT);

            int rowStart = spreadsheet.getFirstRowNum();
            int rowEnd = spreadsheet.getLastRowNum();

            for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                XSSFRow r = spreadsheet.getRow(rowNum);

                if (initHeaderMap(headerMap, r)) continue;

                if (handleEmptyRow(r)) continue;

                Map<String, String> entitiesMap = populateMapForEntityCreation(headerMap, r);

                projects.add(projectCreator(entitiesMap));
            }


        } catch (IOException | InvalidFormatException e) {
            System.out.println(e.getMessage());
        }
        return projects;
    }

    private Map<String, String> populateMapForEntityCreation(Map<String, String> headerMap, XSSFRow r) {
        Map<String, String> entitiesMap = new LinkedHashMap<>();
        entitiesMap.putAll(headerMap);

        List<String> values = new ArrayList<>();

        for (int i = 0; i < r.getLastCellNum(); i++) {
            Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
            String str = getCellValueAsString(cell);
            if (StringUtils.isNotEmpty(str)) {
                values.add(str);
            } else {
                values.add("");
            }
        }

        int amount = 0;
        for (String key : entitiesMap.keySet()) {
            entitiesMap.put(key, values.get(amount++));
        }
        return entitiesMap;
    }

    public List<Company> processExcelTemplateSubsidiariesForCompanies(List<Company> allCompanies, File file) throws IllegalArgumentException { //Pass List of companies from the first sheet
        LOGGER.info("Process excel subsidiaries For Companies started ...");
        List<Company> resultList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(SUBS_SOURCES_SHEET_COMPANIES);

            Map<String, Integer> map = countAndSeparateSubsidiariesActivitiesSourcesSheet(spreadsheet);

            int startIndex = 0;
            int endIndex = 0;

            for (Company c : allCompanies) {
                try {
                    int index = map.get(c.getCorporateName());

                    if (startIndex == 0 && endIndex == 0) {
                        endIndex = index;
                    } else if (endIndex != 0) {
                        startIndex = endIndex + 1;
                        endIndex = index + startIndex - 1;
                    }
                    addSubToCompany(spreadsheet, c, startIndex, endIndex);
                    resultList.add(c);
                } catch (NullPointerException e) {
                    throw new IllegalArgumentException("Can not find company with name " + c.getCorporateName());
                }
            }

        } catch (IOException | InvalidFormatException e) {
            LOGGER.info(e.getMessage());
        }
        LOGGER.info("Process excel subsidiaries For Companies finished");
        return resultList;
    }

    public List<Project> processExcelTemplateSubsidiariesForProjects(List<Project> allProjects, File file) throws IllegalArgumentException { //Pass List of companies from the first sheet
        LOGGER.info("Subsidiaries sheet for Second report started ...");
        List<Project> resultList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(SUBS_SOURCES_SHEET_PROJECTS);

            Map<String, Integer> map = countAndSeparateSubsidiariesActivitiesSourcesSheet(spreadsheet);

            int startIndex = 0;
            int endIndex = 0;

            for (Project p : allProjects) {
                try {
                    int index = map.get(p.getProjectName());

                    if (startIndex == 0 && endIndex == 0) {
                        endIndex = index;
                    } else if (endIndex != 0) {
                        startIndex = endIndex + 1;
                        endIndex = index + startIndex - 1;
                    }
                    addSubToProjects(spreadsheet, p, startIndex, endIndex);
                    resultList.add(p);
                } catch (NullPointerException e) {
                    throw new IllegalArgumentException("Can not find company with name " + p.getProjectName());
                }
            }

        } catch (IOException | InvalidFormatException e) {
            LOGGER.info(e.getMessage());
        }
        LOGGER.info("Subsidiaries sheet for Second report started finished");
        return resultList;
    }

    private void addSubToCompany(XSSFSheet spreadsheet, Company company, int rowStart, int rowEnd) {

        boolean isSubsidiary = true;
        boolean isActivities = false;
        boolean isDataSources = false;

        start:
        for (int rowNum = rowStart + 1; rowNum <= rowEnd; rowNum++) {
            XSSFRow r = spreadsheet.getRow(rowNum);
            if (handleEmptyRow(r)) continue;


            for (int i = 2; i < r.getLastCellNum(); i++) {
                Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
                String str = getCellValueAsString(cell);


                if (isSubsidiary) {
                    if (!DATE_HEADER.equals(str)) {
                        company.getSubsidiaries().add(str);
                        if (i == 5) {
                            company.getSubCountries().add(str);
                        }

                    } else {
                        isSubsidiary = false;
                        isActivities = true;
                        continue start;
                    }
                } else if (isActivities) {
                    if (!StringUtils.equals(DATA_SOURCES_HEADER, str)) {
                        company.getActivities().add(str);
                    } else {
                        isActivities = false;
                        isDataSources = true;
                        continue start;
                    }
                } else if (isDataSources) {
                    if (!SUBSIDIARY_HEADER.equals(str)) {
                        company.getDataSources().add(str);
                    } else {
                        isDataSources = false;
                        isSubsidiary = true;
                        continue start;
                    }
                }
            }
        }
    }

    private void addSubToProjects(XSSFSheet spreadsheet, Project project, int rowStart, int rowEnd) {

        boolean isPictures = true;
        boolean isProjectsVideos = false;
        boolean isActivities = false;
        boolean isDataSources = false;

        start:
        for (int rowNum = rowStart + 1; rowNum <= rowEnd; rowNum++) {
            XSSFRow r = spreadsheet.getRow(rowNum);
            if (handleEmptyRow(r)) continue;


            for (int i = 2; i < r.getLastCellNum(); i++) {
                Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
                String str = getCellValueAsString(cell);

                if (isPictures) {
                    if (!PROJECT_VIDEOS_HEADER.equals(str)) {
                        if (StringUtils.isNotEmpty(str)) {
                            project.getProjectPictures().add(str);
                        }
                    } else {
                        isPictures = false;
                        isProjectsVideos = true;
                        continue start;
                    }
                } else if (isProjectsVideos) {
                    if (!StringUtils.equals(DATE_HEADER, str)) {
                        if (StringUtils.isNotEmpty(str)) {
                            project.getProjectVideos().add(str);
                        }
                    } else {
                        isProjectsVideos = false;
                        isActivities = true;
                        continue start;
                    }
                } else if (isActivities) {
                    if (!StringUtils.equals(DATA_SOURCES_HEADER, str)) {
                        if (StringUtils.isNotEmpty(str)) {
                            project.getProjectActivities().add(str);
                        }
                    } else {
                        isActivities = false;
                        isDataSources = true;
                        continue start;
                    }
                } else if (isDataSources) {
                    if (!PROJECT_PICTURES_HEADER.equals(str)) {
                        if (StringUtils.isNotEmpty(str)) {
                            project.getDataSources().add(str);
                        } else {
                            project.getDataSources().add("");
                        }
                    } else {
                        isDataSources = false;
                        isPictures = true;
                        continue start;
                    }
                }


            }
        }
    }

    private boolean initHeaderMap(Map<String, String> headerMap, XSSFRow r) {
        if (r != null && r.getRowNum() == 0) {
            fillInDescriptionMapWithKeys(r, headerMap);
            return true;
        }
        return false;
    }

    private boolean handleEmptyRow(XSSFRow r) {
        if (r == null) {
            return true;
        } else if (isRowWithEmptyFields(r)) {
            return true;
        }
        return false;
    }

    private boolean isRowWithEmptyFields(XSSFRow r) {
        for (int i = 0; i < r.getLastCellNum(); i++) {
            Cell cell = r.getCell(i, xRow.RETURN_NULL_AND_BLANK);
            if ((cell == null) || (cell.equals("")) || (cell.getCellType() == cell.CELL_TYPE_BLANK)) {
                i++;
            } else {
                return false;
            }
        }
        return true;
    }

    private void fillInDescriptionMapWithKeys(XSSFRow r, Map<String, String> headerMap) {
        for (int i = 0; i < r.getLastCellNum(); i++) {
            Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                String value = cell.getStringCellValue();
                if (StringUtils.isNotEmpty(value)) {
                    headerMap.put(value.trim(), "");
                }
            }
        }
    }

    /**
     * This method for the type of data in the cell, extracts the data and
     * returns it as a string.
     */
    public static String getCellValueAsString(Cell cell) {
        String strCellValue = "";
        if (cell != null) {
            switch (cell.getCellTypeEnum()) {
                case STRING:
                    strCellValue = trimWithNonBrackeSpace(cell.toString());
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "dd/MM/yyyy");
                        strCellValue = dateFormat.format(cell.getDateCellValue());
                    } else if (cell.getCellStyle().getDataFormatString().contains("%")) {
                        DecimalFormat dec = new DecimalFormat("#0.0");
                        Double value = cell.getNumericCellValue() * 100;
                        strCellValue = dec.format(value) + "%";
                    } else {
                        Double value = cell.getNumericCellValue();
                        strCellValue = new BigDecimal(value).toPlainString();
                    }
                    break;
                case BOOLEAN:
                    strCellValue = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case BLANK:
                    strCellValue = "";
                    break;
                default:
                    strCellValue = "";
            }
        }
        return strCellValue;
    }

    private Company companyCreator(Map<String, String> valueMap) {
        Company company = new Company();
        company.setCorporateName(valueMap.get(keyCorpName));
        company.setPaidupCapital(valueMap.get(keyPaidCapital));
        company.setShareParValue(valueMap.get(keyShareValues));
        company.setNumberOfShares(valueMap.get(keyNumberOfShares));
        company.setLegalStructure(valueMap.get(keyLegalStructure));
        company.setCurrency(valueMap.get(keyCurrencySortCut));
        company.setInceptionDate(valueMap.get(keyInceptionDate));
        company.setSector(valueMap.get(keySector));
        company.setProductsServicesOffered(valueMap.get(keyOffered));
        company.setDetailsOfServicesOffered(valueMap.get(keyDetailsOffered));
        company.setCity(valueMap.get(keyCity));
        company.setCountry(valueMap.get(keyCountry));
        company.setStatus(valueMap.get(keyStatus));
        company.setNumberOfEmployees(valueMap.get(keyNumberOfEmployees));
        company.setListingDate(valueMap.get(keyListingDate));
        company.setStockExchangeName(valueMap.get(keyStockName));
        company.setLinkToExchange(valueMap.get(keyLinkToStock));
        company.setPhone(valueMap.get(keyPhone));
        company.setContactEmail(valueMap.get(keyEmail));
        company.setWebsite(valueMap.get(keyWebsite));
        company.setCompanyAddress(valueMap.get(keyCompanyAddress));
        company.setLinkedIn(valueMap.get(keyLinkedId));
        company.setTwitter(valueMap.get(keyTwitter));
        company.setFacebook(valueMap.get(keyFacebook));
        company.setInstagram(valueMap.get(keyInstagram));
        return company;
    }


    private Project projectCreator(Map<String, String> valueMap) {
        Project project = new Project();
        project.setProjectName(valueMap.get(keyProjectName));
        project.setDevelopmentConstructionCost(valueMap.get(keyDevConstCost));
        project.setCurrency(valueMap.get(keyCurrency));
        project.setOwnerCompany(valueMap.get(keyOwnerCompany));
        project.setParentCompany(valueMap.get(keyParentCompany));
        project.setProjectDeveloper(valueMap.get(keyProjectDeveloper));
        project.setProjectContractor(valueMap.get(keyProjectContractor));
        project.setConstructionDate(valueMap.get(keyConstructionDate));
        project.setCompletionDate(valueMap.get(keyCompletionDate));
        project.setSector(valueMap.get(keySector));
        project.setProjectType(valueMap.get(keyProjectType));
        project.setCountry(valueMap.get(keyCountry));
        project.setCity(valueMap.get(keyCity));
        project.setConstructionComprises(valueMap.get(keyConstructionComprises));
        project.setAdditionalArea(valueMap.get(keyOtherAreasMentiond));
        project.setLandOwnership(valueMap.get(keyLandOwnership));
        project.setTotalAreaSize(valueMap.get(keyTotalAreaSize));
        project.setTotalBuiltupArea(valueMap.get(keyTotalBuiltUpArea));
        project.setTotalRentableArea(valueMap.get(keyTotalRentableArea));
        project.setStatus(valueMap.get(keyProjectStatus));
        project.setProjectAddress(valueMap.get(keyProjectAddress));
        project.setProjectWebsite(valueMap.get(keyProjectWebSite));
        project.setProjectLatitude(valueMap.get(keyLatitude));
        project.setProjectLongitude(valueMap.get(keyLongitude));
        return project;
    }

    private Map<String, Integer> countAndSeparateSubsidiariesActivitiesSourcesSheet(XSSFSheet spreadsheet) {

        int rowStart = spreadsheet.getFirstRowNum();
        int rowEnd = spreadsheet.getLastRowNum();
        List<String> labels = new ArrayList<>();

        for (int rowNum = rowStart + 1; rowNum <= rowEnd; rowNum++) {
            XSSFRow r = spreadsheet.getRow(rowNum);
            Cell cell = r.getCell(1);
            labels.add(getCellValueAsString(cell));

        }
        return createFrequencyMap(labels);
    }

    private Map<String, Integer> createFrequencyMap(List<String> list) {
        HashMap<String, Integer> frequencyMap = new LinkedHashMap<>();
        for (String a : list) {
            if (frequencyMap.containsKey(a)) {
                frequencyMap.put(a, frequencyMap.get(a) + 1);
            } else {
                frequencyMap.put(a, 1);
            }
        }
        return frequencyMap;
    }
}
