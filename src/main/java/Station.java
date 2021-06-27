import java.util.Objects;

public class Station implements Comparable<Station> {

  private final String line;
  private final String name;

  public Station(String line, String station) {
    this.name = station;
    this.line = line;
  }

  public String getLine() {
    return line;
  }

  public String getName() {
    return name;
  }

  @Override
  public int compareTo(Station station)
  {
    int lineComparison = line.compareTo(station.getLine());
    if(lineComparison != 0) {
      return lineComparison;
    }
    return name.compareToIgnoreCase(station.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(line, name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Station station = (Station) o;
    return Objects.equals(line, station.line) &&
        Objects.equals(name, station.name);
  }

  @Override
  public String toString()
  {
    return name;
  }
}