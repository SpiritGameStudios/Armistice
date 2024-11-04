package symbolics.division.armistice.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.projectile.ArtilleryShell;
import symbolics.division.armistice.projectile.Missile;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ArmisticeDebugValues {
	public static boolean simpleGun = true;
	public static boolean chassisGravity = false;
	public static boolean ikSolving = true;

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
							mecha.core().setHeat(heat);

							return Command.SINGLE_SUCCESS;
						})
					)
				)
				.then(Commands.literal("artilleryShell")
					.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
					.executes(ctx -> {
						ServerPlayer player = ctx.getSource().getPlayer();
						if (player == null) return 0;

						ArtilleryShell artilleryShell = new ArtilleryShell(
							ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL,
							player.level()
						);
						artilleryShell.setOwner(player);
						artilleryShell.setPos(player.getX(), player.getEyeY(), player.getZ());
						artilleryShell.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5F, 0);
						player.level().addFreshEntity(artilleryShell);
						return Command.SINGLE_SUCCESS;
					})
				)
				.then(Commands.literal("missile")
					.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
					.executes(ctx -> {
						ServerPlayer player = ctx.getSource().getPlayer();
						if (player == null) return 0;

						Missile missile = new Missile(
							ArmisticeEntityTypeRegistrar.MISSILE,
							player.level()
						);
						missile.setOwner(player);
						missile.setPos(player.getX(), player.getEyeY(), player.getZ());
						missile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.5F, 0);
						player.level().addFreshEntity(missile);
						return Command.SINGLE_SUCCESS;
					})
				)
				.then(Commands.literal("missile_aimed")
					.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
					.executes(ctx -> {
						ServerPlayer player = ctx.getSource().getPlayer();
						if (player == null) return 0;
						Level level = player.level();
						Optional<Entity> target = level.getEntities(
							player,
							new AABB(player.position().subtract(50, 50, 50), player.position().add(50, 50, 50)),
							entity -> entity instanceof Monster
						).stream().findFirst();

						return target.map(entity -> {
							Missile missile = Missile.aimedMissile(player.position(), player, entity, 0.5F);
							level.addFreshEntity(missile);
							return Command.SINGLE_SUCCESS;
						}).orElse(0);
					})
				)
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
