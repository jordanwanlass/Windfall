package Csv;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.io.BufferedReader;

public class Spreadsheet {
  private final String[] cellStrings = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
      "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
  private final String ADD = "+";
  private final String SUBTRACT = "-";
  private final String DIVIDE = "/";
  private final String MULTIPLY = "*";
  private final ArrayList<String> operators = new ArrayList<String>(List.of(ADD, SUBTRACT, DIVIDE, MULTIPLY));
  private TreeMap<String, Cell> cells = new TreeMap<String, Cell>();
  private int rowCount = 1;
  private int colCount = 0;

  public Spreadsheet(String inputFile) {
    try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        colCount = values.length;
        for (int i = 0; i < values.length; i++) {
          cells.put(cellStrings[i] + rowCount, new Cell(cellStrings[i] + rowCount, values[i]));
        }
        rowCount++;
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public TreeMap<String, Cell> getCells() {
    return this.cells;
  }

  public void printSpreadsheet() {
    for (Map.Entry<String, Cell> cell : this.getCells().entrySet()) {
      System.out.println(
          cell.getKey() + ": input - " + cell.getValue().getInput() + " output - " + cell.getValue().getOutput());
    }
  }

  public void downloadSpreadsheet() {
    try {
      PrintWriter printWriter = new PrintWriter("/Users/jordanwanlass/Desktop/Windfall/src/output.csv");
      int row = 1;
      while (row < rowCount) {
        ArrayList<String> line = new ArrayList<>();
        for (int i = 0; i < colCount; i++) {
          line.add(cells.get(cellStrings[i] + row).getOutput());
        }
        printWriter.println(line.stream().collect(Collectors.joining(",")));
        row++;
      }
      printWriter.flush();
      printWriter.close();
    } catch (Exception e) {

    }

  }

  public void evalSpreadsheet() {
    for (Map.Entry<String, Cell> cell : this.getCells().entrySet()) {
      try {
        cell.getValue().setOutput(String.valueOf(evalCell(cell.getValue(), cell.getValue().getReferences())));
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private Double evalCell(Cell cell, ArrayList<String> references) throws Exception {
    ArrayList<String> inputValues = separateInput(cell.getInput());
    if (inputValues.size() == 1) {
      return cells.containsKey(inputValues.get(0)) ? evalCell(cells.get(inputValues.get(0)), references)
          : Double.valueOf(cell.getInput());
    }
    ArrayList<String> values = new ArrayList<>();
    int i = 0;
    while (i < inputValues.size() - 1) {
      if (DIVIDE.equals(inputValues.get(i + 1)) || MULTIPLY.equals(inputValues.get(i + 1))) {
        values.add(String
            .valueOf(evalExpression(inputValues.get(i), inputValues.get(i + 2), inputValues.get(i + 1), references)));
        i += 3;
      } else {
        values.add(inputValues.get(i));
        i++;
      }
    }

    Double result = values.size() == 1 ? Double.valueOf(values.get(0)) : 0.0;
    while (values.size() > 1) {
      result = evalExpression(values.get(0), values.get(2), values.get(1), references);
      values.remove(0);
      values.remove(0);
      values.remove(0);
      values.add(0, String.valueOf(result));
    }

    return Double.valueOf(values.get(0));
  }

  private Double evalExpression(String left, String right, String operator, ArrayList<String> references)
      throws Exception {
    Double leftVal = getExpressionValue(left, references);
    Double rightVal = getExpressionValue(right, references);

    switch (operator) {
      case DIVIDE:
        return leftVal / rightVal;
      case MULTIPLY:
        return leftVal * rightVal;
      case ADD:
        return leftVal + rightVal;
      case SUBTRACT:
        return leftVal - rightVal;
    }
    return 0.0;
  }

  private Double getExpressionValue(String value, ArrayList<String> references) throws Exception {
    if (cells.containsKey(value)) {
      if (references.contains(value)) {
        throw new Exception("Your spreadsheet has a circular dependency");
      }
      references.add(value);
      return evalCell(cells.get(value), references);
    }
    return Double.valueOf(value);
  }

  private ArrayList<String> separateInput(String input) {
    ArrayList<String> values = new ArrayList<>();
    char[] inputArr = input.toCharArray();
    String value = "";
    for (int i = 0; i < inputArr.length; i++) {
      if (operators.contains(String.valueOf(inputArr[i]))) {
        values.add(value);
        values.add(String.valueOf(inputArr[i]));
        value = "";
        continue;
      }
      value += inputArr[i];
    }
    values.add(value);
    return values;
  }
}
