import java.util.HashMap;
import java.util.ArrayList;

/* Class Board:
 * Responsible for handling connecting rooms and setting up the board 
 */
public class Board {

  private float rulesL;
  private float rulesR;
  private float rulesT;
  private float rulesB;  
  private float endX, endY;
  private float rehX, rehY;
  private float actX, actY;
  private color buttonColor = color(250);
  private color highlightColor = color(200);

  boolean endOver = false;
  boolean rehOver = false;
  boolean actOver = false;
  boolean trainOver = false;
  boolean jailOver = false;
  boolean genOver = false;
  boolean secOver = false;
  boolean castOver = false;
  boolean ranchOver = false;
  boolean salOver = false;
  boolean mainOver = false;
  boolean trailOver = false;
  boolean bankOver = false;
  boolean churchOver = false;
  boolean hotelOver = false;

  private HashMap<String, Room> rooms = new HashMap<String, Room>();

  /* class Grid
   * Represents an individual section of the board
   * Currently static places, could be extended to be randomized
   */
  private class Grid {
    private PImage view;
    private ArrayList<Room> rooms = new ArrayList<Room>();
    private float xPos;
    private float yPos;

    Grid (PImage v, ArrayList<Room> rms) {
      view = v;
      rooms = rms;
    }

    void setGrid (float gridX, float gridY) {
      xPos = gridX;
      yPos = gridY;
    }

    /* display
     * Preconditions:
     * - xPos: grid's top left x coordinate
     * - yPos: grid's top left y coordinate
     * Postconditions:
     * - Displays itself
     * - Calls display of each subordinate room
     */
    void display () {
      image(view, xPos, yPos, (width-infoBarSize)/2, height/2);
      for (Room r : rooms) {
        if (r != null) {
          r.setCoord(xPos, yPos);
          r.display();
        }
      }
    }
  }

  ArrayList<Grid> arrangement = new ArrayList<Grid>();

  /*============================= Constructors =============================*/

