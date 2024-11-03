package symbolics.division.armistice.registry;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import symbolics.division.armistice.Armistice;

public class ArmisticeJukeboxSongs {
	public static final class Keys {
		public static final ResourceKey<JukeboxSong> PEACE_ENGINES = createKey("peace_engines");
		public static final ResourceKey<JukeboxSong> RECALLED = createKey("recalled");
	}


	public static final DeferredRegister<SoundEvent> DEFERRED_SOUNDS =
		DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Armistice.MODID);

	public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC$RECALLED = DEFERRED_SOUNDS.register(
		"recalled",
		() -> SoundEvent.createVariableRangeEvent(Armistice.id("music.recalled"))
	);

	public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC$PEACE_ENGINES = DEFERRED_SOUNDS.register(
		"music.peace_engines",
		() -> SoundEvent.createVariableRangeEvent(Armistice.id("music.peace_engines"))
	);

	private static ResourceKey<JukeboxSong> createKey(String name) {
		return ResourceKey.create(Registries.JUKEBOX_SONG, Armistice.id(name));
	}

	public static void registerAll(RegisterEvent.RegisterHelper<JukeboxSong> helper) {
		createSong(helper, MUSIC$RECALLED, Keys.RECALLED, 244);
		createSong(helper, MUSIC$PEACE_ENGINES, Keys.PEACE_ENGINES, 126);
	}

	private static void createSong(
		RegisterEvent.RegisterHelper<JukeboxSong> helper, Holder<SoundEvent> holder, ResourceKey<JukeboxSong> key, int lengthInSeconds
	) {
		helper.register(key, new JukeboxSong(holder, Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), (float) lengthInSeconds, 15));
	}
}
