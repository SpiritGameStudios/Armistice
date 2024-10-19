package symbolics.division.armistice.mecha;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.mecha.schematic.*;

import java.util.ArrayList;
import java.util.List;

public class MechaEntity extends Entity {
	protected final MechaCore core;
	private boolean firstTick = true;

	protected MechaEntity(EntityType<? extends Entity> entityType, Level level, MechaCore core) {
		super(entityType, level);
		this.core = core;
		setViewScale(10.0);
		this.noCulling = true;
	}

	public MechaEntity(EntityType<? extends Entity> entityType, Level level, MechaSchematic schematic) {
		this(entityType, level, schematic.make());
	}

	public static MechaEntity temp(EntityType<? extends Entity> entityType, Level level) {
		HullSchematic hull = new HullSchematic(1, List.of(1, 2), new HeatData(1, 0, 0));
		ChassisSchematic chassis = new ChassisSchematic(1, 1, 1);
		List<OrdnanceSchematic> ordnance = new ArrayList<>();
		ArmorSchematic armor = new ArmorSchematic(1, 1);
		return new MechaEntity(entityType, level, new MechaSchematic(hull, ordnance, chassis, armor));
	}

	@Override
	public void tick() {
		super.tick();
		if (firstTick) {
			firstTick = false;
			core.initCore(this);
		}
		core.tick();
	}

	public MechaCore core() {
		return core;
	}

	@Override
	protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {

	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compound) {

	}
}
