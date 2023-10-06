import Csv.*;

public class App {
    public static void main(String[] args) throws Exception {
        Spreadsheet spreadsheet = new Spreadsheet("/Users/jordanwanlass/Desktop/Windfall/src/input.csv");
        spreadsheet.evalSpreadsheet();
        spreadsheet.printSpreadsheet();
        spreadsheet.downloadSpreadsheet();
    }
}
