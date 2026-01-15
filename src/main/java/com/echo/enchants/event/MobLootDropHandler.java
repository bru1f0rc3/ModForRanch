package com.echo.enchants.event;

import com.echo.enchants.EchoEnchantsMod;
import com.echo.enchants.config.EchoEnchantsConfig;
import com.echo.enchants.util.EnchantedBookUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Event handler for mob loot drops.
 * Handles dropping enchanted books from hostile mobs.
 */
public class MobLootDropHandler {
    
    private final Random random = new Random();
    
    // Cache for enabled mobs ResourceLocations
    private Set<ResourceLocation> enabledMobsCache = null;
    private long lastCacheUpdate = 0;
    private static final long CACHE_DURATION = 60000; // 1 minute cache
    
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        try {
            Entity entity = event.getEntity();
            Entity killer = event.getSource().getEntity();
            Level level = entity.level();
            
            // Only run on server side
            if (level.isClientSide()) {
                return;
            }
            
            // Check if drops are enabled in existing chunks
            if (!EchoEnchantsConfig.ENABLE_IN_EXISTING_CHUNKS.get()) {
                return;
            }
            
            // Check if killer is a player (we want drops only from player kills)
            if (!(killer instanceof Player)) {
                return;
            }
            
            // Check if hostile only mode is enabled
            if (EchoEnchantsConfig.HOSTILE_ONLY.get()) {
                if (!(entity instanceof Monster)) {
                    return;
                }
            }
            
            // Check if this mob type is enabled
            if (!isEnabledMob(entity)) {
                return;
            }
            
            // Roll for drop chance (supports decimal percentages like 0.5%)
            double dropChance = EchoEnchantsConfig.DROP_CHANCE_HOSTILE.get();
            double roll = random.nextDouble() * 100.0; // 0.0 to 100.0
            
            if (EchoEnchantsConfig.DEBUG_MODE.get()) {
                EchoEnchantsMod.LOGGER.debug("EchoEnchants: Mob killed - {} | Roll: {:.2f} | Chance: {}%",
                        entity.getType().getDescriptionId(), roll, dropChance);
            }
            
            if (roll < dropChance) {
                // Determine how many books to drop
                int booksPerKill = EchoEnchantsConfig.BOOKS_PER_KILL.get();
                int booksToDrop = booksPerKill > 1 ? random.nextInt(booksPerKill) + 1 : 1;
                
                for (int i = 0; i < booksToDrop; i++) {
                    // Select random enchantment
                    Enchantment enchantment = EnchantedBookUtil.selectRandomEnchantment();
                    int enchLevel = EnchantedBookUtil.getRandomLevel(enchantment);
                    
                    // Create enchanted book
                    ItemStack book = EnchantedBookUtil.createEnchantedBook(enchantment, enchLevel);
                    
                    // Create item entity and add to drops
                    ItemEntity itemEntity = new ItemEntity(
                            level,
                            entity.getX(),
                            entity.getY() + 0.5,
                            entity.getZ(),
                            book
                    );
                    
                    // Add some random velocity
                    itemEntity.setDeltaMovement(
                            (random.nextDouble() - 0.5) * 0.3,
                            random.nextDouble() * 0.2 + 0.1,
                            (random.nextDouble() - 0.5) * 0.3
                    );
                    
                    event.getDrops().add(itemEntity);
                    
                    if (EchoEnchantsConfig.DEBUG_MODE.get()) {
                        EchoEnchantsMod.LOGGER.debug("EchoEnchants: Dropped {} level {} from {}",
                                enchantment.getDescriptionId(), enchLevel, entity.getType().getDescriptionId());
                    }
                }
                
                EchoEnchantsMod.LOGGER.info("EchoEnchants: {} dropped {} enchanted book(s)",
                        entity.getType().getDescriptionId(), booksToDrop);
            }
            
        } catch (Exception e) {
            EchoEnchantsMod.LOGGER.error("EchoEnchants: Error processing mob drops", e);
        }
    }
    
    /**
     * Check if the given entity type is in the enabled mobs list.
     * Uses caching for performance.
     * 
     * @param entity The entity to check
     * @return true if the mob type is enabled for drops
     */
    private boolean isEnabledMob(Entity entity) {
        // Update cache if needed
        long currentTime = System.currentTimeMillis();
        if (enabledMobsCache == null || currentTime - lastCacheUpdate > CACHE_DURATION) {
            updateMobCache();
        }
        
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return entityId != null && enabledMobsCache.contains(entityId);
    }
    
    /**
     * Update the cached set of enabled mob ResourceLocations.
     */
    @SuppressWarnings("deprecation")
    private void updateMobCache() {
        enabledMobsCache = new HashSet<>();
        List<? extends String> enabledMobs = EchoEnchantsConfig.ENABLED_MOBS.get();
        
        for (String mobId : enabledMobs) {
            try {
                ResourceLocation location = new ResourceLocation(mobId);
                enabledMobsCache.add(location);
            } catch (Exception e) {
                EchoEnchantsMod.LOGGER.warn("EchoEnchants: Invalid mob ID in config: {}", mobId);
            }
        }
        
        lastCacheUpdate = System.currentTimeMillis();
        
        if (EchoEnchantsConfig.DEBUG_MODE.get()) {
            EchoEnchantsMod.LOGGER.debug("EchoEnchants: Updated mob cache with {} mobs", enabledMobsCache.size());
        }
    }
}
