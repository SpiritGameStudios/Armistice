package symbolics.division.armistice.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import symbolics.division.armistice.registry.ArmisticeItemRegistrar;

import static symbolics.division.armistice.Armistice.MODID;

public class ArmisticeItemModelProvider extends ItemModelProvider {
	public ArmisticeItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		basicItem(ArmisticeItemRegistrar.CHASSIS_SCHEMATIC);
		basicItem(ArmisticeItemRegistrar.HULL_SCHEMATIC);
		basicItem(ArmisticeItemRegistrar.ORDNANCE_SCHEMATIC);
		basicItem(ArmisticeItemRegistrar.MECHA_SCHEMATIC);
	}
}
