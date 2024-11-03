package symbolics.division.armistice.registry;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import symbolics.division.armistice.mecha.ordnance.HitscanGunOrdnance;
import symbolics.division.armistice.mecha.ordnance.SimpleGunOrdnance;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.network.ExtendedParticlePacket;
import symbolics.division.armistice.projectile.ArtilleryShell;
import symbolics.division.armistice.util.AudioUtil;
import symbolics.division.armistice.util.registrar.OrdnanceRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeOrdnanceRegistrar implements OrdnanceRegistrar {
	public static float BASE_GUN_ATTENUATION = 3;

	public static final OrdnanceSchematic CROSSBOW = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			10,
			9999999,
			1.5,
			(core, posInfo) -> {
				ArtilleryShell shell = new ArtilleryShell(ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL, core.level());
				shell.setPos(posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z());
				return shell;
			},
			(core, posInfo) -> core.level().playSound(
				null,
				posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
				ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL,
				SoundSource.HOSTILE
			)
		)
	);

	public static final OrdnanceSchematic FLAMETHROWER = new OrdnanceSchematic(
		2,
		() -> new SimpleGunOrdnance(
			1,
			200,
			1.5,
			(core, posInfo) -> {
				ArtilleryShell shell = new ArtilleryShell(ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL, core.level());
				shell.setPos(posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z());
				return shell;
			},
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					SoundEvents.SNOWBALL_THROW,
					SoundSource.HOSTILE
				);

				Vec3 normalDir = new Vec3(posInfo.direction().x(), posInfo.direction().y(), posInfo.direction().z()).normalize();

				PacketDistributor.sendToPlayersTrackingEntity(
					core.entity(),
					new ExtendedParticlePacket(
						new Vec3(posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z()),
						Vec3.ZERO,
						normalDir,
						normalDir.scale(0.25),
						50,
						ParticleTypes.FLAME
					)
				);
			}
		)
	);

	public static final OrdnanceSchematic MINIGUN = new OrdnanceSchematic(
		1,
		() -> new HitscanGunOrdnance(
			1,
			50,
			1,
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$MINIGUN,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 1.3f, 0.05f)
				);
			}
		)
	);

	public static final OrdnanceSchematic LIGHT_MACHINE_GUN = new OrdnanceSchematic(
		1,
		() -> new HitscanGunOrdnance(
			3,
			30,
			2,
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$LOW_CAL,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 1, 0.05f)
				);
			}
		)
	);

	public static final OrdnanceSchematic HEAVY_MACHINE_GUN = new OrdnanceSchematic(
		1,
		() -> new HitscanGunOrdnance(
			10,
			60,
			5,
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$LOW_CAL,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 0.6f, 0.05f)
				);
			}
		)
	);
}
