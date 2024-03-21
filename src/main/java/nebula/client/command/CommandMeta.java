package nebula.client.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Gavin
 * @since 08/10/23
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMeta {
  String[] aliases();
  String description() default "No description was provided for this feature";
  String syntax() default "";
}
