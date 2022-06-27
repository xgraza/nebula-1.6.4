package wtf.nebula.util.feature;

import wtf.nebula.util.Globals;

/**
 * A base feature
 *
 * @author aesthetical
 * @since 06/27/22
 */
public class Feature implements Globals {
    protected final String name;

    public Feature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
