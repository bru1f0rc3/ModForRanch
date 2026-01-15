package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Мега-бур (Mega Drill) - Разрушает блоки в области 5x5x2.
 * 
 * Применяется на: Кирки
 * Максимальный уровень: 1
 * Разрушает: 5x5x2 (50 блоков)
 * Редкость: Очень редкий
 */
public class MegaDrillEnchantment extends Enchantment {
    
    public MegaDrillEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 1;
    }
    
    @Override
    public int getMinCost(int level) {
        return 30;
    }
    
    @Override
    public int getMaxCost(int level) {
        return 80;
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
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
    
    @Override
    protected boolean checkCompatibility(Enchantment other) {
        // Несовместимо с обычным Буром
        if (other instanceof DrillEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }
    
    /**
     * Get the radius for the mega drill effect.
     * @return The radius in blocks (2 for 5x5)
     */
    public static int getRadius() {
        return 2; // 5x5 area (2 blocks in each direction)
    }
    
    /**
     * Get the depth for the mega drill effect.
     * @return The depth in blocks
     */
    public static int getDepth() {
        return 2;
    }
}
