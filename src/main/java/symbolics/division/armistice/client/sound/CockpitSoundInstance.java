package symbolics.division.armistice.client.sound;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.registry.ArmisticeSoundEventRegistrar;

@OnlyIn(Dist.CLIENT)
public class CockpitSoundInstance extends SimpleSoundInstance implements TickableSoundInstance {
	private final MechaEntity mecha;
	private final Player pilot;

	public CockpitSoundInstance(MechaEntity mecha, Player pilot, float volume, float pitch, RandomSource random) {
		super(ArmisticeSoundEventRegistrar.ENTITY$MECHA$COCKPIT.getLocation(), SoundSource.HOSTILE, volume, pitch, random, true, 0, Attenuation.NONE, 0, 0, 0, true);
		this.mecha = mecha;
		this.pilot = pilot;
	}

	@Override
	public boolean isStopped() {
		return !mecha.hasPassenger(pilot);
	}

	@Override
	public void tick() {
	}
}
