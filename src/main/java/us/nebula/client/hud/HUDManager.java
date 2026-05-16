package us.nebula.client.hud;

import us.nebula.client.Nebula;
import us.nebula.client.hud.impl.*;
import us.nebula.client.listener.EventBus;
import us.nebula.client.util.ITypedManager;

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
        hudElementList.add(new ArraylistHUDElement());
        hudElementList.add(new WatermarkHUDElement());
        hudElementList.add(new TargetDisplayHUDElement());
        hudElementList.add(new CoordinatesHUDElement());

        hudElementList.forEach(HUDElement::reflectSettings);
        hudElementList.forEach(HUDElement::init);
    }

    @Override
    public List<HUDElement> getAll()
    {
        return hudElementList;
    }
}
