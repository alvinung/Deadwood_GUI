import java.util.ArrayList;

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
        locs[0] = new Coord(26.29, -14.38);
        locs[1] = new Coord(26.29, -7.62);
        locs[2]= new Coord(26.29, -5.08);
      } else if (num == 2) {
        locs[0] = new Coord(26.29, -10.1);
        locs[1] = new Coord(26.29, -5.91);
      } else {
        locs[0] = new Coord(26.29, -7.62);
      }

      // Hotel on-card roles
    } else if (rotR) {
      if (num == 3) {
        locs[0] = new Coord(-14.28, 56.61);
        locs[1] = new Coord(-14.28, 11.42);
        locs[2]= new Coord(-14.28, 6.59);
      } else if (num == 2) {
        locs[0] = new Coord(-14.28, 18.7);
        locs[1] = new Coord(-14.28, 8.34);
      } else {
        locs[0] = new Coord(-14.28, 11.42);
      }
    } else {
      if (num == 3) {
        locs[0] = new Coord(63.16, 20.73);
        locs[1] = new Coord(15.00, 20.73);
        locs[2]= new Coord(8.51, 20.73);
      } else if (num == 2) {
        locs[0] = new Coord(23.53, 20.73);
        locs[1] = new Coord(10.71, 20.73);
      } else {
        locs[0] = new Coord(15.00, 20.73);
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
      cardWidth = (float)height / 4.59;
      cardHeight = ((float)width-infoBarSize) / 10.81;
      dieWidth = ((float)width-infoBarSize) / 30;
      dieHeight = (float)height / 21.25;
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