package com.echo.enchants.network;

import com.echo.enchants.EchoEnchantsMod;
import com.echo.enchants.network.packet.GrowthBeaconUpdatePacket;
import com.echo.enchants.network.packet.GrowthBeaconUpgradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Network handler for client-server communication.
 */
public class ModNetworking {
    
    private static SimpleChannel INSTANCE;
    
    private static int packetId = 0;
    
    private static int id() {
        return packetId++;
    }
    
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(EchoEnchantsMod.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        
        INSTANCE = net;
        
        // Register packets
        net.messageBuilder(GrowthBeaconUpdatePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(GrowthBeaconUpdatePacket::new)
                .encoder(GrowthBeaconUpdatePacket::toBytes)
                .consumerMainThread(GrowthBeaconUpdatePacket::handle)
                .add();
        
        // Packet for upgrades (requires Super Fertilizer)
        net.messageBuilder(GrowthBeaconUpgradePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(GrowthBeaconUpgradePacket::new)
                .encoder(GrowthBeaconUpgradePacket::toBytes)
                .consumerMainThread(GrowthBeaconUpgradePacket::handle)
                .add();
        
        EchoEnchantsMod.LOGGER.info("EchoEnchants: Network packets registered");
    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    public static <MSG> void sendToAllClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
