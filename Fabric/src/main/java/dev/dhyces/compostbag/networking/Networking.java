package dev.dhyces.compostbag.networking;

import dev.dhyces.compostbag.Common;
import dev.dhyces.compostbag.CompostBag;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class Networking {
    public static final PacketType<SyncMaxBonemealPacket> MAX_BONEMEAL = PacketType.create(Common.modLoc("config_sync"), SyncMaxBonemealPacket::read);

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register(Event.DEFAULT_PHASE, (handler, sender, server) ->
                sender.sendPacket(new SyncMaxBonemealPacket(CompostBag.MAX_BONEMEAL)));
    }
}
