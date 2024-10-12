package symbolics.division.armistice.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import symbolics.division.armistice.util.registrar.ItemRegistrar;

public final class ArmisticeItemRegistrar implements ItemRegistrar {
	public static final Item ARMOR_SCHEMATIC = new Item(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.UNCOMMON));

	public static final Item HULL_SCHEMATIC = new Item(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.UNCOMMON));

	public static final Item ORDNANCE_SCHEMATIC = new Item(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.UNCOMMON));

	public static final Item MECHA_SCHEMATIC = new Item(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.RARE));
}
