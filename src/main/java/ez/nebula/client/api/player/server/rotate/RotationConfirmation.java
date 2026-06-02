package ez.nebula.client.api.player.server.rotate;

/**
 * @author xgraza
 * @since 03/22/25
 */
@FunctionalInterface
public interface RotationConfirmation
{
    void onServerRotateConfirm(final float yaw, final float pitch);
}
