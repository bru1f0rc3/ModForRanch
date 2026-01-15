package com.echo.enchants.client;

import com.echo.enchants.client.screen.GrowthBeaconScreen;
import com.echo.enchants.menu.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Client-side setup for EchoEnchants.
 * Registers screens and renderers.
 */
@Mod.EventBusSubscriber(modid = "echo_enchants", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Register screen for Growth Beacon menu
            MenuScreens.register(ModMenus.GROWTH_BEACON_MENU.get(), GrowthBeaconScreen::new);
        });
    }
}
