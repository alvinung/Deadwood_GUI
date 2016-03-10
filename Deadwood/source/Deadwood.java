import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import java.util.ArrayList; 
import java.util.Random; 
import java.io.*; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.ArrayList; 
import java.util.Collections; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Deadwood extends PApplet {

/* Neal Digre, Alvin Ung, Binh Pham
 * CSCI 345 - Winter 2016
 * Assignment 3 */

float cardWidth;
float cardHeight;
float dieWidth;
float dieHeight;
float shotCWidth;
float shotCHeight;

PImage [] dImages = new PImage[6];
PImage [] bImages = new PImage[4];
PImage [] cards = new PImage[40];
PImage cardBack;

static ArrayList<Role> extraRoles;
static ArrayList<Role> starRoles;
ArrayList<Scene> scenes;
static Actor[] actorList;

static int numPlayers;

String [] rules;

PFont westF, regF, reguF;

float infoBarSize = 200;

static int sid = 0;

private static int scenesCount = 10;
private static int day = 1;
private static int numDays;
private static int whoseTurn = 0;
private static boolean didSomething;

private boolean initGame = true;
private static boolean gameOver = false;
static Board gameBoard;
PublicRelations pr;

public class Coord {
  private float xSF;
  private float ySF;

  public Coord(float x, float y) {
    xSF = x;
    ySF = y;
  }
}

/*================================= Setup =================================*/

public void setup() {
  
  surface.setResizable(true);
  //fullScreen();

  // Fonts
  westF = createFont("Cowboys.ttf", 16);
  regF = createFont("Arial.vlw", 14);
  reguF = createFont("NimbusSanL-ReguCond-48.vlw", 12);

  // Load board images
  for (int num = 1; num <= 4; num++) {
    String n = str(num);
    bImages[num-1] = loadImage("Board" + n + ".png");
  }

  // Load card images
  cardBack = loadImage("CardBack.png");
  for (int num = 1; num <= 40; num++) {
    String n = str(num);
    cards[num-1] = loadImage("Card" + n + ".png");
  }

  // Load die images
  for (int num = 1; num <= 6; num++) {
    String n = str(num);
    dImages[num-1] = loadImage(n + ".png");
  }

  // Load rules
  rules = loadStrings("rules.txt");

  // Build Roles
  extraRoles = readRoles("extraRoles.txt");
  starRoles = readRoles("starRoles.txt");

  // Build scenes
  scenes = createScenes(cards, starRoles);
  Collections.shuffle(scenes);

  gameBoard = new Board(bImages, extraRoles);
  pr = new PublicRelations();

  gameBoard.setScenes();
}

/*================================== Draw ==================================*/

// View
public void draw() { 

  if (initGame) {
    textFont(westF, 40);
    textAlign(CENTER);
    fill(153, 102, 51);
    text("Welcome to Deadwood!", width / 2, 60);
    textFont(regF, 16);
    text("You're a bit actor with a simple dream. The dream of getting paid.\n" +
      "You and your cohorts will spend the next four days dressing up as cowboys, \n" +
      "working on terrible films, and pretending you can act.\n" +
      "Your goal is to become the best actor in the backlot. \nBecause it's good to have goals.\n" +
      "So strap on your chaps and mosey up to the \n roof. Your line is \"Aaaiiigggghh!\"", width / 2, 100);
    askNumPlayers();
  } else if (gameOver) {
    fill(255);
    rect(0, 0, width-infoBarSize, height);
    fill(0);
    textFont(regF, 24);
    textAlign(LEFT);
    text(scoreGame(actorList), 60, 60);
  } else {

    gameBoard.display();

    gameBoard.displayActButton();
    gameBoard.displayRehearseButton();
    gameBoard.displayEndButton();
    gameBoard.displayGameInfo();
    gameBoard.displayCurrentPlayer(actorList[whoseTurn]);
    pr.displayLn1();
    pr.display();

    if (actorList[whoseTurn].getPosition().getName().equals("Casting Office")) {
      gameBoard.getRoom("casting office").display();
    }

    for (int i = 0; i < actorList.length; i++) {
      Actor a = actorList[i];
      if (a.getRole() == null) {
        Coord[] spts = a.getPosition().getSpots();
        a.display(a.getPosition().getGridX() + (width - infoBarSize) / spts[i].xSF, a.getPosition().getGridY() + height / spts[i].ySF);
      }
    }

    gameBoard.rules();
  }
}

// Controller
public void mousePressed () {
  boolean roleSelected = false;

  if (initGame) {
    if (mouseX >= 50 && mouseX <= 80) {
      if (mouseY >= 250 && mouseY <= 280) {
        numPlayers = 2;
        actorList = initActors(numPlayers);
        numDays = 3;
        initGame = false;
      } else if (mouseY >= 300 && mouseY <= 330) {
        numPlayers = 3;
        actorList = initActors(numPlayers);
        numDays = 3;
        initGame = false;
      } else if (mouseY >= 350 && mouseY <= 380) {
        numPlayers = 4;
        actorList = initActors(numPlayers);
        numDays = 4;
        initGame = false;
      } else if (mouseY >= 400 && mouseY <= 430) {
        numPlayers = 5;
        actorList = initActors(numPlayers);
        for (Actor a : actorList) {
          a.setCredits(2);
        }
        numDays = 4;
        initGame = false;
      } else if (mouseY >= 450 && mouseY <= 480) {
        numPlayers = 6;
        actorList = initActors(numPlayers);
        for (Actor a : actorList) {
          a.setCredits(4);
        }
        numDays = 4;
        initGame = false;
      }
    }

    // Would you like to play with a random board configuration?
  } else {

    for (Role r : extraRoles) {
      roleSelected = r.mouseOver();
      if (roleSelected == true) {
        actorList[whoseTurn].takeRole(r);
      }
    }

    for (Role r : starRoles) {
      roleSelected = r.mouseOver();
      if (roleSelected == true) {
        actorList[whoseTurn].takeRole(r);
      }
    }

    if (actorList[whoseTurn].getPosition().getName().equals("Casting Office")) {

      CastingOffice.Request req = ((CastingOffice)gameBoard.getRoom("casting office")).rankDetails();

      if (req != null) {
        ((CastingOffice)gameBoard.getRoom("casting office")).requestRankUp(req, actorList[whoseTurn]);
      }
    }

    // pressing buttons
    // Act
    if (gameBoard.actOver) {
      if (actorList[whoseTurn].getRole() != null) {
        actorList[whoseTurn].getRole().act(
          ((Stage) actorList[whoseTurn].getPosition()).getScene(), 
          ((Stage) actorList[whoseTurn].getPosition()));
      } else {
        getPR().setPrintLn1("Can't act.");
      }
    }
    // Rehearse
    if (gameBoard.rehOver) {
      if (actorList[whoseTurn].getRole() != null) {
        actorList[whoseTurn].getRole().rehearse();
      } else {
        getPR().setPrintLn1("Can't Rehearse.");
      }
    }
    // End Turn
    if (gameBoard.endOver) {
      didSomething = false;
      pr.setPrint("");
      pr.setPrintLn1("");
      whoseTurn = (whoseTurn + 1) % numPlayers;
    }
    if (gameBoard.trainOver) {
      actorList[whoseTurn].move("train station");
    }
    if (gameBoard.jailOver) {
      actorList[whoseTurn].move("jail");
    }
    if (gameBoard.genOver) {
      actorList[whoseTurn].move("general store");
    }
    if (gameBoard.castOver) {
      actorList[whoseTurn].move("casting office");
    }
    if (gameBoard.secOver) {
      actorList[whoseTurn].move("secret hideout");
    }
    if (gameBoard.ranchOver) {
      actorList[whoseTurn].move("ranch");
    }
    if (gameBoard.salOver) {
      actorList[whoseTurn].move("saloon");
    }
    if (gameBoard.mainOver) {
      actorList[whoseTurn].move("main street");
    }
    if (gameBoard.trailOver) {
      actorList[whoseTurn].move("trailers");
    }
    if (gameBoard.bankOver) {
      actorList[whoseTurn].move("bank");
    }
    if (gameBoard.churchOver) {
      actorList[whoseTurn].move("church");
    }
    if (gameBoard.hotelOver) {
      actorList[whoseTurn].move("hotel");
    }
  }
}

/*============================ Getters/Setters ============================*/

public int getDay() {
  return day;
}

public PublicRelations getPR() {
  return pr;
}

public static Board getBoard() {
  return gameBoard;
}

public static int getSceneCount() {
  return scenesCount;
}

/* setSceneCount
 * Will perform checks for end of day and game
 */
public static void setSceneCount(int c) {
  if (day == numDays && c == 1) {   
    // End Game
    gameOver = true;
  } else if (c == 1) {   
    // End Day
    gameBoard.setScenes();
    gameBoard.resetShotCounters();
    whoseTurn = (whoseTurn + 1) % numPlayers;

    for (Actor a : actorList) {
      a.setRole(null);
      a.setPosition(gameBoard.getRoom("trailers"));
    }

    for (Role r : extraRoles) {
      r.setActor(null);
    }

    for (Role r : starRoles) {
      r.setActor(null);
    }

    didSomething = false;
    scenesCount = 10;
    day += 1;
  } else {
    scenesCount = c;
  }
}

/*============================= Public Methods =============================*/

public static int diceRoll() {
  Random r = new Random();
  int rolled = r.nextInt(6) + 1;

  return rolled;
}

/*============================= Helper Methods =============================*/

/* readRoles - Reads roles from file
 * Format: Title / Line / Rank
 * The roles a specially ordered to match scenes/stages they fill
 */
private ArrayList<Role> readRoles(String fn) {
  ArrayList<Role> roles = new ArrayList<Role>();
  String title = null;
  String line = null;
  int rank = 0;

  String[] rls = loadStrings(fn);

  // Read in each role and add it to array
  for (String l : rls) {
    String[] roleData = split(l, " / ");
    title = roleData[0];
    line = roleData[1];
    rank = Integer.parseInt(roleData[2]);
    Role r = new Role(title, line, rank);
    roles.add(r);
  }
  return roles;
}

/*
  * createScenes Preconditions: - The file containing scenes is in the
 * following format: num_star_roles / title / description / budget
 * Postconditions: - returns an array of scenes
 */
private ArrayList<Scene> createScenes(PImage[] cards, ArrayList<Role> stars) {
  ArrayList<Scene> scenes = new ArrayList<Scene>();
  int numRoles = 0;
  String title = null;
  String description = null;
  int budget = 0;
  int ix = 0;

  String[] scns = loadStrings("scenes.txt");

  for (int i=0; i < 40; i++) {

    String[] sceneData = split(scns[i], " / ");

    numRoles = Integer.parseInt(sceneData[0]);
    ArrayList<Role> sRoles = new ArrayList<Role>(stars.subList(ix, ix + numRoles));
    ix += numRoles;
    title = sceneData[1];
    description = sceneData[2];
    budget = Integer.parseInt(sceneData[3]);

    Scene sc = new Scene(cards[i], title, description, budget, sRoles);
    scenes.add(sc);
  }

  return scenes;
}

/* askNumPlayers
 * Repeatedly asks for user input until valid number given
 * Postconditions:
 * - returns the number of players
 */
private void askNumPlayers() {
  textFont(regF, 30);
  textAlign(LEFT);
  fill(0);
  text("How many players?", 50, 200);
  if (mouseX >= 50 && mouseX <= 80 && mouseY >= 250 && mouseY <= 280) {
    fill(200);
    rect(50, 250, 30, 30);
  } else if (mouseX >= 50 && mouseX <= 80 && mouseY >= 300 && mouseY <= 330) {
    fill(200);
    rect(50, 300, 30, 30);
  } else if (mouseX >= 50 && mouseX <= 80 && mouseY >= 350 && mouseY <= 380) {
    fill(200);
    rect(50, 350, 30, 30);
  } else if (mouseX >= 50 && mouseX <= 80 && mouseY >= 400 && mouseY <= 430) {
    fill(200);
    rect(50, 400, 30, 30);
  } else if (mouseX >= 50 && mouseX <= 80 && mouseY >= 450 && mouseY <= 480) {
    fill(200);
    rect(50, 450, 30, 30);
  } else {
    fill(255);
    rect(50, 250, 30, 30);
    rect(50, 300, 30, 30);
    rect(50, 350, 30, 30);
    rect(50, 400, 30, 30);
    rect(50, 450, 30, 30);
  }
  fill(0);
  text("2", 100, 275);
  text("3", 100, 325);
  text("4", 100, 375);
  text("5", 100, 425);
  text("6", 100, 475);
}

/* initActors
 * Asks for each players name (id)
 * Postconditions:
 * - Returns an array containing the actor objects
 */
private Actor[] initActors (int num) {
  Actor[] aList = new Actor[num];
  String name;

  for (int i = 0; i < num; i++) {
    int [] rgb = new int[3];
    for (int j = 0; j < 3; j++) {
      rgb[j] = (int)random(50, 250);
    }
    name = "Player " + str(i+1);
    Actor temp = new Actor(name, rgb, getBoard().getRoom("trailers"));
    aList[i] = temp;
  }

  return aList;
}

/* scoreGame 
 * Prints a score board 
 */
private String scoreGame(Actor[] actors) {
  String output = "";
  int score;

  output = output + "Final Score \n==========================\n";
  output = output + "||    Player | Money | Credits | Rank | Score || "
    + "\n||-------------|-----------|-----------|---------|----------||\n";
  for (Actor a : actors) {
    score = 0;
    score += a.getRank() * 6 + a.checkWallet() + a.viewCredits();
    output = output + String.format("|| %s |%10d |%11d |%8d |%9d ||\n", 
      a.getId(), a.checkWallet(), a.viewCredits(), a.getRank(), score);
  }
  output = output + "==========================";

  return output;
}


/* Class Actor:
 * Responsible for handing all information on the actor
 * Implements taking a role, moving the actor, and raising rank
 */
public class Actor {

  private String id;
  private int[] myColor;
  private int rank;
  private int money;
  private int credits;
  private Room position;
  private Role role;
  private boolean amStar;

  /*============================= Constructors =============================*/

  public Actor(String name, int[] clr, Room start)
  {
    id = name;
    myColor = clr;
    rank = 1;
    money = 0;
    credits = 0;
    position = start;
    role = null;
    amStar = false;
  }

  /*============================ Getters/Setters ============================*/

  public boolean getAmStar()
  {
    return amStar;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String s)
  {
    id = s;
  }

  public int getRank()
  {
    return rank;
  }

  public void setRank(int r)
  {
    rank = r;
  }

  public int checkWallet()
  {  
    return money;
  }

  public void setMoney(int m)
  {
    money = m;
  }

  public int viewCredits()
  {
    return credits;
  }

  public void setCredits(int c)
  {
    credits = c;
  }

  public Room getPosition()
  {
    return position;
  }

  public void setPosition(Room r)
  {
    position = r;
  }

  public Role getRole()
  {
    return role;
  }

  public void setRole(Role r)
  {
    role = r;
  }

  /*============================= Public Methods =============================*/

  /* display 
   * Preconditions:
   * - coordinates xPos, yPos are absolute, i.e. not relative to grid
   * Postconditions:
   * - Actors token die is displayed at specified coordinates
   */
  public void display(float xPos, float yPos) {
    tint(myColor[0], myColor[1], myColor[2]);
    image(dImages[rank-1], xPos, yPos, dieWidth, dieHeight);
    noTint();
  }

  /* takeRole
   * Preconditions:
   * - actor has clicked on a role
   * Postconditions:
   * - Actor is assigned role, if valid command sequence
   */
  public void takeRole(Role selection) {
    String desiredRole = selection.getTitle();
    ArrayList<Role> starRoles = new ArrayList<Role>();
    ArrayList<Role> extraRoles = new ArrayList<Role>();

    if (role == null &&
      position.getName() != "Trailers" && position.getName() != "Casting Office" 
      && ((Stage) position).getShotCounter() != 0) {

      Stage stage = (Stage) position;
      Scene scene = ((Stage) position).getScene();

      starRoles = scene.getStarRoles();
      extraRoles = stage.getExtraRoles();

      Role validRole = null;
      for (Role r : starRoles) {
        if (r.getTitle().equals(desiredRole)) {
          validRole = r;
          amStar = true;
        }
      }

      for (Role r : extraRoles) {
        if (r.getTitle().equals(desiredRole)) {
          validRole = r;
          amStar = false;
        }
      }

      if (validRole != null) {
        if (validRole.getRank() <= rank) {
          if (validRole.getActor() == null) {
            role = validRole;
            validRole.setActor(this);
            getPR().setPrintLn1("");
            if (amStar == true) {
              ((Stage) position).setStarCounter(((Stage) position).getStarCounter() + 1);
            }
            didSomething = true;
          } else {
            getPR().setPrintLn1("Role already taken.");
          }
        } else {
          getPR().setPrintLn1("Rank too low.");
        }
      }
    } else {
      getPR().setPrintLn1("Cannot take role.");
    }
  }

  /* move
   * Preconditions:
   * - Actor has clicked on name of stage
   * Postconditions:
   * - Actor has moved or been notified of error
   */
  public void move(String wantedRoom) {
    if (role == null && didSomething == false) {
      Room r = Deadwood.getBoard().getRoom(wantedRoom.toLowerCase());
      if (position.isNeighbor(r)) {
        position = r;
        if (!position.getName().equals("Casting Office") && !position.getName().equals("Trailers")) {
          ((Stage) position).setSceneFlipped(true);
        }
        getPR().setPrintLn1("");
        didSomething = true;
      } else {
        getPR().setPrintLn1("Invalid move.");
      }
    } else {
      getPR().setPrintLn1("Can't move.");
    }
  }
}



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
  private int buttonColor = color(250);
  private int highlightColor = color(200);

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

    public void setGrid (float gridX, float gridY) {
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
    public void display () {
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
    spots0[0] = new Coord(3.05f, 2.80f);
    spots0[1] = new Coord(2.57f, 2.80f);
    spots0[2] = new Coord(2.23f, 2.80f);
    spots0[3] = new Coord(3.05f, 2.40f);
    spots0[4] = new Coord(2.57f, 2.40f);
    spots0[5] = new Coord(2.23f, 2.40f);
    rooms.put("trailers", new Trailer("Trailers", spots0));

    // Casting Office
    Coord[] spots1 = new Coord[6];
    spots1[0] = new Coord(300, 9.0f);
    spots1[1] = new Coord(6.6f, 6.9f);
    spots1[2] = new Coord(6.6f, 12.0f);
    spots1[3] = spots1[0];
    spots1[4] = spots1[1];
    spots1[5] = spots1[2];
    rooms.put("casting office", new CastingOffice("Casting Office", spots1));

    // Build Stages
    // Main Street
    Coord[] spots2 = new Coord[6];
    spots2[0] = new Coord(2.73f, 5.0f);
    spots2[1] = new Coord(2.45f, 5.0f);
    spots2[2] = new Coord(2.22f, 5.0f);
    spots2[3] = spots2[0];
    spots2[4] = spots2[1];
    spots2[5] = spots2[2];
    Coord[] shots2 = new Coord[3];
    shots2[0] = new Coord(3.59f, 17.0f);
    shots2[1] = new Coord(4.27f, 17.0f);
    shots2[2] = new Coord(5.24f, 17.0f);
    Coord[] eRole2 = new Coord[4];
    eRole2[0] = new Coord(25.43f, 28.33f);
    eRole2[1] = new Coord(9.37f, 28.33f);
    eRole2[2] = new Coord(25.43f, 8.10f);
    eRole2[3] = new Coord(9.37f, 8.10f);
    rooms.put("main street", new Stage("Main Street", spots2, shots2, eRole2, 3, 3.27f, 24.29f));
    ((Stage) rooms.get("main street")).setExtraRole(extras.get(0));
    ((Stage) rooms.get("main street")).setExtraRole(extras.get(1));
    ((Stage) rooms.get("main street")).setExtraRole(extras.get(2));
    ((Stage) rooms.get("main street")).setExtraRole(extras.get(3));

    // Saloon
    Coord[] spots3 = new Coord[6];
    spots3[0] = new Coord(26.0f, 2.28f);
    spots3[1] = new Coord(8.75f, 3.95f);
    spots3[2] = new Coord(6.0f, 2.28f);
    spots3[3] = spots3[0];
    spots3[4] = spots3[1];
    spots3[5] = spots3[2];
    Coord[] shots3 = new Coord[2];
    shots3[0] = new Coord(21.05f, 3.75f);
    shots3[1] = new Coord(11.01f, 3.75f);
    Coord[] eRole3 = new Coord[2];
    eRole3[0] = new Coord(4.27f, 3.28f);
    eRole3[1] = new Coord(4.27f, 2.59f);
    rooms.put("saloon", new Stage("Saloon", spots3, shots3, eRole3, 2, 29.27f, 3.22f));
    ((Stage) rooms.get("saloon")).setExtraRole(extras.get(4));
    ((Stage) rooms.get("saloon")).setExtraRole(extras.get(5));

    // Ranch
    Coord[] spots4 = new Coord[6];
    spots4[0] = new Coord(4.61f, 4.75f);
    spots4[1] = new Coord(3.87f, 4.75f);
    spots4[2] = new Coord(3.33f, 4.75f);
    spots4[3] = spots4[0];
    spots4[4] = spots4[1];
    spots4[5] = spots4[2];
    Coord[] shots4 = new Coord[2];
    shots4[0] = new Coord(2.45f, 17.0f);
    shots4[1] = new Coord(2.22f, 17.0f);
    Coord[] eRole4 = new Coord[3];
    eRole4[0] = new Coord(2.91f, 5.52f);
    eRole4[1] = new Coord(2.475f, 5.52f);
    eRole4[2] = new Coord(2.475f, 10.9f);
    rooms.put("ranch", new Stage("Ranch", spots4, shots4, eRole4, 2, 4.73f, 24.31f));
    ((Stage) rooms.get("ranch")).setExtraRole(extras.get(6));
    ((Stage) rooms.get("ranch")).setExtraRole(extras.get(7));
    ((Stage) rooms.get("ranch")).setExtraRole(extras.get(8));

    // Secret Hideout
    Coord[] spots5 = new Coord[6];
    spots5[0] = new Coord(4.61f, 2.45f);
    spots5[1] = new Coord(3.87f, 2.45f);
    spots5[2] = new Coord(3.33f, 2.45f);
    spots5[3] = spots5[0];
    spots5[4] = spots5[1];
    spots5[5] = spots5[2];
    Coord[] shots5 = new Coord[3];
    shots5[0] = new Coord(4.46f, 2.69f);
    shots5[1] = new Coord(3.73f, 2.69f);
    shots5[2] = new Coord(3.2f, 2.69f);
    Coord[] eRole5 = new Coord[4];
    eRole5[0] = new Coord(2.77f, 3.35f);
    eRole5[1] = new Coord(2.32f, 3.35f);
    eRole5[2] = new Coord(2.77f, 2.54f);
    eRole5[3] = new Coord(2.32f, 2.54f);
    rooms.put("secret hideout", new Stage("Secret Hideout", spots5, shots5, eRole5, 3, 34.29f, 3.21f));
    ((Stage) rooms.get("secret hideout")).setExtraRole(extras.get(9));
    ((Stage) rooms.get("secret hideout")).setExtraRole(extras.get(10));
    ((Stage) rooms.get("secret hideout")).setExtraRole(extras.get(11));
    ((Stage) rooms.get("secret hideout")).setExtraRole(extras.get(12));

    // Bank
    Coord[] spots6 = new Coord[6];
    spots6[0] = new Coord(34.29f, 5.99f);
    spots6[1] = new Coord(4.9f, 20.2f);
    spots6[2] = new Coord(7.12f, 5.99f);
    spots6[3] = spots6[0];
    spots6[4] = spots6[1];
    spots6[5] = spots6[2];
    Coord[] shots6 = new Coord[1];
    shots6[0] = new Coord(4.54f, 7.20f);
    Coord[] eRole6 = new Coord[2];
    eRole6[0] = new Coord(3.83f, 31.48f);
    eRole6[1] = new Coord(3.83f, 8.17f);
    rooms.put("bank", new Stage("Bank", spots6, shots6, eRole6, 1, 37.5f, 26.56f));
    ((Stage) rooms.get("bank")).setExtraRole(extras.get(13));
    ((Stage) rooms.get("bank")).setExtraRole(extras.get(14));

    // Church
    Coord[] spots7 = new Coord[6];
    spots7[0] = new Coord(30.0f, 2.25f);
    spots7[1] = new Coord(6.6f, 3.95f);
    spots7[2] = new Coord(6.6f, 2.25f);
    spots7[3] = spots7[0];
    spots7[4] = spots7[1];
    spots7[5] = spots7[2];
    Coord[] shots7 = new Coord[2];
    shots7[0] = new Coord(22.22f, 3.63f);
    shots7[1] = new Coord(10.71f, 3.62f);
    Coord[] eRole7 = new Coord[2];
    eRole7[0] = new Coord(4.60f, 3.23f);
    eRole7[1] = new Coord(4.57f, 2.53f);
    rooms.put("church", new Stage("Church", spots7, shots7, eRole7, 2, 37.5f, 3.18f));
    ((Stage) rooms.get("church")).setExtraRole(extras.get(15));
    ((Stage) rooms.get("church")).setExtraRole(extras.get(16));

    // Hotel
    Coord[] spots8 = new Coord[6];
    spots8[0] = new Coord(3.37f, 2.37f);
    spots8[1] = new Coord(2.29f, 3.05f);
    spots8[2] = new Coord(2.96f, 5.3f);
    spots8[3] = spots8[0];
    spots8[4] = spots8[1];
    spots8[5] = spots8[2];
    Coord[] shots8 = new Coord[3];
    shots8[0] = new Coord(2.20f, 3.36f);
    shots8[1] = new Coord(2.44f, 3.36f);
    shots8[2] = new Coord(2.74f, 3.36f);
    Coord[] eRole8 = new Coord[4];
    eRole8[0] = new Coord(2.33f, 2.43f);
    eRole8[1] = new Coord(2.52f, 2.93f);
    eRole8[2] = new Coord(2.80f, 2.43f);
    eRole8[3] = new Coord(3.06f, 2.93f);
    rooms.put("hotel", new Stage("Hotel", spots8, shots8, eRole8, 3, 2.12f, 25.61f));
    ((Stage) rooms.get("hotel")).setExtraRole(extras.get(17));
    ((Stage) rooms.get("hotel")).setExtraRole(extras.get(18));
    ((Stage) rooms.get("hotel")).setExtraRole(extras.get(19));
    ((Stage) rooms.get("hotel")).setExtraRole(extras.get(20));

    // Jail
    Coord[] spots9 = new Coord[6];
    spots9[0] = new Coord(4.14f, 5.67f);
    spots9[1] = new Coord(2.45f, 4.9f);
    spots9[2] = new Coord(3.08f, 4.9f);
    spots9[3] = spots9[0];
    spots9[4] = spots9[1];
    spots9[5] = spots9[2];
    Coord[] shots9 = new Coord[1];
    shots9[0] = new Coord(2.603f, 5.0f);
    Coord[] eRole9 = new Coord[2];
    eRole9[0] = new Coord(2.33f, 26.56f);
    eRole9[1] = new Coord(2.33f, 8.1f);
    rooms.put("jail", new Stage("Jail", spots9, shots9, eRole9, 1, 4.27f, 25.76f));
    ((Stage) rooms.get("jail")).setExtraRole(extras.get(21));
    ((Stage) rooms.get("jail")).setExtraRole(extras.get(22));

    // General Store
    Coord[] spots10 = new Coord[6];
    spots10[0] = new Coord(4.2f, 3.9f);
    spots10[1] = new Coord(4.2f, 2.27f);
    spots10[2] = new Coord(3.6f, 2.27f);
    spots10[3] = spots10[0];
    spots10[4] = spots10[1];
    spots10[5] = spots10[2];
    Coord[] shots10 = new Coord[2];
    shots10[0] = new Coord(3.57f, 3.03f);
    shots10[1] = new Coord(3.57f, 2.58f);
    Coord[] eRole10 = new Coord[2];
    eRole10[0] = new Coord(5.0f, 3.27f);
    eRole10[1] = new Coord(5.0f, 2.54f);
    rooms.put("general store", new Stage("General Store", spots10, shots10, eRole10, 2, 3.26f, 3.21f));
    ((Stage) rooms.get("general store")).setExtraRole(extras.get(23));
    ((Stage) rooms.get("general store")).setExtraRole(extras.get(24));

    // Train Station
    Coord[] spots11 = new Coord[6];
    spots11[0] = new Coord(48.0f, 8.25f);
    spots11[1] = new Coord(6.3f, 5.0f);
    spots11[2] = new Coord(5.92f, 21.45f);
    spots11[3] = spots11[0];
    spots11[4] = spots11[1];
    spots11[5] = spots11[2];
    Coord[] shots11 = new Coord[3];
    shots11[0] = new Coord(22.64f, 4.86f);
    shots11[1] = new Coord(11.54f, 4.86f);
    shots11[2] = new Coord(7.69f, 4.86f);
    Coord[] eRole11 = new Coord[4];
    eRole11[0] = new Coord(31.15f, 18.89f);
    eRole11[1] = new Coord(15.35f, 8.25f);
    eRole11[2] = new Coord(9.34f, 18.89f);
    eRole11[3] = new Coord(7.35f, 8.25f);
    rooms.put("train station", new Stage("Train Station", spots11, shots11, eRole11, 3, 34.00f, 2.17f));
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

    cardWidth = ((float)width-infoBarSize)/ 6.0f;
    cardHeight = (float)height / 8.173f;

    dieWidth = ((float)width-infoBarSize)/ 30.0f;
    dieHeight = (float)height / 21.795f;

    shotCWidth = ((float)width-infoBarSize)/ 29.268f;
    shotCHeight = (float)height / 21.25f;


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
  public void displayCurrentPlayer(Actor a) {
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
  public void displayActButton() {
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
  public void displayRehearseButton() {
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
  public void displayEndButton() {
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
  public void rules() {

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

  public void update(float x, float y) {
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
    else if (overButton((width - infoBarSize) / 7.741f, height / 3.86f, 0.025f * (width - infoBarSize), 0.188f * height)) {
      trainOver = true;
    }
    // Jail
    else if (overButton((width - infoBarSize) / 3.498f, height / 5.86f, 0.046f * (width - infoBarSize), 0.029f * height)) {
      jailOver = true;
    }
    // General Store
    else if (overButton((width - infoBarSize) / 3.166f, height / 2.26f, 0.145f * (width - infoBarSize), 0.029f * height)) {
      genOver = true;
    }
    // Casting Office
    else if (overButton((width - infoBarSize) / 41.379f, height / 1.88f, 0.153f * (width - infoBarSize), 0.029f * height)) {
      castOver = true;
    }
    // Secret Hideout
    else if (overButton((width - infoBarSize) / 31.578f, height / 1.06f, 0.16f * (width - infoBarSize), 0.029f * height)) {
      secOver = true;
    }
    // Ranch
    else if (overButton((width - infoBarSize) / 4.123f, height / 1.49f, 0.073f * (width - infoBarSize), 0.029f * height)) {
      ranchOver = true;
    }
    // Saloon
    else if (overButton((width - infoBarSize) / 1.731f, height / 2.26f, 0.075f * (width - infoBarSize), 0.029f * height)) {
      salOver = true;
    }
    // Main Street
    else if (overButton((width - infoBarSize) / 1.213f, height / 5.86f, 0.13f * (width - infoBarSize), 0.029f * height)) {
      mainOver = true;
    }
    // Trailers
    else if (overButton((width - infoBarSize) / 1.159f, height / 3.23f, 0.084f * (width - infoBarSize), 0.029f * height)) {
      trailOver = true;
    }
    // Bank
    else if (overButton((width - infoBarSize) / 1.729f, height / 1.49f, 0.06f * (width - infoBarSize), 0.029f * height)) {
      bankOver = true;
    }
    // Church
    else if (overButton((width - infoBarSize) / 1.754f, height / 1.057f, 0.078f * (width - infoBarSize), 0.029f * height)) {
      churchOver = true;
    }
    // Hotel
    else if (overButton((width - infoBarSize) / 1.181f, height / 1.647f, 0.025f * (width - infoBarSize), 0.077f * height)) {
      hotelOver = true;
    } else {
      trainOver = jailOver = genOver = castOver = ranchOver = secOver = mainOver = salOver = bankOver = churchOver = trailOver = hotelOver = actOver = rehOver = endOver = false;
    }
  }

  /* overButton
   * tells whether the mouse is currently over a button
   */
  public boolean overButton(float x, float y, float width, float height) {
    if (mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height)) {
      return true;
    } else {
      return false;
    }
  }
}
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
    dollarsLoc[2] = new Coord(10.63f, 8.5f);
    dollarsLoc[3] = new Coord(10.63f, 7.08f);
    dollarsLoc[4] = new Coord(10.63f, 6.07f);
    dollarsLoc[5] = new Coord(10.63f, 5.31f);
    dollarsLoc[6] = new Coord(10.63f, 4.72f);

    this.creditsLoc = new Coord[7];
    creditsLoc[2] = new Coord(7.46f, 8.5f);
    creditsLoc[3] = new Coord(7.46f, 7.08f);
    creditsLoc[4] = new Coord(7.46f, 6.07f);
    creditsLoc[5] = new Coord(7.46f, 5.31f);
    creditsLoc[6] = new Coord(7.46f, 4.72f);
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

      if (mouseX >= xVal - ((width - infoBarSize) / 66.67f) && mouseX <= xVal + ((width - infoBarSize) / 66.67f) && 
        mouseY >= yVal - (height / 60.00f) && mouseY <= yVal + (height / 60.00f)) {

        noStroke();
        fill(100, 100);
        ellipse(xVal, yVal, (width - infoBarSize) / 66.67f, height / 47.22f);
      }
      // Credits
      xVal = super.getGridX() + (width - infoBarSize) / this.creditsLoc[i].xSF;
      yVal = super.getGridY() + height / this.creditsLoc[i].ySF;

      if (mouseX >= xVal - ((width - infoBarSize) / 66.67f) && mouseX <= xVal + ((width - infoBarSize) / 66.67f) && 
        mouseY >= yVal - (height / 60.00f) && mouseY <= yVal + (height / 60.00f)) {

        noStroke();
        fill(100, 100);
        ellipse(xVal, yVal, (width - infoBarSize) / 66.67f, height / 47.22f);
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

      if (mouseX >= xVal - ((width - infoBarSize) / 66.67f) && mouseX <= xVal + ((width - infoBarSize) / 66.67f) && 
        mouseY >= yVal - (height / 60.00f) && mouseY <= yVal + (height / 60.00f)) {

        Request req = new Request("dollars", i);

        return req;
      }

      // Credits
      xVal = super.getGridX() + (width - infoBarSize) / this.creditsLoc[i].xSF;
      yVal = super.getGridY() + height / this.creditsLoc[i].ySF;

      if (mouseX >= xVal - ((width - infoBarSize) / 66.67f) && mouseX <= xVal + ((width - infoBarSize) / 66.67f) && 
        mouseY >= yVal - (height / 60.00f) && mouseY <= yVal + (height / 60.00f)) {

        Request req = new Request("credits", i);

        return req;
      }
    }

    return null;
  }
}
/* Interface Payment:
 * Interface for implementing payActor method. Called by Stage to pay Extra Roles
 * and Scene to pay Star Roles
 */
public interface Payment {

  /* payActor
   * Precondistions:
   * - Actor has 'acted' and either failed or succeeded
   * Postcondition:
   * - Open for implementation
   */
  public void payActor(String outcome, Actor a);
}


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
/* Class Role:
 * Responsible for handliing what actors are able to do when they have a role.
 * Implements acting and rehearsing
 */

public class Role {
  private float xPos;
  private float yPos;
  private String line;
  private String title;
  private int rank;
  private int rehearsals;
  private Actor actor;

  /*============================= Constructors =============================*/

  public Role (String t, String l, int rk)
  {
    xPos = 0;
    yPos = 0;
    title = t;
    line = l;
    rank = rk;
    rehearsals = 0;
    actor = null;
  }


  /*============================ Getters/Setters ============================*/

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String t)
  {

    title = t;
  }

  public int getRank()
  {
    return rank;
  }

  public void setRank(int r)
  {
    rank = r;
  }

  public String getLine()
  {
    return line;
  }

  public void setLine(String l)
  {
    line = l;
  }

  public int getRehearsals()
  {
    return rehearsals;
  }

  public void setRehearsals(int r)
  {
    rehearsals = r;
  }

  public Actor getActor()
  {
    return actor;
  }

  public void setActor(Actor a)
  {
    actor = a;
  }

  public void setCoord(float x, float y) {
    xPos = x;
    yPos = y;
  }

  public float getX() {
    return xPos;
  }

  public float getY() {
    return yPos;
  }

  /*============================= Public Methods =============================*/

  public boolean mouseOver() {
    if (mouseX >= xPos && mouseX <= xPos + dieWidth
      && mouseY >= yPos && mouseY <= yPos + dieHeight) {
      return true;
    } else {
      return false;
    }
  }

  /* rehearse
   * Preconditions:
   * - Actor has input 'rehearse' command
   * Postconditions:
   * - Actor gains 1 practice chip
   */
  public void rehearse()
  {
    int max = ((Stage) actor.getPosition()).getScene().getBudget() - 1;
    if (!didSomething) {
      if (!actor.getPosition().getName().equals("Casting Office") && 
        !actor.getPosition().getName().equals("Trailers")) {
        if (rehearsals != max) {
          rehearsals += 1;
          didSomething = true;
        } else {
          getPR().setPrintLn1("Can't rehearse");
        }
      } else {
        getPR().setPrintLn1("Can't rehearse");
      }
    } else {
      getPR().setPrintLn1("Can't rehearse");
    }
  }

  /* act
   * Preconditions:
   * - Actor has input 'act' command
   * Postconditions:
   * - Actor rolls a die. Die value is added to practice chip count and compared
   *   Scene budget
   * - If greater than Scene budget, job is successful
   */
  public void act(Scene scene, Stage stage)
  {
    if (!didSomething) {

      int diceRoll = Deadwood.diceRoll();

      //getPR().setPrintLn1("You rolled: " + Integer.toString(diceRoll));

      if ((diceRoll + rehearsals) >= scene.getBudget()) {
        if (actor.getAmStar() == true) {
          scene.payActor("success", actor);
          stage.setShotCounter(stage.getShotCounter() - 1);
          getPR().setPrintLn1("You rolled: " + Integer.toString(diceRoll) + ", Scene success!");
        } else {
          stage.payActor("success", actor);
          stage.setShotCounter(stage.getShotCounter() - 1);
          getPR().setPrintLn1("You rolled: " + Integer.toString(diceRoll) + ", Scene success!");
        }
      } else {
        if (actor.getAmStar() == false) {
          stage.payActor("failed", actor);
          getPR().setPrintLn1("You rolled: " + Integer.toString(diceRoll) + ", Scene failed");
        } else {
          getPR().setPrintLn1("You rolled: " + Integer.toString(diceRoll) + ", Scene failed");
        }
      }
      didSomething = true;
    } else {
      getPR().setPrintLn1("Can't act.");
    }
  }
}


/* Actract Class Room:
 * Responsible for handling all room information
 * Displays all Neighboring rooms of each room and Abstractly implements showing
 * room info
 */
public abstract class Room {
  private Coord[] spots;
  private float gridX;
  private float gridY;
  private ArrayList<Room> neighbors;
  private String name;

  /*============================= Constructors =============================*/

  public Room (String n, Coord[] spts) {
    spots = spts;
    gridX = 0;
    gridY = 0;
    name = n;
    neighbors = new ArrayList<Room>();
  }

  public Room (String n, float gX, float gY) {
    gridX = gX;
    gridY = gY;
    name = n;
    neighbors = new ArrayList<Room>();
  }
  /*============================ Getters/Setters ============================*/

  public String getName() {
    return name;
  }

  public void setNeighbors(Room n1, Room n2, Room n3) {
    neighbors.add(n1);
    neighbors.add(n2);
    neighbors.add(n3);
  }

  public void setNeighbors(Room n1, Room n2, Room n3, Room n4) {
    neighbors.add(n1);
    neighbors.add(n2);
    neighbors.add(n3);
    neighbors.add(n4);
  }

  public Room getNeighbor(int n) {
    return neighbors.get(n);
  }

  public float getGridX() {
    return gridX;
  }

  public float getGridY() {
    return gridY;
  }

  public abstract void setCoord(float x, float y);

  public Coord[] getSpots() {
    return spots;
  }

  /*============================= Public Methods =============================*/

  /* isNeighbor
   * Preconditions:
   * - wanted room to check 
   * Postconditions:
   * - boolean if wanted room is a neighbor of this room
   */
  public boolean isNeighbor(Room r) {
    return neighbors.contains(r);
  }

  /*============================= Abstract Methods =============================*/
  public abstract void display();
}


/* Class Scene:
 * Responsible for handling Scene (card) information
 * Pays star actors and displays Scene information
 */
public class Scene implements Payment {
  private PImage img;
  private String title;
  private String description;
  private float xPos;
  private float yPos;
  private int budget;
  private ArrayList<Role> starRoles;

  /*============================= Constructors =============================*/

  public Scene(PImage p, String t, String d, int b, ArrayList<Role> roles) {
    img = p;
    xPos = 0;
    yPos = 0;
    title = t;
    description = d;
    budget = b;
    starRoles = roles;
  }

  /*============================ Getters/Setters ============================*/

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getBudget() {
    return budget;
  }

  public void setBudget(int budget) {
    this.budget = budget;
  }

  public ArrayList<Role> getStarRoles() {
    return starRoles;
  }

  public void setStarRoles(Role role) {
    starRoles.add(role);
  }

  public void setCoord(float x, float y) {
    Coord[] locs = sRoleLocs(starRoles.size(), false, false);
    xPos = x;
    yPos = y;
    for (int i = 0; i < starRoles.size(); i++) {
      starRoles.get(i).setCoord(xPos + ((float)width-infoBarSize) / locs[i].xSF, yPos + (float)height / locs[i].ySF);
    }
  }

  /*============================= Private Methods =============================*/

  /* sRoleLocs
   * Returns role locations for on a scene card
   * relative to top left of card
   */
  private Coord[] sRoleLocs(int num, boolean rotL, boolean rotR) {
    Coord[] locs = new Coord[num];
    // Train Station on-card roles
    if (rotL) {
      if (num == 3) {
        locs[0] = new Coord(26.29f, -14.38f);
        locs[1] = new Coord(26.29f, -7.62f);
        locs[2]= new Coord(26.29f, -5.08f);
      } else if (num == 2) {
        locs[0] = new Coord(26.29f, -10.1f);
        locs[1] = new Coord(26.29f, -5.91f);
      } else {
        locs[0] = new Coord(26.29f, -7.62f);
      }

      // Hotel on-card roles
    } else if (rotR) {
      if (num == 3) {
        locs[0] = new Coord(-14.28f, 56.61f);
        locs[1] = new Coord(-14.28f, 11.42f);
        locs[2]= new Coord(-14.28f, 6.59f);
      } else if (num == 2) {
        locs[0] = new Coord(-14.28f, 18.7f);
        locs[1] = new Coord(-14.28f, 8.34f);
      } else {
        locs[0] = new Coord(-14.28f, 11.42f);
      }
    } else {
      if (num == 3) {
        locs[0] = new Coord(63.16f, 20.73f);
        locs[1] = new Coord(15.00f, 20.73f);
        locs[2]= new Coord(8.51f, 20.73f);
      } else if (num == 2) {
        locs[0] = new Coord(23.53f, 20.73f);
        locs[1] = new Coord(10.71f, 20.73f);
      } else {
        locs[0] = new Coord(15.00f, 20.73f);
      }
    }
    return locs;
  }

  /*============================= Public Methods =============================*/

  /* payActor
   * Preconditions:
   * - Actor has a Star Role and has 'acted'
   * Postconditions:
   * - If successful, actor is paid 2 credits
   * - If unsuccessful, nothing happens
   */
  public void payActor(String outcome, Actor a) {
    if (outcome == "success") {
      a.setCredits(a.viewCredits() + 2);
      getPR().setPrint(a.getId() + " was paid " + 2 + " credits");
    } else {
      getPR().setPrint("Job Failed.");
    }
  }

  /* display
   * Preconditions:
   * - flipped indicates whether the scene is visible
   * Postconditions:
   * - Scene info is printed
   */
  public void display(boolean flipped, boolean rotL, boolean rotR) {
    Coord[] locs = sRoleLocs(starRoles.size(), rotL, rotR);

    float tempW = cardWidth;
    float tempH = cardHeight;
    float tempDW = dieWidth;
    float tempDH = dieHeight;

    if (rotL || rotR) {

      for (int i = 0; i < starRoles.size(); i++) {
        starRoles.get(i).setCoord(xPos + ((float)width-infoBarSize) / locs[i].xSF, yPos + (float)height/ locs[i].ySF);
      }

      pushMatrix();
      cardWidth = (float)height / 4.59f;
      cardHeight = ((float)width-infoBarSize) / 10.81f;
      dieWidth = ((float)width-infoBarSize) / 30;
      dieHeight = (float)height / 21.25f;
      translate(xPos, yPos);
      xPos = 0;
      yPos = 0;

      if (rotL) {
        rotate(radians(270));
      } else {
        rotate(radians(90));
      }

      if (!flipped) {
        image(cardBack, xPos, yPos, cardWidth, cardHeight);
      } else {
        image(img, xPos, yPos, cardWidth, cardHeight);
      }

      popMatrix();
    } else {
      if (!flipped) {
        image(cardBack, xPos, yPos, cardWidth, cardHeight);
      } else {
        image(img, xPos, yPos, cardWidth, cardHeight);
      }
    }

    for (int i = 0; i < starRoles.size(); i++) {
      Role r = starRoles.get(i);
      if (r.getActor() != null) {
        r.getActor().display(r.getX(), r.getY());
      }
    }

    cardWidth = tempW;
    cardHeight = tempH;
    dieWidth = tempDW;
    dieHeight = tempDH;
  }
}



/* Class Stage:
 * Repsonsible for handling Stage information
 * Implements paying Extra actors, paying out bonuses when Scenes wrap up, and
 * displaying Stage information
 */
public class Stage extends Room implements Payment {
  private String title;
  private int shotCounter;
  private int origShotCounter;
  private int starCounter;
  private Scene scene;
  private ArrayList<Role> extraRoles;
  private float cardXSF;
  private float cardYSF;
  private Coord[] shotSF;
  private Coord[] extraSF;
  private Coord[] spotSF;
  private boolean sceneFlipped;

  /*============================= Constructors =============================*/

  public Stage(String n, Coord[] spts, Coord[] ssf, Coord[] esf, int shotC, float xsf, float ysf) {
    super(n, spts);
    title = n;
    shotCounter = shotC;
    origShotCounter = shotC;
    cardXSF = xsf;
    cardYSF = ysf;
    spotSF = spts;
    shotSF = ssf;
    extraSF = esf;
    starCounter = 0;
    scene = null;
    extraRoles = new ArrayList<Role>();
    sceneFlipped = false;
  }

  /*============================ Getters/Setters ============================*/

  public boolean getSceneFlipped () {
    return sceneFlipped;
  }

  public void setSceneFlipped(boolean sceneFlipped) {
    this.sceneFlipped = sceneFlipped;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getShotCounter() {
    return shotCounter;
  }

  public int getOrigShotCounter() {
    return origShotCounter;
  }

  public void setShotCounter(int shotC) {
    this.shotCounter = shotC;
    if (shotCounter == 0) {

      payBonus(scene);

      // Kick extras off
      for (Role r : extraRoles) {
        if (r.getActor() != null) {
          r.getActor().setRole(null);
          r.setRehearsals(0);
          r.setActor(null);
        }
      }

      // Kick stars off
      for (Role r : scene.getStarRoles()) {
        if (r.getActor() != null) {
          r.getActor().setRole(null);
          r.setRehearsals(0);
          r.setActor(null);
        }
      }

      Deadwood.setSceneCount(Deadwood.getSceneCount() - 1);
      starCounter = 0;
    }
  }

  public Scene getScene() {
    return scene;
  }

  public void setScene(Scene scene) {
    this.scene = scene;
  }

  public int getStarCounter() {
    return starCounter;
  }

  public void setStarCounter(int c) {
    starCounter = c;
  }

  public ArrayList<Role> getExtraRoles() {
    return extraRoles;
  }

  public void setExtraRole(Role role) {
    extraRoles.add(role);
  }

  public void setCoord(float x, float y) {
    super.gridX = x; 
    super.gridY = y;

    float cardX = x + (width-infoBarSize) / cardXSF;
    float cardY = y + height / cardYSF;

    if (scene != null) {
      scene.setCoord(cardX, cardY);
    }

    for (int i=0; i < extraRoles.size(); i ++) {
      extraRoles.get(i).setCoord(x + (width-infoBarSize) / extraSF[i].xSF, y + height / extraSF[i].ySF);
    }
  }

  //  /*============================= Public Methods =============================*/

  /* payActor
   * Preconditions:
   * - Actor has a Extra Role and has 'acted'
   * Postconditions:
   * - If successful, actor is paid $1 and 1 credits
   * - If unsuccessful, actor is paid $1
   */
  public void payActor(String outcome, Actor a) {
    if (outcome == "success") {
      a.setMoney(a.checkWallet() + 1);
      a.setCredits(a.viewCredits() + 1);
      getPR().setPrint(a.getId() + " was paid $1 and 1 credit");
    } else {
      a.setMoney(a.checkWallet() + 1);
      getPR().setPrint(a.getId() + " was paid $1");
    }
  }

  /* display
   * Preconditions:
   * - Actor has input 'info' command
   * Postconditions:
   * - Stage info is printed
   */
  public void display() {
    boolean rotL = false;
    boolean rotR = false;

    if (title.equals("Hotel")) {
      rotR = true;
    } else if (title.equals("Train Station")) {
      rotL = true;
    }

    // Scene
    if (shotCounter > 0) {
      scene.display(sceneFlipped, rotL, rotR);
    }

    // Shot counters
    stroke(0);
    fill(153, 102, 51);
    for (int i = 0; i < shotCounter; i++) {
      ellipse(super.getGridX() + (width-infoBarSize) / shotSF[i].xSF, 
        super.getGridY() + height / shotSF[i].ySF, 
        shotCWidth, shotCHeight);
    }

    // Extra roles
    for (int i = 0; i < extraRoles.size(); i++) {
      Role r = extraRoles.get(i);
      if (r.getActor() != null) {
        r.getActor().display(r.getX(), r.getY());
      }
    }
  }

  /*============================= Helper Methods =============================*/

  /* payBonus
   * Preconditions:
   * - Scene is wrapped up and there is atleast 1 Actor on a Star Role
   * Postconditions:
   * - Number of die equal to Scene budget are rolled
   * - Stars are paid amount on die with highest amount going to leading role and so on
   * - Extras are paid amount equal to the rank of role they are working on
   */
  private void payBonus(Scene s) {

    ArrayList<Integer> starBonuses = new ArrayList<Integer>();
    int payThisActor = 0;

    if (starCounter > 0) {
      int numDiceRolls = s.getBudget();
      ArrayList<Role> starRoles = new ArrayList<Role>();

      // dividing payouts
      for (int i = 1; i <= numDiceRolls; i++) {
        int amount = Deadwood.diceRoll();
        starBonuses.add(amount);
      }
      Collections.sort(starBonuses, Collections.reverseOrder());
      starRoles = sortStarRoles(s.getStarRoles());

      // paying stars    
      for (int payment : starBonuses) {
        if (starRoles.size() == 1) {
          starRoles.get(0).getActor().setMoney(starRoles.get(0).getActor().checkWallet() + payment);
        } else {
          if (payThisActor > starRoles.size() - 1) {
            payThisActor = 0;
          }
          if (starRoles.get(payThisActor).getActor() != null) {
            starRoles.get(payThisActor).getActor().setMoney(starRoles.get(payThisActor).getActor().checkWallet() + payment);
            payThisActor++;
          } else {
            payThisActor++;
          }
        }
      }
      // paying extras
      for (Role extra : extraRoles) {
        if (extra.getActor() != null) {
          int bonus = extra.getRank();
          extra.getActor().setMoney(extra.getActor().checkWallet() + bonus);
        }
      }
    }
  }

  /* sortStarRoles (helper)
   * Preconditions:
   * - Scene wraps up and actors need to be paid a bonus
   * Postconditions:
   * - Star Roles for a specific Scene are sorted from highest to lowest rank
   */
  private ArrayList<Role> sortStarRoles(ArrayList<Role> stars) {

    ArrayList<Role> sortedList = new ArrayList<Role>();
    Role highest;
    Role lowest;

    if (stars.size() == 1) {
      return stars;
    }
    if (stars.size() == 2) {
      if (stars.get(0).getRank() > stars.get(1).getRank()) {
        return stars;
      } else {
        highest = stars.get(1);
        lowest = stars.get(0);
        sortedList.add(highest);
        sortedList.add(lowest);
      }
    }
    if (stars.size() == 3) {
      if (stars.get(0).getRank() > stars.get(1).getRank()) {
        if (stars.get(0).getRank() > stars.get(2).getRank()) {
          highest = stars.get(0);
          if (stars.get(1).getRank() > stars.get(2).getRank()) {
            lowest = stars.get(2);
            sortedList.add(highest);
            sortedList.add(stars.get(1));
            sortedList.add(lowest);
          } else {
            lowest = stars.get(1);
            sortedList.add(highest);
            sortedList.add(stars.get(2));
            sortedList.add(lowest);
          }
        } else {
          highest = stars.get(2);
          lowest = stars.get(1);
          sortedList.add(highest);
          sortedList.add(stars.get(0));
          sortedList.add(lowest);
        }
      }
      if (stars.get(1).getRank() > stars.get(0).getRank()) {
        if (stars.get(1).getRank() > stars.get(2).getRank()) {
          highest = stars.get(1);
          if (stars.get(0).getRank() > stars.get(2).getRank()) {
            lowest = stars.get(2);      
            sortedList.add(highest);
            sortedList.add(stars.get(0));
            sortedList.add(lowest);
          } else {
            lowest = stars.get(0);
            sortedList.add(highest);
            sortedList.add(stars.get(2));
            sortedList.add(lowest);
          }
        } else {
          highest = stars.get(2);
          lowest = stars.get(0);
          sortedList.add(highest);
          sortedList.add(stars.get(1));
          sortedList.add(lowest);
        }
      }
    }
    return sortedList;
  }
}
/* Class Trailer:
 * Responsible for handling information on the Trailer room
 * Displays information on which rooms connect to the Trailer
 */
public class Trailer extends Room {

  private String description;

  /*============================= Constructors =============================*/

  public Trailer(String n, Coord[] spts) {
    super(n, spts);
  }

  /*============================ Getters/Setters ============================*/

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setCoord(float x, float y) {
    super.gridX = x; 
    super.gridY = y;
  }

  /*============================= Public Methods =============================*/


  /* display
   * Preconditions:
   * - Actor has input 'info' command
   * Postconditions:
   * - Neighboring room info is printed
   */
  public void display() {
  }
}
  public void settings() {  size(1400, 850); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Deadwood" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
