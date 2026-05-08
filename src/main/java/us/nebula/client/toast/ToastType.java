package us.nebula.client.toast;

/**
 * @author xgraza
 * @since 03/03/25
 */
public enum ToastType
{
    INFO("C"),
    WARNING("D"),
    ERROR("B");

    private final String iconChar;

    ToastType(final String iconChar)
    {
        this.iconChar = iconChar;
    }

    public String getIconChar()
    {
        return iconChar;
    }
}
