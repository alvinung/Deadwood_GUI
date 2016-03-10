/* Neal Digre, Alvin Ung, Binh Pham
 * CSCI 345 - Winter 2016
 * Assignment 3 */

import java.util.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

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

void setup() {
  size(1400, 850);
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
void draw() { 

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
void mousePressed () {
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