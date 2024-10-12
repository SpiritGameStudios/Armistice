package symbolics.division.armistice.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public interface ParticleSpawner {
	void spawn(ParticleType<?> particle, Vec3 origin, Function<Vec3, Vec3> pos, Function<Vec3, Vec3> velocity);
}
