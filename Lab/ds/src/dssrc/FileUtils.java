// Bindhu Shree Hadya Ravi - 1001699836

package dssrc;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;

// TO READ CONVERSIONS FROM TEXTFILE
public class FileUtils {

	public static Hashtable<String, String> readTableFromFile(String fileName) {
		Hashtable<String, String> res = new Hashtable<String, String>();

		try {

			List<String> lines = Files.readAllLines(Paths.get(fileName));
			for (String line : lines) {
				res.put(line.split(" ")[0], (line.split(" ")[1]));
			}

			return res;

		} catch (IOException e) {

		}

		return null;

	}

}
