package symbolics.division.armistice.mixin;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ExistingFileHelper.class)
public interface ExistingFileHelperAccessor {
	@Invoker
	ResourceManager callGetManager(PackType packType);
}
