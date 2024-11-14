package symbolics.division.armistice.mecha.ordnance;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
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

		cooldownTicks--;
		if (targets().isEmpty()) {
			rotationManager.clearTarget();
			rotationManager.tick();
			return;
		}

		HitResult target = targets().getFirst();
		Vec3 targetPos = target instanceof EntityHitResult entity ? entity.getEntity().position().add(0, entity.getEntity().getBbHeight() / 2, 0) : target.getLocation();

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

		Vec3 desiredDir = targetPos.subtract(absBody).normalize();
		Vec3 idealBarrelDir = desiredDir.scale(barrelLength);
		// go to infinity to make solver try and be as precise as possible
		Vec3 idealBarrelTipPos = absBody.add(idealBarrelDir.scale(100000));

		rotationManager.setTarget(idealBarrelTipPos, absBody);
		rotationManager.tick();

		// you can constrain it by angle, dot product, whatever
		// one problem arises where it solves then tries to calc vector. I'm not sure
		// if this is the correct order to do the check on whether it will be able to fire.
		// it should also check if it would hit itself with the gun (though rotations should normally
		// prevent that, and self-spawned projectiles should phase through us)
		Vec3 currentDirection = rotationManager.currentDirection();
		if (currentDirection.dot(desiredDir) < 0.9 || cooldownTicks > 0) return;

		Vec3 start = absBody.add(currentDirection.scale(barrelLength));
		Vec3 end = absBody.add(currentDirection.scale(barrelLength + maxDistance));

		HitResult blockRay = core.level().clip(new ClipContext(
			start,
			end,
			ClipContext.Block.VISUAL, ClipContext.Fluid.NONE,
			core.entity()
		));

		if (blockRay.getType() != HitResult.Type.MISS)
			end = blockRay.getLocation();

		HitResult ray = ProjectileUtil.getEntityHitResult(
			core.level(),
			core.entity(),
			start,
			end,
			new AABB(start, end),
			entity -> true
		);

		if (!(ray instanceof EntityHitResult hitResult)) return;
		if (target instanceof EntityHitResult entityHitResult && hitResult.getEntity().getId() != entityHitResult.getEntity().getId())
			return;

		// check heat here so we still track rotation
		if (core.overheatingDanger(heatPerShot)) return;

		this.onShoot.accept(
			core,
			new OrdnanceFireInfo(
				absBody.toVector3f(),
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

		consumer.addVertex(poseStack.last(), absBody.add(currentDirection.scale(barrelLength)).toVector3f()).setColor(1, 1f, 1f, 1f);

		consumer.addVertex(poseStack.last(), absBody.add(currentDirection.scale(barrelLength + maxDistance)).toVector3f()).setColor(1, 1f, 1f, 1f);

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

	public double maxDistance() {
		return maxDistance;
	}

	public MechaModelData.MarkerInfo barrelMarker() {
		return barrelMarker;
	}
}
