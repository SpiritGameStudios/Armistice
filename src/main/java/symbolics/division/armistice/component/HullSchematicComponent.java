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
import symbolics.division.armistice.mecha.schematic.HullSchematic;

import java.util.function.Consumer;

public record HullSchematicComponent(
	HullSchematic schematic
) implements TooltipProvider {
	public static final Codec<HullSchematicComponent> CODEC = HullSchematic.CODEC.xmap(HullSchematicComponent::new, HullSchematicComponent::schematic);

	public static final StreamCodec<ByteBuf, HullSchematicComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(HullSchematicComponent.CODEC);

	@Override
	public void addToTooltip(
		@NotNull Item.TooltipContext context,
		@NotNull Consumer<Component> tooltipAdder,
		@NotNull TooltipFlag tooltipFlag
	) {
		tooltipAdder.accept(Component.translatable(schematic.id().getNamespace() + ".hull." + schematic.id().getPath()).withStyle(ChatFormatting.BLUE));
		tooltipAdder.accept(Component.literal("Tier: " + schematic.tier()).withStyle(ChatFormatting.DARK_PURPLE));
		tooltipAdder.accept(Component.literal("Slots: " + String.join(", ", schematic.slots().stream().map(Object::toString).toList())).withStyle(ChatFormatting.DARK_PURPLE));
	}
}
