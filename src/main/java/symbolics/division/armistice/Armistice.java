package symbolics.division.armistice;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import symbolics.division.armistice.particle.ParticleSpawnerRegistrar;
import symbolics.division.armistice.util.registry.Registrar;

@Mod(Armistice.MODID)
public class Armistice {
    public static final String MODID = "armistice";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Armistice(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(Armistice::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        Registrar.process(ArmisticeBlockRegistrar.class, MODID, event);
        Registrar.process(ParticleSpawnerRegistrar.class, MODID, event);
    }
}
