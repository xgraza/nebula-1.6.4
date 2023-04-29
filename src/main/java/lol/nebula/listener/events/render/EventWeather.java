package lol.nebula.listener.events.render;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class EventWeather {
    private float rainStrength, thunderStrength;

    public EventWeather(float rainStrength, float thunderStrength) {
        this.rainStrength = rainStrength;
        this.thunderStrength = thunderStrength;
    }

    public float getRainStrength() {
        return rainStrength;
    }

    public void setRainStrength(float rainStrength) {
        this.rainStrength = rainStrength;
    }

    public float getThunderStrength() {
        return thunderStrength;
    }

    public void setThunderStrength(float thunderStrength) {
        this.thunderStrength = thunderStrength;
    }
}
