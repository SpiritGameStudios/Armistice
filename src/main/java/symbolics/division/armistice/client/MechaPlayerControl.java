package symbolics.division.armistice.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.network.MechaMovementRequestC2SPayload;
import symbolics.division.armistice.registry.ArmisticeSoundEventRegistrar;
import symbolics.division.armistice.util.AudioUtil;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class MechaPlayerControl {

	@SubscribeEvent
	public static void handleMouseInput(InputEvent.MouseButton.Pre event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && !Minecraft.getInstance().isPaused() && player.getVehicle() instanceof MechaEntity mecha) {
			switch (event.getButton()) {
				case 0:
					onLeftClick(player, mecha, event.getAction(), event.getModifiers());
					event.setCanceled(true);
					break;
				case 1:
					onRightClick(player, mecha, event.getAction(), event.getModifiers());
					event.setCanceled(true);
					break;
			}
		}
	}

	// action: 1 mouse down, 0 mouse up
	// modifiers: 0 none, 2 ctrl, 1 probably shift

	private static void onLeftClick(LocalPlayer player, MechaEntity mecha, int action, int modifiers) {

	}

	private static void onRightClick(LocalPlayer player, MechaEntity mecha, int action, int modifiers) {
		if (action == 1) {
			HitResult raycast = Minecraft.getInstance().getCameraEntity().pick(200, 0, false);
			if (raycast.getType() != HitResult.Type.MISS) {
				player.connection.send(new MechaMovementRequestC2SPayload(raycast.getLocation().toVector3f()));
				mecha.core().setPathingTarget(raycast.getLocation().toVector3f());
				System.out.println("sound");
				player.playSound(ArmisticeSoundEventRegistrar.ENTITY$MECHA$ALERT, 0.3f, AudioUtil.randomizedPitch(player.getRandom(), 1, 0.4f));
			}
		}
	}

}
