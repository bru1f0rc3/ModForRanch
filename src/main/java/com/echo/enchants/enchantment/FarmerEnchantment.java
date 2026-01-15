package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Фермер (Farmer) - Увеличивает урожай при сборе.
 * 
 * Применяется на: Мотыги и инструменты
 * Максимальный уровень: 4
 * Бонус урожая: +20% за уровень (до +80% на 4 уровне)
 * Редкость: Необычный
 */
public class FarmerEnchantment extends Enchantment {
    
    /**
     * Бонус урожая за каждый уровень (20%)
     */
    public static final float HARVEST_BONUS_PER_LEVEL = 0.20f;
    
    public FarmerEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 4;
    }
    
    @Override
    public int getMinCost(int level) {
        return 8 + (level - 1) * 6;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof HoeItem || super.canEnchant(stack);
    }
    
    @Override
    public boolean isTradeable() {
        return false;
    }
    
    @Override
    public boolean isDiscoverable() {
        return false;
    }
    
    @Override
    public boolean isTreasureOnly() {
        return true;
    }
    
    /**
     * Get the harvest multiplier for a given enchantment level.
     * @param level The enchantment level (1-4)
     * @return The harvest multiplier (1.2, 1.4, 1.6, 1.8)
     */
    public static float getHarvestMultiplier(int level) {
        return 1.0f + (level * HARVEST_BONUS_PER_LEVEL);
    }
}
