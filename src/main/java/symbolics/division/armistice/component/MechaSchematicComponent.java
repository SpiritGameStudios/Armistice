package symbolics.division.armistice.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.mecha.schematic.*;

import java.util.List;
import java.util.function.Consumer;

public record MechaSchematicComponent(
	MechaSchematic schematic
) implements TooltipProvider {
	public static final Codec<MechaSchematicComponent> CODEC = MechaSchematic.CODEC.xmap(MechaSchematicComponent::new, MechaSchematicComponent::schematic);

	public static final StreamCodec<ByteBuf, MechaSchematicComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(MechaSchematicComponent.CODEC);

	@Override
	public void addToTooltip(
		@NotNull Item.TooltipContext context,
		@NotNull Consumer<Component> tooltipAdder,
		@NotNull TooltipFlag tooltipFlag
	) {
		HullSchematic hull = schematic.hull();
		ArmorSchematic armor = schematic.armor();
		ChassisSchematic chassis = schematic.chassis();
		List<OrdnanceSchematic> ordnance = schematic.ordnance();

		tooltipAdder.accept(Component.literal("Hull:").withStyle(ChatFormatting.DARK_GRAY));
		tooltipAdder.accept(Component.translatable(hull.id().getNamespace() + ".armor." + hull.id().getPath()).withStyle(ChatFormatting.BLUE));
		tooltipAdder.accept(Component.literal("Tier: " + hull.tier()).withStyle(ChatFormatting.DARK_PURPLE));
		tooltipAdder.accept(Component.literal("Slots: " + String.join(", ", hull.slots().stream().map(Object::toString).toList())).withStyle(ChatFormatting.DARK_PURPLE));

		tooltipAdder.accept(Component.literal("Armor:").withStyle(ChatFormatting.DARK_GRAY));
		tooltipAdder.accept(Component.translatable(armor.id().getNamespace() + ".armor." + armor.id().getPath()).withStyle(ChatFormatting.BLUE));
		tooltipAdder.accept(Component.literal("Size: " + armor.size()).withStyle(ChatFormatting.DARK_PURPLE));

		tooltipAdder.accept(Component.literal("Chassis:").withStyle(ChatFormatting.DARK_GRAY));
		tooltipAdder.accept(Component.translatable(chassis.id().getNamespace() + ".chassis." + chassis.id().getPath()).withStyle(ChatFormatting.BLUE));
		tooltipAdder.accept(Component.literal("Tier: " + chassis.tier()).withStyle(ChatFormatting.DARK_PURPLE));
		tooltipAdder.accept(Component.literal("Armor min: " + chassis.minArmorLevel()).withStyle(ChatFormatting.DARK_PURPLE));
		tooltipAdder.accept(Component.literal("Armor max: " + chassis.maxArmorLevel()).withStyle(ChatFormatting.DARK_PURPLE));

		for (int i = 0; i < ordnance.size(); i++) {
			OrdnanceSchematic ordnanceSchematic = ordnance.get(i);

			tooltipAdder.accept(Component.literal("Ordnance " + i + ":").withStyle(ChatFormatting.DARK_GRAY));
			tooltipAdder.accept(Component.translatable(ordnanceSchematic.id().getNamespace() + ".ordnance." + ordnanceSchematic.id().getPath()).withStyle(ChatFormatting.BLUE));
			tooltipAdder.accept(Component.literal("Size: " + ordnanceSchematic.size()).withStyle(ChatFormatting.DARK_PURPLE));
		}
	}
}
