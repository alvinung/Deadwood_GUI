import java.util.ArrayList;

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