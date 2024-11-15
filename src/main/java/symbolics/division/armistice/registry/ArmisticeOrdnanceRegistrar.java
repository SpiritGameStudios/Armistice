package symbolics.division.armistice.registry;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import symbolics.division.armistice.mecha.ordnance.HitscanGunOrdnance;
import symbolics.division.armistice.mecha.ordnance.SimpleGunOrdnance;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.network.ExtendedParticlePacket;
import symbolics.division.armistice.projectile.ArtilleryShell;
import symbolics.division.armistice.projectile.Missile;
import symbolics.division.armistice.util.AudioUtil;
import symbolics.division.armistice.util.registrar.OrdnanceRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeOrdnanceRegistrar implements OrdnanceRegistrar {
	public static float BASE_GUN_ATTENUATION = 7;

	public static final OrdnanceSchematic CROSSBOW = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			2,
			10,
			9999999,
			1.5,
			(core, posInfo) -> {
				ArtilleryShell shell = new ArtilleryShell(ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL, core.level(), 1);
				shell.setPos(posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z());
				return shell;
			},
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL,
					SoundSource.HOSTILE
				);

			}
		)
	);

	public static final OrdnanceSchematic MISSILE_LAUNCHER = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			10,
			40,
			1000,
			0,
			(core, info) -> {
				Missile missile;

				if (info.target() instanceof EntityHitResult entityHitResult) {
					missile = Missile.aimedMissile(
						new Vec3(info.pos().x(), info.pos().y(), info.pos().z()),
						core.entity(),
						entityHitResult.getEntity(),
						0.5F
					);
				} else {
					missile = new Missile(ArmisticeEntityTypeRegistrar.MISSILE, core.level());
				}


				missile.setPos(info.pos().x(), info.pos().y(), info.pos().z());
				return missile;
			},
			(core, posInfo) -> core.level().playSound(
				null,
				posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
				ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL,
				SoundSource.HOSTILE,
				BASE_GUN_ATTENUATION,
				AudioUtil.randomizedPitch(core.level().getRandom(), 1.5f, 0.1f)
			)
		)
	);

	public static final OrdnanceSchematic FLAMETHROWER = new OrdnanceSchematic(
		1,
		() -> new HitscanGunOrdnance(
			1,
			1,
			1000,
			0.5,
			(core, info) -> {
				core.level().playSound(
					null,
					info.pos().x(), info.pos().y(), info.pos().z(),
					SoundEvents.SNOWBALL_THROW,
					SoundSource.HOSTILE
				);

				Vec3 normalDir = new Vec3(info.direction().x(), info.direction().y(), info.direction().z()).normalize();

				PacketDistributor.sendToPlayersTrackingEntity(
					core.entity(),
					new ExtendedParticlePacket(
						new Vec3(info.pos().x(), info.pos().y(), info.pos().z()),
						Vec3.ZERO,
						normalDir,
						normalDir.scale(0.25),
						50,
						ParticleTypes.FLAME
					)
				);

				if (info.target() instanceof EntityHitResult entityHitResult) {
					entityHitResult.getEntity().setRemainingFireTicks(5 * 20);
				}
			}
		)
	);

	public static final OrdnanceSchematic ARTILLERY = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			40,
			20 * 7,
			2000,
			3.5,
			(core, posInfo) -> {
				ArtilleryShell shell = new ArtilleryShell(ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL, core.level(), 35);
				shell.setPos(posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z());
				return shell;
			},
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 0.5f, 0.1f)
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
						ParticleTypes.LARGE_SMOKE
					)
				);
			}
		)
	);

	public static final OrdnanceSchematic AUTOCANNON = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			40,
			20 * 3,
			2000,
			4,
			(core, posInfo) -> {
				ArtilleryShell shell = new ArtilleryShell(ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL, core.level(), 10);
				shell.setPos(posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z());
				return shell;
			},
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 0.8f, 0.1f)
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

	public static final OrdnanceSchematic RAILGUN = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			40,
			20 * 30,
			2000,
			7,
			(core, posInfo) -> {
				ArtilleryShell shell = new ArtilleryShell(ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL, core.level(), 150);
				shell.setPos(posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z());
				return shell;
			},
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 0.3f, 0.1f)
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
			1,
			5000,
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
			1,
			3,
			3000,
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
			3,
			5,
			1000,
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

	public static final OrdnanceSchematic SNIPER = new OrdnanceSchematic(
		1,
		() -> new HitscanGunOrdnance(
			20,
			7 * 20,
			2000,
			19,
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 0.8f, 0.05f)
				);
			}
		)
	);

	public static final OrdnanceSchematic LASER = new OrdnanceSchematic(
		1,
		() -> new HitscanGunOrdnance(
			1,
			1,
			20000,
			1,
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$LASER,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 1, 0.05f)
				);
			}
		)
	);

	public static final OrdnanceSchematic AUTODISPENSER = new OrdnanceSchematic(
		1,
		() -> new HitscanGunOrdnance(
			1,
			80,
			2000,
			19,
			(core, posInfo) -> {
				core.level().playSound(
					null,
					posInfo.pos().x(), posInfo.pos().y(), posInfo.pos().z(),
					ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 0.8f, 0.05f)
				);
			}
		)
	);
}
