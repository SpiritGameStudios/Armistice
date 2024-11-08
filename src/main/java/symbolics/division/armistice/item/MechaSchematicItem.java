package symbolics.division.armistice.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.component.SkinComponent;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;
import symbolics.division.armistice.registry.ArmisticeDataComponentTypeRegistrar;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

import java.util.Optional;

public class MechaSchematicItem extends ComponentTooltipItem {
	public MechaSchematicItem(Properties properties) {
		super(properties, ArmisticeDataComponentTypeRegistrar.MECHA_SCHEMATIC);
	}

	@NotNull
	@Override
	public InteractionResult useOn(@NotNull UseOnContext context) {
		MechaSchematic schematic = Optional.ofNullable(context.getItemInHand().get(ArmisticeDataComponentTypeRegistrar.MECHA_SCHEMATIC)).orElseThrow().schematic();

		SkinComponent skin = context.getItemInHand().get(ArmisticeDataComponentTypeRegistrar.SKIN);

		MechaEntity entity = new MechaEntity(ArmisticeEntityTypeRegistrar.MECHA, context.getLevel(), schematic, Optional.ofNullable(skin).map(SkinComponent::skin).orElse(null));
		entity.setPos(context.getClickLocation().add(0, 20, 0));
		context.getLevel().addFreshEntity(entity);

		return InteractionResult.SUCCESS;
	}
}
