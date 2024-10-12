package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public interface ItemRegistrar extends Registrar<Item> {
	@Override
	default Registry<Item> getRegistry() {
		return BuiltInRegistries.ITEM;
	}

	@Override
	default Class<Item> getObjectType() {
		return Item.class;
	}
}
