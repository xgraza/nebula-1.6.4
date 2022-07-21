package wtf.nebula.impl.gui.ui;

public abstract class DraggableComponent extends Component {
    private boolean dragging = false;
    private double dragX, dragY;

    public DraggableComponent(String name) {
        super(name);
    }

    public void updatePositioning(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }
    }

    public void updateDragging(int mouseX, int mouseY) {
        if (dragging) {
            dragX = x - mouseX;
            dragY = y - mouseY;
        }
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }
}
