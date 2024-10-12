package symbolics.division.armistice;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import symbolics.division.armistice.datagen.ArmisticeDatagen;
import symbolics.division.armistice.event.RegistryEvents;

@Mod(Armistice.MODID)
public class Armistice {
	public static final String MODID = "armistice";
	private static final Logger LOGGER = LogUtils.getLogger();

	public Armistice(IEventBus modEventBus, ModContainer modContainer) {
		RegistryEvents.init(modEventBus);
		modEventBus.register(ArmisticeDatagen.class);
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
