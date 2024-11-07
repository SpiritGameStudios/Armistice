package symbolics.division.armistice.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

public class HitscanBullet extends Entity {

	public static void create(Level level, Vec3 from, Vec3 to, int lifetime) {
		HitscanBullet bullet = ArmisticeEntityTypeRegistrar.HITSCAN_BULLET.create(level);
		bullet.setPos(from);
		bullet.end = to;
		bullet.lifetimeTicks = lifetime;

	}

	public HitscanBullet(EntityType<? extends HitscanBullet> entityType, Level level) {
		super(entityType, level);
	}

	protected int lifetimeTicks;
	protected Vec3 end;

	public Vec3 end() {
		return end;
	}

	@Override
	public void tick() {
		if (tickCount >= lifetimeTicks * 100) {
			kill();
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
}
