package com.itech.ocr;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class FindTextInXLSX {
    @Test
    public void find() throws IOException {
        String text = "Foreign Transaction";
        StringBuilder json = new StringBuilder("{ ");

        boolean isFindBlock = false;
        boolean isNextValueAddToJSON = false;

        File myFile = new File("D://server/1111.xlsx");
        FileInputStream fis = new FileInputStream(myFile); // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis); // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(0); // Get iterator to all the rows in current sheet I
        Iterator<Row> rowIterator = mySheet.iterator(); // Traversing over each row of XLSX file
        while (rowIterator.hasNext()) { Row row = rowIterator.next(); // For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        if(cell.getStringCellValue().contains(text)) {
                            isFindBlock = true;
                        }
                        if(isFindBlock) {
                            if(isNextValueAddToJSON) {
                                String key = cell.getStringCellValue();
                                json.append("\""+toCamelCase(key)+"\": ");
                                isNextValueAddToJSON = false;
                            }
                            if(cell.getStringCellValue().contains("-") &&
                                    !cell.getStringCellValue().contains(text)) {
                                String key = cell.getStringCellValue().trim();
                                String keys[] = key.split("-");
                                if(keys.length != 1) {
                                    String jsonKey = keys[1].trim();
                                    json.append("\""+toCamelCase(jsonKey)+"\": ");
                                } else {
                                    isNextValueAddToJSON = true;
                                }
                                System.out.println(1);
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if(isFindBlock) {
                            json.append(cell.getNumericCellValue()+", ");

                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        break;
                    default :
                }
            }
        }
        json.append(" }");
        System.out.println(json);
    }

    private static String toCamelCase(String str){
        return org.apache.commons.lang3.text.WordUtils.capitalizeFully(str).trim().replaceAll(" ","");
    }
}
