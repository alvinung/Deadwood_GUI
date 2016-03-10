import java.util.ArrayList;
import java.util.Collections;

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