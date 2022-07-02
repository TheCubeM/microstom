package com.github.klainstom.microstom.commands;

import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseCommand extends Command {

    @Getter private final Permission permission;

    public BaseCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);
        this.permission = new Permission(name);
    }

    public void register() {
        MinecraftServer.getCommandManager().register(this);
    }
}
