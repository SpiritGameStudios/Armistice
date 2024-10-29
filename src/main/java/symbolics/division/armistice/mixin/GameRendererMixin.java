package symbolics.division.armistice.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.armistice.client.render.hud.MechaOverlayRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "resize", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;resize(II)V"))
	private void resize(int width, int height, CallbackInfo ci) {
		MechaOverlayRenderer.resize(width, height);
	}

	@Inject(method = "close", at = @At("RETURN"))
	private void close(CallbackInfo ci) {
		MechaOverlayRenderer.close();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = At.Shift.AFTER))
	private void render(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
		if (!MechaOverlayRenderer.shouldProcessMechaOverlay()) return;
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.resetTextureMatrix();
		MechaOverlayRenderer.processMechaOverlay(deltaTracker.getGameTimeDeltaTicks());
	}
}
