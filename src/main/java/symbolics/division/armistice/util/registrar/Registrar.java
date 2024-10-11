package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;
import symbolics.division.armistice.util.ReflectionHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * A registrar registers all static fields in within itself to a registry.
 *
 * @param <T> Type of object to register
 */
public interface Registrar<T> {
    /**
     * Process a registrar class and register all objects.
     *
     * @param clazz     Registrar class to process
     * @param namespace Namespace to register objects in
     */
    static <T> void process(Class<? extends Registrar<T>> clazz, String namespace, RegisterEvent event) {
        Registrar<T> registrar = ReflectionHelper.instantiate(clazz);
        registrar.init(namespace, event);
    }

    /**
     * Initialize the registrar and register all objects.
     * Do not call this method directly, use {@link Registrar#process(Class, String, RegisterEvent)} instead.
     *
     * @param namespace Namespace to register objects in
     */
    @ApiStatus.Internal
    default void init(String namespace, RegisterEvent event) {
        event.register(
                getRegistry().key(),
                registry -> {
                    ReflectionHelper.forEachStaticField(this.getClass(), getObjectType(), (value, name, field) -> {
                        if (field.isAnnotationPresent(Ignore.class)) return;

                        String objectName = ReflectionHelper.getAnnotation(field, Name.class)
                                .map(Name::value)
                                .orElseGet(name::toLowerCase);

                        register(objectName, namespace, value, field, registry);
                    });
                }
        );
    }

    /**
     * Register an object to the registry.
     *
     * @param name      Name of the object
     * @param namespace Namespace to register the object in
     * @param object    Object to register
     * @param field     Field the object is stored in
     */
    default void register(String name, String namespace, T object, Field field, RegisterEvent.RegisterHelper<T> helper) {
        helper.register(ResourceLocation.fromNamespaceAndPath(namespace, name), object);
    }

    /**
     * Get the type of object to register.
     * If {@link T} has a generic type of ?, use {@link Registrar#fixGenerics(Class)} to force the correct type.
     *
     * @return The type of object to register
     */
    Class<T> getObjectType();

    /**
     * Get the registry to register objects in.
     *
     * @return The registry to register objects in
     */
    Registry<T> getRegistry();

    /**
     * Workaround for Java's type erasure.
     * Use this if {@link T} has a generic type of ?.
     */
    @SuppressWarnings("unchecked")
    static <T> Class<T> fixGenerics(Class<?> clazz) {
        return (Class<T>) clazz;
    }

    /**
     * Ignore a field when registering objects.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Ignore {
    }

    /**
     * Set the name of the object to register.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Name {
        String value();
    }
}
