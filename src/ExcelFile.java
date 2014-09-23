import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelFile {

	private Workbook workbook;
	private WritableWorkbook wworkbook;

	public ExcelFile(String fileName) {
		try {
			workbook = Workbook.getWorkbook(new File(fileName));
			wworkbook = Workbook.createWorkbook(new File(fileName), workbook);
		} catch (BiffException be) {
			System.err.println("BiffException be: failed to locate workbook '"
					+ fileName + "' .");
		} catch (IOException ioe) {
			System.err.println("IOException ioe: failed to create workbook.");
		}
		try {
			if (wworkbook == null) {
				wworkbook = Workbook.createWorkbook(new File(fileName));
			}
		} catch (IOException ioe) {
			System.err.println("IOException ioe: failed to create workbook.");
		}
	}

	public void createWorksheet(String sheetName) {
		for (WritableSheet ws : wworkbook.getSheets()) {
			if (ws.getName().equals(sheetName)) {
				System.err.println("Worksheet already exists.");
				System.err.println("Worksheet was not created.");
				return;
			}
		}
		wworkbook.createSheet(sheetName, wworkbook.getNumberOfSheets());
	}

	public void addLabel(String sheetName, int column, int row, String text) {
		try {
			wworkbook.getSheet(sheetName).addCell(new Label(column, row, text));
		} catch (RowsExceededException ree) {
			System.err.println("Number of rows exceeded.");
		} catch (WriteException we) {
			System.err.println("Failed to write to worksheet '" + sheetName
					+ "'");
		}
	}

	public void addNumber(String sheetName, int column, int row, Double number) {
		try {
			wworkbook.getSheet(sheetName).addCell(
					new Number(column, row, number));
		} catch (RowsExceededException ree) {
			System.err.println("Number of rows exceeded.");
		} catch (WriteException we) {
			System.err.println("Failed to write to worksheet '" + sheetName
					+ "'");
		}
	}

	public void write() {
		try {
			wworkbook.write();
		} catch (IOException ioe) {
			System.err.println("Failed to write to workbook.");
		}
	}

	public void close() {
		try {
			wworkbook.write();
			wworkbook.close();
			if (workbook != null) {
				workbook.close();
			}
		} catch (WriteException we) {
			System.err.println("Failed to write to workbook.");
		} catch (IOException ioe) {
			System.err.println("Failed to close workbook.");
		}
	}

}