  /* Board 
   * Preconditions:
   * - extras is in order for exact game recreation
   * Postconditions:
   * - Returns new instance of game-board
   * - Exact replication of board in online pdf version
   * Possible Extensions:
   * - Randomize extra role assignment with method
   *    that ensures fair rank distribution
   * - Randomize assignment of neighbors
   */
  Board(PImage[] bImg, ArrayList<Role> extras) {

    // Initialize Rooms
    // Trailers
    Coord[] spots0 = new Coord[6];
    spots0[0] = new Coord(3.05, 2.80);
    spots0[1] = new Coord(2.57, 2.80);
    spots0[2] = new Coord(2.23, 2.80);
    spots0[3] = new Coord(3.05, 2.40);
    spots0[4] = new Coord(2.57, 2.40);
    spots0[5] = new Coord(2.23, 2.40);
    rooms.put("trailers", new Trailer("Trailers", spots0));

    // Casting Office
    Coord[] spots1 = new Coord[6];
    spots1[0] = new Coord(300, 9.0);
    spots1[1] = new Coord(6.6, 6.9);
    spots1[2] = new Coord(6.6, 12.0);
    spots1[3] = spots1[0];
    spots1[4] = spots1[1];
    spots1[5] = spots1[2];
    rooms.put("casting office", new CastingOffice("Casting Office", spots1));

    // Build Stages
    // Main Street
    Coord[] spots2 = new Coord[6];
    spots2[0] = new Coord(2.73, 5.0);
    spots2[1] = new Coord(2.45, 5.0);
    spots2[2] = new Coord(2.22, 5.0);
    spots2[3] = spots2[0];
    spots2[4] = spots2[1];
    spots2[5] = spots2[2];
    Coord[] shots2 = new Coord[3];
    shots2[0] = new Coord(3.59, 17.0);
    shots2[1] = new Coord(4.27, 17.0);
    shots2[2] = new Coord(5.24, 17.0);
    Coord[] eRole2 = new Coord[4];
    eRole2[0] = new Coord(25.43, 28.33);
    eRole2[1] = new Coord(9.37, 28.33);
    eRole2[2] = new Coord(25.43, 8.10);
    eRole2[3] = new Coord(9.37, 8.10);
    rooms.put("main street", new Stage("Main Street", spots2, shots2, eRole2, 3, 3.27, 24.29));
    ((Stage) rooms.get("main street")).setExtraRole(extras.get(0));
    ((Stage) rooms.get("main street")).setExtraRole(extras.get(1));
    ((Stage) rooms.get("main street")).setExtraRole(extras.get(2));
    ((Stage) rooms.get("main street")).setExtraRole(extras.get(3));

    // Saloon
    Coord[] spots3 = new Coord[6];
    spots3[0] = new Coord(26.0, 2.28);
    spots3[1] = new Coord(8.75, 3.95);
    spots3[2] = new Coord(6.0, 2.28);
    spots3[3] = spots3[0];
    spots3[4] = spots3[1];
    spots3[5] = spots3[2];
    Coord[] shots3 = new Coord[2];
    shots3[0] = new Coord(21.05, 3.75);
    shots3[1] = new Coord(11.01, 3.75);
    Coord[] eRole3 = new Coord[2];
    eRole3[0] = new Coord(4.27, 3.28);
    eRole3[1] = new Coord(4.27, 2.59);
    rooms.put("saloon", new Stage("Saloon", spots3, shots3, eRole3, 2, 29.27, 3.22));
    ((Stage) rooms.get("saloon")).setExtraRole(extras.get(4));
    ((Stage) rooms.get("saloon")).setExtraRole(extras.get(5));

    // Ranch
    Coord[] spots4 = new Coord[6];
    spots4[0] = new Coord(4.61, 4.75);
    spots4[1] = new Coord(3.87, 4.75);
    spots4[2] = new Coord(3.33, 4.75);
    spots4[3] = spots4[0];
    spots4[4] = spots4[1];
    spots4[5] = spots4[2];
    Coord[] shots4 = new Coord[2];
    shots4[0] = new Coord(2.45, 17.0);
    shots4[1] = new Coord(2.22, 17.0);
    Coord[] eRole4 = new Coord[3];
    eRole4[0] = new Coord(2.91, 5.52);
    eRole4[1] = new Coord(2.475, 5.52);
    eRole4[2] = new Coord(2.475, 10.9);
    rooms.put("ranch", new Stage("Ranch", spots4, shots4, eRole4, 2, 4.73, 24.31));
    ((Stage) rooms.get("ranch")).setExtraRole(extras.get(6));
    ((Stage) rooms.get("ranch")).setExtraRole(extras.get(7));
    ((Stage) rooms.get("ranch")).setExtraRole(extras.get(8));

    // Secret Hideout
    Coord[] spots5 = new Coord[6];
    spots5[0] = new Coord(4.61, 2.45);
    spots5[1] = new Coord(3.87, 2.45);
    spots5[2] = new Coord(3.33, 2.45);
    spots5[3] = spots5[0];
    spots5[4] = spots5[1];
    spots5[5] = spots5[2];
    Coord[] shots5 = new Coord[3];
    shots5[0] = new Coord(4.46, 2.69);
    shots5[1] = new Coord(3.73, 2.69);
    shots5[2] = new Coord(3.2, 2.69);
    Coord[] eRole5 = new Coord[4];
    eRole5[0] = new Coord(2.77, 3.35);
    eRole5[1] = new Coord(2.32, 3.35);
    eRole5[2] = new Coord(2.77, 2.54);
    eRole5[3] = new Coord(2.32, 2.54);
    rooms.put("secret hideout", new Stage("Secret Hideout", spots5, shots5, eRole5, 3, 34.29, 3.21));
    ((Stage) rooms.get("secret hideout")).setExtraRole(extras.get(9));
    ((Stage) rooms.get("secret hideout")).setExtraRole(extras.get(10));
    ((Stage) rooms.get("secret hideout")).setExtraRole(extras.get(11));
    ((Stage) rooms.get("secret hideout")).setExtraRole(extras.get(12));

    // Bank
    Coord[] spots6 = new Coord[6];
    spots6[0] = new Coord(34.29, 5.99);
    spots6[1] = new Coord(4.9, 20.2);
    spots6[2] = new Coord(7.12, 5.99);
    spots6[3] = spots6[0];
    spots6[4] = spots6[1];
    spots6[5] = spots6[2];
    Coord[] shots6 = new Coord[1];
    shots6[0] = new Coord(4.54, 7.20);
    Coord[] eRole6 = new Coord[2];
    eRole6[0] = new Coord(3.83, 31.48);
    eRole6[1] = new Coord(3.83, 8.17);
    rooms.put("bank", new Stage("Bank", spots6, shots6, eRole6, 1, 37.5, 26.56));
    ((Stage) rooms.get("bank")).setExtraRole(extras.get(13));
    ((Stage) rooms.get("bank")).setExtraRole(extras.get(14));

    // Church
    Coord[] spots7 = new Coord[6];
    spots7[0] = new Coord(30.0, 2.25);
    spots7[1] = new Coord(6.6, 3.95);
    spots7[2] = new Coord(6.6, 2.25);
    spots7[3] = spots7[0];
    spots7[4] = spots7[1];
    spots7[5] = spots7[2];
    Coord[] shots7 = new Coord[2];
    shots7[0] = new Coord(22.22, 3.63);
    shots7[1] = new Coord(10.71, 3.62);
    Coord[] eRole7 = new Coord[2];
    eRole7[0] = new Coord(4.60, 3.23);
    eRole7[1] = new Coord(4.57, 2.53);
    rooms.put("church", new Stage("Church", spots7, shots7, eRole7, 2, 37.5, 3.18));
    ((Stage) rooms.get("church")).setExtraRole(extras.get(15));
    ((Stage) rooms.get("church")).setExtraRole(extras.get(16));

    // Hotel
    Coord[] spots8 = new Coord[6];
    spots8[0] = new Coord(3.37, 2.37);
    spots8[1] = new Coord(2.29, 3.05);
    spots8[2] = new Coord(2.96, 5.3);
    spots8[3] = spots8[0];
    spots8[4] = spots8[1];
    spots8[5] = spots8[2];
    Coord[] shots8 = new Coord[3];
    shots8[0] = new Coord(2.20, 3.36);
    shots8[1] = new Coord(2.44, 3.36);
    shots8[2] = new Coord(2.74, 3.36);
    Coord[] eRole8 = new Coord[4];
    eRole8[0] = new Coord(2.33, 2.43);
    eRole8[1] = new Coord(2.52, 2.93);
    eRole8[2] = new Coord(2.80, 2.43);
    eRole8[3] = new Coord(3.06, 2.93);
    rooms.put("hotel", new Stage("Hotel", spots8, shots8, eRole8, 3, 2.12, 25.61));
    ((Stage) rooms.get("hotel")).setExtraRole(extras.get(17));
    ((Stage) rooms.get("hotel")).setExtraRole(extras.get(18));
    ((Stage) rooms.get("hotel")).setExtraRole(extras.get(19));
    ((Stage) rooms.get("hotel")).setExtraRole(extras.get(20));

    // Jail
    Coord[] spots9 = new Coord[6];
    spots9[0] = new Coord(4.14, 5.67);
    spots9[1] = new Coord(2.45, 4.9);
    spots9[2] = new Coord(3.08, 4.9);
    spots9[3] = spots9[0];
    spots9[4] = spots9[1];
    spots9[5] = spots9[2];
    Coord[] shots9 = new Coord[1];
    shots9[0] = new Coord(2.603, 5.0);
    Coord[] eRole9 = new Coord[2];
    eRole9[0] = new Coord(2.33, 26.56);
    eRole9[1] = new Coord(2.33, 8.1);
    rooms.put("jail", new Stage("Jail", spots9, shots9, eRole9, 1, 4.27, 25.76));
    ((Stage) rooms.get("jail")).setExtraRole(extras.get(21));
    ((Stage) rooms.get("jail")).setExtraRole(extras.get(22));

    // General Store
    Coord[] spots10 = new Coord[6];
    spots10[0] = new Coord(4.2, 3.9);
    spots10[1] = new Coord(4.2, 2.27);
    spots10[2] = new Coord(3.6, 2.27);
    spots10[3] = spots10[0];
    spots10[4] = spots10[1];
    spots10[5] = spots10[2];
    Coord[] shots10 = new Coord[2];
    shots10[0] = new Coord(3.57, 3.03);
    shots10[1] = new Coord(3.57, 2.58);
    Coord[] eRole10 = new Coord[2];
    eRole10[0] = new Coord(5.0, 3.27);
    eRole10[1] = new Coord(5.0, 2.54);
    rooms.put("general store", new Stage("General Store", spots10, shots10, eRole10, 2, 3.26, 3.21));
    ((Stage) rooms.get("general store")).setExtraRole(extras.get(23));
    ((Stage) rooms.get("general store")).setExtraRole(extras.get(24));

    // Train Station
    Coord[] spots11 = new Coord[6];
    spots11[0] = new Coord(48.0, 8.25);
    spots11[1] = new Coord(6.3, 5.0);
    spots11[2] = new Coord(5.92, 21.45);
    spots11[3] = spots11[0];
    spots11[4] = spots11[1];
    spots11[5] = spots11[2];
    Coord[] shots11 = new Coord[3];
    shots11[0] = new Coord(22.64, 4.86);
    shots11[1] = new Coord(11.54, 4.86);
    shots11[2] = new Coord(7.69, 4.86);
    Coord[] eRole11 = new Coord[4];
    eRole11[0] = new Coord(31.15, 18.89);
    eRole11[1] = new Coord(15.35, 8.25);
    eRole11[2] = new Coord(9.34, 18.89);
    eRole11[3] = new Coord(7.35, 8.25);
    rooms.put("train station", new Stage("Train Station", spots11, shots11, eRole11, 3, 34.00, 2.17));
    ((Stage) rooms.get("train station")).setExtraRole(extras.get(25));
    ((Stage) rooms.get("train station")).setExtraRole(extras.get(26));
    ((Stage) rooms.get("train station")).setExtraRole(extras.get(27));
    ((Stage) rooms.get("train station")).setExtraRole(extras.get(28));

    ArrayList<Room> gridZerRms = new ArrayList<Room>();
    gridZerRms.add(rooms.get("main street"));
    gridZerRms.add(rooms.get("saloon"));
    gridZerRms.add(rooms.get("trailers"));

    ArrayList<Room> gridOneRms = new ArrayList<Room>();
    gridOneRms.add(rooms.get("ranch"));
    gridOneRms.add(rooms.get("secret hideout"));
    gridOneRms.add(rooms.get("casting office"));

    ArrayList<Room> gridTwoRms = new ArrayList<Room>();
    gridTwoRms.add(rooms.get("bank"));
    gridTwoRms.add(rooms.get("church"));
    gridTwoRms.add(rooms.get("hotel"));

    ArrayList<Room> gridThrRms = new ArrayList<Room>();
    gridThrRms.add(rooms.get("jail"));
    gridThrRms.add(rooms.get("general store"));
    gridThrRms.add(rooms.get("train station"));

    // Create grid objects
    arrangement.add(new Grid(bImg[0], gridZerRms));
    arrangement.add(new Grid(bImg[1], gridOneRms));
    arrangement.add(new Grid(bImg[2], gridTwoRms));
    arrangement.add(new Grid(bImg[3], gridThrRms));

    // Randomize here

    // Set Neighbors
    rooms.get("trailers").setNeighbors(
      rooms.get("main street"), 
      rooms.get("hotel"), 
      rooms.get("saloon"));
    rooms.get("casting office").setNeighbors(
      rooms.get("train station"), 
      rooms.get("ranch"), 
      rooms.get("secret hideout"));
    rooms.get("main street").setNeighbors(
      rooms.get("trailers"), 
      rooms.get("jail"), 
      rooms.get("saloon"));
    rooms.get("saloon").setNeighbors(
      rooms.get("trailers"), 
      rooms.get("main street"), 
      rooms.get("general store"), 
      rooms.get("bank"));
    rooms.get("ranch").setNeighbors(
      rooms.get("casting office"), 
      rooms.get("general store"), 
      rooms.get("bank"), 
      rooms.get("secret hideout"));
    rooms.get("secret hideout").setNeighbors(
      rooms.get("casting office"), 
      rooms.get("ranch"), 
      rooms.get("church"));
    rooms.get("bank").setNeighbors(
      rooms.get("hotel"), 
      rooms.get("saloon"), 
      rooms.get("church"), 
      rooms.get("ranch"));
    rooms.get("church").setNeighbors(
      rooms.get("bank"), 
      rooms.get("hotel"), 
      rooms.get("secret hideout"));
    rooms.get("hotel").setNeighbors(
      rooms.get("trailers"), 
      rooms.get("bank"), 
      rooms.get("church"));
    rooms.get("jail").setNeighbors(
      rooms.get("train station"), 
      rooms.get("general store"), 
      rooms.get("main street"));
    rooms.get("general store").setNeighbors(
      rooms.get("train station"), 
      rooms.get("saloon"), 
      rooms.get("jail"), 
      rooms.get("ranch"));
    rooms.get("train station").setNeighbors(
      rooms.get("general store"), 
      rooms.get("casting office"), 
      rooms.get("jail"));
  }

