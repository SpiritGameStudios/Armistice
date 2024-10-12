package symbolics.division.armistice.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;

import java.util.function.Consumer;

public record ChassisSchematicComponent(
	ChassisSchematic schematic
) implements TooltipProvider {
	public static final Codec<ChassisSchematicComponent> CODEC = ChassisSchematic.REGISTRY_CODEC.xmap(ChassisSchematicComponent::new, ChassisSchematicComponent::schematic);

	public static final StreamCodec<ByteBuf, ChassisSchematicComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(ChassisSchematicComponent.CODEC);

	@Override
	public void addToTooltip(
		@NotNull Item.TooltipContext context,
		@NotNull Consumer<Component> tooltipAdder,
		@NotNull TooltipFlag tooltipFlag
	) {
		tooltipAdder.accept(Component.translatable(schematic.id().toLanguageKey()));
		tooltipAdder.accept(Component.literal("Tier: " + schematic.tier()));
		tooltipAdder.accept(Component.literal("Armor min: " + schematic.minArmorLevel()));
		tooltipAdder.accept(Component.literal("Armor max: " + schematic.maxArmorLevel()));
	}
}
