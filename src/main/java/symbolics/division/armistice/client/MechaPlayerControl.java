package symbolics.division.armistice.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.network.MechaMovementRequestC2SPayload;
import symbolics.division.armistice.network.MechaTargetRequestC2SPayload;
import symbolics.division.armistice.registry.ArmisticeSoundEventRegistrar;
import symbolics.division.armistice.util.AudioUtil;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MechaPlayerControl {

	@SubscribeEvent
	public static void handleMouseInput(InputEvent.MouseButton.Pre event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().screen == null && player.getVehicle() instanceof MechaEntity mecha) {
			switch (event.getButton()) {
				case 0 -> {
					onLeftClick(player, mecha, event.getAction(), event.getModifiers());
					event.setCanceled(true);
				}
				case 1 -> {
					onRightClick(player, mecha, event.getAction(), event.getModifiers());
					event.setCanceled(true);
				}
			}
		}
	}

	// action: 1 mouse down, 0 mouse up
	// modifiers: 0 none, 2 ctrl, 1 probably shift

	private static void onLeftClick(LocalPlayer player, MechaEntity mecha, int action, int modifiers) {
		if (action != 1) return;

		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		if (cameraEntity == null) return;

		Vec3 start = cameraEntity.getEyePosition(0);
		Vec3 end = start.add(cameraEntity.getViewVector(0).scale(500));

		HitResult raycast = ProjectileUtil.getEntityHitResult(
			mecha.core().level(),
			mecha.core().entity(),
			start,
			end,
			new AABB(start, end),
			entity -> true
		);

		if (raycast == null || raycast.getType() == HitResult.Type.MISS) {
			raycast = mecha.level().clip(new ClipContext(
				start,
				end,
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE,
				CollisionContext.of(mecha.core().entity())
			));
		}

		if (raycast.getType() == HitResult.Type.MISS) return;

		// temp: choose ordnance
		player.connection.send(new MechaTargetRequestC2SPayload(raycast, 0));
		player.playSound(ArmisticeSoundEventRegistrar.ENTITY$MECHA$ALERT, 0.3f, AudioUtil.randomizedPitch(player.getRandom(), 1, 0.4f));

	}

	private static void onRightClick(LocalPlayer player, MechaEntity mecha, int action, int modifiers) {
		if (action != 1) return;

		HitResult raycast = Minecraft.getInstance().getCameraEntity().pick(200, 0, false);
		if (raycast.getType() == HitResult.Type.MISS) return;

		player.connection.send(new MechaMovementRequestC2SPayload(raycast.getLocation().toVector3f()));
		mecha.core().setPathingTarget(raycast.getLocation().toVector3f());
		player.playSound(ArmisticeSoundEventRegistrar.ENTITY$MECHA$ALERT, 0.3f, AudioUtil.randomizedPitch(player.getRandom(), 1, 0.4f));
	}

}
