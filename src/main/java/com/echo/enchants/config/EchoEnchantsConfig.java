package com.echo.enchants.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration for EchoEnchants mod.
 * Config file location: .minecraft/config/echo-enchants-config.toml
 */
public class EchoEnchantsConfig {
    
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // ==================== Drop Settings ====================
    
    /**
     * Шанс выпадения зачарования с враждебного моба (%)
     * Рекомендуемое значение: 0.4-0.7% для редкости
     */
    public static final ForgeConfigSpec.DoubleValue DROP_CHANCE_HOSTILE;
    
    /**
     * Минимальный уровень моба для выпадения (для будущей совместимости с модами уровней)
     */
    public static final ForgeConfigSpec.IntValue MIN_MOB_LEVEL;
    
    /**
     * Количество выпадающих книг за одно убийство (1-3)
     */
    public static final ForgeConfigSpec.IntValue BOOKS_PER_KILL;
    
    /**
     * Максимальное количество зачарований на одном предмете
     */
    public static final ForgeConfigSpec.IntValue MAX_ENCHANTMENTS_PER_ITEM;
    
    // ==================== Mob Settings ====================
    
    /**
     * Только враждебные мобы (исключить нейтральные и дружественные)
     */
    public static final ForgeConfigSpec.BooleanValue HOSTILE_ONLY;
    
    /**
     * Включить выпадения в существующих чанках
     */
    public static final ForgeConfigSpec.BooleanValue ENABLE_IN_EXISTING_CHUNKS;
    
    /**
     * Враждебные мобы для выпадения лута
     */
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENABLED_MOBS;
    
    // ==================== Enchantment Weights ====================
    
    /**
     * Веса редкости для каждого зачарования
     */
    public static final ForgeConfigSpec.IntValue WEIGHT_MAGNETISM;
    public static final ForgeConfigSpec.IntValue WEIGHT_EXPERIENCED;
    public static final ForgeConfigSpec.IntValue WEIGHT_UNBREAKABLE;
    public static final ForgeConfigSpec.IntValue WEIGHT_FILTER;
    public static final ForgeConfigSpec.IntValue WEIGHT_DRILL;
    public static final ForgeConfigSpec.IntValue WEIGHT_MEGA_DRILL;
    public static final ForgeConfigSpec.IntValue WEIGHT_AUTO_SMELT;
    public static final ForgeConfigSpec.IntValue WEIGHT_LUMBERJACK;
    public static final ForgeConfigSpec.IntValue WEIGHT_FARMER;
    public static final ForgeConfigSpec.IntValue WEIGHT_SEEDING;
    public static final ForgeConfigSpec.IntValue WEIGHT_DELICATE;
    
    // ==================== Debug Settings ====================
    
    /**
     * Включить отладочное логирование
     */
    public static final ForgeConfigSpec.BooleanValue DEBUG_MODE;
    
