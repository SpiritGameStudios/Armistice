package symbolics.division.armistice.event;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.debug.command.HealCommand;
import symbolics.division.armistice.mecha.schematic.ArmorSchematic;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;
import symbolics.division.armistice.mecha.schematic.HullSchematic;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.registry.*;
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

	private static final class ModEvents {
		@SubscribeEvent
		private static void onRegister(RegisterEvent event) {
			Registrar.process(ArmisticeDataComponentTypeRegistrar.class, MODID, event);
			Registrar.process(ArmisticeBlockRegistrar.class, MODID, event);
			Registrar.process(ArmisticeEntityTypeRegistrar.class, MODID, event);
			Registrar.process(ArmisticeItemRegistrar.class, MODID, event);
			Registrar.process(ArmisticeCreativeModeTabRegistrar.class, MODID, event);

			// region Debug
			if (FMLEnvironment.production) return;

			event.register(
				ArmisticeRegistries.ARMOR_KEY,
				registry -> registry.register(Armistice.id("test_armor"), new ArmorSchematic(1))
			);

			event.register(
				ArmisticeRegistries.CHASSIS_KEY,
				registry -> registry.register(Armistice.id("test_chassis"), new ChassisSchematic(1, 1, 2))
			);

			event.register(
				ArmisticeRegistries.HULL_KEY,
				registry -> registry.register(Armistice.id("test_hull"), new HullSchematic(1, List.of(1, 2, 3)))
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
	}
}
