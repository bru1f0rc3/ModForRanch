package com.echo.enchants.event;

import com.echo.enchants.EchoEnchantsMod;
import com.echo.enchants.enchantment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

/**
 * Handles all tool enchantment effects for EchoEnchants.
 */
public class ToolEnchantmentHandler {
    
    // Prevent recursive block breaking
    private static final Set<UUID> currentlyBreaking = new HashSet<>();
    
    // ==================== MAGNETISM ====================
    
    /**
     * Магнетизм - притягивает предметы к игроку после добычи блока.
     */
    @SubscribeEvent
    public void onBlockBreakMagnetism(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null || event.getLevel().isClientSide()) return;
        
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        
        int magnetismLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.MAGNETISM.get(), tool);
        
        if (magnetismLevel > 0) {
            // Schedule item collection for next tick
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                BlockPos pos = event.getPos();
                serverLevel.getServer().execute(() -> {
                    collectNearbyItems(serverLevel, player, pos, 8.0);
                });
            }
        }
    }
    
    private void collectNearbyItems(ServerLevel level, Player player, BlockPos pos, double radius) {
        AABB area = new AABB(pos).inflate(radius);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        
        for (ItemEntity item : items) {
            if (!item.isRemoved() && item.isAlive()) {
                // Teleport item to player
                item.setPos(player.getX(), player.getY(), player.getZ());
                item.setNoPickUpDelay();
                
                // Play pickup sound
                level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, 
                        SoundSource.PLAYERS, 0.2f, 
                        ((level.random.nextFloat() - level.random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            }
        }
    }
    
    // ==================== EXPERIENCED ====================
    
    /**
     * Опытный - увеличивает получаемый опыт.
     */
    @SubscribeEvent
    public void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() == null) return;
        
        Player player = event.getAttackingPlayer();
        ItemStack tool = player.getMainHandItem();
        
        int experiencedLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.EXPERIENCED.get(), tool);
        
        if (experiencedLevel > 0) {
            int originalXp = event.getDroppedExperience();
            float multiplier = ExperiencedEnchantment.getXpMultiplier(experiencedLevel);
            int newXp = (int) (originalXp * multiplier);
            event.setDroppedExperience(newXp);
            
            EchoEnchantsMod.LOGGER.debug("Experienced: {} -> {} XP (x{})", 
                    originalXp, newXp, multiplier);
        }
    }
    
    // ==================== UNBREAKABLE ====================
    
    /**
     * Неразрушимость - предотвращает использование при низкой прочности.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreakUnbreakable(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null) return;
        
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        
        int unbreakableLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.UNBREAKABLE.get(), tool);
        
        if (unbreakableLevel > 0 && UnbreakableEnchantment.isAtMinDurability(tool)) {
            event.setCanceled(true);
            
            // Notify player
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§c§l⚠ §7Инструмент требует ремонта! §c(Неразрушимость)"),
                        true
                );
            }
        }
    }
    
    // ==================== DRILL / MEGA DRILL ====================
    
    /**
     * Бур и Мега-Бур - добыча области блоков.
     */
    @SubscribeEvent
    public void onBlockBreakDrill(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null || event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        
        Player player = event.getPlayer();
        UUID playerId = player.getUUID();
        
        // Prevent recursive calls
        if (currentlyBreaking.contains(playerId)) return;
        
        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof PickaxeItem)) return;
        
        int drillLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.DRILL.get(), tool);
        int megaDrillLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.MEGA_DRILL.get(), tool);
        
        if (drillLevel <= 0 && megaDrillLevel <= 0) return;
        
        int radius, depth;
        if (megaDrillLevel > 0) {
            radius = MegaDrillEnchantment.getRadius();
            depth = MegaDrillEnchantment.getDepth();
        } else {
            radius = DrillEnchantment.getRadius(drillLevel);
            depth = DrillEnchantment.getDepth(drillLevel);
        }
        
        BlockPos center = event.getPos();
        Direction facing = player.getDirection();
        
        try {
            currentlyBreaking.add(playerId);
            breakBlocksInArea(serverLevel, player, tool, center, radius, depth, facing);
        } finally {
            currentlyBreaking.remove(playerId);
        }
    }
    
    private void breakBlocksInArea(ServerLevel level, Player player, ItemStack tool, 
                                    BlockPos center, int radius, int depth, Direction facing) {
        List<BlockPos> toBreak = new ArrayList<>();
        
        // Определяем направление взгляда игрока
        Direction.Axis axis = facing.getAxis();
        boolean lookingVertical = axis == Direction.Axis.Y;
        
        // Если игрок смотрит вверх/вниз - копаем горизонтальную площадь
        // Если смотрит в сторону - копаем вертикальную стену на уровне игрока
        
        for (int d = 0; d < depth; d++) {
            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    BlockPos pos;
                    if (lookingVertical) {
                        // Смотрим вверх/вниз - копаем XZ площадь
                        int yOffset = facing == Direction.UP ? d : -d;
                        pos = center.offset(i, yOffset, j);
                    } else if (axis == Direction.Axis.X) {
                        // Смотрим на восток/запад - копаем YZ стену
                        int xOffset = facing == Direction.EAST ? d : -d;
                        pos = center.offset(xOffset, i, j);
                    } else {
                        // Смотрим на север/юг - копаем XY стену
                        int zOffset = facing == Direction.SOUTH ? d : -d;
                        pos = center.offset(i, j, zOffset);
                    }
                    
                    if (!pos.equals(center)) {
                        toBreak.add(pos);
                    }
                }
            }
        }
        
        for (BlockPos pos : toBreak) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir() || state.getDestroySpeed(level, pos) < 0) continue;
            if (!tool.isCorrectToolForDrops(state)) continue;
            
            // Break the block
            level.destroyBlock(pos, true, player);
            
            // Damage tool
            if (!player.getAbilities().instabuild) {
                tool.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
            
            if (tool.isEmpty()) break;
        }
    }
    
    // ==================== AUTO SMELT ====================
    
    /**
     * Автоплавка - заменяет руды на слитки.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onBlockBreakAutoSmelt(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null || event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        
        int autoSmeltLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.AUTO_SMELT.get(), tool);
        
        if (autoSmeltLevel <= 0) return;
        
        // Check for Silk Touch (incompatible)
        if (EnchantmentHelper.getItemEnchantmentLevel(
                net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH, tool) > 0) {
            return;
        }
        
        BlockPos pos = event.getPos();
        BlockState state = serverLevel.getBlockState(pos);
        Block block = state.getBlock();
        
        ItemStack smeltedResult = getSmeltedResult(block);
        if (!smeltedResult.isEmpty()) {
            event.setCanceled(true);
            
            // Calculate fortune bonus
            int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE, tool);
            int count = 1 + serverLevel.random.nextInt(fortuneLevel + 1);
            
            // Remove the block
            serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            
            // Drop smelted items
            ItemStack drop = smeltedResult.copy();
            drop.setCount(count);
            Block.popResource(serverLevel, pos, drop);
            
            // Drop experience
            int xp = getSmeltingExperience(block);
            if (xp > 0) {
                ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), xp);
            }
            
            // Damage tool
            if (!player.getAbilities().instabuild) {
                tool.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
        }
    }
    
    private ItemStack getSmeltedResult(Block block) {
        // Iron ore -> Iron ingot
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            return new ItemStack(Items.IRON_INGOT);
        }
        // Gold ore -> Gold ingot
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            return new ItemStack(Items.GOLD_INGOT);
        }
        // Copper ore -> Copper ingot
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
            return new ItemStack(Items.COPPER_INGOT);
        }
        // Ancient debris -> Netherite scrap
        if (block == Blocks.ANCIENT_DEBRIS) {
            return new ItemStack(Items.NETHERITE_SCRAP);
        }
        // Cobblestone -> Stone
        if (block == Blocks.COBBLESTONE) {
            return new ItemStack(Items.STONE);
        }
        // Sand -> Glass
        if (block == Blocks.SAND || block == Blocks.RED_SAND) {
            return new ItemStack(Items.GLASS);
        }
        // Raw ores don't drop from blocks, but handled for consistency
        return ItemStack.EMPTY;
    }
    
    private int getSmeltingExperience(Block block) {
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) return 1;
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) return 1;
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) return 1;
        if (block == Blocks.ANCIENT_DEBRIS) return 2;
        return 0;
    }
    
    // ==================== DELICATE ====================
    
    /**
     * Деликатный - защита невыросших растений.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreakDelicate(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null) return;
        
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        
        int delicateLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.DELICATE.get(), tool);
        
        if (delicateLevel <= 0) return;
        
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        
        // Check if it's a crop
        if (block instanceof CropBlock cropBlock) {
            if (!cropBlock.isMaxAge(state)) {
                // Level 2+ cancels breaking
                if (DelicateEnchantment.shouldCancelImmatureBreak(delicateLevel)) {
                    event.setCanceled(true);
                    
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("§e§l⚠ §7Растение ещё не выросло! §e(Деликатный)"),
                                true
                        );
                    }
                } else {
                    // Level 1 just warns
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("§e⚠ §7Внимание: растение не созрело!"),
                                true
                        );
                    }
                }
            }
        }
    }
    
    // ==================== FARMER ====================
    
    /**
     * Фермер - увеличение урожая реализуется через лут-таблицы или отдельный обработчик.
     * Для простоты добавим дополнительные дропы при сборе урожая.
     */
    @SubscribeEvent
    public void onHarvestCropFarmer(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null || event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        
        int farmerLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.FARMER.get(), tool);
        
        if (farmerLevel <= 0) return;
        
        BlockPos pos = event.getPos();
        BlockState state = serverLevel.getBlockState(pos);
        Block block = state.getBlock();
        
        // Check if it's a mature crop
        if (block instanceof CropBlock cropBlock && cropBlock.isMaxAge(state)) {
            // Calculate bonus drops
            float multiplier = FarmerEnchantment.getHarvestMultiplier(farmerLevel);
            int bonusDrops = serverLevel.random.nextFloat() < (multiplier - 1) ? 1 : 0;
            
            if (bonusDrops > 0) {
                // Get the crop's drop item
                ItemStack bonusItem = getCropBonusDrop(block);
                if (!bonusItem.isEmpty()) {
                    bonusItem.setCount(bonusDrops);
                    Block.popResource(serverLevel, pos, bonusItem);
                }
            }
        }
    }
    
    private ItemStack getCropBonusDrop(Block block) {
        if (block == Blocks.WHEAT) return new ItemStack(Items.WHEAT);
        if (block == Blocks.CARROTS) return new ItemStack(Items.CARROT);
        if (block == Blocks.POTATOES) return new ItemStack(Items.POTATO);
        if (block == Blocks.BEETROOTS) return new ItemStack(Items.BEETROOT);
        return ItemStack.EMPTY;
    }
    
    // ==================== SEEDING ====================
    
    /**
     * Посев - автоматическая пересадка культур.
     * Забирает 1 семя из дропа для пересадки, остальное падает игроку.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHarvestCropSeeding(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) return;
        if (event.getPlayer() == null || event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        
        int seedingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.SEEDING.get(), tool);
        
        if (seedingLevel <= 0) return;
        
        BlockPos pos = event.getPos();
        BlockState state = serverLevel.getBlockState(pos);
        Block block = state.getBlock();
        
        // Check if it's a mature crop
        if (block instanceof CropBlock cropBlock && cropBlock.isMaxAge(state)) {
            Item seedItem = getSeedForCrop(block);
            if (seedItem == null) return;
            
            // Schedule replanting and seed reduction for next tick
            serverLevel.getServer().execute(() -> {
                replantCropFromDrop(serverLevel, player, pos, block, seedItem);
            });
        }
    }
    
    private void replantCropFromDrop(ServerLevel level, Player player, BlockPos pos, Block originalBlock, Item seedItem) {
        // Check if farmland is still there
        BlockState below = level.getBlockState(pos.below());
        if (!(below.getBlock() instanceof FarmBlock)) return;
        
        // Check if position is now air
        if (!level.getBlockState(pos).isAir()) return;
        
        // Find seed items on the ground near the harvested position
        AABB area = new AABB(pos).inflate(2.0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        
        ItemEntity seedEntity = null;
        for (ItemEntity item : items) {
            if (!item.isRemoved() && item.getItem().is(seedItem)) {
                seedEntity = item;
                break;
            }
        }
        
        // If no seeds found on ground, check inventory
        if (seedEntity != null) {
            // Take 1 seed from the dropped pile
            ItemStack seedStack = seedEntity.getItem();
            if (seedStack.getCount() > 1) {
                seedStack.shrink(1);
            } else {
                seedEntity.discard();
            }
        } else {
            // No seeds on ground - skip replanting
            return;
        }
        
        // Plant the crop
        if (originalBlock instanceof CropBlock cropBlock) {
            level.setBlock(pos, cropBlock.defaultBlockState(), 3);
            
            // Play planting sound
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }
    
    private Item getSeedForCrop(Block block) {
        if (block == Blocks.WHEAT) return Items.WHEAT_SEEDS;
        if (block == Blocks.CARROTS) return Items.CARROT;
        if (block == Blocks.POTATOES) return Items.POTATO;
        if (block == Blocks.BEETROOTS) return Items.BEETROOT_SEEDS;
        return null;
    }
}