    static {
        BUILDER.comment("===== EchoEnchants Configuration =====");
        BUILDER.comment("Customize enchantment drop rates and mob settings");
        BUILDER.push("drop_settings");
        
        DROP_CHANCE_HOSTILE = BUILDER
                .comment("Шанс выпадения зачарования с враждебного моба (%)")
                .comment("Рекомендуемое значение: 0.4-0.7% для редкости")
                .comment("Drop chance for enchanted book from hostile mobs (%)")
                .defineInRange("drop_chance_hostile", 0.5, 0.0, 100.0);
        
        MIN_MOB_LEVEL = BUILDER
                .comment("Минимальный уровень моба для выпадения")
                .comment("Minimum mob level for drops (for future mod compatibility)")
                .defineInRange("min_mob_level", 0, 0, 100);
        
        BOOKS_PER_KILL = BUILDER
                .comment("Количество выпадающих книг за одно убийство (1-3)")
                .comment("Number of books dropped per kill")
                .defineInRange("books_per_kill", 1, 1, 3);
        
        MAX_ENCHANTMENTS_PER_ITEM = BUILDER
                .comment("Максимальное количество зачарований на одном предмете")
                .comment("Maximum enchantments per item")
                .defineInRange("max_enchantments_per_item", 5, 1, 10);
        
        BUILDER.pop();
        
        BUILDER.push("mob_settings");
        
        HOSTILE_ONLY = BUILDER
                .comment("Только враждебные мобы (исключить нейтральные и дружественные)")
                .comment("Only hostile mobs drop enchanted books")
                .define("hostile_only", true);
        
        ENABLE_IN_EXISTING_CHUNKS = BUILDER
                .comment("Включить выпадения в существующих чанках")
                .comment("Enable drops in existing chunks (no chunk regeneration needed)")
                .define("enable_in_existing_chunks", true);
        
        ENABLED_MOBS = BUILDER
                .comment("Враждебные мобы для выпадения лута")
                .comment("List of hostile mobs that can drop enchanted books")
                .defineListAllowEmpty(
                        Arrays.asList("enabled_mobs"),
                        () -> Arrays.asList(
                                "minecraft:zombie",
                                "minecraft:skeleton",
                                "minecraft:creeper",
                                "minecraft:spider",
                                "minecraft:enderman",
                                "minecraft:blaze",
                                "minecraft:ghast",
                                "minecraft:wither_skeleton",
                                "minecraft:cave_spider",
                                "minecraft:slime",
                                "minecraft:witch",
                                "minecraft:phantom",
                                "minecraft:drowned",
                                "minecraft:husk",
                                "minecraft:stray",
                                "minecraft:pillager",
                                "minecraft:vindicator",
                                "minecraft:evoker",
                                "minecraft:ravager",
                                "minecraft:piglin_brute",
                                "minecraft:hoglin",
                                "minecraft:zoglin",
                                "minecraft:magma_cube",
                                "minecraft:silverfish",
                                "minecraft:endermite",
                                "minecraft:guardian",
                                "minecraft:elder_guardian",
                                "minecraft:shulker",
                                "minecraft:vex",
                                "minecraft:warden"
                        ),
                        obj -> obj instanceof String
                );
        
        BUILDER.pop();
        
        BUILDER.push("enchantment_weights");
        BUILDER.comment("Веса редкости для каждого зачарования (больше = чаще выпадает)");
        BUILDER.comment("Rarity weights for each enchantment (higher = more common)");
        
        WEIGHT_MAGNETISM = BUILDER.comment("Магнетизм / Magnetism").defineInRange("weight_magnetism", 10, 1, 100);
        WEIGHT_EXPERIENCED = BUILDER.comment("Опытный / Experienced").defineInRange("weight_experienced", 15, 1, 100);
        WEIGHT_UNBREAKABLE = BUILDER.comment("Неразрушимость / Unbreakable").defineInRange("weight_unbreakable", 5, 1, 100);
        WEIGHT_FILTER = BUILDER.comment("Фильтр / Filter").defineInRange("weight_filter", 10, 1, 100);
        WEIGHT_DRILL = BUILDER.comment("Бур / Drill").defineInRange("weight_drill", 12, 1, 100);
        WEIGHT_MEGA_DRILL = BUILDER.comment("Мега-бур / Mega Drill").defineInRange("weight_mega_drill", 3, 1, 100);
        WEIGHT_AUTO_SMELT = BUILDER.comment("Автоплавка / Auto Smelt").defineInRange("weight_auto_smelt", 8, 1, 100);
        WEIGHT_LUMBERJACK = BUILDER.comment("Дровосек / Lumberjack").defineInRange("weight_lumberjack", 10, 1, 100);
        WEIGHT_FARMER = BUILDER.comment("Фермер / Farmer").defineInRange("weight_farmer", 12, 1, 100);
        WEIGHT_SEEDING = BUILDER.comment("Посев / Seeding").defineInRange("weight_seeding", 12, 1, 100);
        WEIGHT_DELICATE = BUILDER.comment("Деликатный / Delicate").defineInRange("weight_delicate", 10, 1, 100);
        
        BUILDER.pop();
        
        BUILDER.push("debug");
        
        DEBUG_MODE = BUILDER
                .comment("Включить отладочное логирование / Enable debug logging")
                .define("debug_mode", false);
        
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }
}
