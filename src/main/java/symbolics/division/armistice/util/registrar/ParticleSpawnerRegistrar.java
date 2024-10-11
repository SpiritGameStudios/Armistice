package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.particle.ParticleSpawner;

public interface ParticleSpawnerRegistrar extends Registrar<ParticleSpawner> {
    @Override
    default Class<ParticleSpawner> getObjectType() {
        return ParticleSpawner.class;
    }

    @Override
	default Registry<ParticleSpawner> getRegistry() {
        return ParticleSpawner.REGISTRY;
    }
}
