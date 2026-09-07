package ez.nebula.client.api.manager.hud;

import ez.nebula.client.Nebula;
import ez.nebula.client.impl.config.HUDConfig;
import ez.nebula.client.impl.hud.*;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.manager.ITypedManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDManager implements ITypedManager<HUDElement>
{
    static final Logger LOGGER = LogManager.getLogger("HUD");

    private final List<HUDElement> hudElementList = new LinkedList<>();

    @Override
    public void init()
    {
        EventBus.subscribe(this);
        Nebula.CONFIGS.addConfiguration(new HUDConfig(this));

        hudElementList.add(new ArmorStatusHUDElement());
        hudElementList.add(new ArraylistHUDElement());
        hudElementList.add(new CoordinatesHUDElement());
        hudElementList.add(new FPSHUDElement());
        hudElementList.add(new PotionStatusHUDElement());
        hudElementList.add(new ServerStatusHUDElement());
        hudElementList.add(new SpeedHUDElement());
        hudElementList.add(new TargetDisplayHUDElement());
        hudElementList.add(new TPSHUDElement());
        hudElementList.add(new WatermarkHUDElement());

        LOGGER.info("Registered {} HUD elements", hudElementList.size());

        hudElementList.forEach(HUDElement::discoverSettings);
        hudElementList.forEach(HUDElement::init);
    }

    @Override
    public List<HUDElement> getAll()
    {
        return hudElementList;
    }
}
