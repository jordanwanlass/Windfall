package Csv;
import java.util.ArrayList;

public class Cell {
  private String name;
  private String input;
  private String output;
  private ArrayList<String> references = new ArrayList<>();

  public Cell(String name, String input) {
    this.name = name;
    this.input = input;
  }

  public String getName() {
    return this.name;
  }
  
  public String getInput() {
    return this.input;
  }

  public ArrayList<String> getReferences() {
    return this.references;
  }

  public String getOutput() {
    return this.output;
  }

  public void setOutput(String output) {
    this.output = output;
  }
}