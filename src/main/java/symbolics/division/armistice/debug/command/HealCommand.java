package symbolics.division.armistice.debug.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.loading.FMLEnvironment;

public class HealCommand {
	private static final SimpleCommandExceptionType NOT_LIVING_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.heal.not_living"));

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		if (FMLEnvironment.production) return;

		dispatcher.register(Commands.literal("heal")
			.requires(source -> source.hasPermission(2))
			.executes(context -> fullHeal(context.getSource().getEntityOrException(), context.getSource()))
			.then(Commands.argument("target", EntityArgument.entity())
				.executes(context -> fullHeal(EntityArgument.getEntity(context, "target"), context.getSource()))
				.then(Commands.argument("amount", FloatArgumentType.floatArg(0))
					.executes(context -> heal(EntityArgument.getEntity(context, "target"), FloatArgumentType.getFloat(context, "amount"), context.getSource()))
				)
			)
		);
	}

	private static int fullHeal(Entity entity, CommandSourceStack source) throws CommandSyntaxException {
		if (!(entity instanceof LivingEntity livingEntity))
			throw NOT_LIVING_EXCEPTION.create();

		return heal(entity, livingEntity.getMaxHealth(), source);
	}

	private static int heal(Entity entity, float amount, CommandSourceStack source) throws CommandSyntaxException {
		if (!(entity instanceof LivingEntity livingEntity))
			throw NOT_LIVING_EXCEPTION.create();

		float oldHealth = livingEntity.getHealth();
		livingEntity.heal(amount);

		source.sendSuccess(() -> Component.translatable("commands.heal.success", livingEntity.getDisplayName(), livingEntity.getHealth() - oldHealth), true);

		return Command.SINGLE_SUCCESS;
	}
}
