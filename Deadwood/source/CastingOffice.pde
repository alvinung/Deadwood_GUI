/* Class CastingOffice
 * Responsible for handing all information on the Casting Office
 * Implements checks on raising rank, displaying rank costs and neighboring rooms
 */

public class CastingOffice extends Room {
  private final int[] dollars;
  private final int[] credits;
  private final Coord[] dollarsLoc;
  private final Coord[] creditsLoc;

  public class Request {
    private String type;
    private int rank;

    public Request(String t, int r) {
      type = t;
      rank = r;
    }

    public int getRequestedRank() {
      return rank;
    }

    public String getRequestType() {
      return type;
    }
  }

  /*============================= Constructors =============================*/

  public CastingOffice(String n, Coord[] spts) {
    super(n, spts);

    this.dollars = new int[7];
    dollars[2] = 4;
    dollars[3] = 10;
    dollars[4] = 18;
    dollars[5] = 28;
    dollars[6] = 40;

    this.credits = new int[7];
    credits[2] = 5;
    credits[3] = 10;
    credits[4] = 15;
    credits[5] = 20;
    credits[6] = 25;

    this.dollarsLoc = new Coord[7];
    dollarsLoc[2] = new Coord(10.63, 8.5);
    dollarsLoc[3] = new Coord(10.63, 7.08);
    dollarsLoc[4] = new Coord(10.63, 6.07);
    dollarsLoc[5] = new Coord(10.63, 5.31);
    dollarsLoc[6] = new Coord(10.63, 4.72);

    this.creditsLoc = new Coord[7];
    creditsLoc[2] = new Coord(7.46, 8.5);
    creditsLoc[3] = new Coord(7.46, 7.08);
    creditsLoc[4] = new Coord(7.46, 6.07);
    creditsLoc[5] = new Coord(7.46, 5.31);
    creditsLoc[6] = new Coord(7.46, 4.72);
  }

  /*============================= Public Methods =============================*/

  public void requestRankUp(Request r, Actor anActor) {
    String type = r.getRequestType();
    int requestedRank = r.getRequestedRank();
    print(type);
    print(requestedRank);

    if (type.equals("dollars")) {
      if (dollars[requestedRank] <= anActor.checkWallet()) {
        anActor.setRank(requestedRank);
        anActor.setMoney(anActor.checkWallet() - dollars[requestedRank]);

        getPR().setPrint("Actor has ranked up to " + requestedRank + "\nYou have " + anActor.checkWallet() + " dollars left");
      } else {
        getPR().setPrintLn1("Insufficient dollars");
      }
    } else if (type.equals("credits")) {
      if (credits[requestedRank] <= anActor.viewCredits()) {
        anActor.setRank(requestedRank);
        anActor.setCredits(anActor.viewCredits() - credits[requestedRank]);

        getPR().setPrint("Actor has ranked up to " + requestedRank + "\nYou have " + anActor.viewCredits() + " credits left");
      } else {
        getPR().setPrintLn1("Insufficient credits");
      }
    }
  }

  public void setCoord(float x, float y) {
    super.gridX = x; 
    super.gridY = y;
  }

  /* display
   * display circles to indicate which rand increase mouse is over
   */
  public void display() {

    float xVal;
    float yVal;

    for (int i = 2; i <= 6; i++) {
      // Dollars
      xVal = super.getGridX() + (width - infoBarSize) / this.dollarsLoc[i].xSF;
      yVal = super.getGridY() + height / this.dollarsLoc[i].ySF;

      if (mouseX >= xVal - ((width - infoBarSize) / 66.67) && mouseX <= xVal + ((width - infoBarSize) / 66.67) && 
        mouseY >= yVal - (height / 60.00) && mouseY <= yVal + (height / 60.00)) {

        noStroke();
        fill(100, 100);
        ellipse(xVal, yVal, (width - infoBarSize) / 66.67, height / 47.22);
      }
      // Credits
      xVal = super.getGridX() + (width - infoBarSize) / this.creditsLoc[i].xSF;
      yVal = super.getGridY() + height / this.creditsLoc[i].ySF;

      if (mouseX >= xVal - ((width - infoBarSize) / 66.67) && mouseX <= xVal + ((width - infoBarSize) / 66.67) && 
        mouseY >= yVal - (height / 60.00) && mouseY <= yVal + (height / 60.00)) {

        noStroke();
        fill(100, 100);
        ellipse(xVal, yVal, (width - infoBarSize) / 66.67, height / 47.22);
      }
    }
  }

  /* rankDetails
   * returns type and amount of rank request
   */
  public Request rankDetails() {

    float xVal;
    float yVal;

    for (int i = 2; i <= 6; i++) {
      // Dollars
      xVal = super.getGridX() + (width - infoBarSize) / this.dollarsLoc[i].xSF;
      yVal = super.getGridY() + height / this.dollarsLoc[i].ySF;

      if (mouseX >= xVal - ((width - infoBarSize) / 66.67) && mouseX <= xVal + ((width - infoBarSize) / 66.67) && 
        mouseY >= yVal - (height / 60.00) && mouseY <= yVal + (height / 60.00)) {

        Request req = new Request("dollars", i);

        return req;
      }

      // Credits
      xVal = super.getGridX() + (width - infoBarSize) / this.creditsLoc[i].xSF;
      yVal = super.getGridY() + height / this.creditsLoc[i].ySF;

      if (mouseX >= xVal - ((width - infoBarSize) / 66.67) && mouseX <= xVal + ((width - infoBarSize) / 66.67) && 
        mouseY >= yVal - (height / 60.00) && mouseY <= yVal + (height / 60.00)) {

        Request req = new Request("credits", i);

        return req;
      }
    }

    return null;
  }
}