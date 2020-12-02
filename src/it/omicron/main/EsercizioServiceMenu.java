package it.omicron.main;

import java.io.*;
import com.google.gson.stream.JsonReader;
import it.omicron.esercizio.*;

public class EsercizioServiceMenu {
	
	public static void main(String[] args) {
		
		/*Creazione di un oggetto Json Reader.
		 *Se non trovato il file, programma lancia eccezione e si ferma. 
		 *Se trovato, stampa messaggio di avvenuta "ricezione" (il file nella cartella Input esiste).
		 */
		
		try {
			JsonReader reader = new JsonReader(new FileReader("input/ServiceMenu.json"));
		} catch (FileNotFoundException e) {
			System.out.println("EXCEPTION: no such file!");
			return;
		}
		
		//Parsing di un file JSON per recuperare i dati, da implementare.
		
		System.out.println("Reached the file!");
	}
}
