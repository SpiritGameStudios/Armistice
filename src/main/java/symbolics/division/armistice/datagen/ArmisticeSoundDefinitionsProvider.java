package symbolics.division.armistice.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.registry.ArmisticeSoundEventRegistrar;

public class ArmisticeSoundDefinitionsProvider extends SoundDefinitionsProvider {
	public ArmisticeSoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, Armistice.MODID, helper);
	}

	@Override
	public void registerSounds() {
		add(ArmisticeSoundEventRegistrar.AMBIENT$MECHA1, SoundDefinition.definition()
			.with(sound(Armistice.id("ambient/mecha1")))
			.subtitle("sound.armistice.ambient.mecha1"));

		add(ArmisticeSoundEventRegistrar.AMBIENT$MECHA2, SoundDefinition.definition()
			.with(sound(Armistice.id("ambient/mecha2")))
			.subtitle("sound.armistice.ambient.mecha2"));


		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$BOOT, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/boot")))
			.subtitle("sound.armistice.entity.mecha.boot"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$STEP1, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/step1")))
			.subtitle("sound.armistice.entity.mecha.step1"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$STEP2, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/step2")))
			.subtitle("sound.armistice.entity.mecha.step2"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$STEP3, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/step3")))
			.subtitle("sound.armistice.entity.mecha.step3"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$WEAPON$LOW_CAL, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/low_cal")))
			.subtitle("sound.armistice.entity.mecha.weapon.low_cal"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$WEAPON$HIGH_CAL, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/high_cal")))
			.subtitle("sound.armistice.entity.mecha.weapon.high_cal"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$WEAPON$MINIGUN, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/minigun")))
			.subtitle("sound.armistice.entity.mecha.weapon.minigun"));

		add(ArmisticeSoundEventRegistrar.MUSIC$RECALLED, SoundDefinition.definition()
			.with(sound(Armistice.id("music/recalled")))
			.subtitle("sound.armistice.music.recalled"));

		add(ArmisticeSoundEventRegistrar.BLOCK$ARMISTEEL$PLACE, SoundDefinition.definition()
			.with(sound(Armistice.id("block/armisteel/place"))));

		add(ArmisticeSoundEventRegistrar.BLOCK$ARMISTEEL$BREAK, SoundDefinition.definition()
			.with(sound(Armistice.id("block/armisteel/break"))));

		add(ArmisticeSoundEventRegistrar.BLOCK$ARMISTEEL$FALL, SoundDefinition.definition()
			.with(sound(Armistice.id("block/armisteel/fall"))));

		add(ArmisticeSoundEventRegistrar.BLOCK$ARMISTEEL$HIT, SoundDefinition.definition()
			.with(
				sound(Armistice.id("block/armisteel/hit1")),
				sound(Armistice.id("block/armisteel/hit2")),
				sound(Armistice.id("block/armisteel/hit3")),
				sound(Armistice.id("block/armisteel/hit4"))
			));

		add(ArmisticeSoundEventRegistrar.BLOCK$ARMISTEEL$STEP, SoundDefinition.definition()
			.with(
				sound(Armistice.id("block/armisteel/step1")),
				sound(Armistice.id("block/armisteel/step2")),
				sound(Armistice.id("block/armisteel/step3")),
				sound(Armistice.id("block/armisteel/step4"))
			));
	}
}
