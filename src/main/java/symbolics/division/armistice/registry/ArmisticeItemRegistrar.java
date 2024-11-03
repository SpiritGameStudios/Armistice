package symbolics.division.armistice.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import symbolics.division.armistice.item.ComponentTooltipItem;
import symbolics.division.armistice.item.MechaSchematicItem;
import symbolics.division.armistice.util.registrar.ItemRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeItemRegistrar implements ItemRegistrar {
	public static final Item ARMOR_SCHEMATIC = new ComponentTooltipItem(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.UNCOMMON),
		ArmisticeDataComponentTypeRegistrar.ARMOR_SCHEMATIC);

	public static final Item HULL_SCHEMATIC = new ComponentTooltipItem(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.UNCOMMON),
		ArmisticeDataComponentTypeRegistrar.HULL_SCHEMATIC);

	public static final Item CHASSIS_SCHEMATIC = new ComponentTooltipItem(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.UNCOMMON),
		ArmisticeDataComponentTypeRegistrar.CHASSIS_SCHEMATIC);

	public static final Item ORDNANCE_SCHEMATIC = new ComponentTooltipItem(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.UNCOMMON),
		ArmisticeDataComponentTypeRegistrar.ORDNANCE_SCHEMATIC);

	public static final Item MECHA_SCHEMATIC = new MechaSchematicItem(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.RARE));

	public static final Item MECHA_SKIN = new Item(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.RARE));

	public static final Item MUSIC_FILE_RECALLED = new Item(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.RARE)
		.jukeboxPlayable(ArmisticeJukeboxSongs.Keys.RECALLED));

	public static final Item MUSIC_FILE_PEACE_ENGINES = new Item(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.RARE)
		.jukeboxPlayable(ArmisticeJukeboxSongs.Keys.PEACE_ENGINES));
}
