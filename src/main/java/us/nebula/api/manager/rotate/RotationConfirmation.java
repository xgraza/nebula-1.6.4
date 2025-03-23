package us.nebula.api.manager.rotate;

/**
 * @author xgraza
 * @since 03/22/25
 */
@FunctionalInterface
public interface RotationConfirmation
{
    void onServerRotateConfirm(final float yaw, final float pitch);
}
