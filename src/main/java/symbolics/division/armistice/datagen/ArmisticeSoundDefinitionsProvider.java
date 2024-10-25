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

		add(ArmisticeSoundEventRegistrar.ENTITY$MECHA$STEP, SoundDefinition.definition()
			.with(sound(Armistice.id("entity/mecha/step1")), sound(Armistice.id("entity/mecha/step2")))
			.subtitle("sound.armistice.entity.mecha.step"));

		add(ArmisticeSoundEventRegistrar.MUSIC$RECALLED, SoundDefinition.definition()
			.with(sound(Armistice.id("music/recalled")))
			.subtitle("sound.armistice.music.recalled"));
	}
}
