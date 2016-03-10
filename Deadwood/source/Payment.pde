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