package symbolics.division.armistice.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import symbolics.division.armistice.mecha.MechaEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ArmisticeDebugValues {
	public static boolean simpleGun = false;
	public static boolean chassisGravity = false;
	public static boolean ikSolving = false;

	@SubscribeEvent
	private static void registerCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(
			Commands.literal("armistice_debug")
				.then(setter("simpleGun", value -> simpleGun = value, () -> simpleGun))
				.then(setter("ikSolving", value -> ikSolving = value, () -> ikSolving))
				.then(setter("chassisGravity", value -> chassisGravity = value, () -> chassisGravity))
				.then(Commands.literal("heat")
					.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
					.executes(ctx -> {
						Entity entity = ctx.getSource().getEntity();
						if (entity == null) return 0;
						Entity vehicle = entity.getVehicle();
						if (!(vehicle instanceof MechaEntity mecha)) return 0;

						ctx.getSource().sendSuccess(() -> Component.literal(String.valueOf(mecha.core().getHeat())), true);

						return Command.SINGLE_SUCCESS;
					})
					.then(Commands.argument("value", IntegerArgumentType.integer(0))
						.executes(ctx -> {
							int heat = IntegerArgumentType.getInteger(ctx, "value");

							Entity entity = ctx.getSource().getEntity();
							if (entity == null) return 0;
							Entity vehicle = entity.getVehicle();
							if (!(vehicle instanceof MechaEntity mecha)) return 0;
							if (heat > mecha.core().getMaxHeat()) return 0;
							mecha.core().setHeat(heat);

							return Command.SINGLE_SUCCESS;
						})))
		);
	}

	public static LiteralArgumentBuilder<CommandSourceStack> setter(String name, Consumer<Boolean> setter, Supplier<Boolean> getter) {
		return Commands.literal(name)
			.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
			.executes(ctx -> {
				setter.accept(!getter.get());
				return Command.SINGLE_SUCCESS;
			})
			.then(Commands.argument("enable", BoolArgumentType.bool())
				.executes(ctx -> {
					setter.accept(BoolArgumentType.getBool(ctx, "enable"));
					return Command.SINGLE_SUCCESS;
				}));
	}
}
