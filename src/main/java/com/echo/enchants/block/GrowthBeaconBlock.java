package com.echo.enchants.block;

import com.echo.enchants.block.entity.GrowthBeaconBlockEntity;
import com.echo.enchants.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Маяк роста (Growth Beacon) - Блок который ускоряет рост растений вокруг себя.
 * 
 * Радиус действия: настраиваемый (1-10 блоков)
 * Эффект: Каждые 2 секунды применяет эффект костной муки к случайным растениям
 * ПКМ - открывает GUI для настройки
 * 
 * Крафт: 4 изумруда + 4 костной муки + 1 золотой блок в центре
 */
public class GrowthBeaconBlock extends BaseEntityBlock {
    
    // Slightly smaller than full block for aesthetic
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 14, 14);
    
    public GrowthBeaconBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof GrowthBeaconBlockEntity growthBeacon) {
                NetworkHooks.openScreen((ServerPlayer) player, growthBeacon, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GrowthBeaconBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.GROWTH_BEACON.get(), GrowthBeaconBlockEntity::serverTick);
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // Check if beacon is active
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GrowthBeaconBlockEntity growthBeacon && !growthBeacon.isActive()) {
            return; // No particles if disabled
        }
        
        // Spawn particles on client side
        if (random.nextInt(3) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double y = pos.getY() + 0.8 + random.nextDouble() * 0.3;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0.05, 0);
        }
        
        // Occasional enchantment particle going up
        if (random.nextInt(10) == 0) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            
            level.addParticle(ParticleTypes.ENCHANT, x, y, z, 
                    (random.nextDouble() - 0.5) * 2, 
                    random.nextDouble() + 0.5, 
                    (random.nextDouble() - 0.5) * 2);
        }
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§a§lМаяк Роста"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Ускоряет рост растений"));
        tooltip.add(Component.literal("§7в настраиваемом радиусе."));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§eПКМ §7- открыть настройки"));
        tooltip.add(Component.literal("§8Поставьте рядом с грядками"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
