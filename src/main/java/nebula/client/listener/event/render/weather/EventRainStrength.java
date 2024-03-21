package nebula.client.listener.event.render.weather;

/**
 * @author Gavin
 * @since 08/24/23
 */
public class EventRainStrength {

  private float strength;

  public EventRainStrength(float strength) {
    this.strength = strength;
  }

  public float strength() {
    return strength;
  }

  public void setStrength(float strength) {
    this.strength = strength;
  }
}
