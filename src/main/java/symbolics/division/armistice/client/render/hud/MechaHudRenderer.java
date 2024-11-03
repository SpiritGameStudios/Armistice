package symbolics.division.armistice.client.render.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.ArmisticeClient;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mixin.GameRenderAccessor;

import java.util.ArrayList;
import java.util.List;

public final class MechaHudRenderer {
	/**
	 * Meter: Pos(0, 0), Size(3, 61)
	 * Arrow: Pos(8, 0), Size(5, 7)
	 */
	private static final ResourceLocation ALTITUDE_SPRITESHEET = Armistice.id("textures/hud/altitude.png");

	/**
	 * N: Pos(0, 0), Size(5, 7)
	 * S: Pos(5, 0), Size(5, 7)
	 * E: Pos(10, 0), Size(5, 7)
	 * W: Pos(15, 0), Size(5, 7)
	 */
	private static final ResourceLocation HEADING_FONT = Armistice.id("textures/hud/heading.png");

	private static final ResourceLocation HEAT = Armistice.id("textures/hud/heat.png");

	private static final ResourceLocation SPEEDOMETER = Armistice.id("textures/hud/speedometer.png");

	private static final ResourceLocation CROSSHAIR = Armistice.id("textures/hud/crosshair.png");
	private static final ResourceLocation PATHTARGET = Armistice.id("textures/hud/pathtarget.png");

	private static final ResourceLocation CAM_OVERLAY = Armistice.id("textures/hud/cam_overlay.png");

	@SubscribeEvent
	private static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(Armistice.id("mecha_hud"), (guiGraphics, deltaTracker) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) return;

			Entity vehicle = player.getVehicle();
			if (!(vehicle instanceof MechaEntity mecha) || !mecha.core().ready()) {
				ArmisticeClient.renderVanillaHUD = true;
				return;
			}

			ArmisticeClient.renderVanillaHUD = false;

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
			renderSpeedometer(drawHelper, mecha);
			renderCrosshair(drawHelper, mecha);
			renderHeat(drawHelper, mecha);
			renderElevation(drawHelper, mecha);
			renderPathtarget(drawHelper, mecha);
			renderOverlay(drawHelper, mecha);

			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		});
	}

	private static void renderAltitude(DrawHelper drawHelper, Entity mecha) {
		float windowW = drawHelper.guiGraphics().guiWidth();
		float windowH = drawHelper.guiGraphics().guiHeight();
		final float elementHeight = 118;
		final float rightOffset = windowW - windowW / 4;
		final float topOffset = windowH / 2 - elementHeight / 2;
		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				ALTITUDE_SPRITESHEET,
				(int) pos.x, (int) pos.y,
				8, (int) elementHeight,
				0, 0,
				3, 61,
				16, 64
			),
			new Vec2(
				rightOffset,
				topOffset
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
				rightOffset - 8 - 2,
				topOffset + Mth.map(
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

	private static void renderCrosshair(DrawHelper drawHelper, Entity mecha) {
		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				CROSSHAIR,
				(int) pos.x, (int) pos.y,
				7, 7,
				0, 0,
				15, 15,
				15, 15
			),
			new Vec2(
				(drawHelper.guiGraphics().guiWidth() - 15) / 2F,
				(drawHelper.guiGraphics().guiHeight() - 15) / 2F
			),
			lightbulbColor()
		);

		resetColor();
	}

	private static void renderOverlay(DrawHelper drawHelper, Entity mecha) {
		int textureW = 600;
		int textureH = 600;
		int gw = drawHelper.guiGraphics().guiWidth();
		int gh = drawHelper.guiGraphics().guiHeight();
		float cx = gw / 2;
		float cy = gh / 2;

		// vertical should map to inner plus some change
		int innerDiameter = 420;
		int uOffset = (textureW - innerDiameter) / 2;
		int vOffset = (textureH - innerDiameter) / 2;

		float maxsize = Math.min(gh, gw);

//		drawHelper.renderFlicker(
//			pos -> drawHelper.guiGraphics().blit(
//				CAM_OVERLAY,
//				, 0,
//				gw, gh,
//				textureH - innerDiameter * 2, 0,
//				textureW, textureH,
//				textureW, textureW
//			),
//			new Vec2(
//				cx,
//				cy
//			),
//			lightbulbColor()
//		);

		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				CAM_OVERLAY,
				(int) pos.x, (int) pos.y + 20,
				100, 100,
				0, 0,
				100, 100,
				100, 100
			),
			new Vec2(
				(drawHelper.guiGraphics().guiWidth() - 15) / 2F,
				(drawHelper.guiGraphics().guiHeight() - 15) / 2F
			),
			lightbulbColor()
		);


