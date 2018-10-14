package com.echoinacup.excel;

import com.echoinacup.file.FileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelHandler {

    private FileService fileService;
    private static MissingCellPolicy xRow;


    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }


    public void readExcelTemplate() {
        List<Map<String, Object>> allCompanies = new ArrayList<>();
        File file = fileService.readFile();
        Map<String, String> headerMap = new LinkedHashMap<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet spreadsheet = workbook.getSheetAt(1);

            int rowStart = spreadsheet.getFirstRowNum();
            int rowEnd = spreadsheet.getLastRowNum();

            Map<String, Object> companyMap = new LinkedHashMap<>();

            for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                XSSFRow r = spreadsheet.getRow(rowNum);
                if (r.getRowNum() == 0) {
                    fillInDescriptionMapWithKeys(r, headerMap);
                    continue;
                }
                if (r == null) {
                    // This whole row is empty
                    // Handle it as needed
                    continue;
                }

//                System.out.println("map size " + headerMap.size());
//                headerMap.forEach((k, v) -> System.out.println("key " + k));
                companyMap.putAll(headerMap);
                List<String> values = new ArrayList<>();
//
                for (int i = 0; i < r.getLastCellNum(); i++) {
                    Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
                    String str = getCellValueAsString(cell);
                    if (StringUtils.isNotEmpty(str)) {
                        values.add(str);
                    } else {
                        values.add(" ");
                    }
                }
                System.out.println("values size " + values.size());
                values.forEach(v -> System.out.println(v));
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

    private void iterateCells(XSSFRow r, int lastColumn) {
        for (int i = 0; i < lastColumn; i++) {
            Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                // The spreadsheet is empty in this cell
                System.out.println(getCellValueAsString(cell));
            } else {
                // Do something useful with the cell's contents
                System.out.println(getCellValueAsString(cell));
            }
        }
    }


    private void fillInDescriptionMapWithKeys(XSSFRow r, Map<String, String> headerMap) {
        for (int i = 0; i < 27; i++) { //TODO add constans
            Cell cell = r.getCell(i, xRow.RETURN_BLANK_AS_NULL);
            String value = cell.getStringCellValue();
//            if (StringUtils.isNotEmpty(value) && !value.trim().equals("BASIC INFO") && !value.trim().equals("SOCIAL MEDIA:")) {
            if (StringUtils.isNotEmpty(value)) {
                headerMap.put(value, "");
            }
//            if (cell == null) {
//                // The spreadsheet is empty in this cell
//                System.out.println(getCellValueAsString(cell));
//            } else {
//                // Do something useful with the cell's contents
//                System.out.println(getCellValueAsString(cell));
//            }
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
                    strCellValue = "empty";
                    break;
            }
        }
        return strCellValue;
    }
}

// Decide which rows to process
//    int rowStart = Math.min(15, sheet.getFirstRowNum());
//    int rowEnd = Math.max(1400, sheet.getLastRowNum());
//
//    for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
//        Row r = sheet.getRow(rowNum);
//        if (r == null) {
//        // This whole row is empty
//        // Handle it as needed
//        continue;
//        }
//
//        int lastColumn = Math.max(r.getLastCellNum(), MY_MINIMUM_COLUMN_COUNT);
//
//        for (int cn = 0; cn < lastColumn; cn++) {
//        Cell c = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
//        if (c == null) {
//        // The spreadsheet is empty in this cell
//        } else {
//        // Do something useful with the cell's contents
//        }
//        }
//        }
