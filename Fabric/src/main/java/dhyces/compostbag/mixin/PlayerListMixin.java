package dhyces.compostbag.mixin;

import dhyces.compostbag.Common;
import dhyces.compostbag.platform.Services;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Shadow public abstract void broadcastAll(Packet<?> $$0);

    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void compostbag$reloadResources(CallbackInfo info) {
        var packet = PacketByteBufs.create();
        packet.writeInt(Services.PLATFORM.maxBonemeal().get());
        broadcastAll(ServerPlayNetworking.createS2CPacket(Common.modLoc("config_sync"), packet));
    }
}