//		drawHelper.renderFlicker(
//			pos -> drawHelper.guiGraphics().blit(
//				CAM_OVERLAY,
//				(int) pos.x, (int) pos.y,
//				100, 100,
//				0, 0,
//				textureW, textureH,
//				textureW, textureW
//			),
//			new Vec2(
//				cx,
//				cy
//			),
//			lightbulbColor()
//		);

		resetColor();
	}

	private static void renderHeat(DrawHelper drawHelper, MechaEntity mecha) {
		final float windowW = drawHelper.guiGraphics().guiWidth();
		final float windowH = drawHelper.guiGraphics().guiWidth();
		final float leftOffset = windowW / 4.3f;
		final float topOffset = windowH / 3.6f;
		float heat = (float) mecha.core().getHeat() / (float) mecha.core().getMaxHeat();

		if (heat >= 0.99F) drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				HEAT,
				(int) pos.x, (int) pos.y,
				22, 86,
				22, 0,
				11, 43,
				33, 43
			),
			new Vec2(
				leftOffset - 8,
				topOffset - 86
			),
			lightbulbColor()
		);

		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				HEAT,
				(int) pos.x, (int) pos.y,
				22, Mth.floor(86 * heat),
				11, 0,
				11, Mth.floor(43 * heat),
				33, 43
			),
			new Vec2(
				leftOffset - 8,
				topOffset - Mth.floor(86 * heat)
			),
			lightbulbColor().sub(0, 0, 0, 0.5F)
		);

		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				HEAT,
				(int) pos.x, (int) pos.y,
				22, 86,
				0, 0,
				11, 43,
				33, 43
			),
			new Vec2(
				leftOffset - 8,
				topOffset - 86
			),
			lightbulbColor()
		);

		resetColor();
	}

	private static void renderElevation(DrawHelper drawHelper, MechaEntity mecha) {
		float windowW = drawHelper.guiGraphics().guiWidth();
		float windowH = drawHelper.guiGraphics().guiHeight();
		float left = windowW / 3;
		float right = windowW - left;
		float top = drawHelper.guiGraphics().guiHeight() / 4F;
		float bottom = windowH - top;
		float pitchDeg = Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() / 2;

		float tickSep = 40;
		float tickWidth = 15;
		float bottomTick = windowH + pitchDeg + 90;
		float tickDif = 5;
		float sign = 1;

		for (float i = bottomTick; i > 0; i -= tickSep) {
			if (i > top && i < bottom) {
				float finalI = i;
				float finalSign = sign;

				drawHelper.renderFlicker(
					pos -> drawHelper.hLine(
						(left - tickWidth) + (tickDif * finalSign) + pos.x,
						left + pos.x,
						finalI + pos.y,
						2
					),
					Vec2.ZERO,
					lightbulbColor()
				);

				drawHelper.renderFlicker(
					pos ->
						drawHelper.hLine(
							right + pos.x,
							((right + tickWidth) - (tickDif * finalSign)) + pos.x,
							finalI + pos.y,
							2f
						),
					Vec2.ZERO,
					lightbulbColor()
				);
			}
			sign = -sign;
		}

		resetColor();
	}

	private static void renderHeading(DrawHelper drawHelper, MechaEntity mecha) {
		int left = drawHelper.guiGraphics().guiWidth() / 3;
		int right = (drawHelper.guiGraphics().guiWidth() / 3) * 2;
		final float topOffset = 50;

		drawHelper.renderFlicker(
			pos -> drawHelper.hLine(
				left - 2 + pos.x,
				right + 2 + pos.x,
				pos.y,
				2
			),
			new Vec2(0, topOffset + 6),
			lightbulbColor()
		);

		int degPerPixel = (drawHelper.guiGraphics().guiWidth() / Minecraft.getInstance().options.fov().get());

		Vec3 dir = Minecraft.getInstance().player.getLookAngle();
		double yaw = Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG;

		drawHelper.renderCenteredNumber(
			Mth.wrapDegrees(Mth.floor(yaw)),
			drawHelper.guiGraphics().guiWidth() / 2F,
			topOffset + 6 + 5,
			1,
			lightbulbColor()
		);

		int offset = (drawHelper.guiGraphics().guiWidth() / 2) - Mth.floor(Mth.wrapDegrees(yaw + 180) * degPerPixel);

		List<Runnable> letters = new ArrayList<>();

		for (int i = -360; i < 360; i++) {
			int x = (i * degPerPixel) + offset;
			if (x < left) continue;
			if (x > right) break;

			drawHelper.renderFlicker(
				pos -> drawHelper.vLine(
					pos.x,
					pos.y,
					6 + pos.y,
					1
				),
				new Vec2(x, topOffset),
				lightbulbColor()
			);

			if (i % 90 == 0) {
				int finalI = i;

				letters.add(() -> drawHelper.renderFlicker(
					pos -> drawHelper.guiGraphics().blit(
						HEADING_FONT,
						(int) pos.x, (int) pos.y,
						5, 7,
						5 * (finalI / 90F), 0,
						5, 7,
						20, 7
					),
					new Vec2(x - 2.5F, topOffset + 6 + 5 + 9 + 2),
					lightbulbColor()
				));
			}
		}

		letters.forEach(Runnable::run);

		resetColor();
	}

	private static void renderPathtarget(DrawHelper drawHelper, Entity mecha) {
		float gw = drawHelper.guiGraphics().guiWidth();
		float gh = drawHelper.guiGraphics().guiHeight();
		float ar = gw / gh;
		if (mecha instanceof MechaEntity mechaEntity) {
			var tgt = mechaEntity.core().getPathingTarget();
			if (tgt != null) {
				GameRenderer gr = Minecraft.getInstance().gameRenderer;
				Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
				Camera cam = gr.getMainCamera();

				double fov = Math.max(
					Minecraft.getInstance().options.fov().get().intValue(),
					((GameRenderAccessor) gr).invokeGetFov(cam, cam.getPartialTickTime(), false)
				);

				// camera to screen matrix
				Matrix4f mat = gr.getProjectionMatrix(fov);

				// world to camera matrix
				Vec3 camPos = cameraEntity.getEyePosition(cam.getPartialTickTime());
				mat.rotate(cam.rotation().conjugate(new Quaternionf()));
				mat.translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);

				Vector3f p = mat.transformPosition(tgt.toVector3f());

				if (p.z < 0) return; // not in frame

				// perspective division!!!!!!
				p.mul(1 / p.z);
				float screenX = gw / 2 * (p.x + 1);
				float screenY = gh / 2 * (-p.y + 1);

				drawHelper.renderFlicker(
					pos -> drawHelper.guiGraphics().blit(
						PATHTARGET,
						(int) pos.x, (int) pos.y,
						15, 15,
						0, 0,
						15, 15,
						15, 15
					),
					new Vec2(
						screenX - 9,
						screenY - 9
					),
					lightbulbColor()
				);

				drawHelper.renderCenteredNumber(
					(int) camPos.distanceTo(tgt),
					screenX,
					screenY + 10,
					1,
					lightbulbColor()
				);

				resetColor();
			}
		}
	}

	private static void renderSpeedometer(DrawHelper drawHelper, Entity mecha) {
		final float bottomOffset = drawHelper.guiGraphics().guiHeight() - drawHelper.guiGraphics().guiHeight() / 3f;
		final float leftOffset = drawHelper.guiGraphics().guiWidth() / 4.6f;
		double speed = mecha.getDeltaMovement().length();
		double progressDegrees = Mth.wrapDegrees(Mth.map(
			speed,
			0,
			4,
			0,
			180
		) + 225.0f);

		float progressRadians = (float) (progressDegrees * Mth.DEG_TO_RAD);


		drawHelper.renderFlicker(
			pos -> drawHelper.aLine(
				new Vec2(
					10 * Mth.cos(progressRadians),
					10 * Mth.sin(progressRadians)
				).add(pos),
				pos,
				2F
			),
			new Vec2(
				leftOffset + 17f,
				bottomOffset - 17.0f
			),
			lightbulbColor()
		);

		drawHelper.renderFlicker(
			pos -> drawHelper.guiGraphics().blit(
				SPEEDOMETER,
				(int) pos.x, (int) pos.y,
				34, 34,
				0, 0,
				34, 34,
				34, 34
			),
			new Vec2(
				leftOffset,
				bottomOffset - 34
			),
			lightbulbColor()
		);

		resetColor();
	}

	public static Vector4f lightbulbColor() {
		return new Vector4f(0.5f, 0.2125f, 0.1625f, 1);
	}

	private static void resetColor() {
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
