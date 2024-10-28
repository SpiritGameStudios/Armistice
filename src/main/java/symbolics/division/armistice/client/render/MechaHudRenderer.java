package symbolics.division.armistice.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.MechaEntity;

import java.util.function.Consumer;

public final class MechaHudRenderer {
	private static final RandomSource RANDOM = RandomSource.create();

	@SubscribeEvent
	private static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(Armistice.id("mecha_hud"), (guiGraphics, deltaTracker) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) return;

			Entity vehicle = player.getVehicle();
			if (!(vehicle instanceof MechaEntity mecha)) return;

			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE,
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
			);

			Font font = Minecraft.getInstance().font;
			MechaCore core = mecha.core();

			renderFlicker(
				pos -> {
					guiGraphics.blit(
						Armistice.id("textures/gui/altitude.png"),
						(int) pos.x,
						(int) pos.y,
						0,
						0,
						16,
						128,
						16,
						128
					);
				},
				new Vec2(
					guiGraphics.guiWidth() - 24,
					8
				),
				0.5f,
				0.2125f,
				0.1625f
			);

			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		});
	}

	// Yttr FlickeryRenderer was heavily referenced for this
	private static void renderFlicker(Consumer<Vec2> render, Vec2 pos, float r, float g, float b) {
		float alpha = RANDOM.nextInt(15) == 0 ? Math.min(RANDOM.nextFloat(), 0.5F) : 1.0F;
		alpha = (0.3F + (alpha * 0.7F));

		RenderSystem.setShaderColor(r, g, b, alpha * 0.05F);

		for (float i = -0.8F; i < 0.8F; i += 0.2F) {
			for (float j = -0.8F; j < 0.8F; j += 0.2F) {
				render.accept(pos.add(new Vec2(i, j)));
			}
		}

		RenderSystem.setShaderColor(r, g, b, alpha);
		render.accept(pos);

		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
