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

public class ModelOutlinerReloadListener implements PreparableReloadListener {
	public static final ModelOutlinerReloadListener INSTANCE = new ModelOutlinerReloadListener();
	private static final Gson GSON = new Gson();
	private static Map<ResourceLocation, List<OutlinerNode>> nodes = Map.of();

	public static Map<ResourceLocation, List<OutlinerNode>> getNodes() {
		return nodes;
	}

	@Nullable
	public static List<OutlinerNode> getNode(ResourceLocation id) {
		return nodes.get(id);
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

	protected CompletableFuture<Map<ResourceLocation, List<OutlinerNode>>> prepare(ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, List<OutlinerNode>> data = new Object2ObjectOpenHashMap<>();

			FileToIdConverter filetoidconverter = FileToIdConverter.json("model_bones");

			for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(resourceManager).entrySet()) {
				ResourceLocation file = entry.getKey();
				ResourceLocation id = filetoidconverter.fileToId(file);

				DataResult<List<OutlinerNode>> nodeData = null;
				try (Reader reader = entry.getValue().openAsReader()) {
					JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
					nodeData = OutlinerNode.CODEC.listOf().parse(JsonOps.INSTANCE, element);
				} catch (IllegalArgumentException | IOException | JsonParseException e) {
					Armistice.LOGGER.error("Couldn't parse data file {} from {}", id, file, e);
				}

				if (nodeData == null || nodeData.error().isPresent()) {
					Armistice.LOGGER.error("Couldn't parse data file {} from {}", id, file);
					if (nodeData != null) Armistice.LOGGER.error("{}", nodeData.error().get());
					continue;
				}

				data.put(id, nodeData.getOrThrow());
			}

			return data;
		}, executor);
	}

	protected CompletableFuture<Void> apply(Map<ResourceLocation, List<OutlinerNode>> data, ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			ImmutableMap.Builder<ResourceLocation, List<OutlinerNode>> builder = ImmutableMap.builder();
			data.forEach(builder::put);
			nodes = builder.build();
		}, executor);
	}
}
