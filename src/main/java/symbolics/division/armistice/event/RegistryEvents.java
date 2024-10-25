package symbolics.division.armistice.event;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.debug.command.HealCommand;
import symbolics.division.armistice.mecha.schematic.*;
import symbolics.division.armistice.model.ModelElementReloadListener;
import symbolics.division.armistice.model.ModelOutlinerReloadListener;
import symbolics.division.armistice.network.OutlinerSyncS2CPayload;
import symbolics.division.armistice.recipe.MechaSchematicRecipe;
import symbolics.division.armistice.registry.*;
import symbolics.division.armistice.util.registrar.*;

import java.util.List;

import static symbolics.division.armistice.Armistice.MODID;

public final class RegistryEvents {
	public static void init(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.register(RegistryEvents.class);
		modEventBus.register(ModEvents.class);
	}

	@SubscribeEvent
	private static void onRegisterCommands(RegisterCommandsEvent event) {
		HealCommand.register(event.getDispatcher());
	}

	@SubscribeEvent
	private static void onAddReloadListener(AddReloadListenerEvent event) {
		event.addListener(ModelOutlinerReloadListener.INSTANCE);
	}

	private static final class ModEvents {
		@SubscribeEvent
		private static void onRegister(RegisterEvent event) {
			Registrar.process(ArmisticeDataComponentTypeRegistrar.class, MODID, event);
			Registrar.process(ArmisticeBlockRegistrar.class, MODID, event);
			Registrar.process(ArmisticeEntityTypeRegistrar.class, MODID, event);
			Registrar.process(ArmisticeItemRegistrar.class, MODID, event);
			Registrar.process(ArmisticeCreativeModeTabRegistrar.class, MODID, event);
			Registrar.process(ArmisticeSoundEventRegistrar.class, MODID, event);

			Registrar.process(ChassisRegistrar.class, MODID, event);
			Registrar.process(HullRegistrar.class, MODID, event);
			Registrar.process(OrdnanceRegistrar.class, MODID, event);
			Registrar.process(ArmorRegistrar.class, MODID, event);

			event.register(
				Registries.RECIPE_SERIALIZER,
				registry -> registry.register(Armistice.id("mecha_schematic"), MechaSchematicRecipe.SERIALIZER)
			);

			// region Debug
			if (FMLEnvironment.production) return;

			event.register(
				ArmisticeRegistries.ARMOR_KEY,
				registry -> registry.register(Armistice.id("test_armor"), new ArmorSchematic(1, 1))
			);

			event.register(
				ArmisticeRegistries.CHASSIS_KEY,
				registry -> registry.register(Armistice.id("test_chassis"), new ChassisSchematic(1, 1, 2))
			);

			event.register(
				ArmisticeRegistries.HULL_KEY,
				registry -> registry.register(Armistice.id("test_hull"), new HullSchematic(1, List.of(1, 2, 3), new HeatData(1, 0, 0)))
			);

			event.register(
				ArmisticeRegistries.ORDNANCE_KEY,
				registry -> registry.register(Armistice.id("test_ordnance"), new OrdnanceSchematic(1))
			);

			// endregion
		}

		@SubscribeEvent
		private static void onNewRegistry(NewRegistryEvent event) {
			event.register(ArmisticeRegistries.PARTICLE_SPAWNER);
			event.register(ArmisticeRegistries.ORDNANCE);
			event.register(ArmisticeRegistries.HULL);
			event.register(ArmisticeRegistries.ARMOR);
			event.register(ArmisticeRegistries.CHASSIS);
		}

		@SubscribeEvent
		private static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
			event.registerReloadListener(ModelElementReloadListener.INSTANCE);
		}

		@SubscribeEvent
		private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
			PayloadRegistrar registrar = event.registrar("1");

			registrar.playToClient(
				OutlinerSyncS2CPayload.TYPE,
				OutlinerSyncS2CPayload.STREAM_CODEC,
				OutlinerSyncS2CPayload::receive
			);
		}
	}
}
