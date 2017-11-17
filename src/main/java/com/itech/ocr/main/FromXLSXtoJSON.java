package com.itech.ocr.main;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class FromXLSXtoJSON {
    public static String find(String text) throws IOException {
        StringBuilder json = new StringBuilder("{ ");

        boolean isFindBlock = false;

        File myFile = new File("D://server/out.xlsx");
        FileInputStream fis = new FileInputStream(myFile); // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis); // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(0); // Get iterator to all the rows in current sheet I
        Iterator<Row> rowIterator = mySheet.iterator(); // Traversing over each row of XLSX file

        boolean isKeyAlreadyExist = false;
        boolean isValueAlreadyExist = false;
        String tmp = "";

        while (rowIterator.hasNext()) {
            if(isKeyAlreadyExist && isValueAlreadyExist) {
                json.append(tmp);
            }
            Row row = rowIterator.next(); // For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                DataFormatter formatter = new DataFormatter();
                String val = formatter.formatCellValue(cell);
                if(isFindBlock && !val.equals("")) return json.toString() +" }";
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        if(cell.getStringCellValue().contains(text) &&
                                cell.getStringCellValue().contains("-")) {
                            System.out.println(cell.getStringCellValue()+" "+text);
                            isFindBlock = true;
                            row = rowIterator.next();
                            cellIterator = row.cellIterator();
                            break;
                        }
                        if(isFindBlock) {
                            isKeyAlreadyExist = false;
                            isValueAlreadyExist = false;
                            tmp = "";
                            while (cellIterator.hasNext()) {
                                switch (cell.getCellType()) {
                                    case Cell.CELL_TYPE_STRING: {
                                        if(!cell.getStringCellValue().equals("") && !isKeyAlreadyExist) {
                                            isKeyAlreadyExist = true;
                                            String key = cell.getStringCellValue();
                                            tmp += "\""+toCamelCase(key)+"\": ";
                                        }
                                        break;
                                    }
                                }
                                cell = cellIterator.next();
                                if(!cellIterator.hasNext() && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    tmp += cell.getNumericCellValue()+", ";
                                    isValueAlreadyExist = true;
                                }
                            }
                        }
                        break;
                    default :
                }
            }
        }
        json.append(" }");
        return json.toString();
    }

    private static String toCamelCase(String str){
        return org.apache.commons.lang3.text.WordUtils.capitalizeFully(str).trim().replaceAll(" ","");
    }
}
