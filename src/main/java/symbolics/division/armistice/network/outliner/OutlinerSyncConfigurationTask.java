package symbolics.division.armistice.network.outliner;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.model.ModelOutlinerReloadListener;

import java.util.function.Consumer;

public class OutlinerSyncConfigurationTask implements ICustomConfigurationTask {
	public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(Armistice.id("outliner_task"));

	@Override
	public void run(@NotNull Consumer<CustomPacketPayload> sender) {
		OutlinerSyncS2CPayload payload = new OutlinerSyncS2CPayload(ModelOutlinerReloadListener.getNodes());
		sender.accept(payload);
	}

	@NotNull
	@Override
	public Type type() {
		return TYPE;
	}
}
