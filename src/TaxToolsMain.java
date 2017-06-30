package com.taxtools.main;

import java.io.IOException;

public class TaxToolsMain {

	static public void main(String args[]) {
		try {

			// different files
			String filename = "data/JohnLocke2013.pdf";

			// instantiate object
			BruteTaxForm myForm = new BruteTaxForm(filename);

//			System.out.println(myForm.getAllText());

			System.out.println(myForm.getPeople());
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
