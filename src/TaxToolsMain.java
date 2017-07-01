import java.io.IOException;

public class TaxToolsMain {

	public static void main(String args[]) {
		try {

			// different files
			String filename = "data/JohnLocke2013.pdf";

			// instantiate object
//			BruteTaxForm myForm = new BruteTaxForm(filename);

//			System.out.println(myForm.getAllText());

//			System.out.println(myForm.getPeople());
			
			OnlineFilingNavigator nav = new OnlineFilingNavigator("860292099");
			String myURL = nav.getIndexURL("2013");
			nav.getIdentifier(nav.MY_EIN, myURL);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
