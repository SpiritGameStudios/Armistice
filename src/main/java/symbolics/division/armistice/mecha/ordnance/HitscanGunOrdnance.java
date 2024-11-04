package symbolics.division.armistice.mecha.ordnance;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import symbolics.division.armistice.debug.ArmisticeDebugValues;
import symbolics.division.armistice.math.OrdnanceFireInfo;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.model.MechaModelData;

import java.util.function.BiConsumer;

public class HitscanGunOrdnance extends OrdnancePart {
	protected final int cooldown;
	protected final double maxDistance;
	protected final double damage;
	protected final BiConsumer<MechaCore, OrdnanceFireInfo> onShoot;

	protected int cooldownTicks;
	protected MechaModelData.MarkerInfo barrelMarker;
	protected final int heatPerShot;
	protected int heatThisTick = 0;

	public HitscanGunOrdnance(int heatPerShot, int cooldown, double maxDistance, double damage, BiConsumer<MechaCore, OrdnanceFireInfo> onShoot) {
		super(1);

		this.heatPerShot = heatPerShot;
		this.cooldown = cooldown;
		this.maxDistance = maxDistance;
		this.damage = damage;
		this.onShoot = onShoot;
	}

	@Override
	protected boolean isValidTarget(HitResult hitResult) {
		return !(hitResult.distanceTo(core.entity()) > maxDistance * maxDistance);
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);

		barrelMarker = core.model().ordnanceInfo(this, core).markers().get(1);
	}

	@Override
	public void serverTick() {
		super.serverTick();

		if (!ArmisticeDebugValues.simpleGun) return;

		// region temp: debug targeting
		Player player = core.level().getNearestPlayer(core.entity(), 100);
		if (player != null) {
			HitResult result = new EntityHitResult(player);
			startTargeting(result);
		}
		// endregion

		cooldownTicks--;
		if (targets().isEmpty() || !(targets().getFirst() instanceof EntityHitResult target))
			return;

		// temp: inappropriate use of rotationmanager. also, try to apply logic to ordnance in general.
		MechaModelData.OrdnanceInfo info = core.model().ordnanceInfo(this, core);

		// NOT A SAFE ASSUMPTION. the body may not always be centered on origin (though it should)
		var barrelLength = barrelMarker.origin().with(Direction.Axis.Y, 0).length();
		var baseRotation = info.mountPoint().rotationInfo().bbRotation().scale(Mth.DEG_TO_RAD);

		Vec3 evilBodyOffsetPleaseUpdateModelData = info.body().origin();

		Vec3 absBody = new Vec3(rel2Abs(
			new Quaternionf().rotateZYX(
				(float) baseRotation.z, (float) baseRotation.y, (float) baseRotation.x
			).transform(evilBodyOffsetPleaseUpdateModelData.toVector3f())
		));

		Vec3 idealBarrelDir = target.getEntity().position().subtract(absBody).normalize().scale(barrelLength);
		Vec3 idealBarrelTipPos = absBody.add(idealBarrelDir);

		double x = target.getEntity().getX() - idealBarrelTipPos.x;
		double z = target.getEntity().getZ() - idealBarrelTipPos.z;
		double y = target.getEntity().getY(1.0 / 3.0) - idealBarrelTipPos.y;

		// temp: rotation manager example
		Vec3 desiredDir = new Vec3(x, y, z).normalize();

		rotationManager.setTarget(idealBarrelTipPos.add(idealBarrelDir));
		rotationManager.tick();

		// you can constrain it by angle, dot product, whatever
		// one problem arises where it solves then tries to calc vector. I'm not sure
		// if this is the correct order to do the check on whether it will be able to fire.
		// it should also check if it would hit itself with the gun (though rotations should normally
		// prevent that, and self-spawned projectiles should phase through us)
		Vec3 currentDirection = rotationManager.currentDirection();
		if (currentDirection.dot(desiredDir) < 0.95 || cooldownTicks > 0) return;

		Vec3 start = absBody.add(currentDirection.scale(barrelLength));
		Vec3 end = absBody.add(currentDirection.scale(barrelLength + maxDistance));

		HitResult ray = ProjectileUtil.getEntityHitResult(
			core.level(),
			core.entity(),
			start,
			end,
			new AABB(start, end),
			entity -> true
		);

		if (!(ray instanceof EntityHitResult hitResult)) return;

		this.onShoot.accept(
			core,
			new OrdnanceFireInfo(
				idealBarrelTipPos.toVector3f(),
				idealBarrelDir.toVector3f(),
				new Quaternionf(),
				target
			)
		);
		hitResult.getEntity().hurt(core.entity().damageSources().magic(), (float) damage);

		cooldownTicks = cooldown;
		heatThisTick = heatPerShot;
	}

	@Override
	public void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {
		super.renderDebug(bufferSource, poseStack);

		MechaModelData.OrdnanceInfo info = core.model().ordnanceInfo(this, core);

		var barrelLength = barrelMarker.origin().with(Direction.Axis.Y, 0).length();
		var baseRotation = info.mountPoint().rotationInfo().bbRotation().scale(Mth.DEG_TO_RAD);

		Vec3 evilBodyOffsetPleaseUpdateModelData = info.body().origin();

		Vec3 absBody = new Vec3(rel2Abs(
			new Quaternionf().rotateZYX(
				(float) baseRotation.z, (float) baseRotation.y, (float) baseRotation.x
			).transform(evilBodyOffsetPleaseUpdateModelData.toVector3f())
		));

		Vec3 currentDirection = rotationManager.currentDirection();

		VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugLineStrip(50));
		consumer.addVertex(poseStack.last(), absBody.toVector3f()).setColor(0, 1f, 0, 1f);
		consumer.addVertex(poseStack.last(), absBody.add(currentDirection.scale(barrelLength)).toVector3f()).setColor(0, 1f, 0, 1f);

		consumer.addVertex(poseStack.last(), absBody.add(currentDirection.scale(barrelLength)).toVector3f()).setColor(0, 0f, 1f, 1f);

		consumer.addVertex(poseStack.last(), absBody.add(currentDirection.scale(barrelLength + maxDistance)).toVector3f()).setColor(0, 0f, 1f, 1f);

//		RenderSystem.enableBlend();
//		RenderSystem.blendFuncSeparate(
//			GlStateManager.SourceFactor.SRC_ALPHA,
//			GlStateManager.DestFactor.ONE,
//			GlStateManager.SourceFactor.SRC_ALPHA,
//			GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
//		);
//
//		DrawHelper.renderHologramFlicker(
//			pos -> {
//				BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
//					VertexFormat.Mode.DEBUG_LINE_STRIP,
//					DefaultVertexFormat.POSITION_COLOR
//				);
//
//				bufferBuilder.addVertex(poseStack.last(), absBody.add(pos).toVector3f())
//					.setColor(1, 1, 1, 1.0f);
//
//				bufferBuilder.addVertex(poseStack.last(), absBody.add(currentDirection.scale(barrelLength + maxDistance)).add(pos).toVector3f())
//					.setColor(1, 1, 1, 1.0f);
//
//				BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
//			},
//			Vec3.ZERO,
//			MechaHudRenderer.lightbulbColor()
//		);
//
//		RenderSystem.disableBlend();
//		RenderSystem.defaultBlendFunc();
//		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	@Override
	public int heat() {
		int out = heatThisTick;
		heatThisTick = 0;
		return out;
	}
}
