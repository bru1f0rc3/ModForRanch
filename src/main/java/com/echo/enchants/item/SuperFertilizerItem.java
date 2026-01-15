package com.echo.enchants.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Супер-удобрение (Super Fertilizer) - Удобряет площадь 5x5.
 * 
 * Применение: ПКМ по растению или земле
 * Эффект: Применяет эффект костной муки ко всем растениям в радиусе 2 блоков (5x5)
 * 
 * Крафт: 4 костной муки + 1 изумруд в центре
 */
public class SuperFertilizerItem extends Item {
    
    private static final int RADIUS = 2; // 5x5 area
    
    public SuperFertilizerItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos centerPos = context.getClickedPos();
        ItemStack itemStack = context.getItemInHand();
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        ServerLevel serverLevel = (ServerLevel) level;
        boolean anyGrown = false;
        
        // Apply bone meal effect in 5x5 area
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                BlockPos pos = centerPos.offset(dx, 0, dz);
                
                // Also check one block above (for tall grass, etc.)
                if (tryGrowPlant(serverLevel, pos)) {
                    anyGrown = true;
                } else if (tryGrowPlant(serverLevel, pos.above())) {
                    anyGrown = true;
                }
            }
        }
        
        if (anyGrown) {
            // Play sound
            level.playSound(null, centerPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
            
            // Spawn particles in the area
            spawnFertilizerParticles(serverLevel, centerPos);
            
            // Consume item
            if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            
            return InteractionResult.CONSUME;
        }
        
        return InteractionResult.PASS;
    }
    
    private boolean tryGrowPlant(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        
        if (block instanceof BonemealableBlock bonemealable) {
            if (bonemealable.isValidBonemealTarget(level, pos, state, false)) {
                if (bonemealable.isBonemealSuccess(level, level.random, pos, state)) {
                    bonemealable.performBonemeal(level, level.random, pos, state);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void spawnFertilizerParticles(ServerLevel level, BlockPos center) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                
                // Spawn green particles
                level.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        5,
                        0.3, 0.3, 0.3,
                        0.0
                );
            }
        }
    }
}
