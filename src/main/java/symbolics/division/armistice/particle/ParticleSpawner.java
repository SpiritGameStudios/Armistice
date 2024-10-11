package symbolics.division.armistice.particle;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

import static symbolics.division.armistice.Armistice.MODID;

public interface ParticleSpawner {
    void spawn(ParticleType<?> particle, Vec3 origin, Function<Vec3, Vec3> pos, Function<Vec3, Vec3> velocity);

    ResourceKey<Registry<ParticleSpawner>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "particle_spawner"));

    Registry<ParticleSpawner> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
            .sync(true)
            .create();
}