  /* display
   * Displays the board (each grid), side bar, rules box
   */
  public void display() {

    cardWidth = ((float)width-infoBarSize)/ 6.0;
    cardHeight = (float)height / 8.173;

    dieWidth = ((float)width-infoBarSize)/ 30.0;
    dieHeight = (float)height / 21.795;

    shotCWidth = ((float)width-infoBarSize)/ 29.268;
    shotCHeight = (float)height / 21.25;


    arrangement.get(0).setGrid((width-infoBarSize)/2, 0);
    arrangement.get(1).setGrid(0, height/2);
    arrangement.get(2).setGrid((width-infoBarSize)/2, height/2);
    arrangement.get(3).setGrid(0, 0);

    // Draw Board
    arrangement.get(0).display();
    arrangement.get(1).display();
    arrangement.get(2).display();
    arrangement.get(3).display();

    // Side bar
    fill(250, 250, 250);
    stroke(0);
    rect((float)width-(infoBarSize-5), 5, infoBarSize-10, height-10);
    fill(153, 102, 51);
    textFont(westF, 20);
    textAlign(CENTER);
    text("Game Info", (float)width-(infoBarSize/2), 40);

    // Rules Box
    rulesL = (float)width-infoBarSize + 10;
    rulesT = height-60;
    rulesR = rulesL + (infoBarSize-20);
    rulesB = rulesT + 50;
    rect(rulesL, rulesT, infoBarSize-20, 50);
    textFont(westF, 36);
    fill(255);
    text("RULES", (float)width-(infoBarSize/2), height-20);
  }

