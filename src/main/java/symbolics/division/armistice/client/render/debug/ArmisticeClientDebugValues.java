package symbolics.division.armistice.client.render.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.util.function.Consumer;

import static symbolics.division.armistice.debug.ArmisticeDebugValues.setter;

public final class ArmisticeClientDebugValues {
	public static double max = Math.PI / 4;
	public static double min = -Math.PI / 4;

	public static boolean debugRenderer = true;

	public static boolean showHull = true;
	public static boolean showOrdnance = true;
	public static boolean showChassis = true;

	@SubscribeEvent
	private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("armistice_debug_client")
			.then(Commands.literal("mecha")
				.then(setter("debug", value -> debugRenderer = value, () -> debugRenderer))
				.then(
					setter("part", value -> {
						showChassis = value;
						showHull = value;
						showOrdnance = value;
					}, () -> showChassis || showHull || showOrdnance)
						.then(setter("chassis", value -> showChassis = value, () -> showChassis))
						.then(setter("hull", value -> showHull = value, () -> showHull))
						.then(setter("ordnance", value -> showOrdnance = value, () -> showOrdnance))
				)
				.then(Commands.literal("leg")
					.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
					.executes(ctx -> {
						GeometryDebugRenderer.setAll(ctx.getSource().getPosition());
						return Command.SINGLE_SUCCESS;
					}).then(doubleSetter("max", v -> max = v * Math.PI))
					.then(doubleSetter("min", v -> min = v * Math.PI))
					.then(doubleSetter("rotate", GeometryDebugRenderer::rotate))
					.then(Commands.literal("update").executes(ctx -> {
						GeometryDebugRenderer.update();
						return Command.SINGLE_SUCCESS;
					}))
				)
			)
		);
	}

	public static LiteralArgumentBuilder<CommandSourceStack> doubleSetter(String name, Consumer<Double> c) {
		return Commands.literal(name).then(
			Commands.argument(name, DoubleArgumentType.doubleArg())
				.executes(ctx -> {
					c.accept(DoubleArgumentType.getDouble(ctx, name));
					return Command.SINGLE_SUCCESS;
				}));
	}
}
