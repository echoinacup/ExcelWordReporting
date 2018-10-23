package com.echoinacup.service.excel;

import com.echoinacup.domain.Company;
import com.echoinacup.service.file.FileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.echoinacup.utils.HelpUtils.*;

public class ExcelHandler {


    private static final String pathToExcelTemplate = "templates/excel/test_example.xlsx";
    private static MissingCellPolicy xRow;
    private static final int BASIC_INFO_SHEET = 1;
    private static final int SUBS_SOURCES_SHEET = 2;
    private static final String dataSourcesHeader = "DATA SOURCES (COMPANY WEBSITE, COMPANY PROFILE IN STOCK EXCHANGE, NEWS ARTICLES OR OTHER): LINKS (HTTP://…)";


    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    //TODO handle shit in other rows!!!!
    public List<Company> processExcelBasicInfoSheet() {
        File file = fileService.readFile(pathToExcelTemplate);
        List<Company> companies = new ArrayList<>();
        Map<String, String> headerMap = new LinkedHashMap<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(BASIC_INFO_SHEET);

            int rowStart = spreadsheet.getFirstRowNum();
            int rowEnd = spreadsheet.getLastRowNum();

            for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                Map<String, String> companyMap = new LinkedHashMap<>();
                XSSFRow r = spreadsheet.getRow(rowNum);

                if (initHeaderMap(headerMap, r)) continue;

                if (handleEmptyRow(r)) continue;

                companyMap.putAll(headerMap);

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
                for (String key : companyMap.keySet()) {
                    companyMap.put(key, values.get(amount++));
                }
                companies.add(companyCreator(companyMap));
            }


        } catch (IOException | InvalidFormatException e) {
            System.out.println(e.getMessage());
        }
        return companies;
    }


    public List<Company> processExcelTemplateSub(List<Company> allCompanies) { //Pass List of companies from the first sheet
        File file = fileService.readFile(pathToExcelTemplate);
        List<Company> resultList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(SUBS_SOURCES_SHEET);

            Map<String, Integer> map = countAndSeparateSubSheet(spreadsheet);

            int startIndex = 0;
            int endIndex = 0;

            for (Company c : allCompanies) {
                int index = map.get(c.getCorporateName());

                if (startIndex == 0 && endIndex == 0) {
                    endIndex = index;
                } else if (startIndex == 0 && endIndex != 0) {
                    startIndex = endIndex;
                    endIndex = index;
                }
                addSubToCompany(spreadsheet, c, startIndex, endIndex);
                resultList.add(c);
            }

            resultList.forEach(i -> System.out.println(i));

        } catch (IOException | InvalidFormatException e) {
            System.out.println(e.getMessage());
        }
        return resultList;
    }


    private void addSubToCompany(XSSFSheet spreadsheet, Company company, int rowStart, int rowEnd) {

        boolean isSubsidiary = true;
        boolean isActivities = false;
        boolean isDataSources = false;

        start:
        for (int rowNum = rowStart + 1; rowNum <= rowEnd; rowNum++) { //skip header  //TODO ask if structure the same
            XSSFRow r = spreadsheet.getRow(rowNum);
            if (handleEmptyRow(r)) continue;


            for (int i = 1; i < r.getLastCellNum(); i++) {
                Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
                String str = getCellValueAsString(cell);

                if (isSubsidiary) {
                    if (!"DATE (DAY FULL MONTH YEAR)".equals(str)) {
                        if (StringUtils.isNotEmpty(str)) {
                            company.getSubsidiaries().add(str);
                        } else {
                            company.getSubsidiaries().add("");
                        }
                    } else {
                        isSubsidiary = false;
                        isActivities = true;
                        continue start;
                    }
                } else if (isActivities) {
                    if (!StringUtils.equals(dataSourcesHeader, str)) {
                        if (StringUtils.isNotEmpty(str)) {
                            company.getActivities().add(str);
                        } else {
                            company.getActivities().add("");
                        }
                    } else {
                        isActivities = false;
                        isDataSources = true;
                        continue start;
                    }
                } else if (isDataSources) {
                    if (!"SUBSIDIARY COMPANY (ALL THE ONES YOU CAN FIND)".equals(str)) {
                        if (StringUtils.isNotEmpty(str)) {
                            company.getDataSources().add(str);
                        } else {
                            company.getDataSources().add("");
                        }
                    } else {
                        isDataSources = false;
                        isSubsidiary = true;
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
        if (r == null) { // This whole row is empty and handle it as needed
            return true;
        }
        return false;
    }

    private void fillInDescriptionMapWithKeys(XSSFRow r, Map<String, String> headerMap) {
        for (int i = 0; i < 29; i++) {
            Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
            String value = cell.getStringCellValue();
            if (StringUtils.isNotEmpty(value)) {
                headerMap.put(value.trim(), "");
            }
        }
    }

    /**
     * This method for the type of data in the cell, extracts the data and
     * returns it as a string.
     */
    public static String getCellValueAsString(Cell cell) {
        String strCellValue = null;
        if (cell != null) {
            switch (cell.getCellTypeEnum()) {
                case STRING:
                    strCellValue = cell.toString();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "dd/MM/yyyy");
                        strCellValue = dateFormat.format(cell.getDateCellValue());
                    } else if (cell.getCellStyle().getDataFormatString().contains("%")) {
                        Double value = cell.getNumericCellValue() * 100;
                        strCellValue = value.toString() + "%";
                    } else {
                        Double value = cell.getNumericCellValue();
                        Long longValue = value.longValue();
                        strCellValue = new String(longValue.toString());
                    }
                    break;
                case BOOLEAN:
                    strCellValue = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case BLANK:
                    strCellValue = "";
                    break;
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

    private Map<String, Integer> countAndSeparateSubSheet(XSSFSheet spreadsheet) {

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
