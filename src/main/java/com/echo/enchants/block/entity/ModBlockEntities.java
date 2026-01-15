package com.echo.enchants.block.entity;

import com.echo.enchants.EchoEnchantsMod;
import com.echo.enchants.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for all block entities in EchoEnchants.
 */
public class ModBlockEntities {
    
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EchoEnchantsMod.MOD_ID);
    
    public static final RegistryObject<BlockEntityType<GrowthBeaconBlockEntity>> GROWTH_BEACON = 
            BLOCK_ENTITIES.register("growth_beacon", 
                    () -> BlockEntityType.Builder.of(GrowthBeaconBlockEntity::new, 
                            ModBlocks.GROWTH_BEACON.get()).build(null));
    
    /**
     * Register all block entities to the mod event bus.
     */
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        EchoEnchantsMod.LOGGER.info("EchoEnchants: Registering block entities");
    }
}
