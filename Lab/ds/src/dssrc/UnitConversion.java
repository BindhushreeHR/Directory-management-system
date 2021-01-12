// Bindhu Shree Hadya Ravi - 1001699836

package dssrc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.stream.Collectors;

public class UnitConversion {

	private String factor;
	private String unit;

	public String getFactor() {
		return factor;
	}

	public String getUnit() {
		return unit;
	}

	public UnitConversion(String unit, String factor) {
		this.factor = factor;
		this.unit = unit;
	}

	public String convert(String srcValue) {
//		return this.factor * srcValue;
		return this.factor;

	}
}

class ConversionSet {

	private ArrayList<UnitConversion> conversions;
	private ArrayList<HashMap<String, String>> results;

	public ConversionSet() {
		conversions = new ArrayList<UnitConversion>();
		results = new ArrayList<HashMap<String, String>>();
	}
	//READS THE CONVERSION CHART AND CHANGES IT INTO A KEY VALUE PAIR, WHICH FURTHUR IS CONSOLIDATED INTO LIST
	public ConversionSet(String filename) {

		Hashtable<String, String> rules = FileUtils.readTableFromFile(filename);
		conversions = new ArrayList<>(rules.entrySet().stream().map(e -> new UnitConversion(e.getKey(), e.getValue()))
				.collect(Collectors.toList()));
		results = new ArrayList<HashMap<String, String>>();
	}

	public void addConversion(String unit, String factor) {
		conversions.add(new UnitConversion(unit, factor));
	}
	//COMPUTE THE CONVERSIONS IN GIVEN QUEUE
	public void compute(String srcValue) {

		HashMap<String, String> _result = new HashMap<String, String>();

		for (UnitConversion unitConversion : conversions) {
			_result.put(unitConversion.getUnit(), unitConversion.convert(srcValue));
		}
		results.add(_result);
	}
	// ONCE THE UPLOADED MESSAGES ARE READ, DELETING FROM THE QUEUE
	public void deleteMessages() {
		if (!results.isEmpty())
			results.clear();
	}

	public String peekResults() {
		String res = "";
		if (!results.isEmpty()) {
			for (int i = 0; i < results.size(); i++) {
				res += results.get(i).entrySet().stream().map(e -> e.getKey() + " ==> " + (e.getValue()))
						.collect(Collectors.joining("  ,  ")) + "   ,    ,  ";
				System.out.println();
			}
		}
		return res;
	}

	public String getResults() {
		String res = peekResults();
		if (!res.equals(""))
			deleteMessages();
		return res;
	}
	// TO CHECK THE QUEUE COUNT
	public int getResultCount() {
		return this.results.size();
	}

}
