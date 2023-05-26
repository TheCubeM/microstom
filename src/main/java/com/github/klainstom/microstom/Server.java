package com.github.klainstom.microstom;

import com.github.klainstom.microstom.commands.Commands;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.ping.ResponseData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Objects;

public class Server {
    public static final String VERSION = "&version";
    public static final String MINESTOM_VERSION = "&minestomVersion";
    private static final String START_SCRIPT_FILENAME = "start.sh";
    @Getter private static InstanceContainer spawningInstanceContainer;
    private static String cachedFavicon;

    public static void main(String[] args) throws IOException {
        Settings.read();
        if (Settings.getTps() != null)
            System.setProperty("minestom.tps", Settings.getTps());
        if (Settings.getChunkViewDistance() != null)
            System.setProperty("minestom.chunk-view-distance", Settings.getChunkViewDistance());
        if (Settings.getEntityViewDistance() != null)
            System.setProperty("minestom.entity-view-distance", Settings.getEntityViewDistance());
        if (Settings.isTerminalDisabled())
            System.setProperty("minestom.terminal.disabled", "");
        MinecraftServer.LOGGER.info("====== VERSIONS ======");
        MinecraftServer.LOGGER.info("Java: " + Runtime.version());
        MinecraftServer.LOGGER.info("&Name: " + VERSION);
        MinecraftServer.LOGGER.info("Minestom: " + MINESTOM_VERSION);
        MinecraftServer.LOGGER.info("Supported protocol: %d (%s)".formatted(MinecraftServer.PROTOCOL_VERSION, MinecraftServer.VERSION_NAME));
        MinecraftServer.LOGGER.info("======================");

        if (args.length > 0 && args[0].equalsIgnoreCase("-v")) System.exit(0);

        File startScriptFile = new File(START_SCRIPT_FILENAME);
        if (startScriptFile.isDirectory()) MinecraftServer.LOGGER.warn("Can't create startup script!");
        if (!startScriptFile.isFile()) {
            MinecraftServer.LOGGER.info("Creating startup script");
            Files.copy(
                    Objects.requireNonNull(Server.class.getClassLoader().getResourceAsStream(START_SCRIPT_FILENAME)),
                    startScriptFile.toPath());
            Runtime.getRuntime().exec("chmod u+x start.sh");
        }

        // Actually start server
        MinecraftServer server = MinecraftServer.init();

        // register commands so they can actually be used
        for(Commands command : Commands.values()) {
            command.getCommand().register();
        }

        // load instance from ./world
        spawningInstanceContainer = MinecraftServer.getInstanceManager()
                .createInstanceContainer();

        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, event -> {
            int onlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayers().size();
            int maxPlayers = Settings.getMaxPlayers();
            if(Settings.getMaxPlayers() > -1 &&
                    onlinePlayers >= maxPlayers) {
                event.getPlayer().kick(String.format("The server is full! (%d/%d)",onlinePlayers,maxPlayers));
            }
            event.setSpawningInstance(spawningInstanceContainer);
            Settings.Position pos = Settings.getSpawnPoint();
            event.getPlayer().setRespawnPoint(new Pos(pos.getX(),pos.getY(),pos.getZ(),pos.getYaw(), pos.getPitch()));
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            if (MinecraftServer.getInstanceManager().getInstances().isEmpty())
                event.getPlayer().kick(Component.text("There is no instance available!", NamedTextColor.RED));
        });

        // Set Server List information

        // cache favicon image displayed in server list, needs to be base64 format
        try {
            BufferedImage image = ImageIO.read(new File("./server-icon.png")); // Use vanilla file name
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            cachedFavicon = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            outputStream.close();
        } catch (IOException e) {
            cachedFavicon = "";
        }

        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> {
            int onlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayers().size();
            ResponseData responseData = event.getResponseData();
            responseData.setOnline(onlinePlayers);
            responseData.setMaxPlayer(Settings.getMaxPlayersVisual());
            responseData.setPlayersHidden(Settings.hidePlayerList());
            responseData.setDescription(LegacyComponentSerializer.legacyAmpersand().deserialize(Settings.getDescription()));
            responseData.setProtocol(760);
            responseData.setVersion("The Cube");
            if(!cachedFavicon.isEmpty()) {
                responseData.setFavicon("data:image/png;base64,"+cachedFavicon);
            }
        });

        switch (Settings.getMode()) {
            case OFFLINE:
                break;
            case ONLINE:
                MojangAuth.init();
                break;
            case BUNGEECORD:
                BungeeCordProxy.enable();
                break;
            case VELOCITY:
                if (!Settings.hasVelocitySecret())
                    throw new IllegalArgumentException("The velocity secret is mandatory.");
                VelocityProxy.enable(Settings.getVelocitySecret());
        }

        MinecraftServer.LOGGER.info("Running in " + Settings.getMode() + " mode.");
        MinecraftServer.LOGGER.info("Listening on " + Settings.getServerIp() + ":" + Settings.getServerPort());

        server.start(Settings.getServerIp(), Settings.getServerPort());
    }
}