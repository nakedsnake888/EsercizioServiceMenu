package it.omicron.main;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

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
		//createExcel(menu);
		recursivePrint(menu.getNodes(), 0);

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
		sheet = recursiveMenu(sheet, menu.getNodes(), 0, 0);
		saveExcel(wb);
		
	}
	
	//Crea e formatta in maniera corretta la prima riga. Formattazione da implementare.
	
	public static Row createFirstRow(Sheet sheet) {
		Row row = sheet.createRow(0);
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
	//Sposta tutte le 5 stringhe in avanti di uno per poter fare posto a un nuovo indice.
	
	public static Sheet addNewColumn(Sheet sheet, int index) {
		int rows = sheet.getPhysicalNumberOfRows();
		int columns = sheet.getRow(0).getPhysicalNumberOfCells();
		for(int i = 0; i < rows; i++) {
			Row r = sheet.getRow(i);
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
	public static Sheet recursiveMenu(Sheet sheet, List<MenuNode> nodes, int index, int node_index) {
		//Caso base 1: Lista Nodes nulla, ritorno.
		if(nodes == null) {
			return sheet;
		}
		
		//Caso base 2: Node null, ritorno.
		MenuNode node = (node_index < nodes.size()) ? nodes.get(node_index) : null;
		if(node == null) {
			return sheet;
		}
		
		//Node non nullo, elaboro informazioni.
		int max_index = sheet.getRow(0).getLastCellNum() - 7;
		if(index > max_index) {
			sheet = addNewColumn(sheet, index);
			max_index = index;
		}
		Row r = sheet.createRow(sheet.getPhysicalNumberOfRows());
		r.createCell(index).setCellValue("X");
		
		//DEBUGGING
		for(int i = 0; i < index; i++) System.out.print("-");
		System.out.println(node.getNodeName());
		
		if(node.getNodeType().equals("service")) {
			r.createCell(max_index + 1).setCellValue(node.getNodeId());
		} else {
			r.createCell(max_index + 1).setCellValue("");
		}
		r.createCell(max_index + 2).setCellValue(node.getNodeName());
		r.createCell(max_index + 3).setCellValue(node.getNodeType());
		r.createCell(max_index + 4).setCellValue(node.getGroupType());
		r.createCell(max_index + 5).setCellValue(node.getFlowType());
		if(node.getResource() != null) {
			r.createCell(max_index + 6).setCellValue(node.getResource().getId());
		} else {
			r.createCell(max_index + 6).setCellValue("");
		}
		sheet = recursiveMenu(sheet, node.getNodes(), index+1, node_index);
		sheet = recursiveMenu(sheet, nodes, index, node_index + 1);
		return sheet;
	}
	
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
}
