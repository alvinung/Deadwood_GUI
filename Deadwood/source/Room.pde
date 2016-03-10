import java.util.*;

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