package symbolics.division.armistice.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

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
