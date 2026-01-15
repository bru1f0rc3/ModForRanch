package com.echo.enchants.item;

import com.echo.enchants.EchoEnchantsMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for all custom items in EchoEnchants.
 */
public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, EchoEnchantsMod.MOD_ID);
    
    /**
     * Супер-удобрение (Super Fertilizer) - Удобряет площадь 5x5
     */
    public static final RegistryObject<Item> SUPER_FERTILIZER = ITEMS.register("super_fertilizer",
            () -> new SuperFertilizerItem(new Item.Properties().stacksTo(64)));
    
    /**
     * Register all items to the mod event bus.
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        EchoEnchantsMod.LOGGER.info("EchoEnchants: Registering {} custom items", ITEMS.getEntries().size());
    }
}
