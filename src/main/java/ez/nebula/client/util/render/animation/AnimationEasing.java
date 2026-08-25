package ez.nebula.client.util.render.animation;

/**
 * @author xgraza, Easings.net
 * @link {<a href="https://easings.net/">Easings.net</a>}
 * @since 03/01/25
 */
public interface AnimationEasing
{

    AnimationEasing EXPO_IN_OUT = (x) -> x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;

    AnimationEasing CUBIC_IN_OUT = (x) -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;

    double ease(final double x);
}
