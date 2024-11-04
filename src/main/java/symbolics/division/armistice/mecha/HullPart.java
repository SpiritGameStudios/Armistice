package symbolics.division.armistice.mecha;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Vector2f;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.ordnance.NullOrdnancePart;
import symbolics.division.armistice.mecha.schematic.HeatData;
import symbolics.division.armistice.mecha.schematic.HullSchematic;

import java.util.Map;

import static symbolics.division.armistice.mecha.MechaEntity.BARREL_ROTATIONS;
import static symbolics.division.armistice.mecha.MechaEntity.HEAT;

public class HullPart extends AbstractMechaPart {
	protected final NonNullList<OrdnancePart> ordnance;
	protected final HeatData heatData;

	protected final Map<Integer, Vector2f> ordnanceRotationChanges = new Int2ObjectArrayMap<>();

	protected int coolingDelay;

	public HullPart(HullSchematic schematic) {
		ordnance = NonNullList.withSize(schematic.slots().size(), new NullOrdnancePart());
		this.heatData = schematic.heat();
	}

	@VisibleForTesting
	public void setHeat(int heat) {
		this.core.entity().getEntityData().set(HEAT, heat);
	}

	public int getHeat() {
		if (this.core == null) return -1;
		return this.core.entity().getEntityData().get(HEAT);
	}

	public int getMaxHeat() {
		return heatData.max();
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);

		for (int i = 0; i < core.schematic.ordnance().size(); i++) {
			ordnance.set(i, core.schematic.ordnance().get(i).make());
		}

		ordnance.forEach(part -> part.init(core));
	}

	@Override
	public void tick() {
		super.tick();

		int prevHeat = getHeat();
		setHeat(prevHeat + ordnance.stream().mapToInt(OrdnancePart::heat).sum());

		if (getHeat() > prevHeat) coolingDelay = heatData.delay();
		else coolingDelay = Math.max(coolingDelay - 1, 0);

		if (coolingDelay == 0)
			setHeat(Math.max(getHeat() - heatData.decay(), 0));

		if (getHeat() > heatData.max()) onOverheat();
	}

	@Override
	public void clientTick(float tickDelta) {
		ordnance.forEach(part -> part.clientTick(tickDelta));
		super.clientTick(tickDelta);
	}

	@Override
	public void serverTick() {
		ordnance.forEach(Part::serverTick);
		core.entity().getEntityData().set(BARREL_ROTATIONS, ordnanceRotationChanges);

		super.serverTick();
	}

	protected OrdnancePart getOrdnance(int slot) {
		return ordnance.get(slot);
	}

	public void changeRotation(OrdnancePart part, Vector2f rotation) {
		ordnanceRotationChanges.put(core.ordnanceIndex(part), rotation);
	}

	protected void onOverheat() {
		core.level()
			.explode(
				core.entity(),
				Explosion.getDefaultDamageSource(core.level(), core.entity()),
				null,
				core.entity().getX(),
				core.entity().getY(0.0625),
				core.entity().getZ(),
				4.0F,
				false,
				Level.ExplosionInteraction.TNT
			);
	}

	@Override
	public ChassisPart parent() {
		return this.core.chassis;
	}

	@Override
	public Vector3fc relPos() {
		return parent().relHullPos();
	}

	@Override
	public void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {
		super.renderDebug(bufferSource, poseStack);

		ordnance.forEach(part -> part.renderDebug(bufferSource, poseStack));
	}

	@Override
	public boolean ready() {
		return super.ready() && ordnance.stream().allMatch(AbstractMechaPart::ready);
	}
}
