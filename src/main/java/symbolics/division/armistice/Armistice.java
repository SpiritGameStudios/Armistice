package symbolics.division.armistice;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import symbolics.division.armistice.datagen.ArmisticeDatagen;
import symbolics.division.armistice.debug.ArmisticeDebugValues;
import symbolics.division.armistice.event.RegistryEvents;
import symbolics.division.armistice.network.OutlinerSyncS2CPayload;

@Mod(Armistice.MODID)
public class Armistice {
	public static final String MODID = "armistice";
	public static final Logger LOGGER = LogUtils.getLogger();

	public Armistice(IEventBus modEventBus, ModContainer modContainer) {
		RegistryEvents.init(modEventBus);
		modEventBus.register(ArmisticeDatagen.class);

		NeoForge.EVENT_BUS.register(OutlinerSyncS2CPayload.class);

		if (FMLEnvironment.production) return;

		NeoForge.EVENT_BUS.register(ArmisticeDebugValues.class);
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
