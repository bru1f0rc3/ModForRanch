package com.echo.enchants.menu;

import com.echo.enchants.EchoEnchantsMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for all custom menus in EchoEnchants.
 */
public class ModMenus {
    
    public static final DeferredRegister<MenuType<?>> MENUS = 
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, EchoEnchantsMod.MOD_ID);
    
    public static final RegistryObject<MenuType<GrowthBeaconMenu>> GROWTH_BEACON_MENU = 
            MENUS.register("growth_beacon_menu", 
                    () -> IForgeMenuType.create(GrowthBeaconMenu::new));
    
    /**
     * Register all menus to the mod event bus.
     */
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
        EchoEnchantsMod.LOGGER.info("EchoEnchants: Registering menus");
    }
}
