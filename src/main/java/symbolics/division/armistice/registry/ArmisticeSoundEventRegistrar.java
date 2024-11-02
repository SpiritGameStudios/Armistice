package symbolics.division.armistice.registry;

import net.minecraft.sounds.SoundEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.util.registrar.SoundEventRegistrar;

public final class ArmisticeSoundEventRegistrar implements SoundEventRegistrar {
	public static final SoundEvent AMBIENT$MECHA1 = SoundEvent.createVariableRangeEvent(Armistice.id("ambient.mecha1"));

	public static final SoundEvent AMBIENT$MECHA2 = SoundEvent.createVariableRangeEvent(Armistice.id("ambient.mecha2"));

	public static final SoundEvent ENTITY$MECHA$BOOT = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.boot"));


	public static final SoundEvent ENTITY$MECHA$STEP1 = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.step1"));
	public static final SoundEvent ENTITY$MECHA$STEP2 = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.step2"));
	public static final SoundEvent ENTITY$MECHA$STEP3 = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.step3"));

	public static final SoundEvent ENTITY$MECHA$WEAPON$LOW_CAL = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.low_cal"));
	public static final SoundEvent ENTITY$MECHA$WEAPON$HIGH_CAL = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.high_cal"));
	public static final SoundEvent ENTITY$MECHA$WEAPON$MINIGUN = SoundEvent.createVariableRangeEvent(Armistice.id("entity.mecha.weapon.minigun"));

	public static final SoundEvent MUSIC$RECALLED = SoundEvent.createVariableRangeEvent(Armistice.id("music.recalled"));

}
