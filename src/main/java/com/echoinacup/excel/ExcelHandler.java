package com.echoinacup.excel;

import com.echoinacup.entities.Company;
import com.echoinacup.file.FileService;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelHandler {

    private FileService fileService;
    private static MissingCellPolicy xRow;
    private static final int BASIC_INFO_SHEET = 1;
    private static final int SUBS_SOURCES_SHEET = 2;
    private static final String dataSourcesHeader = "DATA SOURCES (COMPANY WEBSITE, COMPANY PROFILE IN STOCK EXCHANGE, NEWS ARTICLES OR OTHER): LINKS (HTTP://…)";

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }


    public void readExcelTemplateSub() {
        List<Map<String, Object>> allCompanies = new ArrayList<>();
        File file = fileService.readFile();
        List<Company> companies = new ArrayList<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(SUBS_SOURCES_SHEET);

            int rowStart = spreadsheet.getFirstRowNum();
            int rowEnd = spreadsheet.getLastRowNum();

            String initCompanyName = "";

            XSSFRow firstRow = spreadsheet.getRow(1);
            if (firstRow != null) {
                initCompanyName = getCellValueAsString(firstRow.getCell(1, xRow.RETURN_BLANK_AS_NULL));
            }
            //TODO handle ERROR
            boolean isSubsidiary = true;
            boolean isActivities = false;
            boolean isDataSources = false;

            start:
            for (int rowNum = rowStart + 1; rowNum <= rowEnd; rowNum++) { //skip header  //TODO ask if structure the same
                XSSFRow r = spreadsheet.getRow(rowNum);

                if (handleEmptyRow(r)) continue;


                List<String> sub = new ArrayList<>();
                List<String> activities = new ArrayList<>();
                List<String> dataSources = new ArrayList<>();


                for (int i = 1; i < r.getLastCellNum(); i++) {
                    String currentName = getCellValueAsString(r.getCell(1, xRow.RETURN_BLANK_AS_NULL));
                    Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
                    String str = getCellValueAsString(cell);

                    if (isSubsidiary && initCompanyName.equals(currentName)) {
                        if (!"DATE (DAY FULL MONTH YEAR)".equals(str)) {
                            if (StringUtils.isNotEmpty(str)) {
                                sub.add(str);
                            } else {
                                sub.add("");
                            }
                        } else {
                            isSubsidiary = false;
                            isActivities = true;
                            continue start;
                        }
                    } else if (isActivities && initCompanyName.equals(currentName)) {
                        if (!StringUtils.equals(dataSourcesHeader, str)) {
                            if (StringUtils.isNotEmpty(str)) {
                                activities.add(str);
                            } else {
                                activities.add("");
                            }
                        } else {
                            isActivities = false;
                            isDataSources = true;
                            continue start;
                        }
                    } else if (isDataSources) {
                        if (!"SUBSIDIARY COMPANY (ALL THE ONES YOU CAN FIND)".equals(str)) {
                            if (StringUtils.isNotEmpty(str)) {
                                dataSources.add(str);
                            } else {
                                dataSources.add("");
                            }
                        } else {
                            initCompanyName = currentName;
                            isDataSources = false;
                            isSubsidiary = true;
                            continue start;
                        }
                    }
                    // }


                }
//                sub.forEach(v -> System.out.println(v + "  "));
//
//                activities.forEach(v -> System.out.println(v + "  "));
//
//                dataSources.forEach(v -> System.out.println(v + "  "));

            }

        } catch (IOException | InvalidFormatException e) {
            System.out.println(e.getMessage());
        }


    }


    public void readExcelBasicInfo() {
        List<Map<String, Object>> allCompanies = new ArrayList<>();
        File file = fileService.readFile();
        Map<String, String> headerMap = new LinkedHashMap<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(BASIC_INFO_SHEET);

            int rowStart = spreadsheet.getFirstRowNum();
            int rowEnd = spreadsheet.getLastRowNum();

            for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                Map<String, Object> companyMap = new LinkedHashMap<>();
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
                        values.add(" ");
                    }
                }

                int amount = 0;
                for (String key : companyMap.keySet()) {
                    companyMap.put(key, values.get(amount++));
                }

                allCompanies.add(companyMap);
            }


        } catch (IOException | InvalidFormatException e) {
            System.out.println(e.getMessage());
        }


        allCompanies.forEach(i -> System.out.println(i));
    }

    private boolean initHeaderMap(Map<String, String> headerMap, XSSFRow r) {
        if (r.getRowNum() == 0) {
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
        for (int i = 0; i < 27; i++) {
            Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
            String value = cell.getStringCellValue();
            if (StringUtils.isNotEmpty(value)) {
                headerMap.put(value, "");
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
}
