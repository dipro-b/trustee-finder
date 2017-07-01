import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TaxXMLHandler extends DefaultHandler {
	
	// initialize list
	private ArrayList<Trustee> trusteeList = null;
	private Trustee dummyTrustee = null;
	
	// getter method
	public ArrayList<Trustee> getTrustees() {
		return trusteeList;
	}
	
	// boolean vars to keep track of where we are
	boolean isTrustee = false;
	boolean hasName = false;
	boolean hasJob = false;
	
	@Override
	public void startElement(String uri,  String localName, String qName, Attributes attributes) throws SAXException {

		// if trustee section, initialize a trustee
		if (qName.equalsIgnoreCase("Form990PartVIISectionAGrp")) {
			isTrustee = true;
			dummyTrustee = new Trustee();
			if (trusteeList == null)
				trusteeList = new ArrayList<Trustee>();
		}
		else if (qName.equalsIgnoreCase("PersonNm") && (isTrustee)) {
			hasName = true;
			if (dummyTrustee == null) {
				dummyTrustee = new Trustee();
			}
		}
		else if (qName.equalsIgnoreCase("TitleTxt") && (isTrustee)) {
			hasJob = true;
			if (dummyTrustee == null) {
				dummyTrustee = new Trustee();
			}
		}
	}
	
	@Override
	public void endElement(String uri,  String localName, String qName) throws SAXException {
		
		// null test is in case xml file is malformed/messy
		if (qName.equalsIgnoreCase("Form990PartVIISectionAGrp") && (dummyTrustee != null)) {
			trusteeList.add(dummyTrustee);
			dummyTrustee = null;
			System.out.println("Trustee added!");
		}
		else if (qName.equalsIgnoreCase("TitleTxt")) {
			trusteeList.add(dummyTrustee);
			dummyTrustee = null;
			System.out.println("Trustee added!");
		}
	}
	
	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		
		// only add if name is trustee (and not some random person)
		if (isTrustee && hasName) {
			dummyTrustee.setName(new String(ch, start, length));
			hasName = false;
		}
		else if (isTrustee && hasJob) {
			dummyTrustee.setJob(new String(ch, start, length));
			hasJob = false;
		}
	}
	
}
