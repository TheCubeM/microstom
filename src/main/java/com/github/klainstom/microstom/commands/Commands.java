package com.github.klainstom.microstom.commands;

import lombok.Getter;
import net.minestom.server.command.builder.Command;

public enum Commands {
    SHUTDOWN(new ShutdownCommand()),
    RESTART(new RestartCommand()),
    GAMEMODE(new GamemodeCommand());

    @Getter private final Command command;
    Commands(Command command) {
        this.command = command;
    }
}
