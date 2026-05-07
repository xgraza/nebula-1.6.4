package us.nebula.client.api.manager.hud;

import us.nebula.client.Nebula;
import us.nebula.client.api.listener.EventBus;
import us.nebula.client.api.manager.ITypedManager;
import us.nebula.client.impl.hud.ArmorStatusHUDElement;
import us.nebula.client.impl.hud.CoordinatesHUDElement;
import us.nebula.client.impl.hud.WatermarkHUDElement;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDManager implements ITypedManager<HUDElement>
{
    private final List<HUDElement> hudElementList = new LinkedList<>();

    @Override
    public void init()
    {
        EventBus.subscribe(this);
        Nebula.INSTANCE.getConfigurationManager()
                .addConfiguration(new HUDConfig(this));

        hudElementList.add(new ArmorStatusHUDElement());
        hudElementList.add(new WatermarkHUDElement());
        hudElementList.add(new CoordinatesHUDElement());

        hudElementList.forEach(HUDElement::reflectSettings);
    }

    @Override
    public List<HUDElement> getAll()
    {
        return hudElementList;
    }
}
