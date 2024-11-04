package symbolics.division.armistice.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.registry.ArmisticeJukeboxSongs;
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

		add(ArmisticeSoundEventRegistrar.AMBIENT$GEIGER, SoundDefinition.definition()
			.with(sound(Armistice.id("ambient/geiger")))
			.subtitle("sound.armistice.ambient.geiger"));

		add(ArmisticeSoundEventRegistrar.ENV$EXPLOSION1, SoundDefinition.definition()
			.with(sound(Armistice.id("ambient/explosion1")))
			.subtitle("sound.armistice.ambient.explosion1"));

		add(ArmisticeSoundEventRegistrar.ENV$EXPLOSION2, SoundDefinition.definition()
			.with(sound(Armistice.id("ambient/explosion2")))
			.subtitle("sound.armistice.ambient.explosion2"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$BOOT, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/boot")))
			.subtitle("sound.armistice.entity.mecha.boot"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$COCKPIT, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/cockpit")))
			.subtitle("sound.armistice.entity.mecha.cockpit"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$ALERT, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/alert")))
			.subtitle("sound.armistice.entity.mecha.alert"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$ALLGOOD, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/allgood")))
			.subtitle("sound.armistice.entity.mecha.allgood"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$STEAM, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/steam")))
			.subtitle("sound.armistice.entity.mecha.steam"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$EMPTY, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/empty")))
			.subtitle("sound.armistice.entity.mecha.empty"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$STEP1, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/step1")))
			.subtitle("sound.armistice.entity.mecha.step1"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$STEP2, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/step2")))
			.subtitle("sound.armistice.entity.mecha.step2"));

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$STEP3, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/step3")))
			.subtitle("sound.armistice.entity.mecha.step3"));

		add(ArmisticeSoundEventRegistrar.WEAPON$LOW_CAL, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/low_cal")))
			.subtitle("sound.armistice.entity.mecha.weapon.low_cal"));

		add(ArmisticeSoundEventRegistrar.WEAPON$HIGH_CAL, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/high_cal")))
			.subtitle("sound.armistice.entity.mecha.weapon.high_cal"));

		add(ArmisticeSoundEventRegistrar.WEAPON$MINIGUN, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/minigun")))
			.subtitle("sound.armistice.entity.mecha.weapon.minigun"));

		add(ArmisticeSoundEventRegistrar.WEAPON$LASER_START, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/laser_start")))
			.subtitle("sound.armistice.entity.mecha.weapon.laser_start"));

		add(ArmisticeSoundEventRegistrar.WEAPON$LASER, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/laser")))
			.subtitle("sound.armistice.entity.mecha.weapon.laser"));

		add(ArmisticeSoundEventRegistrar.WEAPON$LASER_END, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/weapon/laser_end")))
			.subtitle("sound.armistice.entity.mecha.weapon.laser_end"));

		add(ArmisticeJukeboxSongs.MUSIC$RECALLED, SoundDefinition.definition()
			.with(sound(Armistice.id("music/recalled")))
			.subtitle("sound.armistice.music.recalled"));

		add(ArmisticeJukeboxSongs.MUSIC$PEACE_ENGINES, SoundDefinition.definition()
			.with(sound(Armistice.id("music/peace_engines")))
			.subtitle("sound.armistice.music.peace_engines"));

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
