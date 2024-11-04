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
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;

import java.util.function.Consumer;

public record OrdnanceSchematicComponent(
	OrdnanceSchematic schematic
) implements TooltipProvider {
	public static final Codec<OrdnanceSchematicComponent> CODEC = OrdnanceSchematic.REGISTRY_CODEC.xmap(OrdnanceSchematicComponent::new, OrdnanceSchematicComponent::schematic);

	public static final StreamCodec<ByteBuf, OrdnanceSchematicComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(OrdnanceSchematicComponent.CODEC);

	@Override
	public void addToTooltip(
		@NotNull Item.TooltipContext context,
		@NotNull Consumer<Component> tooltipAdder,
		@NotNull TooltipFlag tooltipFlag
	) {
		tooltipAdder.accept(Component.translatable(schematic.id().toLanguageKey()).withStyle(ChatFormatting.BLUE));
		tooltipAdder.accept(Component.literal("Size: " + schematic.size()).withStyle(ChatFormatting.DARK_PURPLE));
	}
}
