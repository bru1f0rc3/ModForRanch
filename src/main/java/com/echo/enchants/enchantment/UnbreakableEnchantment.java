package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Неразрушимость (Unbreakable) - Защищает инструмент от поломки.
 * Инструмент прекращает работать при 10% прочности вместо полного разрушения.
 * 
 * Применяется на: Все инструменты
 * Максимальный уровень: 1
 * Минимальная прочность: 10%
 * Редкость: Очень редкий
 */
public class UnbreakableEnchantment extends Enchantment {
    
    /**
     * Минимальный процент прочности, при котором инструмент прекращает работать
     */
    public static final float MIN_DURABILITY_PERCENT = 0.10f;
    
    public UnbreakableEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 1;
    }
    
    @Override
    public int getMinCost(int level) {
        return 25;
    }
    
    @Override
    public int getMaxCost(int level) {
        return 75;
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.isDamageableItem();
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
     * Check if the item should stop working due to low durability.
     * @param stack The item stack to check
     * @return true if the item is at or below the minimum durability threshold
     */
    public static boolean isAtMinDurability(ItemStack stack) {
        if (!stack.isDamageableItem()) return false;
        int maxDamage = stack.getMaxDamage();
        int currentDamage = stack.getDamageValue();
        int remainingDurability = maxDamage - currentDamage;
        float durabilityPercent = (float) remainingDurability / maxDamage;
        return durabilityPercent <= MIN_DURABILITY_PERCENT;
    }
}
