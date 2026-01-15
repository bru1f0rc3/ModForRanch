package com.echo.enchants.network.packet;

import com.echo.enchants.block.entity.GrowthBeaconBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet для обновления настроек Growth Beacon с клиента на сервер.
 * Поддерживает базовые настройки и систему улучшений.
 */
public class GrowthBeaconUpdatePacket {
    
    private final BlockPos pos;
    private final int radius;
    private final boolean active;
    private final int speedLevel;
    private final int rangeLevel;
    
    public GrowthBeaconUpdatePacket(BlockPos pos, int radius, boolean active, int speedLevel, int rangeLevel) {
        this.pos = pos;
        this.radius = radius;
        this.active = active;
        this.speedLevel = speedLevel;
        this.rangeLevel = rangeLevel;
    }
    
    public GrowthBeaconUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.radius = buf.readInt();
        this.active = buf.readBoolean();
        this.speedLevel = buf.readInt();
        this.rangeLevel = buf.readInt();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(radius);
        buf.writeBoolean(active);
        buf.writeInt(speedLevel);
        buf.writeInt(rangeLevel);
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Server-side handling
            ServerPlayer player = context.getSender();
            if (player == null) return;
            
            // Validate player is close enough to the block
            if (!pos.closerToCenterThan(player.position(), 10.0)) return;
            
            BlockEntity be = player.level().getBlockEntity(pos);
            if (be instanceof GrowthBeaconBlockEntity growthBeacon) {
                growthBeacon.setSpeedLevel(speedLevel);
                growthBeacon.setRangeLevel(rangeLevel);
                growthBeacon.setRadius(radius);
                growthBeacon.setActive(active);
            }
        });
        return true;
    }
}
