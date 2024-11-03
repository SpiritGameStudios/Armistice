package symbolics.division.armistice.registry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.util.registrar.SoundEventRegistrar;

public final class ArmisticeSoundEventRegistrar implements SoundEventRegistrar {
	public static final SoundEvent AMBIENT$MECHA1 = SoundEvent.createVariableRangeEvent(Armistice.id("ambient.mecha1"));

	public static final SoundEvent AMBIENT$MECHA2 = SoundEvent.createVariableRangeEvent(Armistice.id("ambient.mecha2"));

	public static final SoundEvent AMBIENT$GEIGER = SoundEvent.createVariableRangeEvent(Armistice.id("ambient.geiger"));

	public static final SoundEvent ENTITY$MECHA$BOOT = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.boot"));

	public static final SoundEvent ENTITY$MECHA$ALERT = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.alert"));
	public static final SoundEvent ENTITY$MECHA$ALLGOOD = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.allgood"));
	public static final SoundEvent ENTITY$MECHA$STEAM = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.steam"));
	public static final SoundEvent ENTITY$MECHA$EMPTY = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.empty"));

	public static final SoundEvent ENTITY$MECHA$STEP1 = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.step1"));
	public static final SoundEvent ENTITY$MECHA$STEP2 = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.step2"));
	public static final SoundEvent ENTITY$MECHA$STEP3 = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.step3"));

	public static final SoundEvent WEAPON$LOW_CAL = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.low_cal"));
	public static final SoundEvent WEAPON$HIGH_CAL = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.high_cal"));
	public static final SoundEvent WEAPON$MINIGUN = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.minigun"));
	public static final SoundEvent WEAPON$LASER_START = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.laser_start"));
	public static final SoundEvent WEAPON$LASER = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.laser"));
	public static final SoundEvent WEAPON$LASER_END = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.laser_end"));

	public static final SoundEvent MUSIC$RECALLED = SoundEvent.createVariableRangeEvent(Armistice.id("music.recalled"));

	public static final SoundEvent BLOCK$ARMISTEEL$PLACE = SoundEvent.createVariableRangeEvent(Armistice.id("block.armisteel.place"));

	public static final SoundEvent BLOCK$ARMISTEEL$BREAK = SoundEvent.createVariableRangeEvent(Armistice.id("block.armisteel.break"));

	public static final SoundEvent BLOCK$ARMISTEEL$FALL = SoundEvent.createVariableRangeEvent(Armistice.id("block.armisteel.fall"));

	public static final SoundEvent BLOCK$ARMISTEEL$HIT = SoundEvent.createVariableRangeEvent(Armistice.id("block.armisteel.hit"));

	public static final SoundEvent BLOCK$ARMISTEEL$STEP = SoundEvent.createVariableRangeEvent(Armistice.id("block.armisteel.step"));


	@SuppressWarnings("deprecation")
	public static class Types {
		public static final SoundType ARMISTEEL = new SoundType(
			1.0F,
			1.0F,
			BLOCK$ARMISTEEL$BREAK,
			BLOCK$ARMISTEEL$STEP,
			BLOCK$ARMISTEEL$PLACE,
			BLOCK$ARMISTEEL$HIT,
			BLOCK$ARMISTEEL$FALL
		);
	}


}
