package symbolics.division.armistice.registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Snowball;
import symbolics.division.armistice.mecha.ordnance.HitscanGunOrdnance;
import symbolics.division.armistice.mecha.ordnance.SimpleGunOrdnance;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.util.AudioUtil;
import symbolics.division.armistice.util.registrar.OrdnanceRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeOrdnanceRegistrar implements OrdnanceRegistrar {
	public static float BASE_GUN_ATTENUATION = 3;

	public static final OrdnanceSchematic CROSSBOW = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			5,
			9999999,
			1.5,
			(core, pos) -> new Snowball(core.level(), pos.x(), pos.y(), pos.z()),
			(core, pos) -> core.level().playSound(
				null,
				pos.x(), pos.y(), pos.z(),
				ArmisticeSoundEventRegistrar.ENTITY$MECHA$WEAPON$HIGH_CAL,
				SoundSource.HOSTILE
			)
		)
	);

	public static final OrdnanceSchematic FLAMETHROWER = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			1,
			50,
			1.5,
			(core, pos) -> new Snowball(core.level(), pos.x(), pos.y(), pos.z()),
			(core, pos) -> core.level().playSound(
				null,
				pos.x(), pos.y(), pos.z(),
				SoundEvents.SNOWBALL_THROW,
				SoundSource.HOSTILE
			)
		)
	);

	public static final OrdnanceSchematic MINIGUN = new OrdnanceSchematic(
		1,
		() -> new HitscanGunOrdnance(
			1,
			50,
			1,
			(core, pos) -> {
				core.level().playSound(
					null,
					pos.x(), pos.y(), pos.z(),
					ArmisticeSoundEventRegistrar.ENTITY$MECHA$WEAPON$MINIGUN,
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
			(core, pos) -> {
				core.level().playSound(
					null,
					pos.x(), pos.y(), pos.z(),
					ArmisticeSoundEventRegistrar.ENTITY$MECHA$WEAPON$LOW_CAL,
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
			(core, pos) -> {
				core.level().playSound(
					null,
					pos.x(), pos.y(), pos.z(),
					ArmisticeSoundEventRegistrar.ENTITY$MECHA$WEAPON$LOW_CAL,
					SoundSource.HOSTILE,
					BASE_GUN_ATTENUATION,
					AudioUtil.randomizedPitch(core.level().getRandom(), 0.6f, 0.05f)
				);
			}
		)
	);
}
