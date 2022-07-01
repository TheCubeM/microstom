package com.github.klainstom.microstom.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.entity.Player;

public class ShutdownCommand extends BaseCommand {
    public ShutdownCommand() {
        super("shutdown", "stop");
        setCondition(((sender, commandString) -> (sender instanceof ServerSender)
                || (sender instanceof ConsoleSender)
                || sender instanceof Player p && (p.getPermissionLevel() < 4 || p.hasPermission(getPermission()))
        ));
        addSyntax(((sender, context) -> MinecraftServer.stopCleanly()));
    }
}
