package symbolics.division.armistice.client.render.model;

import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.model.BBModelData;
import symbolics.division.armistice.model.MechaModelData;

import java.io.IOException;

/**
 * load arbitrary bbmodel
 */

public class TestModel {

	public static final ModelResourceLocation TEST_MODEL = ModelResourceLocation.standalone(
		ResourceLocation.fromNamespaceAndPath("armistice", "chassis/ref_mech")
	);

	public static void loadTestModel(ModelEvent.RegisterAdditional event) {
		event.register(TEST_MODEL);
	}

	public static void setAltTexture(ModelEvent.ModifyBakingResult event) {

	}

	public static boolean loadModel() {

		var resource = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath("armistice", "models/chassis/ref_mech.bbmodel"));
		return resource.map(r -> {
			Minecraft.getInstance().execute(() -> {
				try {
					var bb = BBModelData.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseReader(r.openAsReader())).getOrThrow(IOException::new);
					MechaModelData.of(bb.getFirst());
				} catch (IOException e) {
					Armistice.LOGGER.error("failed to load resource: ", e);
				}
			});
			return true;
		}).orElse(false);
//		BBModelData model;

//		DataResult<BBModelData> result = BBModelData.CODEC.parse(JsonOps.INSTANCE, )
//		return ModelPartData.of(model);
	}

	public static LiteralArgumentBuilder<CommandSourceStack> registerClientCommands(LiteralArgumentBuilder<CommandSourceStack> cmd) {
		return cmd.then(Commands.literal("model")
			.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
			.executes(ctx -> {
				ctx.getSource().sendSystemMessage(Component.literal(loadModel() ? "loaded" : "failed"));
				return Command.SINGLE_SUCCESS;
			}));
	}

}
