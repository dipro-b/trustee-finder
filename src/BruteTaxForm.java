package com.taxtools.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class BruteTaxForm implements ITaxForm {

	protected PDDocument myPDF; // stores the PDF of the taxform
	protected TreeMap<String, String> myPeople;  // keys = name, values = job
	protected HashMap<Integer, HashSet<String>> myNames;

	// throws IOException if file does not exist, or is encrypted.
	public BruteTaxForm(String filename) throws IOException {
		try {
			myPDF = this.loadForm(filename);
			myNames = new HashMap<Integer, HashSet<String>>();
		}
		catch (IOException e) {
			throw e;
		}
	}

	public BruteTaxForm(PDDocument form) {
		myPDF = form;
		myNames = new HashMap<Integer, HashSet<String>>();
	}

	/**
	 * Method to load the document
	 *
	 * @param filename - file to be loaded
	 * @return a PDDocument
	 * @throws IOException if file is encrypted or invalid.
	 */

	private PDDocument loadForm(String filename) throws IOException {
		try {
			// load file, check if it is valid
			File pdf = new File(filename);
			if (!pdf.exists()) {
				throw new IOException("File does not exist!");
			}

			// use parser to open document
			PDFParser parser = new PDFParser(new RandomAccessFile(pdf, "r")); // open for reading only
			parser.parse();
			COSDocument cosDoc = parser.getDocument();
			PDDocument inputDocument = new PDDocument(cosDoc);

			// check if encrypted
			if (inputDocument.isEncrypted()) {
	            inputDocument.close();
				throw new IOException("Cannot open PDF document because it is encrypted.");
			}
			return inputDocument;
		}
		catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Function to create a dictionary that maps hashcode to name, used to compare strings
	 * in the form to names
	 *
	 */
	private void addNames(String filename) {

		// load file, initialize scanner
		try {
			File nameFile = new File(filename);
			Scanner names = new Scanner(nameFile);
			int hash = 0;

			// loop through file, adding words to dictionary
			while (names.hasNext()) {
				String entry = names.next().toLowerCase();

				// if scanner finds hash (comment), skip line
				if (entry.equals("#")) {
					names.nextLine();
					continue;
				}

				hash = entry.hashCode();
				if (myNames.containsKey(hash)) {
					HashSet<String> val = myNames.get(hash);
					val.add(entry);
					myNames.put(hash, val);
				}
				else {
					HashSet<String> val = new HashSet<>();
					val.add(entry);
					myNames.put(hash, val);
				}
			}

			// close scanner, return dictionary
			names.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to parse the text from a file. Returns a string
	 * with all the text in the file.
	 *
	 * @param inputDocument : document to be parsed
=	 */
	public String getAllText() {
		try {
			PDDocument inputDocument = this.myPDF;

			// check null
			if (inputDocument == null) {
				throw new IOException("Document to parse is null!");
			}

			// check encrypted
			if (inputDocument.isEncrypted()) {
				inputDocument.close();
				throw new IOException("Cannot open PDF document because it is encrypted.");
			}

			// initialize text stripper, write to output stream
			PDFTextStripper textStripper = new PDFTextStripper();

//			// set start and end pages
//			textStripper.setStartPage(1);
//			textStripper.setEndPage(2);

			// print test
			String ans = textStripper.getText(inputDocument);
			System.out.println("PDF Contents:\n"+ ans);
			return ans;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Helper function to check if a string is a name in the dictionary
	 * @param word - word to check
	 * @return true if name in dict, false if not
	 */
	private boolean isName(String word) {
		word = word.toLowerCase();
		int wordHash = word.hashCode();
		if (!myNames.containsKey(wordHash))
			return false;
		HashSet<String> names = myNames.get(wordHash);
		if (names.contains(word))
			return true;
		return false;
	}

	/**
	 * Helper function to check if string contains alphabets
	 * @param word
	 * @return
	 */
	private boolean isAlph(String word) {
		if (word.toLowerCase().equals(word.toUpperCase()))
			return false;
		for (int i = 0; i < word.length(); i++) {
			Character c = word.charAt(i);
			if (!Character.isLetter(c))
				return false;
		}
		return true;
	}

	/**
	 * Function that pulls names from the PDF
	 *
	 */
	public Map<String, String> getPeople() {

		//initialize scanner for user input
		Scanner userInput = new Scanner(System.in);

		// make dictionaries of names
		addNames("res/MaleNames.txt");
		addNames("res/FemaleNames.txt");

//		System.out.println(myNames);

		// ask user which pages to scan
		System.out.println("Enter the page you want to get staff from "
				+ "(to search all pages, type 0):");
		int page = Integer.parseInt(userInput.next());
		userInput.close();

		// get text from pages
		String text = "";
		try {
			PDFTextStripper stripper = new PDFTextStripper();

			if (page != 0) {
				stripper.setStartPage(page);
				stripper.setEndPage(page);
			}
			text = stripper.getText(this.myPDF);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// scan text for names
		Scanner textScanner = new Scanner(text);
		TreeSet<String> names = new TreeSet<String>();

		// initialize pointers for "prev" and "next"
		String previousLine = "";
		String currentLine = "";
		String nextLine = textScanner.nextLine();


		// traverse line by line
		// note: scanner points to the line ahead of the 'currentLine'
		while (textScanner.hasNextLine()) {

			previousLine = currentLine;
			currentLine = nextLine;
			nextLine = textScanner.nextLine();
			Scanner lineScanner = new Scanner(currentLine);


			// check line for names
			while (lineScanner.hasNext()) {
				String word = lineScanner.next();

				// if line contains a name, add to set and move on
				if (isName(word)) {
					System.out.println("Found a name: " + word);
					names.add(previousLine);
					names.add(currentLine);
					names.add(nextLine);
				}
			}
			lineScanner.close();
		}
		textScanner.close();

//		System.out.println(text);
//		System.out.println("Length of text is: " + text.length());
		for (String lines : names) {
			System.out.println(lines);
		}
		return null;
	}

//	public static void main(String[] args) {
//		myNames = new HashMap<Integer, HashSet<String>>();
//		addNames("res/MaleNames.txt");
//		System.out.println(myNames.toString());
//	}
}
