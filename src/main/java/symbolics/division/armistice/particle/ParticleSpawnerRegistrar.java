package symbolics.division.armistice.particle;

import net.minecraft.core.Registry;
import symbolics.division.armistice.util.registry.Registrar;

public class ParticleSpawnerRegistrar implements Registrar<ParticleSpawner> {

    @Override
    public Class<ParticleSpawner> getObjectType() {
        return ParticleSpawner.class;
    }

    @Override
    public Registry<ParticleSpawner> getRegistry() {
        return ParticleSpawner.REGISTRY;
    }
}
