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
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.debug.command.HealCommand;
import symbolics.division.armistice.mecha.MechaSkin;
import symbolics.division.armistice.mecha.schematic.ArmorSchematic;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;
import symbolics.division.armistice.mecha.schematic.HeatData;
import symbolics.division.armistice.mecha.schematic.HullSchematic;
import symbolics.division.armistice.model.ModelElementReloadListener;
import symbolics.division.armistice.model.ModelOutlinerReloadListener;
import symbolics.division.armistice.network.ExtendedParticlePacket;
import symbolics.division.armistice.network.MechaMovementRequestC2SPayload;
import symbolics.division.armistice.network.MechaTargetRequestC2SPayload;
import symbolics.division.armistice.network.OutlinerSyncS2CPayload;
import symbolics.division.armistice.recipe.MechaSchematicRecipe;
import symbolics.division.armistice.recipe.MechaSkinRecipe;
import symbolics.division.armistice.registry.*;
import symbolics.division.armistice.util.registrar.ArmorRegistrar;
import symbolics.division.armistice.util.registrar.ChassisRegistrar;
import symbolics.division.armistice.util.registrar.HullRegistrar;
import symbolics.division.armistice.util.registrar.Registrar;

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
			Registrar.process(ArmisticeEntityDataSerializerRegistrar.class, MODID, event);
			Registrar.process(ArmisticeEntityTypeRegistrar.class, MODID, event);
			Registrar.process(ArmisticeItemRegistrar.class, MODID, event);
			Registrar.process(ArmisticeCreativeModeTabRegistrar.class, MODID, event);
			Registrar.process(ArmisticeSoundEventRegistrar.class, MODID, event);

			Registrar.process(ChassisRegistrar.class, MODID, event);
			Registrar.process(HullRegistrar.class, MODID, event);
			Registrar.process(ArmisticeOrdnanceRegistrar.class, MODID, event);
			Registrar.process(ArmorRegistrar.class, MODID, event);

			event.register(
				Registries.RECIPE_SERIALIZER,
				registry -> {
					registry.register(Armistice.id("mecha_schematic"), MechaSchematicRecipe.SERIALIZER);
					registry.register(Armistice.id("skin"), MechaSkinRecipe.SERIALIZER);
				}
			);

			// region Debug
			if (FMLEnvironment.production) return;

			event.register(
				ArmisticeRegistries.ARMOR_KEY,
				registry -> registry.register(Armistice.id("test_armor"), new ArmorSchematic(1, 1))
			);

			event.register(
				ArmisticeRegistries.CHASSIS_KEY,
				registry -> registry.register(Armistice.id("test_chassis"), new ChassisSchematic(1, 1, 2, 10))
			);

			event.register(
				ArmisticeRegistries.HULL_KEY,
				registry -> registry.register(Armistice.id("test_hull"), new HullSchematic(1, List.of(1, 2, 3), new HeatData(100, 10, 1)))
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
		private static void onRegisterDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
			event.dataPackRegistry(
				ArmisticeRegistries.SKIN_KEY,
				MechaSkin.CODEC,
				MechaSkin.CODEC
			);
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

			registrar.playToClient(
				ExtendedParticlePacket.TYPE,
				ExtendedParticlePacket.STREAM_CODEC,
				ExtendedParticlePacket::receive
			);

			registrar.playToServer(
				MechaMovementRequestC2SPayload.TYPE,
				MechaMovementRequestC2SPayload.STREAM_CODEC,
				MechaMovementRequestC2SPayload::receive
			);

			registrar.playToServer(
				MechaTargetRequestC2SPayload.TYPE,
				MechaTargetRequestC2SPayload.STREAM_CODEC,
				MechaTargetRequestC2SPayload::receive
			);
		}
	}
}
