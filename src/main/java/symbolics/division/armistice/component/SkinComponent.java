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
import symbolics.division.armistice.mecha.MechaSkin;

import java.util.function.Consumer;

public record SkinComponent(
	@NotNull
	MechaSkin skin
) implements TooltipProvider {
	public static final Codec<SkinComponent> CODEC = MechaSkin.CODEC.xmap(SkinComponent::new, SkinComponent::skin);

	public static final StreamCodec<ByteBuf, SkinComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(SkinComponent.CODEC);

	@Override
	public void addToTooltip(
		@NotNull Item.TooltipContext context,
		@NotNull Consumer<Component> tooltipAdder,
		@NotNull TooltipFlag tooltipFlag
	) {
		tooltipAdder.accept(Component.translatable(skin.id().getNamespace() + ".skin." + skin.id().getPath()).withStyle(ChatFormatting.BLUE));
	}
}
