package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Магнетизм (Magnetism) - Автоматически собирает выпавший лут в инвентарь игрока.
 * 
 * Применяется на: Все инструменты
 * Максимальный уровень: 1
 * Редкость: Редкий
 */
public class MagnetismEnchantment extends Enchantment {
    
    public MagnetismEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 1;
    }
    
    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 9;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 50;
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
}
