package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Опытный (Experienced) - Увеличивает получаемый опыт при добыче.
 * 
 * Применяется на: Все инструменты
 * Максимальный уровень: 3
 * Множитель опыта: 1.3 * уровень (30%, 60%, 90% бонус)
 * Редкость: Необычный
 */
public class ExperiencedEnchantment extends Enchantment {
    
    /**
     * Базовый множитель опыта за каждый уровень (30%)
     */
    public static final float XP_MULTIPLIER_PER_LEVEL = 0.3f;
    
    public ExperiencedEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 8;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 40;
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.isDamageableItem() || super.canEnchant(stack);
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
     * Calculate the XP multiplier for a given enchantment level.
     * @param level The enchantment level (1-3)
     * @return The XP multiplier (1.3, 1.6, 1.9)
     */
    public static float getXpMultiplier(int level) {
        return 1.0f + (level * XP_MULTIPLIER_PER_LEVEL);
    }
}
