package us.nebula.api.manager.overlay;

import us.nebula.api.manager.ITypedManager;
import us.nebula.impl.gui.overlay.ArmorOverlay;
import us.nebula.impl.gui.overlay.ArraylistOverlay;
import us.nebula.impl.gui.overlay.CoordinatesOverlay;
import us.nebula.impl.gui.overlay.WatermarkOverlay;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xgraza
 * @since 02/26/25
 */
@SuppressWarnings("unchecked")
public final class OverlayManager implements ITypedManager<Overlay>
{
    private final Map<String, Overlay> overlayIdMap = new LinkedHashMap<>();
    private final List<Overlay> overlayList = new LinkedList<>();

    @Override
    public void init()
    {
        addOverlay(new ArmorOverlay());
        addOverlay(new ArraylistOverlay());
        addOverlay(new CoordinatesOverlay());
        addOverlay(new WatermarkOverlay());
    }

    public void addOverlay(final Overlay overlay)
    {
        overlayIdMap.put(overlay.getManifest().value(), overlay);
        overlayList.add(overlay);
    }

    public <T extends Overlay> T getReference(final String id)
    {
        return (T)overlayIdMap.get(id);
    }

    @Override
    public List<Overlay> getAll()
    {
        return overlayList;
    }
}
