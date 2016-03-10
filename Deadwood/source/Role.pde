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