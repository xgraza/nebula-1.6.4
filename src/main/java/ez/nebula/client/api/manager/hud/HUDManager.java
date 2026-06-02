package ez.nebula.client.api.manager.hud;

import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.config.HUDConfig;
import ez.nebula.client.impl.hud.*;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.manager.ITypedManager;

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
        hudElementList.add(new CoordinatesHUDElement());
        hudElementList.add(new PotionStatusHUDElement());
        hudElementList.add(new ServerStatusHUDElement());
        hudElementList.add(new SpeedHUDElement());
        hudElementList.add(new TargetDisplayHUDElement());
        hudElementList.add(new TPSHUDElement());
        hudElementList.add(new WatermarkHUDElement());

        hudElementList.forEach(HUDElement::discoverSettings);
        hudElementList.forEach(HUDElement::init);
    }

    @Override
    public List<HUDElement> getAll()
    {
        return hudElementList;
    }
}
