package symbolics.division.armistice.client.render.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import symbolics.division.armistice.Armistice;

import java.util.function.Consumer;

public final class MechaHudRenderer {
	private static final RandomSource RANDOM = RandomSource.create();

	/**
	 * Meter: Pos(0, 0), Size(3, 61)
	 * Arrow: Pos(8, 0), Size(5, 7)
	 */
	private static final ResourceLocation ALTITUDE_SPRITESHEET = Armistice.id("textures/gui/altitude.png");

	/**
	 * N: Pos(0, 0), Size(5, 7)
	 * S: Pos(5, 0), Size(5, 7)
	 * E: Pos(10, 0), Size(5, 7)
	 * W: Pos(15, 0), Size(5, 7)
	 */
	private static final ResourceLocation HEADING_FONT = Armistice.id("textures/gui/heading.png");

	@SubscribeEvent
	private static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(Armistice.id("mecha_hud"), (guiGraphics, deltaTracker) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) return;

			Entity vehicle = player.getVehicle();
//			if (!(vehicle instanceof MechaEntity mecha)) return;
			Entity mecha = player;

			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE,
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
			);

			DrawHelper drawHelper = new DrawHelper(guiGraphics);

			renderAltitude(drawHelper, mecha);
			renderHeading(drawHelper, mecha);

			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		});
	}

	private static void renderAltitude(DrawHelper drawHelper, Entity mecha) {
		lightbulbColor();

		renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				ALTITUDE_SPRITESHEET,
				(int) pos.x, (int) pos.y,
				6, 122,
				0, 0,
				3, 61,
				16, 64
			),
			new Vec2(
				drawHelper.guiGraphics().guiWidth() - 14,
				8
			)
		);

		renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				ALTITUDE_SPRITESHEET,
				(int) pos.x, (int) pos.y,
				10, 14,
				8, 0,
				5, 7,
				16, 64
			),
			new Vec2(
				drawHelper.guiGraphics().guiWidth() - 26,
				6 + Mth.map(
					(int) mecha.getY(),
					mecha.level().getMinBuildHeight(),
					mecha.level().getMaxBuildHeight(),
					122,
					0
				)
			)
		);

		resetColor();
	}

	private static void renderHeading(DrawHelper drawHelper, Entity mecha) {
		lightbulbColor();

		int left = drawHelper.guiGraphics().guiWidth() / 3;
		int right = (drawHelper.guiGraphics().guiWidth() / 3) * 2;

		renderFlicker(
			pos -> drawHelper.hLine(
				left - 2 + pos.x,
				right + 2 + pos.x,
				pos.y,
				2
			),
			new Vec2(0, 9)
		);

		int degPerPixel = (drawHelper.guiGraphics().guiWidth() / Minecraft.getInstance().options.fov().get());
		int offset = (drawHelper.guiGraphics().guiWidth() / 2) - Mth.floor(Mth.wrapDegrees(mecha.getYRot() + 180.0F) * degPerPixel);

		for (int i = -360; i < 360; i++) {
			int x = (i * degPerPixel) + offset;
			if (x < left) continue;
			if (x > right) break;

			if (i % 90 == 0) {
				int finalI = i;
				renderFlicker(
					pos -> drawHelper.guiGraphics().blit(
						HEADING_FONT,
						(int) pos.x, (int) pos.y,
						5, 7,
						5 * (finalI / 90F), 0,
						5, 7,
						20, 7
					),
					new Vec2(x - 2, 14)
				);
			}

			renderFlicker(
				pos -> drawHelper.vLine(
					pos.x,
					pos.y,
					6 + pos.y,
					1
				),
				new Vec2(x, 3)
			);
		}

		resetColor();
	}

	// Yttr FlickeryRenderer was heavily referenced for this
	private static void renderFlicker(Consumer<Vec2> render, Vec2 pos) {
		float[] currentColor = RenderSystem.getShaderColor();

		float alpha = RANDOM.nextInt(15) == 0 ? Math.min(RANDOM.nextFloat(), 0.5F) : 1.0F;
		alpha = (0.3F + (alpha * 0.7F)) * currentColor[3];

		RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], alpha * 0.05F);

		for (float i = -0.8F; i < 0.8F; i += 0.2F) {
			for (float j = -0.8F; j < 0.8F; j += 0.2F) {
				render.accept(pos.add(new Vec2(i, j)));
			}
		}

		RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], alpha);
		render.accept(pos);

		RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], 1);
	}

	private static void lightbulbColor() {
		RenderSystem.setShaderColor(0.5f, 0.2125f, 0.1625f, 1);
	}

	private static void resetColor() {
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
