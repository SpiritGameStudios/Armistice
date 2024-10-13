package symbolics.division.armistice.mecha;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.mecha.movement.Leggy;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

import java.util.ArrayList;
import java.util.List;

public class MechaEntity extends Mob {
	protected final MechaCore core;
	protected final List<Leggy> legs = new ArrayList<>();

	public static MechaEntity temp(EntityType<? extends Mob> entityType, Level level) {
		MechaCore m = null;
		return new MechaEntity(entityType, level, m);
	}
	protected MechaEntity(EntityType<? extends Mob> entityType, Level level, MechaCore core) {
		super(entityType, level);
		this.core = core;
		for (int i=0; i<8; i++) {
			legs.add(new Leggy(10));
		}
	}

	public MechaEntity(EntityType<? extends Mob> entityType, Level level, MechaSchematic schematic) {
		this(entityType, level, schematic.make());
	}

	@Override
	public void tick() {
		super.tick();

		float delta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
		Vec3 pos = getPosition(delta);

		for (int i = 0; i < legs.size(); i++) {
			var s = (legs.get(i).getMaxDistance()-0.1) / 2;
			double x = Math.sin(((double)(this.tickCount + i*20) + delta) / 60);
			double y = Math.cos(((double)(this.tickCount - i*30) + delta) / 30);
			double z = Math.sin(((double)(this.tickCount + i*200) + delta) / 10);
			legs.get(i).setTarget(pos.add(s*x, s*y, s*z));
		}

		for (var leg: legs) {
			leg.setRootPos(this.getPosition(0));
			leg.tick();
		}

	}

	public List<Leggy> legs() {
		return legs;
	}
}
