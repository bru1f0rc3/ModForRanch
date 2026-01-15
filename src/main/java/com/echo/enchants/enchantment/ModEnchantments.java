package com.echo.enchants.enchantment;

import com.echo.enchants.EchoEnchantsMod;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for all EchoEnchants enchantments.
 * Provides centralized registration and access to all custom enchantments.
 */
public class ModEnchantments {
    
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = 
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, EchoEnchantsMod.MOD_ID);
    
    // ==================== Tool Enchantments ====================
    
    /**
     * Магнетизм (Magnetism) - Собирает лут в инвентарь
     * Уровень: 1
     */
    public static final RegistryObject<Enchantment> MAGNETISM = ENCHANTMENTS.register("magnetism",
            MagnetismEnchantment::new);
    
    /**
     * Опытный (Experienced) - Увеличивает получаемый опыт
     * Уровень: 1-3 (множитель 1.3 * уровень)
     */
    public static final RegistryObject<Enchantment> EXPERIENCED = ENCHANTMENTS.register("experienced",
            ExperiencedEnchantment::new);
    
    /**
     * Неразрушимость (Unbreakable) - Защита от поломки при 10% прочности
     * Уровень: 1
     */
    public static final RegistryObject<Enchantment> UNBREAKABLE = ENCHANTMENTS.register("unbreakable",
            UnbreakableEnchantment::new);
    
    // ==================== Pickaxe Enchantments ====================
    
    /**
     * Бур (Drill) - Разрушает 3x3
     * Уровень: 1-2 (3x3x1 / 3x3x2)
     */
    public static final RegistryObject<Enchantment> DRILL = ENCHANTMENTS.register("drill",
            DrillEnchantment::new);
    
    /**
     * Мега-бур (Mega Drill) - Разрушает 5x5x2
     * Уровень: 1
     */
    public static final RegistryObject<Enchantment> MEGA_DRILL = ENCHANTMENTS.register("mega_drill",
            MegaDrillEnchantment::new);
    
    /**
     * Автоплавка (Auto Smelt) - Автоматически переплавляет руду
     * Уровень: 1
     */
    public static final RegistryObject<Enchantment> AUTO_SMELT = ENCHANTMENTS.register("auto_smelt",
            AutoSmeltEnchantment::new);
    
    // ==================== Hoe/Farming Enchantments ====================
    
    /**
     * Фермер (Farmer) - Увеличивает урожай
     * Уровень: 1-4 (+20% культур за уровень)
     */
    public static final RegistryObject<Enchantment> FARMER = ENCHANTMENTS.register("farmer",
            FarmerEnchantment::new);
    
    /**
     * Посев (Seeding) - Автоматическое переплантирование
     * Уровень: 1-4
     */
    public static final RegistryObject<Enchantment> SEEDING = ENCHANTMENTS.register("seeding",
            SeedingEnchantment::new);
    
    /**
     * Деликатный (Delicate) - Защита невыросших растений
     * Уровень: 1-3
     */
    public static final RegistryObject<Enchantment> DELICATE = ENCHANTMENTS.register("delicate",
            DelicateEnchantment::new);
    
    /**
     * Register all enchantments to the mod event bus.
     */
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
        EchoEnchantsMod.LOGGER.info("EchoEnchants: Registering {} enchantments", ENCHANTMENTS.getEntries().size());
    }
    
    /**
     * Get a list of all registered enchantments (after registry is loaded).
     */
    public static List<RegistryObject<Enchantment>> getAllEnchantments() {
        List<RegistryObject<Enchantment>> list = new ArrayList<>();
        list.add(MAGNETISM);
        list.add(EXPERIENCED);
        list.add(UNBREAKABLE);
        list.add(DRILL);
        list.add(MEGA_DRILL);
        list.add(AUTO_SMELT);
        list.add(FARMER);
        list.add(SEEDING);
        list.add(DELICATE);
        return list;
    }
}
