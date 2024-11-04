package symbolics.division.armistice.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaSkin;
import symbolics.division.armistice.mecha.schematic.ArmorSchematic;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;
import symbolics.division.armistice.mecha.schematic.HullSchematic;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.particle.ParticleSpawner;

public final class ArmisticeRegistries {
	// region Keys
	public static final ResourceKey<Registry<ParticleSpawner>> PARTICLE_SPAWNER_KEY = ResourceKey.createRegistryKey(Armistice.id("particle_spawner"));

	public static final ResourceKey<Registry<OrdnanceSchematic>> ORDNANCE_KEY = ResourceKey.createRegistryKey(Armistice.id("ordnance"));

	public static final ResourceKey<Registry<HullSchematic>> HULL_KEY = ResourceKey.createRegistryKey(Armistice.id("hull"));

	public static final ResourceKey<Registry<ArmorSchematic>> ARMOR_KEY = ResourceKey.createRegistryKey(Armistice.id("armor"));

	public static final ResourceKey<Registry<ChassisSchematic>> CHASSIS_KEY = ResourceKey.createRegistryKey(Armistice.id("chassis"));

	public static final ResourceKey<Registry<MechaSkin>> SKIN_KEY = ResourceKey.createRegistryKey(Armistice.id("skin"));

	// endregion

	// region Registries
	public static final Registry<ParticleSpawner> PARTICLE_SPAWNER = new RegistryBuilder<>(PARTICLE_SPAWNER_KEY)
		.sync(true)
		.create();

	public static final Registry<OrdnanceSchematic> ORDNANCE = new RegistryBuilder<>(ORDNANCE_KEY)
		.sync(true)
		.create();
	// endregion

	private ArmisticeRegistries() {
	}
}
