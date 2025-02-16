package us.nebula.api.manager.command.argument;

/**
 * @author xgraza
 * @since 02/16/25
 */
public abstract class Constraint<T>
{
    protected Argument<T> argument;
    private final String failReason;

    public Constraint(final String failReason)
    {
        this.failReason = failReason;
    }

    public abstract boolean passes(final String raw);

    public void setArgument(final Argument<T> argument)
    {
        this.argument = argument;
    }

    public String getFailReason()
    {
        return failReason;
    }
}
