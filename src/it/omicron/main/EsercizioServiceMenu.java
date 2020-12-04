package it.omicron.main;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;

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
		//recursivePrint(menu.getNodes(), 0);

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
		Row firstRow = createFirstRow(wb);
		sheet = recursiveMenu(sheet, menu.getNodes(), 0);
		makeRowBoldAndAutofitColumns(wb);
		saveExcel(wb);
		
	}
	
	//Crea e formatta in maniera corretta la prima riga. Formattazione da implementare.
	
	public static Row createFirstRow(Workbook wb) {
		Row row = wb.getSheetAt(0).createRow(0);
		row.createCell(0).setCellValue(0);
		row.createCell(1).setCellValue("ServiceId");
		row.createCell(2).setCellValue("NodeName");
		row.createCell(3).setCellValue("NodeType");
		row.createCell(4).setCellValue("GroupType");
		row.createCell(5).setCellValue("FlowType");
		row.createCell(6).setCellValue("ResourceId");
		return row;
	}
	
	//Questo metodo aggiunge una colonna vuota dinamicamente, per poter aumentare il livello di profondità di un nodo.
	//Sposta tutte le 6 celle numeriche/stringhe in avanti di uno per poter fare posto a un nuovo indice.
	
	public static Sheet addNewColumn(Sheet sheet, int index) {
		int columns = sheet.getRow(0).getPhysicalNumberOfCells();
		for(Row r : sheet) {
			r.createCell(columns);
			for(int y = columns; y > columns - 6; y--) {
				Cell c = r.getCell(y-1);
				if(c.getCellType() == CellType.STRING) {
					r.getCell(y).setCellValue(r.getCell(y-1).getStringCellValue());
				} else {
					r.getCell(y).setCellValue(r.getCell(y-1).getNumericCellValue());
				}
			}
			r.getCell(columns-6).setCellValue("");
		}
		sheet.getRow(0).getCell(columns - 6).setCellValue(index);
		return sheet;
	}
	
	//Metodo ricorsivo per gestione 
	public static Sheet recursiveMenu(Sheet sheet, List<MenuNode> nodes, int index) {
		//Caso base 1: Lista Nodes nulla, ritorno.
		if(nodes == null) {
			return sheet;
		}
		
		//Caso base 2: Node null, ritorno.
		MenuNode node = null;
		if(nodes.size() != 0) {
			node = nodes.remove(0);
		}else return sheet;
		
		//Node non nullo, elaboro informazioni.
		int max_index = sheet.getRow(0).getLastCellNum() - 7;
		//Controllo se devo aggiungere una colonna, se sì chiamo il metodo per aggiungerla.
		if(index > max_index) {
			sheet = addNewColumn(sheet, index);
			max_index = index;
		}
		
		//Creo una nuova riga assegno il valore X alla casella corretta.
		Row r = sheet.createRow(sheet.getPhysicalNumberOfRows());
		r.createCell(index).setCellValue("X");
		
		//Scrittura delle restanti celle utilizzando le informazioni del nodo "node".
		if(node.getNodeType().equals("service")) {
			r.createCell(max_index + 1).setCellValue(node.getNodeId());
		} else {
			r.createCell(max_index + 1).setCellValue("");
		}
		r.createCell(max_index + 2).setCellValue(node.getNodeName());
		r.createCell(max_index + 3).setCellValue(node.getNodeType());
		r.createCell(max_index + 4).setCellValue((node.getGroupType() != null) ? node.getGroupType() : "");
		r.createCell(max_index + 5).setCellValue((node.getFlowType() != null) ? node.getFlowType() : "");
		if(node.getResource() != null) {
			r.createCell(max_index + 6).setCellValue(node.getResource().getId());
		} else {
			r.createCell(max_index + 6).setCellValue("");
		}
		
		//Chiamate ricorsive e chiamata finale.
		sheet = recursiveMenu(sheet, node.getNodes(), index+1);
		sheet = recursiveMenu(sheet, nodes, index);
		return sheet;
	}
	
	//Semplice metodo che scrive il WorkBook attuale su un file chiamato ServiceMenu situato nella cartella "input".
	//ATTENZIONE: da implementare tutt i casi eccezionali, per ora.
	
	public static void saveExcel(Workbook wb) {
		try (OutputStream fileOut = new FileOutputStream("output/ServiceMenu.xlsx")) {
		    wb.write(fileOut);
		} catch(IOException e) {
			System.out.println("EXCEPTION: Couldn't write this!");
		}
	}
	
	public static void recursivePrint(List<MenuNode> nodes, int index) {
		
		//Caso Base 1, se nullo ritorno.
		if(nodes == null) return;
		
		//Caso Base 2, controllo che il nodo interno corrente esista.
		MenuNode node = null;
		if(nodes.size() != 0) {
			node = nodes.remove(0);
		} else return;
		
		//Stampa.
		for(int i = 0; i < index; i++) System.out.print("-");
		System.out.println("* " + node.getNodeName());
		
		recursivePrint(node.getNodes(), index + 1);
		recursivePrint(nodes, index);
		
	}
	
	public static void makeRowBoldAndAutofitColumns(Workbook wb) {
		//Creo uno stile e un font per la prima riga.
	    CellStyle style = wb.createCellStyle();
	    Font font = wb.createFont();
	    font.setBold(true);
	    style.setFont(font);
	    
	    //Prelevo la prima riga e setto lo stile in ogni cella della riga.
	    Row row = wb.getSheetAt(0).getRow(0);
	    for(int i = 0; i < row.getLastCellNum(); i++) {
	        row.getCell(i).setCellStyle(style);
	    }
	    
	    //AutoFit dei campi, aumentanta leggibilità.
	    for(int i = 0; i < wb.getSheetAt(0).getRow(0).getPhysicalNumberOfCells(); i++) wb.getSheetAt(0).autoSizeColumn(i);
	}
}
