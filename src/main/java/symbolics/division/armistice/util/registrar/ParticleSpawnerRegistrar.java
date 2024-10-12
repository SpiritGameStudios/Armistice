package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.particle.ParticleSpawner;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public interface ParticleSpawnerRegistrar extends Registrar<ParticleSpawner> {
	@Override
	default Class<ParticleSpawner> getObjectType() {
		return ParticleSpawner.class;
	}

	@Override
	default Registry<ParticleSpawner> getRegistry() {
		return ArmisticeRegistries.PARTICLE_SPAWNER;
	}
}
