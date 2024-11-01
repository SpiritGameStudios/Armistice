package symbolics.division.armistice.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import symbolics.division.armistice.client.ArmisticeClient;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@WrapMethod(method = {
		"renderCrosshair",
		"renderHotbar",
		"maybeRenderExperienceBar",
		"maybeRenderPlayerHealth"
	})
	private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!ArmisticeClient.renderVanillaHUD) return;
		original.call(guiGraphics, deltaTracker);
	}

	@WrapMethod(method = {"renderHealthLevel", "renderFoodLevel"})
	private void render(GuiGraphics guiGraphics, Operation<Void> original) {
		if (!ArmisticeClient.renderVanillaHUD) return;
		original.call(guiGraphics);
	}
}
