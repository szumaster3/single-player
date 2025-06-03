package core.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a plugin manifest.
 * @author Emperor
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginManifest {

    /**
     * Gets the plugin type.
     * @return The plugin type.
     */
    PluginType type() default PluginType.ACTION;

    /**
     * Get name.
     * @return the string
     */
    String name() default "";
}