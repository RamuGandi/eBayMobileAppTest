package resources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

public class ExcelUtils {
	
	XSSFWorkbook excelWorkbook = null;
	XSSFSheet excelSheet = null;
	XSSFRow row = null;
	XSSFCell cell = null;
	
    public Object[][] readData(){
	FileInputStream fis = null;
	try {
		fis = new FileInputStream("src\\test\\java\\resources\\TestData.xlsx");
	} catch (FileNotFoundException e) {			
		e.printStackTrace();
	} 
	try {
		excelWorkbook = new XSSFWorkbook(fis);
	} catch (IOException e) {
		e.printStackTrace();
	}
	// Read sheet inside the workbook by its name
	excelSheet = excelWorkbook.getSheet("TestData"); //sheet name
	// Find number of rows in excel file
	System.out.println("First Row Number:"+ excelSheet.getFirstRowNum() + " *** Last Row Number:"
			+ excelSheet.getLastRowNum());
	int rowCount = excelSheet.getLastRowNum() - excelSheet.getFirstRowNum()+1;
	int colCount = excelSheet.getRow(0).getLastCellNum();
	System.out.println("Row Count is: " + rowCount
			+ " *** Column count is: " + colCount);
	Object data[][] = new Object[rowCount-1][colCount];
	for (int rNum = 2; rNum <= rowCount; rNum++) 
	{
		for (int cNum = 0; cNum < colCount; cNum++) 
		{
			System.out.print(getCellData("Sheet1", cNum, rNum) + " "); 
			data[rNum - 2][cNum] = getCellData("Sheet1", cNum, rNum); 
		}
		System.out.println();
	}
	return data;
	}
	
	@SuppressWarnings("deprecation")
	public String getCellData(String sheetName, int colNum, int rowNum) 
	{
		try
		{
			if (rowNum <= 0)
				return "";
			int index = excelWorkbook.getSheetIndex(sheetName);
			if (index == -1)
				return "";
			excelSheet = excelWorkbook.getSheetAt(index);
			row = excelSheet.getRow(rowNum - 1);
			if (row == null)
				return "";
			cell = row.getCell(colNum);
			if (cell == null)
				return "";
			if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				return cell.getStringCellValue();
			else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
					|| cell.getCellType() == Cell.CELL_TYPE_FORMULA)
			{
				String cellText = String.valueOf(cell.getNumericCellValue());
				return cellText;
			} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());
		} catch (Exception e)
		{
			e.printStackTrace();
			return "row " + rowNum + " or column " + colNum
					+ " does not exist in xls";
		}
	}

}