  /* displayGameInfo
   * prints player information in side bar and day number
   */
  public void displayGameInfo() {
    int yPlace = 80;
    float xPlace = (float)width-(infoBarSize - 12);

    for (Actor a : actorList) {
      fill(0, 0, 0);
      textFont(westF, 12);
      textAlign(LEFT);
      text(a.getId() + ":", xPlace, yPlace);
      a.display(width-70, yPlace - 10);
      yPlace += 15;
      textFont(reguF, 12);
      text("Money: $" + a.checkWallet(), xPlace, yPlace);
      yPlace += 15;
      text("Credits: " + a.viewCredits(), xPlace, yPlace);
      yPlace += 15;
      if (a.getRole() == null) {
        text("Rehearsal Chips: 0", xPlace, yPlace);
      } else {
        text("Rehearsal Chips: " + a.getRole().getRehearsals(), xPlace, yPlace);
      }
      yPlace += 20;
    }
    yPlace += 10;
    // Day counter
    textFont(westF, 16);
    fill(153, 102, 51);
    textAlign(CENTER);
    text("Day: " + getDay(), (float)width-(infoBarSize/2), yPlace);
  }

  /* displayCurrentPlayer
   * prints location, role, line of current player
   */
  void displayCurrentPlayer(Actor a) {
    int yOff = height - 285;
    float xPlace = (float)width-(infoBarSize - 12);

    if (a.getRole() == null) {
      fill(0);
      textAlign(LEFT);
      textFont(westF, 10);
      text("No role ", xPlace, yOff);
      yOff -= 15;
    } else {
      fill(0);
      textFont(regF, 10);
      textAlign(LEFT);
      text("  Line: \"" + a.getRole().getLine() + "\"", xPlace, yOff, (float)width-(infoBarSize-12), 13);
      yOff -= 15;
      textFont(westF, 10);
      text(a.getRole().getTitle(), xPlace, yOff);
      yOff -= 15;
    }
    text(a.getPosition().getName(), xPlace, yOff);
    yOff -= 25;
    textFont(westF, 14);
    fill(153, 102, 51);
    textAlign(CENTER);
    text(a.getId() +"'s" + " turn", (float)width-(infoBarSize/2), yOff);
  }

