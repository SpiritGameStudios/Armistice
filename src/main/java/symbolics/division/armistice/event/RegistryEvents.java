package symbolics.division.armistice.event;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import symbolics.division.armistice.debug.command.HealCommand;
import symbolics.division.armistice.particle.ParticleSpawner;
import symbolics.division.armistice.registry.ArmisticeBlockRegistrar;
import symbolics.division.armistice.util.registrar.Registrar;

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
            Registrar.process(ArmisticeBlockRegistrar.class, MODID, event);
        }

        @SubscribeEvent
        private static void onNewRegistry(NewRegistryEvent event) {
            event.register(ParticleSpawner.REGISTRY);
        }
    }
}
