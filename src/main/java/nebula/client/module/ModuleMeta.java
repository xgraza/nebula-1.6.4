package nebula.client.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author Gavin
 * @since 08/09/23
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleMeta {
  String name();
  String description() default "No description was provided for this feature";
  boolean defaultState() default false;
  int defaultMacro() default -1;
}
