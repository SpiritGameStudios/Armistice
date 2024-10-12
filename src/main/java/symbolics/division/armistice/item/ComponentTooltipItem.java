package symbolics.division.armistice.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ComponentTooltipItem extends Item {
	private final DataComponentType<? extends TooltipProvider> type;

	public ComponentTooltipItem(Properties properties, DataComponentType<? extends TooltipProvider> type) {
		super(properties);
		this.type = type;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		Optional.ofNullable(stack.get(type))
			.ifPresent(component -> component.addToTooltip(context, tooltipComponents::add, tooltipFlag));
	}
}
