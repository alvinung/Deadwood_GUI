import java.util.HashMap;

/* Class PublicRelations:
 * Responsible for handling all interactions with the user
 * Handles user input data and printing all data to the user
 */
public class PublicRelations {
  private String printables = "";
  private String print = "";

  /*============================= Public Methods =============================*/

  /* print
   * Postconditions:
   * - print string
   */
  public void setPrint(String s) {
    printables = s;
  }

  public void setPrintLn1(String s) {
    print = s;
  }

  public void displayLn1() {
    fill(153, 102, 51);
    textFont(reguF, 10);
    textAlign(LEFT);
    text(print, (float)width-(infoBarSize-12), height - 265, infoBarSize-24, 15);
  }

  public void display() {
    fill(153, 102, 51);
    textFont(reguF, 10);
    textAlign(LEFT);
    text(printables, (float)width-(infoBarSize-12), height - 250, infoBarSize-24, 15);
  }
}