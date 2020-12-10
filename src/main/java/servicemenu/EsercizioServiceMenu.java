package servicemenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import it.omicron.esercizio.MenuContent;
import it.omicron.esercizio.MenuNode;

public class EsercizioServiceMenu {
	
	private static final List<String>labels = Arrays.asList("ServiceId", "NodeName", "NodeType", "GroupType", "FlowType", "ResourceId");
	
	public static void main(String[] args) {
		
		// Creazione di un oggetto JsonElement.
		JsonElement element = readFromFile();

		// Parsing di un file JSON per recuperare i dati.
		MenuContent menu = parseFile(element);

		// Creazione di un file Excel vuoto con il nome corretto.
		createExcel(menu);

		// Metodo usato per Debugging del metodo ricorsivo.
		// recursivePrint(menu.getNodes(), 0);

	}

	public static JsonElement readFromFile() {

		// Creo elemento JsonElement nullo.

		JsonElement element = null;

		// Se no eccezioni, ritorno element.

		File f = new File("input" + File.separator + "ServiceMenu.json");
		try {
			element = JsonParser.parseReader((new FileReader(f)));
			
		//Se file non esiste, chiudo l'applicazione.
			
		} catch (FileNotFoundException e) {
			System.out.println("EXCEPTION: no such file!");
			System.exit(1);
		}

		return element;
	}

	public static MenuContent parseFile(JsonElement element) {
		Gson gson = new Gson();
		MenuContent menu = null;
		try {
			menu = gson.fromJson(element, MenuContent.class);
		
		//Se file non JSON, chiudo l'applicazione.
			
		} catch (JsonSyntaxException e) {
			System.out.println("EXCEPTION: This element is not a valid JSON file!");
			System.exit(1);
		}
		return menu;
	}

	// Metodo per creazione di un foglio excel completo. Implementata aggiunta
	// dinamica di una colonna.

	public static void createExcel(MenuContent menu) {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("Menu " + menu.getVersion());
		createFirstRow(wb);
		recursiveMenu(sheet, menu.getNodes(), 0);
		makeRowBoldAndAutofitColumns(wb);
		saveExcel(wb);

	}

	// Crea e formatta in maniera corretta la prima riga. 

	public static void createFirstRow(Workbook wb) {
		Row row = wb.getSheetAt(0).createRow(0);
		row.createCell(0).setCellValue(0);
		for(int i = 1; i < labels.size() + 1; i++) {
			row.createCell(i).setCellValue(labels.get(i-1));
		}
	}

	// Questo metodo aggiunge una colonna vuota dinamicamente, per poter aumentare
	// il livello di profondità di un nodo.
	// Sposta tutte le 6 celle numeriche/stringhe in avanti di uno per poter fare
	// posto a un nuovo indice.

	public static Sheet addNewColumn(Sheet sheet, int index) {
		int columns = sheet.getRow(0).getPhysicalNumberOfCells();
		for (Row r : sheet) {
			r.createCell(columns);
			for (int y = columns; y > columns - labels.size(); y--) {
				Cell c = r.getCell(y - 1);
				if (c.getCellType() == CellType.STRING) {
					r.getCell(y).setCellValue(r.getCell(y - 1).getStringCellValue());
				} else {
					r.getCell(y).setCellValue(r.getCell(y - 1).getNumericCellValue());
				}
			}
			r.getCell(columns - labels.size()).setCellValue("");
		}
		sheet.getRow(0).getCell(columns - labels.size()).setCellValue(index);
		return sheet;
	}

	// Metodo ricorsivo per gestione
	public static void recursiveMenu(Sheet sheet, List<MenuNode> nodes, int index) {
		// Caso base 1: Lista Nodes nulla, ritorno.
		if (nodes == null)
			return;

		// Caso base 2: Node null, ritorno.
		MenuNode node = null;
		if (nodes.size() != 0) {
			node = nodes.remove(0);
		} else
			return;

		// Node non nullo, elaboro informazioni.
		int max_index = sheet.getRow(0).getLastCellNum() - labels.size() - 1;
		// Controllo se devo aggiungere una colonna, se sì chiamo il metodo per
		// aggiungerla.
		if (index > max_index) {
			sheet = addNewColumn(sheet, index);
			max_index = index;
		}

		// Creo una nuova riga assegno il valore X alla casella corretta.
		Row r = sheet.createRow(sheet.getPhysicalNumberOfRows());
		r.createCell(index).setCellValue("X");

		// Scrittura delle restanti celle utilizzando le informazioni del nodo "node".
		populateRow(node, r, max_index);

		// Chiamate ricorsive e chiamata finale.
		recursiveMenu(sheet, node.getNodes(), index + 1);
		recursiveMenu(sheet, nodes, index);
	}

	// Semplice metodo che scrive il WorkBook attuale su un file chiamato
	// ServiceMenu situato nella cartella "input".
	// ATTENZIONE: da implementare tutt i casi eccezionali, per ora.

	public static void saveExcel(Workbook wb) {
		File f = new File("output");
		if (!f.exists())
			f.mkdir();
		f = new File(f.getPath() + File.separator + "ServiceMenu.xlsx");
		try (OutputStream fileOut = new FileOutputStream(f)) {
			wb.write(fileOut);
		} catch (IOException e) {
			System.out.println("EXCEPTION: Couldn't write this!");
			System.exit(1);
		}
	}
	
	//Spostato codice per popolare la riga in una funzione a parte per maggiore pulizia.
	
	public static void populateRow(MenuNode node, Row r, int max_index) {
		if (node.getNodeType().equals("service")) {
			r.createCell(max_index + 1).setCellValue(node.getNodeId());
		} else {
			r.createCell(max_index + 1).setCellValue("");
		}
		r.createCell(max_index + 2).setCellValue(node.getNodeName());
		r.createCell(max_index + 3).setCellValue(node.getNodeType());
		r.createCell(max_index + 4).setCellValue((node.getGroupType() != null) ? node.getGroupType() : "");
		r.createCell(max_index + 5).setCellValue((node.getFlowType() != null) ? node.getFlowType() : "");
		if (node.getResource() != null) {
			r.createCell(max_index + 6).setCellValue(node.getResource().getId());
		} else {
			r.createCell(max_index + 6).setCellValue("");
		}
	}

	public static void recursivePrint(List<MenuNode> nodes, int index) {

		// Caso Base 1, se nullo ritorno.
		if (nodes == null)
			return;

		// Caso Base 2, controllo che il nodo interno corrente esista.
		MenuNode node = null;
		if (nodes.size() != 0) {
			node = nodes.remove(0);
		} else
			return;

		// Stampa.
		for (int i = 0; i < index; i++)
			System.out.print("-");
		System.out.println("* " + node.getNodeName());

		recursivePrint(node.getNodes(), index + 1);
		recursivePrint(nodes, index);

	}

	public static void makeRowBoldAndAutofitColumns(Workbook wb) {
		// Creo uno stile e un font per la prima riga.
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);

		// Prelevo la prima riga e setto lo stile in ogni cella della riga.
		Row row = wb.getSheetAt(0).getRow(0);
		for (int i = 0; i < row.getLastCellNum(); i++) {
			row.getCell(i).setCellStyle(style);
		}

		// AutoFit dei campi, aumentanta leggibilità.
		for (int i = 0; i < wb.getSheetAt(0).getRow(0).getPhysicalNumberOfCells(); i++)
			wb.getSheetAt(0).autoSizeColumn(i);
	}
}
