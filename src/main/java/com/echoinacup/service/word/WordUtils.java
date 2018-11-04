package com.echoinacup.service.word;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

public class WordUtils {


    public static void addHyperlink(XWPFParagraph para, String text, String bookmark) {
        //Create hyperlink in paragraph
        CTHyperlink cLink = para.getCTP().addNewHyperlink();
        cLink.setAnchor(bookmark);
        //Create the linked text
        CTText ctText = CTText.Factory.newInstance();
        ctText.setStringValue(text);
        CTR ctr = CTR.Factory.newInstance();
        ctr.setTArray(new CTText[]{ctText});

        //Create the formatting
        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setAscii("Calibri Light");
        CTRPr rpr = ctr.addNewRPr();
        CTColor colour = CTColor.Factory.newInstance();
        colour.setVal("0000FF");
        rpr.setColor(colour);
        CTRPr rpr1 = ctr.addNewRPr();
        rpr1.addNewU().setVal(STUnderline.SINGLE);

        //Insert the linked text into the link
        cLink.setRArray(new CTR[]{ctr});
    }

    public static void setStyleOfTableBorders(XWPFTable table) {
        CTTblPr tblpro = table.getCTTbl().getTblPr();

        CTTblBorders borders = tblpro.addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.NONE);
        borders.addNewLeft().setVal(STBorder.NONE);
        borders.addNewRight().setVal(STBorder.NONE);
        borders.addNewTop().setVal(STBorder.NONE);
        //also inner borders
        borders.addNewInsideH().setVal(STBorder.NONE);
        borders.addNewInsideV().setVal(STBorder.NONE);
    }

    public static void setRun(XWPFRun run, String fontFamily, int fontSize, String text, boolean bold, boolean addBreak) {
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
//        run.setColor(colorRGB);
        run.setText(text);
        run.setBold(bold);
        if (addBreak) run.addBreak();
    }
}
