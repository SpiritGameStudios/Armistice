package symbolics.division.armistice.mecha;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.mecha.movement.DirectionState;
import symbolics.division.armistice.mecha.movement.GeometryUtil;
import symbolics.division.armistice.mecha.movement.Leggy;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

import java.util.ArrayList;
import java.util.List;

public class MechaEntity extends Entity {
	protected final MechaCore core;
	protected final List<Leggy> legs = new ArrayList<>();
	public final List<Vec3> stepAreas = new ArrayList<>();

	public final DirectionState direction = new DirectionState(Math.PI);
	public Vec3 followPos = Vec3.ZERO;
	public final float followTolerance = 5;

	protected MechaEntity(EntityType<? extends Entity> entityType, Level level, MechaCore core) {
		super(entityType, level);
		this.core = core;
		for (int i = 0; i < 8; i++) {
			legs.add(new Leggy(8));
			legs.get(i).setRootDir(new Vec3(0, 1, 0));
			stepAreas.add(Vec3.ZERO);
		}
	}

	public MechaEntity(EntityType<? extends Entity> entityType, Level level, MechaSchematic schematic) {
		this(entityType, level, schematic.make());
	}

	public static MechaEntity temp(EntityType<? extends Entity> entityType, Level level) {
		MechaCore m = null;
		return new MechaEntity(entityType, level, m);
	}

	boolean firstTick = true;

	@Override
	public void tick() {
		super.tick();

		if (firstTick) {
			firstTick = false;
			for (Leggy l : legs) {
				l.setRootPosAll(this.position().add(0, 1, 0));
			}
		}

		float tickDelta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
		double stepTolerance = 2;

		Player player = level().getNearestPlayer(this, 100);

		if (player != null) {
			var eye = player.getEyePosition();
			var limit = eye.add(player.getLookAngle().scale(30));
			BlockHitResult raycast = level().clip(new ClipContext(
				player.getEyePosition(),
				limit,
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE,
				player
			));
			if (raycast.getType() == HitResult.Type.BLOCK) {
				followPos = raycast.getLocation();
			}
		}

		var horz = followPos.with(Direction.Axis.Y, this.position().y);
		if (horz.distanceTo(this.position()) > followTolerance) {
			this.direction.setTarget(horz.subtract(this.position()));
			if (!stepping()) {
				this.moveTo(this.position().add(horz.subtract(this.position()).normalize().scale(0.3)));
			}
		}

		if (!stepping()) {
			this.direction.tick();
		}

		this.lookAt(EntityAnchorArgument.Anchor.EYES, this.getEyePosition().add(this.direction.curDir()));

		for (int i = 0; i < legs.size(); i++) {
			// select locations around this for leg tips
			stepAreas.set(i, this.position().add(this.direction.curDir().scale(1).yRot(((float) Math.PI / 4) + (float) Math.PI * 2 * ((float) i / (float) legs.size())).scale(4)));
			Leggy leg = legs.get(i);
			if (!GeometryUtil.inRange(leg.getStepTarget(), stepAreas.get(i), stepTolerance)
			) {
				int l = i - 1 < 0 ? legs.size() - 1 : i - 1;
				int r = i + 1 >= legs.size() ? 0 : i + 1;
				boolean lneighborStepping = legs.get(l).stepping();
				boolean rneighborStepping = legs.get(r).stepping();
				if (!(lneighborStepping || rneighborStepping)) {
					leg.setStepTarget(stepAreas.get(i));
				}
			}
			leg.setRootPos(this.position().add(0, 1, 0));
			leg.tick();
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}

	public boolean stepping() {
		boolean s = false;
		for (var l : legs) s |= l.stepping();
		return s;
	}

	public List<Leggy> legs() {
		return legs;
	}

}
