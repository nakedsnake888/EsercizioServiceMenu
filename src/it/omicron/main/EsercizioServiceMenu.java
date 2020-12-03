package it.omicron.main;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.stream.*;
import com.google.gson.*;
import it.omicron.esercizio.*;

public class EsercizioServiceMenu {
	
	public static void main(String[] args) {
		
		//Creazione di un oggetto JsonElement.
		JsonElement element = readFromFile();
		
		//Parsing di un file JSON per recuperare i dati, da implementare.
		MenuContent menu = parseFile(element);
		
		//Creazione di un file Excel vuoto con il nome corretto.
		createExcel(menu);

	}
	
	public static JsonElement readFromFile() {
		
		//Creo elemento JsonElement nullo.
		
		JsonElement element = null;
		
		//Se no eccezioni, ritorno element.
		
		try {
			element = JsonParser.parseReader((new FileReader("input/ServiceMenu.json")));
			
		} catch (FileNotFoundException e) {
			System.out.println("EXCEPTION: no such file!");
		}
		
		return element;
	}
	
	public static MenuContent parseFile(JsonElement element) {
		Gson gson = new Gson();
		MenuContent menu = null;
		try {
			menu = gson.fromJson(element, MenuContent.class);
		} catch(JsonSyntaxException e) {
			System.out.println("EXCEPTION: This element is not a valid JSON file!");
		}
		return menu;
	}
	
	//Metodo per creazione di un foglio excel completo. Implementata aggiunta dinamica di una colonna.
	
	public static void createExcel(MenuContent menu) {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("Menu " + menu.getVersion());
		Row firstRow = createFirstRow(sheet);
		addNewColumn(sheet, 3);
		saveExcel(wb);
		
	}
	
	//Crea e formatta in maniera corretta la prima riga. Formattazione da implementare.
	
	private static Row createFirstRow(Sheet sheet) {
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue(0);
		row.createCell(1).setCellValue("ServiceId");
		row.createCell(2).setCellValue("NodeType");
		row.createCell(3).setCellValue("GroupType");
		row.createCell(4).setCellValue("FlowType");
		row.createCell(5).setCellValue("ResourceId");
		return row;
	}
	
	//Questo metodo aggiunge una colonna vuota dinamicamente, per poter aumentare il livello di profondità di un nodo.
	//Sposta tutte le 5 stringhe in avanti di uno per poter fare posto a un nuovo indice.
	
	private static void addNewColumn(Sheet sheet, int index) {
		int rows = sheet.getPhysicalNumberOfRows();
		int columns = sheet.getRow(0).getPhysicalNumberOfCells();
		for(int i = 0; i < rows; i++) {
			Row r = sheet.getRow(i);
			r.createCell(columns);
			for(int y = columns; y > columns - 5; y--) {
				r.getCell(y).setCellValue(r.getCell(y-1).getStringCellValue());
			}
			r.getCell(columns-5).setCellValue("");
		}
		sheet.getRow(0).getCell(columns - 5).setCellValue(index);
	}

	public static void saveExcel(Workbook wb) {
		try (OutputStream fileOut = new FileOutputStream("output/ServiceMenu.xlsx")) {
		    wb.write(fileOut);
		} catch(IOException e) {
			System.out.println("EXCEPTION: Couldn't write this!");
		}
	}
}
