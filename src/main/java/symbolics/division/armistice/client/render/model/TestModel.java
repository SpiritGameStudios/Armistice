package symbolics.division.armistice.client.render.model;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

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

}
