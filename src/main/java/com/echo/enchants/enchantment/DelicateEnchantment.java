package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Деликатный (Delicate) - Защищает невыросшие растения от разрушения.
 * 
 * Применяется на: Мотыги
 * Максимальный уровень: 3
 * Уровень I: Предупреждение о невыросших растениях
 * Уровень II: Отмена разрушения невыросших растений
 * Уровень III: Полная защита + подсветка выросших
 * Редкость: Необычный
 */
public class DelicateEnchantment extends Enchantment {
    
    public DelicateEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    @Override
    public int getMinCost(int level) {
        return 12 + (level - 1) * 8;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof HoeItem;
    }
    
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
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
     * Check if this level should cancel breaking of immature crops.
     * @param level The enchantment level (1-3)
     * @return true if breaking should be cancelled for immature crops
     */
    public static boolean shouldCancelImmatureBreak(int level) {
        return level >= 2;
    }
    
    /**
     * Check if this level should show mature crop indicators.
     * @param level The enchantment level (1-3)
     * @return true if mature crops should be highlighted
     */
    public static boolean shouldShowMatureIndicator(int level) {
        return level >= 3;
    }
}
