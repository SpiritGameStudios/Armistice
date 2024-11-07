package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public interface ParticleTypeRegistrar extends Registrar<ParticleType<?>> {
	@Override
	default Class<ParticleType<?>> getObjectType() {
		return Registrar.fixGenerics(ParticleType.class);
	}

	@Override
	default Registry<ParticleType<?>> getRegistry() {
		return BuiltInRegistries.PARTICLE_TYPE;
	}
}
