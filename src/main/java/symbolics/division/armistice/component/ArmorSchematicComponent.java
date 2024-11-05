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
import symbolics.division.armistice.mecha.schematic.ArmorSchematic;

import java.util.function.Consumer;

public record ArmorSchematicComponent(
	ArmorSchematic schematic
) implements TooltipProvider {
	public static final Codec<ArmorSchematicComponent> CODEC = ArmorSchematic.CODEC.xmap(ArmorSchematicComponent::new, ArmorSchematicComponent::schematic);

	public static final StreamCodec<ByteBuf, ArmorSchematicComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(ArmorSchematicComponent.CODEC);

	@Override
	public void addToTooltip(
		@NotNull Item.TooltipContext context,
		@NotNull Consumer<Component> tooltipAdder,
		@NotNull TooltipFlag tooltipFlag
	) {
		tooltipAdder.accept(Component.translatable(schematic.id().getNamespace() + ".armor." + schematic.id().getPath()).withStyle(ChatFormatting.BLUE));
		tooltipAdder.accept(Component.literal("Size: " + schematic.size()).withStyle(ChatFormatting.DARK_PURPLE));
	}
}
