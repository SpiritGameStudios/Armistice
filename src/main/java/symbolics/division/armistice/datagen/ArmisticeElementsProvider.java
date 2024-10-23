package symbolics.division.armistice.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mixin.ExistingFileHelperAccessor;
import symbolics.division.armistice.model.BBModelData;
import symbolics.division.armistice.model.Element;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static symbolics.division.armistice.Armistice.MODID;

public class ArmisticeElementsProvider extends JsonCodecProvider<List<Element>> {
	private static final Gson GSON = new Gson();

	public ArmisticeElementsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, PackOutput.Target.RESOURCE_PACK, "model_elements", PackType.CLIENT_RESOURCES, Element.CODEC.listOf(), lookupProvider, MODID, existingFileHelper);
	}

	@Override
	protected void gather() {
		FileToIdConverter converter = new FileToIdConverter("mecha_models", ".bbmodel");

		ResourceManager manager = ((ExistingFileHelperAccessor) existingFileHelper).callGetManager(PackType.CLIENT_RESOURCES);
		for (Map.Entry<ResourceLocation, Resource> entry : converter.listMatchingResources(manager).entrySet()) {
			ResourceLocation file = entry.getKey();
			ResourceLocation id = converter.fileToId(file);

			DataResult<BBModelData> modelData = null;
			try (Reader reader = entry.getValue().openAsReader()) {
				JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
				modelData = BBModelData.CODEC.parse(JsonOps.INSTANCE, element);
			} catch (IllegalArgumentException | IOException | JsonParseException e) {
				Armistice.LOGGER.error("Couldn't parse data file {} from {}", id, file, e);
			}

			if (modelData == null || modelData.error().isPresent()) {
				Armistice.LOGGER.error("Couldn't parse data file {} from {}", id, file);
				if (modelData != null) Armistice.LOGGER.error("{}", modelData.error().get());
				continue;
			}

			unconditional(id, modelData.getOrThrow().elements());
		}
	}
}
