package symbolics.division.armistice.mecha;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.mecha.movement.Leggy;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

import java.util.ArrayList;
import java.util.List;

public class MechaEntity extends PathfinderMob {
	protected final MechaCore core;
	protected final List<Leggy> legs = new ArrayList<>();
	public List<Vec3> tempLegTargets = null;
	int m = 0;

	protected MechaEntity(EntityType<? extends PathfinderMob> entityType, Level level, MechaCore core) {
		super(entityType, level);
		this.core = core;
		for (int i = 0; i < 8; i++) {
			legs.add(new Leggy(10));
		}
	}

	public MechaEntity(EntityType<? extends PathfinderMob> entityType, Level level, MechaSchematic schematic) {
		this(entityType, level, schematic.make());
	}

	public static MechaEntity temp(EntityType<? extends PathfinderMob> entityType, Level level) {
		MechaCore m = null;
		return new MechaEntity(entityType, level, m);
	}

	@Override
	public void tick() {
		super.tick();

		if (tempLegTargets == null) {
			tempLegTargets = new ArrayList<>();
			for (var leg : legs()) {
				tempLegTargets.add(leg.getTarget());
			}
		}

		double legSpeed = this.getSpeed() * 1.2; // blocks/sec
		double distPerTick = legSpeed / 20; // blocks/sec / tick/sec = blocks/tick

		float delta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
		Vec3 pos = getPosition(delta);

		if (this.getDeltaMovement().lengthSqr() > 0.00001 && this.tickCount % 10 > 0) {
			int u = (m++) % legs.size();
			var joint = legs.get(u);
			double d = joint.getMaxDistance();

			var look = this.getLookAngle(); // me when getLookAngle returns a 3d vector
			var left = look.yRot((float) Math.PI / 4)
				.with(Direction.Axis.Y, 0)
				.normalize()
				.scale((double) legs.size() / 2 - u);
			Vec3 target = this.getPosition(0).add(look.scale(d / 2)).add(left);


			for (var collision : level().getBlockCollisions(this, AABB.unitCubeFromLowerCorner(target).inflate(0, 1, 0))) {
				target = collision.toAabbs().getFirst().getCenter();
				break; // lol
			}

			tempLegTargets.set(u, target);


//			for (int i = 0; i < legs.size(); i++) {
//				var s = (legs.get(i).getMaxDistance() - 0.1) / 2;
//				legs.get(i).setTarget(pos.add(s * x, s * y, s * z));
//			}
		}

		for (int i = 0; i < legs.size(); i++) {
			Leggy leg = legs.get(i);
			leg.setTarget(tempLegTargets.get(i));
//			Vec3 target = tempLegTargets.get(i);
//			Vec3 cur = leg.getTarget();
//			double dist = Math.min(cur.distanceTo(target), distPerTick);
//			Vec3 intermediate = target.subtract(cur).normalize().scale(dist * delta).add(cur);
//			leg.setTarget(intermediate);
			leg.setRootPos(this.getPosition(0));
			leg.tick();
		}

//			double x = Math.sin(((double) (this.tickCount + i * 20) + delta) / 60);
//			double y = Math.cos(((double) (this.tickCount - i * 30) + delta) / 30);
//			double z = Math.sin(((double) (this.tickCount + i * 200) + delta) / 10);


//		for (var leg : legs) {
//			leg.setRootPos(this.getPosition(0));
//			leg.tick();
//		}

	}

	public List<Leggy> legs() {
		return legs;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
	}
}
