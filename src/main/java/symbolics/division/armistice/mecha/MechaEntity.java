package symbolics.division.armistice.mecha;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

public class MechaEntity extends Entity {
    protected final MechaCore core;

    protected MechaEntity(EntityType<?> entityType, Level level, MechaCore core) {
        super(entityType, level);
        this.core = core;
    }

    public MechaEntity(EntityType<?> entityType, Level level, MechaSchematic schematic) {
        this(entityType, level, schematic.make());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        throw new NotImplementedException();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        throw new NotImplementedException();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        throw new NotImplementedException();
    }
}