  /* displayActButton
   * printed relative to rehearse text
   */
  void displayActButton() {
    actX = width - infoBarSize + 10;
    actY = rehY - 55;

    update(mouseX, mouseY);
    if (actOver) {
      fill(highlightColor);
    } else {
      fill(buttonColor);
    }
    stroke(0);
    rect(actX, actY, infoBarSize - 20, 50);
    textFont(westF, 20);
    fill(153, 102, 51);
    textAlign(CENTER);
    text("Act", (float)width-(infoBarSize/2), rehY - 20);
  }

  /* displayRehearseButton
   * text printed relative to endTurn button
   */
  void displayRehearseButton() {
    rehX = width - infoBarSize + 10;
    rehY = endY - 55;

    update(mouseX, mouseY);
    if (rehOver) {
      fill(highlightColor);
    } else {
      fill(buttonColor);
    }
    stroke(0);
    rect(rehX, rehY, infoBarSize - 20, 50);
    textFont(westF, 20);
    fill(153, 102, 51);
    textAlign(CENTER);
    text("Rehearse", (float)width-(infoBarSize/2), endY - 20);
  }

  /* displayEndButton
   * display the End Turn Button
   * text printed relative to top of rules button
   */
  void displayEndButton() {
    endX = width - infoBarSize + 10;
    endY = rulesT - 60;

    update(mouseX, mouseY);
    if (endOver) {
      fill(highlightColor);
    } else {
      fill(buttonColor);
    }
    stroke(0);
    rect(endX, endY, infoBarSize - 20, 50);
    textFont(westF, 20);
    fill(153, 102, 51);
    textAlign(CENTER);
    text("End Turn", (float)width-(infoBarSize/2), rulesT - 27);
  }

