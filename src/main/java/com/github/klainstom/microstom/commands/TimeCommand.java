package com.github.klainstom.microstom.commands;

import com.github.klainstom.microstom.Server;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentLong;
import net.minestom.server.entity.Player;

public class TimeCommand extends BaseCommand {
    public TimeCommand() {
        super("time");
        ArgumentLong time = ArgumentType.Long("time");
        ArgumentLiteral rate = ArgumentType.Literal("rate");
        ArgumentLiteral set = ArgumentType.Literal("set");

        setDefaultExecutor((sender, context) -> {
            String commandName = context.getCommandName();

            sender.sendMessage(Component.text("Usage: /" + commandName + " rate|set <Long>", NamedTextColor.RED), MessageType.SYSTEM);
        });
        // time set <value>
        addSyntax((sender, context) -> {
            if (sender instanceof Player p && !p.hasPermission(getPermission())) {
                sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
                return;
            }
            Server.getSpawningInstanceContainer()
                    .setTime(context.get(time));
            sender.sendMessage(Component.text("Time successfully set to " + context.get(time)), MessageType.SYSTEM);
        },set,time);
        // time rate <value>
        addSyntax((sender, context) -> {
            if (sender instanceof Player p && !p.hasPermission(getPermission())) {
                sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
                return;
            }
            Server.getSpawningInstanceContainer()
                    .setTimeRate(context.get(time).intValue());
            sender.sendMessage(Component.text("Time rate successfully set to " + context.get(time)), MessageType.SYSTEM);
        },rate,time);
    }
}
