package us.nebula.client.api.trait;

/**
 * @author xgraza
 * @since 3/23/26
 */
public interface Togglable
{
    default void onEnable()
    {

    }

    default void onDisable()
    {

    }

    void toggle();

    void setToggled(boolean state);

    boolean isToggled();
}
