package com.echo.enchants.item;

import com.echo.enchants.EchoEnchantsMod;
import com.echo.enchants.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Creative tab for EchoEnchants mod items.
 */
public class ModCreativeTabs {
    
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EchoEnchantsMod.MOD_ID);
    
    public static final RegistryObject<CreativeModeTab> ECHO_ENCHANTS_TAB = CREATIVE_TABS.register("echo_enchants_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.GROWTH_BEACON.get()))
                    .title(Component.translatable("itemGroup.echo_enchants"))
                    .displayItems((parameters, output) -> {
                        // Add items
                        output.accept(ModItems.SUPER_FERTILIZER.get());
                        
                        // Add blocks
                        output.accept(ModBlocks.GROWTH_BEACON_ITEM.get());
                    })
                    .build());
    
    /**
     * Register creative tabs to the mod event bus.
     */
    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
