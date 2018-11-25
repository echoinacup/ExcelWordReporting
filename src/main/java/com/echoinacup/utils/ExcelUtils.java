package com.echoinacup.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import java.io.IOException;

public class ExcelUtils {

    private static Row.MissingCellPolicy xRow;

    public static boolean handleEmptyXSSFRow(XSSFRow r) {
        if (r == null) {
            return true;
        } else if (isRowWithEmptyFields(r)) {
            return true;
        }
        return false;
    }

    public static boolean isRowWithEmptyFields(XSSFRow r) {
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

    public static CTRow getCtRowWithStyle(XWPFTableRow rowTemplate) {
        XWPFTableRow oldRow = rowTemplate;
        CTRow ctrow = null;
        try {
            ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());

        } catch (XmlException | IOException e) {
            System.out.println(e.getMessage());
        }
        return ctrow;
    }
}
