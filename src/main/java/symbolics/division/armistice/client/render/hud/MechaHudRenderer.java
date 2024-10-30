package symbolics.division.armistice.client.render.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.joml.Vector4f;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;

public final class MechaHudRenderer {
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

	private static final ResourceLocation HEAT = Armistice.id("textures/gui/heat.png");

	@SubscribeEvent
	private static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(Armistice.id("mecha_hud"), (guiGraphics, deltaTracker) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) return;

			Entity vehicle = player.getVehicle();
			if (!(vehicle instanceof MechaEntity mecha)) return;
			if (!mecha.core().ready()) return;

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
			renderHeat(drawHelper, mecha);

			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		});
	}

	private static void renderAltitude(DrawHelper drawHelper, Entity mecha) {
		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				ALTITUDE_SPRITESHEET,
				(int) pos.x, (int) pos.y,
				8, 118,
				0, 0,
				3, 61,
				16, 64
			),
			new Vec2(
				drawHelper.guiGraphics().guiWidth() - 14,
				8
			),
			lightbulbColor()
		);

		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				ALTITUDE_SPRITESHEET,
				(int) pos.x, (int) pos.y,
				8, 5,
				16, 0,
				8, 5,
				32, 128
			),
			new Vec2(
				drawHelper.guiGraphics().guiWidth() - 14 - 8 - 2,
				3.5F + Mth.map(
					(int) mecha.getY(),
					mecha.level().getMinBuildHeight(),
					mecha.level().getMaxBuildHeight(),
					118,
					0
				)
			),
			lightbulbColor()
		);

		resetColor();
	}

	private static void renderHeat(DrawHelper drawHelper, MechaEntity mecha) {
		float heatRadians = Mth.wrapDegrees(Mth.map(
			mecha.core().getHeat(),
			0,
			mecha.core().getMaxHeat(),
			45,
			315
		) + 90) * Mth.DEG_TO_RAD;

		drawHelper.renderFlicker(
			pos -> drawHelper.aLine(
				pos,
				new Vec2(
					20 * Mth.cos(heatRadians),
					20 * Mth.sin(heatRadians)
				).add(pos),
				2.5F
			),
			new Vec2(
				32 + 8,
				drawHelper.guiGraphics().guiHeight() - 32 + 8
			),
			lightbulbColor()
		);

		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				HEAT,
				(int) pos.x, (int) pos.y,
				64, 64,
				0, 0,
				64, 64,
				64, 64
			),
			new Vec2(
				8,
				drawHelper.guiGraphics().guiHeight() - 64 + 8
			),
			lightbulbColor()
		);

		resetColor();
	}

	private static void renderHeading(DrawHelper drawHelper, MechaEntity mecha) {
		int left = drawHelper.guiGraphics().guiWidth() / 3;
		int right = (drawHelper.guiGraphics().guiWidth() / 3) * 2;

		drawHelper.renderFlicker(
			pos -> drawHelper.hLine(
				left - 2 + pos.x,
				right + 2 + pos.x,
				pos.y,
				2
			),
			new Vec2(0, 9),
			lightbulbColor()
		);

		int degPerPixel = (drawHelper.guiGraphics().guiWidth() / Minecraft.getInstance().options.fov().get());

		Vec3 dir = mecha.core().direction().normalize();
		double yaw = Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG;

		drawHelper.renderCenteredNumber(
			Mth.wrapDegrees(Mth.floor(yaw)),
			drawHelper.guiGraphics().guiWidth() / 2F,
			9 + 5,
			1,
			lightbulbColor()
		);

		int offset = (drawHelper.guiGraphics().guiWidth() / 2) - Mth.floor(Mth.wrapDegrees(yaw + 180) * degPerPixel);

		for (int i = -360; i < 360; i++) {
			int x = (i * degPerPixel) + offset;
			if (x < left) continue;
			if (x > right) break;

			if (i % 90 == 0) {
				int finalI = i;
				drawHelper.renderFlicker(
					pos -> drawHelper.guiGraphics().blit(
						HEADING_FONT,
						(int) pos.x, (int) pos.y,
						5, 7,
						5 * (finalI / 90F), 0,
						5, 7,
						20, 7
					),
					new Vec2(x - 2.5F, 9 + 5 + 7 + 2),
					lightbulbColor()
				);
			}

			drawHelper.renderFlicker(
				pos -> drawHelper.vLine(
					pos.x,
					pos.y,
					6 + pos.y,
					1
				),
				new Vec2(x, 3),
				lightbulbColor()
			);
		}

		resetColor();
	}

	public static Vector4f lightbulbColor() {
		return new Vector4f(0.5f, 0.2125f, 0.1625f, 1);
	}

	private static void resetColor() {
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
