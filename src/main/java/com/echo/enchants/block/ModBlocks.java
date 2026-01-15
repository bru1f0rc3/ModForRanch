package com.echo.enchants.block;

import com.echo.enchants.EchoEnchantsMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for all custom blocks in EchoEnchants.
 */
public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = 
            DeferredRegister.create(ForgeRegistries.BLOCKS, EchoEnchantsMod.MOD_ID);
    
    public static final DeferredRegister<Item> BLOCK_ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, EchoEnchantsMod.MOD_ID);
    
    /**
     * Ускоритель роста (Growth Beacon) - Ускоряет рост растений в радиусе 5 блоков
     */
    public static final RegistryObject<Block> GROWTH_BEACON = BLOCKS.register("growth_beacon",
            () -> new GrowthBeaconBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.EMERALD)
                    .strength(2.5f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)
                    .noOcclusion()));
    
    public static final RegistryObject<Item> GROWTH_BEACON_ITEM = BLOCK_ITEMS.register("growth_beacon",
            () -> new BlockItem(GROWTH_BEACON.get(), new Item.Properties()));
    
    /**
     * Register all blocks to the mod event bus.
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
        EchoEnchantsMod.LOGGER.info("EchoEnchants: Registering {} custom blocks", BLOCKS.getEntries().size());
    }
}
