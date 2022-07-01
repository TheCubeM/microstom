package com.github.klainstom.microstom.commands;

import com.github.klainstom.microstom.Settings;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.entity.Player;

import java.io.IOException;

public class RestartCommand extends BaseCommand {
    public RestartCommand() {
        super("restart");
        setCondition(((sender, commandString) -> (sender instanceof ServerSender)
                || (sender instanceof ConsoleSender)
                || Settings.isAllowPlayerRestart()
                || sender instanceof Player p && (p.getPermissionLevel() < 4 || p.hasPermission(getPermission()))
        ));
        addSyntax((sender, context) -> {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    new ProcessBuilder("./start.sh").start();
                    MinecraftServer.LOGGER.info("Start new server.");
                } catch (IOException e) {
                    if (!(sender instanceof ConsoleSender)) sender.sendMessage("Could not restart server.");
                    LOGGER.error("Could not restart server.", e);
                }
            }, "RestartHook"));
            MinecraftServer.stopCleanly();
        });
    }
}
