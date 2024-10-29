package symbolics.division.armistice.client.render.hud;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import symbolics.division.armistice.Armistice;

import java.io.IOException;

public final class MechaOverlayRenderer {
	private static final ResourceLocation MECHA_OVERLAY_LOCATION = Armistice.id("shaders/post/mecha_overlay.json");
	private static PostChain mechaOverlay;

	@SubscribeEvent
	private static void registerShaders(RegisterShadersEvent event) {
		if (mechaOverlay != null) mechaOverlay.close();
		Minecraft client = Minecraft.getInstance();

		try {
			mechaOverlay = new PostChain(client.getTextureManager(), event.getResourceProvider(), client.getMainRenderTarget(), MECHA_OVERLAY_LOCATION);
			mechaOverlay.resize(client.getWindow().getWidth(), client.getWindow().getHeight());
		} catch (IOException ioexception) {
			Armistice.LOGGER.warn("Failed to load shader: {}", MECHA_OVERLAY_LOCATION, ioexception);
		} catch (JsonSyntaxException jsonsyntaxexception) {
			Armistice.LOGGER.warn("Failed to parse shader: {}", MECHA_OVERLAY_LOCATION, jsonsyntaxexception);
		}
	}

	public static void resize(int width, int height) {
		if (mechaOverlay != null) mechaOverlay.resize(width, height);
	}

	public static void close() {
		if (mechaOverlay != null) mechaOverlay.close();
	}

	public static boolean shouldProcessMechaOverlay() {
		// TEMP: add actual conditional logic

		return true;
	}

	public static void processMechaOverlay(float partialTick) {
		if (mechaOverlay == null) return;

		// TODO: Set uniforms here

		mechaOverlay.process(partialTick);
	}
}
