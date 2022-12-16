package wtf.nebula.client.utils.render.renderers;

import wtf.nebula.client.utils.client.Wrapper;
import wtf.nebula.client.utils.render.enums.Dimension;

public abstract class Renderer implements Wrapper {
    protected Dimension dimension;

    public abstract void render();

    public Renderer setDimension(Dimension dimension) {
        this.dimension = dimension;
        return this;
    }
}
