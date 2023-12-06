package dev.dhyces.compostbag.networking;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public record SyncMaxBonemealPacket(int maxBonemeal) implements FabricPacket {
    public static SyncMaxBonemealPacket read(FriendlyByteBuf buf) {
        return new SyncMaxBonemealPacket(buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(maxBonemeal);
    }

    @Override
    public PacketType<?> getType() {
        return Networking.MAX_BONEMEAL;
    }
}
