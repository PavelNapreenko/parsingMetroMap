import java.util.Objects;

public class Line implements Comparable<Line> {

  private final String number;
  private final String name;

  public Line(String number, String name) {
    this.number = number;
    this.name = name;
  }

  public String getNumber() {
    return number;
  }

  public String getName() {
    return name;
  }

  @Override
  public int compareTo(Line line) {
    int lineComparison = number.compareTo(line.getNumber());
    if(lineComparison != 0) {
      return lineComparison;
    }
    return name.compareToIgnoreCase(line.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Line line = (Line) o;
    return Objects.equals(number, line.number) &&
        Objects.equals(name, line.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, name);
  }
}