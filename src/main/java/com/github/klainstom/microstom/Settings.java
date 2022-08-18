package com.github.klainstom.microstom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.*;

public class Settings {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private static final File settingsFile = new File("micro-settings.json");

    private static SettingsState currentSettings = null;

    public static void read() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(settingsFile));
            currentSettings = gson.fromJson(reader, SettingsState.class);
        } catch (FileNotFoundException e) {
            currentSettings = new SettingsState();
            try {
                write();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void write() throws IOException {
        String json = gson.toJson(currentSettings);
        Writer writer = new FileWriter(settingsFile);
        writer.write(json);
        writer.close();
    }

    private static class SettingsState {
        private final String SERVER_IP;
        private final int SERVER_PORT;

        private final RunMode MODE;
        private final String VELOCITY_SECRET;

        private final String DESCRIPTION;
        private final Number MAX_PLAYERS_VISUAL;
        private final Number MAX_PLAYERS;
        private final boolean HIDE_PLAYER_NAMES;

        private final Position SPAWN;

        // JVM arguments
        private final String TPS;
        private final String CHUNK_VIEW_DISTANCE;
        private final String ENTITY_VIEW_DISTANCE;
        private final Bool TERMINAL_DISABLED;

        private SettingsState() {
            this.SERVER_IP = "localhost";
            this.SERVER_PORT = 25565;

            this.MODE = RunMode.ONLINE;
            this.VELOCITY_SECRET = "";

            this.DESCRIPTION = "";
            this.MAX_PLAYERS_VISUAL = 0;
            this.MAX_PLAYERS = 1;
            this.HIDE_PLAYER_NAMES = false;

            this.SPAWN = new Position(0, 0, 0, 0, 0);

            this.TPS = null;
            this.CHUNK_VIEW_DISTANCE = null;
            this.ENTITY_VIEW_DISTANCE = null;
            this.TERMINAL_DISABLED = Bool.FALSE;
        }

    }

    public enum RunMode {
        OFFLINE("offline"),
        ONLINE("online"),
        BUNGEECORD("BungeeCord"),
        VELOCITY("Velocity");

        private final String name;

        RunMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private enum Bool {
        TRUE(true), FALSE(false);

        private final boolean value;

        Bool(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }
    }

    static class Position {
        @Getter private final double x;
        @Getter private final double y;
        @Getter private final double z;
        @Getter private final float yaw;
        @Getter private final float pitch;

        private Position(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    public static RunMode getMode() { return currentSettings.MODE; }

    public static String getServerIp() {
        return System.getProperty("server.ip", currentSettings.SERVER_IP);
    }
    public static int getServerPort() {
        int port = Integer.getInteger("server.port", currentSettings.SERVER_PORT);
        if (port < 1) return 25565;
        return port;
    }

    public static boolean hasVelocitySecret() {
        return !currentSettings.VELOCITY_SECRET.isBlank();
    }

    public static String getVelocitySecret() {
        return currentSettings.VELOCITY_SECRET;
    }

    public static String getTps() { return currentSettings.TPS; }
    public static String getChunkViewDistance() { return currentSettings.CHUNK_VIEW_DISTANCE; }
    public static String getEntityViewDistance() { return currentSettings.ENTITY_VIEW_DISTANCE; }
    public static boolean isTerminalDisabled() { return currentSettings.TERMINAL_DISABLED.getValue(); }
    public static String getDescription() { return currentSettings.DESCRIPTION;}
    public static int getMaxPlayers() {return currentSettings.MAX_PLAYERS.intValue();}
    public static int getMaxPlayersVisual() {return currentSettings.MAX_PLAYERS_VISUAL.intValue();}
    public static boolean hidePlayerList() { return currentSettings.HIDE_PLAYER_NAMES; }
    public static Position getSpawnPoint() { return currentSettings.SPAWN; }
}
