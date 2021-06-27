import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Loader {

  private static final String metroMapURL = "https://www.moscowmap.ru/metro.html#lines";
  private static final String jsonFileURL = "src/main/resources/map.json";
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private static final Comparator<List<Station>> stationListComparator = (o1, o2) -> {
    if (o1.get(0).equals(o2.get(0)) && o1.get(1).equals(o2.get(1))) {
      return 0;
    }
    return 1;
  };

  private static final Comparator<Station> stationComparator = Comparator
    .comparing(Station::getLine);

  public static Map<String, Object> metro = new LinkedHashMap<>();
  public static Map<String, List<String>> stations = new LinkedHashMap<>();
  public static Set<String> lineNumbers = new LinkedHashSet<>();
  public static Set<Line> lines = new LinkedHashSet<>();
  public static Set<List<Station>> connections = new TreeSet<>(stationListComparator);

  public static void main(String[] args) throws IOException, ParseException {

    loadingHTML();

    StationsCount.parseJsonFile(Loader.jsonFileURL);
  }

  private static void loadingHTML() throws IOException {
    Document doc =  Jsoup.connect(Loader.metroMapURL).maxBodySize(0).timeout(10 * 1000).get();
    createMetroMap(doc);
  }

  private static void createMetroMap(Document doc) {
    Elements linesElements = doc.select("span.js-metro-line");
    Elements stationsElements = doc.select("div.js-metro-stations");
    Elements connectionsElements = doc.select("span.t-icon-metroln");

    linesElements.forEach(tr -> lineNumbers.add(tr.attr("data-line")));

    lineNumbers.forEach(line -> {
      linesElements.stream().filter(tr -> tr.attr("data-line").equals(line))
          .findFirst().ifPresent(t -> lines.add(new Line(
          line,
          t.select("span.js-metro-line").text())));

      stationsElements.stream().filter(tr -> tr.attr("data-line").equals(line))
          .forEach(tr -> stations.put(line, tr.select("span.name").eachText()));

      connectionsElements.stream()
          .filter(element -> element.hasAttr("title"))
          .forEach(t -> {
            List<Station> stationsOfConnection = new LinkedList<>();
            String lineNum = t.parents().attr("data-line");
            String stationName = t.siblingElements().get(1).text();
            String connectionLineNum = t.attr("class")
                .replaceAll("[^[A-Z]\\d]", "");
            String connectionStationName = t.attr("title");
            connectionStationName = connectionStationName
                .substring(connectionStationName.indexOf("«") + 1,
                    connectionStationName.indexOf("»"));
            stationsOfConnection.add(new Station(lineNum, stationName));
            stationsOfConnection.add(new Station(connectionLineNum, connectionStationName));
            stationsOfConnection.sort(stationComparator);
            connections.add(stationsOfConnection);
          });
    });

    Set<List<Station>> con1 = new HashSet<>(connections);

    metro.put("stations", stations);
    metro.put("lines", lines);
    metro.put("connections", con1);

    writingJsonFile(metro);
  }

  private static void writingJsonFile(Map<String, Object> map) {
    try (FileWriter file = new FileWriter(Loader.jsonFileURL)) {
      file.write(GSON.toJson(map));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
