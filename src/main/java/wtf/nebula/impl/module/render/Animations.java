package wtf.nebula.impl.module.render;

import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class Animations extends Module {
    public Animations() {
        super("Animations", ModuleCategory.RENDER);
    }

    public final Value<Mode> mode = new Value<>("Mode", Mode.FATHUM);

//    public void hookRenderBlockAnimation(ItemRenderer renderer, float swingProgress) {
//        switch (mode.getValue()) {
//            case DEFAULT:
//                renderer.doDefaultBlockAnimation();
//                break;
//
//            case STRAIGHT:
//                if (swingProgress == 0.0f) {
//                    renderer.doDefaultBlockAnimation();
//                    break;
//                }
//
//                glPushMatrix();
//                glRotatef(-swingProgress * 55 / 2.0f, -8.0f, 0.0f, 9.0f);
//                glRotatef(-swingProgress * 45, 1.0f, 0.0f, -0.0f);
//                renderer.doDefaultBlockAnimation();
//                glTranslated(1.2, 0.3, 0.5);
//                glTranslated(-1, mc.thePlayer.isSneaking() ? -0.1f : 0.2f, 0.2f);
//                glPopMatrix();
//                break;
//
//            case FATHUM:
//                glPopMatrix();
//                glRotated(25, 0.0, 0.2, 0.0);
//                renderer.transformFirstPersonItem(0.0f, swingProgress);
//                glScaled(0.9, 0.9, 0.9);
//                renderer.doDefaultBlockAnimation();
//                glPushMatrix();
//                break;
//
//            case SMOOTH:
//                glTranslated(0, 0.2, 0);
//                glRotatef(-swingProgress, 4, -0.8f, -1.0f);
//                renderer.doDefaultBlockAnimation();
//                break;
//        }
//    }

    public enum Mode {
        DEFAULT,
        STRAIGHT,
        FATHUM,
        SMOOTH
    }
}
