package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.RegisterEvent;
import symbolics.division.armistice.util.ReflectionHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A registrar registers all static fields in within itself to a registry.
 * Block Registrars also register a BlockItem for each block, unless annotated with {@link NoBlockItem}.
 */
public interface BlockRegistrar extends Registrar<Block> {
    @Override
    default void init(String namespace, RegisterEvent event) {
        Registrar.super.init(namespace, event);

        event.register(
                BuiltInRegistries.ITEM.key(),
                registry -> {
                    ReflectionHelper.forEachStaticField(this.getClass(), getObjectType(), (value, name, field) -> {
                        if (field.isAnnotationPresent(Ignore.class)) return;

                        String objectName = ReflectionHelper.getAnnotation(field, Name.class)
                                .map(Name::value)
                                .orElseGet(name::toLowerCase);

                        registerBlockItem(value, namespace, objectName, registry);
                    });
                }
        );
    }

    default void registerBlockItem(Block block, String namespace, String name, RegisterEvent.RegisterHelper<Item> helper) {
        BlockItem item = new BlockItem(block, new Item.Properties());
        helper.register(ResourceLocation.fromNamespaceAndPath(namespace, name), item);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NoBlockItem {
    }

    @Override
    default Registry<Block> getRegistry() {
        return BuiltInRegistries.BLOCK;
    }

    @Override
    default Class<Block> getObjectType() {
        return Block.class;
    }
}
