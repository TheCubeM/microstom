package com.github.klainstom.microstom.commands;

import lombok.Getter;

public enum Commands {
    SHUTDOWN(new ShutdownCommand()),
    RESTART(new RestartCommand()),
    GAMEMODE(new GamemodeCommand()),
    TIME(new TimeCommand());

    @Getter private final BaseCommand command;
    Commands(BaseCommand command) {
        this.command = command;
    }
}
