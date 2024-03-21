package nebula.client.listener.event.render;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class EventRenderLiving extends Cancelable {
  private final EntityLivingBase entity;
  private final ModelBase modelBase;
  private float par2, par3, par4, par5, par6, par7;

  public EventRenderLiving(ModelBase modelBase, EntityLivingBase entity, float par2, float par3, float par4, float par5, float par6, float par7) {
    this.entity = entity;
    this.modelBase = modelBase;
    this.par2 = par2;
    this.par3 = par3;
    this.par4 = par4;
    this.par5 = par5;
    this.par6 = par6;
    this.par7 = par7;
  }

  public EntityLivingBase entity() {
    return entity;
  }

  public ModelBase modelBase() {
    return modelBase;
  }

  public float getPar2() {
    return par2;
  }

  public float getPar3() {
    return par3;
  }

  public float getPar4() {
    return par4;
  }

  public float getPar5() {
    return par5;
  }

  public float getPar6() {
    return par6;
  }

  public float getPar7() {
    return par7;
  }

  public void renderModel() {
    modelBase.render(entity, par2, par3, par4, par5, par6, par7);
  }
}
