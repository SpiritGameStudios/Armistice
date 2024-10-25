package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.lang.reflect.Field;

/**
 * Automatically register each sound event using reflection.
 * Unlike normal registrars, <code>$</code> in the field name is replaced with <code>.</code> in the registry to allow for easier naming.
 */
public interface SoundEventRegistrar extends Registrar<SoundEvent> {
	@Override
	default void register(String name, String namespace, SoundEvent object, Field field, RegisterEvent.RegisterHelper<SoundEvent> helper) {
		helper.register(ResourceLocation.fromNamespaceAndPath(namespace, name.replace('$', '.')), object);
	}

	@Override
	default Registry<SoundEvent> getRegistry() {
		return BuiltInRegistries.SOUND_EVENT;
	}

	@Override
	default Class<SoundEvent> getObjectType() {
		return SoundEvent.class;
	}
}
