package symbolics.division.armistice.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.MechaEntity;

public final class MechaHudRenderer {
	@SubscribeEvent
	private static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(Armistice.id("mecha_hud"), (guiGraphics, deltaTracker) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) return;

			Entity vehicle = player.getVehicle();
			if (!(vehicle instanceof MechaEntity mecha)) return;

			MechaCore core = mecha.core();

			double verticalSpeed = mecha.getDeltaMovement().y;
			guiGraphics.drawString(
				Minecraft.getInstance().font,
				String.valueOf(verticalSpeed),
				0,
				0,
				0x17BC0F
			);
		});
	}
}
