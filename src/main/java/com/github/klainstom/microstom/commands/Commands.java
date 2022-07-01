package com.github.klainstom.microstom.commands;

import net.minestom.server.command.builder.Command;

import java.util.LinkedHashMap;
import java.util.Map;

public class Commands {
    public static final Map<String,Command> commands = new LinkedHashMap<>();
    static {
        commands.put("SHUTDOWN", new ShutdownCommand());
        commands.put("RESTART", new RestartCommand());
    }
}
