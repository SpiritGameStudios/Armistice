package symbolics.division.armistice.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import symbolics.division.armistice.Armistice;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ModelElementReloadListener implements PreparableReloadListener {
	public static final ModelElementReloadListener INSTANCE = new ModelElementReloadListener();
	private static final Gson GSON = new Gson();
	private static Map<ResourceLocation, List<Element>> models;

	@Nullable
	public static List<Element> getModel(ResourceLocation id) {
		return models.get(id);
	}

	@NotNull
	@Override
	public final CompletableFuture<Void> reload(
		@NotNull PreparableReloadListener.PreparationBarrier stage,
		@NotNull ResourceManager resourceManager,
		@NotNull ProfilerFiller preparationsProfiler,
		@NotNull ProfilerFiller reloadProfiler,
		@NotNull Executor backgroundExecutor,
		@NotNull Executor gameExecutor
	) {
		return prepare(resourceManager, preparationsProfiler, backgroundExecutor)
			.thenCompose(stage::wait)
			.thenCompose(data -> apply(data, resourceManager, reloadProfiler, gameExecutor));
	}

	protected CompletableFuture<Map<ResourceLocation, List<Element>>> prepare(ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, List<Element>> data = new Object2ObjectOpenHashMap<>();

			FileToIdConverter filetoidconverter = FileToIdConverter.json("model_elements");

			for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(resourceManager).entrySet()) {
				ResourceLocation file = entry.getKey();
				ResourceLocation id = filetoidconverter.fileToId(file);

				DataResult<List<Element>> modelData = null;
				try (Reader reader = entry.getValue().openAsReader()) {
					JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
					modelData = Element.CODEC.listOf().parse(JsonOps.INSTANCE, element);
				} catch (IllegalArgumentException | IOException | JsonParseException e) {
					Armistice.LOGGER.error("Couldn't parse data file {} from {}", id, file, e);
				}

				if (modelData == null || modelData.error().isPresent()) {
					Armistice.LOGGER.error("Couldn't parse data file {} from {}", id, file);
					if (modelData != null) Armistice.LOGGER.error("{}", modelData.error().get());
					continue;
				}

				data.put(id, modelData.getOrThrow());
			}

			return data;
		}, executor);
	}

	protected CompletableFuture<Void> apply(Map<ResourceLocation, List<Element>> data, ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			profiler.push("Build model element map");
			ImmutableMap.Builder<ResourceLocation, List<Element>> builder = ImmutableMap.builder();
			data.forEach(builder::put);
			models = builder.build();
			profiler.pop();
		}, executor);
	}
}
