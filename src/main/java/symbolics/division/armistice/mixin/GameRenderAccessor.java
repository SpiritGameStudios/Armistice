package symbolics.division.armistice.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRenderAccessor {
	@Invoker
	double invokeGetFov(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting);
}
