// Bindhu Shree Hadya Ravi - 1001699836

package dssrc;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class CreateConversionSet {

	private ArrayList<HashMap<String, Float>> conversationChart = new ArrayList<HashMap<String, Float>>();
	private HashMap<String, Float> setA;
	private HashMap<String, Float> setB;
	private HashMap<String, Float> setC;

	public ArrayList<HashMap<String, Float>> getConversationSets() {
		return conversationChart;
	}

	public HashMap<String, Float> getSetA() {
		return conversationChart.get(0);
	}

	public HashMap<String, Float> getSetB() {
		return conversationChart.get(1);
	}

	public HashMap<String, Float> getSetC() {
		return conversationChart.get(2);
	}

	CreateConversionSet() throws IOException {
		try {
//			  setA=new ConversionSet();
//			  setB=new ConversionSet();
//			  setC=new ConversionSet();
			HashMap<String, Float> storehashmap = new HashMap<String, Float>();
			File myObj = new File("conversionset2.txt");
			Scanner myReader = new Scanner(myObj);
			int line = 0;
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				String[] splitdata = data.split(" ");

				for (int i = 0; i < splitdata.length; i = i + 2) {

					storehashmap.put(splitdata[i], Float.parseFloat(splitdata[i + 1]));

				}
				conversationChart.add(line, storehashmap);
				System.out.println(data);

				line++;
				System.out.println("-----------------------------------------------------------------------------");
			}
			System.out.print("arraylist size" + conversationChart.size());
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			CreateConversionSet instance = new CreateConversionSet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
