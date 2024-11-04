package symbolics.division.armistice.cursed;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class Ambiguator {
	private static Supplier<RegistryAccess.Frozen> akasha = () -> ServerLifecycleHooks.getCurrentServer().registryAccess();

	@OnlyIn(Dist.CLIENT)
	private static final class TeeHee {
		static {
			akasha = () -> Minecraft.getInstance().getSingleplayerServer() != null ?
				Minecraft.getInstance().getSingleplayerServer().registryAccess() :
				Minecraft.getInstance().getConnection().registryAccess();
		}
	}

	public static RegistryAccess.Frozen akashicRegistryAccess() {
		return akasha.get();
	}
}
