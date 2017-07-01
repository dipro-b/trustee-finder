import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class OnlineFilingNavigator {
	
	protected static String MY_EIN;
	
	public OnlineFilingNavigator(String ein) {
		MY_EIN = ein;
	}
	
	public static String getIndexURL(String year) {
		String ans = "https://s3.amazonaws.com/irs-form-990/index_" + year + ".json";
		System.out.println("URL Generated! \nURL = " + ans + "\n");
		return ans;
	}
	
	public static String getIdentifier(String ein, String indexURL) throws Exception {
		URL index = new URL(indexURL);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(index.openStream()));
		Scanner reader = new Scanner(in);
		reader.useDelimiter("\"");
		
		boolean found = false;
		String token = "";
		long sysTime = System.currentTimeMillis();
		long now;
		while (reader.hasNext()) {
			// timer
			now = System.currentTimeMillis();
			if ((now - sysTime) > 5000) {
				System.out.println("Working...");
				sysTime = System.currentTimeMillis();
			}
			
			// parsing logic
			token = reader.next();
			if (token.equals("EIN")) {
				reader.next();
				token = reader.next();
				if (token.equals(ein)) {
					found = true;
					break;
				}
			}
		}
		
		// throw exception if parsed whole document
		if (found == false) {
			reader.close();
			in.close();
			throw new IOException("Entity does not exist! Check EIN number and try again.");
		}
		
		while (!token.equals("URL")) {
			if (!reader.hasNext()) break;
			token = reader.next();
		}
		reader.next();
		String ans = reader.next();
		
		reader.close();
        in.close();
		
        System.out.println("Identifier found! \nId = " + ans + "\n");
		return ans;
	}
	
	public static ArrayList<Trustee> getTrustees(String url) throws Exception {
		// initialize parser
		TaxXMLParserSAX parser = new TaxXMLParserSAX(url);
		
		// look through XML doc for <Form990PartVIISectionAGrp> and <PersonNm>
		ArrayList<Trustee> trustees = parser.parse();
		
		return trustees;
	}
	
	public static void main(String[] args) {
		try {
//			String EIN = "860292099"; // Safari Club Foundation
			String EIN = "860974183"; // Safari Club International
			String url = getIndexURL("2014");
			String id = getIdentifier(EIN, url);
//			System.out.println(id);
			
//			String id = "https://s3.amazonaws.com/irs-form-990/201441359349303644_public.xml";
			getTrustees(id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
