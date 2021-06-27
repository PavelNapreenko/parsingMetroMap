import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StationsCount {

  private static final JSONParser parser = new JSONParser();

  public static void parseJsonFile(String URL) throws ParseException {

    JSONObject jsonData = (JSONObject) parser.parse(getJsonFile(URL));
    JSONObject stationsObject = (JSONObject) jsonData.get("stations");
    JSONArray connectionsArray = (JSONArray) jsonData.get("connections");

    stationsObject.keySet().forEach(lineNumberObject -> {
      JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
      int stationsCount = stationsArray.size();
      System.out.printf("Номер линии:  %s         -     Кол-во станций:     %s\n",lineNumberObject,stationsCount);
    });

    System.out.printf("Кол-во пересадок в Москвском Метро: %d",connectionsArray.size());
  }

  private static String getJsonFile(String URL) {
    StringBuilder builder = new StringBuilder();
    try {
      List<String> lines = Files.readAllLines(Paths.get(URL));
      lines.forEach(builder::append);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return builder.toString();
  }
}