  /* rules
   * Prints the rules box if mouse hovering of rules box
   */
  void rules() {

    if (((mouseX >= rulesL) && (mouseX <= rulesR)) && ((mouseY >= rulesT) && (mouseY <= rulesB))) {
      fill(255);
      rect(0, 0, (float)width-infoBarSize, height);
      textFont(regF, 16);
      fill(0);
      textAlign(LEFT);
      int yVal = 20;
      for (int i = 0; i < rules.length; i++) {
        text(rules[i], 5, yVal);
        yVal += 16;
      }
    }
  }

  /*============================ Getters/Setters ============================*/

  /* getRoom
   * Preconditions:
   * - request is a a valid String
   * Postconditions:
   * - returns the requested room if name is valid, else null
   */
  public Room getRoom (String request) {
    String roomName = request.toLowerCase();

    if (rooms.containsKey(roomName)) {
      return rooms.get(roomName);
    } else {
      return null;
    }
  }

  public HashMap<String, Room> getAllRooms() {
    return rooms;
  }

  /* setScenes
   * Preconditions:
   * - scenes is a randomized list of scenes of size NUM_STAGES
   * Postconditions:
   * - Each stage has been given a new scene
   */
  public void setScenes () {

    for (HashMap.Entry<String, Room> entry : rooms.entrySet()) {
      if (entry.getValue() instanceof Stage) {
        ((Stage) rooms.get(entry.getKey())).setScene(scenes.get(sid));
        sid++;
      }
    }
  }

