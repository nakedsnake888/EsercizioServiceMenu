package it.omicron.main;

import java.io.*;
import java.util.*;

import com.google.gson.stream.*;
import com.google.gson.*;
import it.omicron.esercizio.*;

public class EsercizioServiceMenu {
	
	public static void main(String[] args) {
		
		//Creazione di un oggetto JsonElement.
		JsonElement element = readFromFile();
		
		//Parsing di un file JSON per recuperare i dati, da implementare.
		MenuContent menu = parseFile(element);
		
		//Stampa di un valore di uno dei nodi interni, reachable.
		System.out.println(menu.getNodes().get(0).getNodes().get(0).getNodes().get(1).getNodeName());
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
}
