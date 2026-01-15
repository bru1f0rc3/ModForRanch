package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Посев (Seeding) - Автоматически пересаживает культуры после сбора.
 * 
 * Применяется на: Мотыги и инструменты
 * Максимальный уровень: 4
 * Уровень влияет на радиус автопосева (1-4 блока)
 * Редкость: Необычный
 */
public class SeedingEnchantment extends Enchantment {
    
    public SeedingEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 4;
    }
    
    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 7;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 35;
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
     * Get the replanting radius for a given enchantment level.
     * @param level The enchantment level (1-4)
     * @return The radius in blocks
     */
    public static int getReplantRadius(int level) {
        return level;
    }
}
