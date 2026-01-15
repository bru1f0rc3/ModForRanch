package com.echo.enchants;

import com.echo.enchants.block.ModBlocks;
import com.echo.enchants.block.entity.ModBlockEntities;
import com.echo.enchants.config.EchoEnchantsConfig;
import com.echo.enchants.enchantment.ModEnchantments;
import com.echo.enchants.event.MobLootDropHandler;
import com.echo.enchants.event.ToolEnchantmentHandler;
import com.echo.enchants.item.ModCreativeTabs;
import com.echo.enchants.item.ModItems;
import com.echo.enchants.menu.ModMenus;
import com.echo.enchants.network.ModNetworking;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * EchoEnchants - A Minecraft Forge mod that adds custom enchantments
 * dropped by hostile mobs.
 * 
 * Features:
 * - 9 unique enchantments for tools
 * - Custom items: Super Fertilizer, Growth Accelerator
 * - Custom blocks: Growth Beacon
 * - Configurable drop rates from hostile mobs
 * - Works in existing worlds without chunk regeneration
 */
@Mod(EchoEnchantsMod.MOD_ID)
public class EchoEnchantsMod {
    
    public static final String MOD_ID = "echo_enchants";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    @SuppressWarnings("removal")
    public EchoEnchantsMod() {
        LOGGER.info("===========================================");
        LOGGER.info("  EchoEnchants Mod Initializing...");
        LOGGER.info("  Version: 1.1.0 for Minecraft 1.20.1");
        LOGGER.info("===========================================");
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register enchantments
        ModEnchantments.register(modEventBus);
        
        // Register custom items
        ModItems.register(modEventBus);
        
        // Register custom blocks
        ModBlocks.register(modEventBus);
        
        // Register block entities
        ModBlockEntities.register(modEventBus);
        
        // Register menus
        ModMenus.register(modEventBus);
        
        // Register creative tabs
        ModCreativeTabs.register(modEventBus);
        
        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EchoEnchantsConfig.SPEC, "echo-enchants-config.toml");
        
        // Register common setup event
        modEventBus.addListener(this::commonSetup);
        
        // Register event handlers to the Forge event bus
        MinecraftForge.EVENT_BUS.register(new MobLootDropHandler());
        MinecraftForge.EVENT_BUS.register(new ToolEnchantmentHandler());
        
        LOGGER.info("EchoEnchants: Registration complete!");
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Register network packets
        event.enqueueWork(ModNetworking::register);
        
        LOGGER.info("EchoEnchants: Common setup phase");
        LOGGER.info("EchoEnchants: Drop chance configured to {}%", EchoEnchantsConfig.DROP_CHANCE_HOSTILE.get());
        LOGGER.info("EchoEnchants: Hostile only mode: {}", EchoEnchantsConfig.HOSTILE_ONLY.get());
        LOGGER.info("EchoEnchants: Enabled mobs count: {}", EchoEnchantsConfig.ENABLED_MOBS.get().size());
        LOGGER.info("===========================================");
        LOGGER.info("  EchoEnchants loaded successfully!");
        LOGGER.info("  9 enchantments registered");
        LOGGER.info("  2 custom items registered");
        LOGGER.info("  1 custom block registered");
        LOGGER.info("  Mob loot drops: ENABLED");
        LOGGER.info("  Tool effects: ENABLED");
        LOGGER.info("===========================================");
    }
}
