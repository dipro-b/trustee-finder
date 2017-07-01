import java.util.Map;

public interface ITaxForm {

	/**
	 * Returns the text of the taxform
	 * @return text
	 */
	public String getAllText();

	/**
	 * Processes the tax form and returns a dictionary of staff
	 * and their positions
	 *
	 * @return dictionary of people
	 */
	public Map<String, String> getPeople();

}
