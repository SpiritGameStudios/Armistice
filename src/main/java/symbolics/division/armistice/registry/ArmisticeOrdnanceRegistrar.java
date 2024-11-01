package symbolics.division.armistice.registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Snowball;
import symbolics.division.armistice.mecha.ordnance.HitscanGunOrdnance;
import symbolics.division.armistice.mecha.ordnance.SimpleGunOrdnance;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.util.registrar.OrdnanceRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeOrdnanceRegistrar implements OrdnanceRegistrar {
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
				SoundEvents.SNOWBALL_THROW,
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
			3,
			50,
			1,
			(core, pos) -> {
				core.level().playSound(
					null,
					pos.x(), pos.y(), pos.z(),
					SoundEvents.WITHER_SHOOT,
					SoundSource.HOSTILE
				);
			}
		)
	);
}