  /* resetShotCounters
   * Preconditions:
   * - It's the start of a new day
   * Postconditions:
   * - Every stage's shot counter has been reset to original
   */
  public void resetShotCounters () {
    for (HashMap.Entry<String, Room> entry : rooms.entrySet()) {
      if (entry.getValue() instanceof Stage) {
        Stage s = ((Stage) rooms.get(entry.getKey()));
        s.setShotCounter(s.getOrigShotCounter());
        s.setSceneFlipped(false);
      }
    }
  }

  void update(float x, float y) {
    // end button
    if (overButton(endX, endY, infoBarSize - 20, 50)) {
      endOver = true; 
      actOver = false;
      rehOver = false;
    }
    // act button
    else if (overButton(actX, actY, infoBarSize - 20, 50)) {
      actOver = true;
      rehOver = false;
      endOver = false;
    }
    // rehearse button
    else if (overButton(rehX, rehY, infoBarSize - 20, 50)) {
      rehOver = true;
      actOver = false;
      endOver = false;
    }
    // Train Station
    else if (overButton((width - infoBarSize) / 7.741, height / 3.86, 0.025 * (width - infoBarSize), 0.188 * height)) {
      trainOver = true;
    }
    // Jail
    else if (overButton((width - infoBarSize) / 3.498, height / 5.86, 0.046 * (width - infoBarSize), 0.029 * height)) {
      jailOver = true;
    }
    // General Store
    else if (overButton((width - infoBarSize) / 3.166, height / 2.26, 0.145 * (width - infoBarSize), 0.029 * height)) {
      genOver = true;
    }
    // Casting Office
    else if (overButton((width - infoBarSize) / 41.379, height / 1.88, 0.153 * (width - infoBarSize), 0.029 * height)) {
      castOver = true;
    }
    // Secret Hideout
    else if (overButton((width - infoBarSize) / 31.578, height / 1.06, 0.16 * (width - infoBarSize), 0.029 * height)) {
      secOver = true;
    }
    // Ranch
    else if (overButton((width - infoBarSize) / 4.123, height / 1.49, 0.073 * (width - infoBarSize), 0.029 * height)) {
      ranchOver = true;
    }
    // Saloon
    else if (overButton((width - infoBarSize) / 1.731, height / 2.26, 0.075 * (width - infoBarSize), 0.029 * height)) {
      salOver = true;
    }
    // Main Street
    else if (overButton((width - infoBarSize) / 1.213, height / 5.86, 0.13 * (width - infoBarSize), 0.029 * height)) {
      mainOver = true;
    }
    // Trailers
    else if (overButton((width - infoBarSize) / 1.159, height / 3.23, 0.084 * (width - infoBarSize), 0.029 * height)) {
      trailOver = true;
    }
    // Bank
    else if (overButton((width - infoBarSize) / 1.729, height / 1.49, 0.06 * (width - infoBarSize), 0.029 * height)) {
      bankOver = true;
    }
    // Church
    else if (overButton((width - infoBarSize) / 1.754, height / 1.057, 0.078 * (width - infoBarSize), 0.029 * height)) {
      churchOver = true;
    }
    // Hotel
    else if (overButton((width - infoBarSize) / 1.181, height / 1.647, 0.025 * (width - infoBarSize), 0.077 * height)) {
      hotelOver = true;
    } else {
      trainOver = jailOver = genOver = castOver = ranchOver = secOver = mainOver = salOver = bankOver = churchOver = trailOver = hotelOver = actOver = rehOver = endOver = false;
    }
  }

  /* overButton
   * tells whether the mouse is currently over a button
   */
  boolean overButton(float x, float y, float width, float height) {
    if (mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height)) {
      return true;
    } else {
      return false;
    }
  }
}