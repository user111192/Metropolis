package team.dovecotmc.metropolis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;
import team.dovecotmc.metropolis.abstractinterface.util.MALocalizationUtil;

public class MTRTicketSystemCommands {

    private static final SimpleCommandExceptionType ERROR_NO_PLAYER = new SimpleCommandExceptionType(MALocalizationUtil.translatableText("message.metropolis.ticket.no_player"));
    public static void register (CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("ticket")
                        // .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("get")
                                .executes(context -> getSelfTicketInfo(context.getSource()))
                                .then(Commands.argument("player", EntityArgument.player()).requires(source -> source.hasPermission(2)).executes(context -> getTicketInfo(context.getSource(), EntityArgument.getPlayer(context, "player"))))
                                .then(Commands.literal("me").executes(context -> getSelfTicketInfo(context.getSource())))
                        )
                        .then(Commands.literal("enter").requires(source -> source.hasPermission(2)).then(Commands.argument("zone", IntegerArgumentType.integer()).executes(context -> enterStation(context.getSource(), IntegerArgumentType.getInteger(context, "zone"))).then(Commands.argument("player", EntityArgument.player()).executes(context -> enterStation(context.getSource(), IntegerArgumentType.getInteger(context, "zone"), EntityArgument.getPlayer(context, "player"))))))
                        .then(Commands.literal("exit").requires(source -> source.hasPermission(2)).then(Commands.argument("zone", IntegerArgumentType.integer()).executes(context -> exitStation(context.getSource(), IntegerArgumentType.getInteger(context, "zone"))).then(Commands.argument("player", EntityArgument.player()).executes(context -> exitStation(context.getSource(), IntegerArgumentType.getInteger(context, "zone"), EntityArgument.getPlayer(context, "player"))))))
        );
    }

    static class EntryStatus {
        private int entry_zone;
        private boolean entered;
        EntryStatus(int entry_zone) {
            this.entry_zone = entry_zone;
            this.entered = true;
        }
        EntryStatus(int entry_zone, boolean entered) {
            this.entry_zone = entry_zone;
            this.entered = entered;
        }
        public int getEntryZone() {
            return entry_zone;
        }
        public boolean isEntered() {return entered;}
    }
    private static EntryStatus decodeEntryZone(int entry_zone_original) {
        if (entry_zone_original > 0) return new EntryStatus(entry_zone_original-1);
        else if (entry_zone_original < 0) return new EntryStatus(entry_zone_original);
        else {
            // entry_zone_original == 0
            return new EntryStatus(0, false);
        }
    }

    private static int encodeZone(int zone) {
        if (zone >= 0) return zone+1;
        else return zone;
    }

    private static int getTicketInfo(CommandSourceStack source, ServerPlayer serverplayer) {
        // not tested
        Scoreboard boards = source.getServer().getScoreboard();
        String playername = serverplayer.getScoreboardName();
        int balance = boards.getOrCreatePlayerScore(playername, boards.getObjective("mtr_balance")).getScore();
        int entry_zone_original = boards.getOrCreatePlayerScore(playername, boards.getObjective("mtr_entry_zone")).getScore();
        EntryStatus entry_zone = decodeEntryZone(entry_zone_original);
        if (entry_zone.isEntered()) {
            source.sendSuccess(MALocalizationUtil.translatableText("message.metropolis.ticket.get_entered", playername, balance, entry_zone.getEntryZone()),true);
        } else {
            source.sendSuccess(MALocalizationUtil.translatableText("message.metropolis.ticket.get_not_entered", playername, balance),true);
        }
        return 0;
    }

    private static int getSelfTicketInfo(CommandSourceStack source) throws CommandSyntaxException {
        if (source.getPlayer() == null) throw ERROR_NO_PLAYER.create();
        return getTicketInfo(source, source.getPlayer());
    }

    enum FareEvasionHandling {
        FAIL,
        SUCCESS,
        SUCCESS_WITH_FINE
    }

    private static final FareEvasionHandling DEFAULT_FARE_EVASION_HANDLING = FareEvasionHandling.FAIL;

    private static int enterStation(CommandSourceStack source, int zone, @NotNull ServerPlayer serverplayer, FareEvasionHandling fare_evasion_handling) {
        Scoreboard boards = source.getServer().getScoreboard();
        String playername = serverplayer.getScoreboardName();
        int balance = boards.getOrCreatePlayerScore(playername, boards.getObjective("mtr_balance")).getScore();
        int entry_zone_original = boards.getOrCreatePlayerScore(playername, boards.getObjective("mtr_entry_zone")).getScore();
        EntryStatus entry_zone = decodeEntryZone(entry_zone_original);

        if (balance < 0) {
            source.sendFailure(MALocalizationUtil.translatableText("message.metropolis.ticket.insufficient_balance"));
            return 0;
        }

        if (entry_zone.isEntered()) {
            if (fare_evasion_handling == FareEvasionHandling.FAIL) {
                source.sendFailure(MALocalizationUtil.translatableText("message.metropolis.ticket.already_entered"));
                return 0;
            } else if (fare_evasion_handling == FareEvasionHandling.SUCCESS_WITH_FINE) {
                boards.getOrCreatePlayerScore(playername, boards.getObjective("mtr_balance")).add(-500);
            }
        }
        boards.getOrCreatePlayerScore(playername, boards.getObjective("mtr_entry_zone")).setScore(encodeZone(zone));

        int balance_now = boards.getOrCreatePlayerScore(playername, boards.getObjective("mtr_balance")).getScore();

        source.sendSuccess(MALocalizationUtil.translatableText("message.metropolis.ticket.enter", playername, balance_now, zone),true);
        return 0;
    }

    private static int enterStation(CommandSourceStack source, int zone, ServerPlayer serverplayer) {
        return enterStation(source, zone, serverplayer, DEFAULT_FARE_EVASION_HANDLING);
    }

    private static int enterStation(CommandSourceStack source, int zone) throws CommandSyntaxException {
        if (source.getPlayer() == null) throw ERROR_NO_PLAYER.create();
        return enterStation(source, zone, source.getPlayer());
    }

    private static int enterStation(CommandSourceStack source, int zone, FareEvasionHandling fare_evasion_handling) throws CommandSyntaxException {
        if (source.getPlayer() == null) throw ERROR_NO_PLAYER.create();
        return enterStation(source, zone, source.getPlayer(), fare_evasion_handling);
    }

    private static int exitStation(CommandSourceStack source, int zone, ServerPlayer serverplayer, FareEvasionHandling fare_evasion_handling) {
        // todo: WIP
        source.sendFailure(MALocalizationUtil.literalText("暂未完成"));
        return 0;
    }

    private static int exitStation(CommandSourceStack source, int zone, ServerPlayer serverplayer) {
        return exitStation(source, zone, serverplayer, DEFAULT_FARE_EVASION_HANDLING);
    }

    private static int exitStation(CommandSourceStack source, int zone) throws CommandSyntaxException {
        if (source.getPlayer() == null) throw ERROR_NO_PLAYER.create();
        return exitStation(source, zone, source.getPlayer(), DEFAULT_FARE_EVASION_HANDLING);
    }

    private static int exitStation(CommandSourceStack source, int zone, FareEvasionHandling fare_evasion_handling) throws CommandSyntaxException {
        if (source.getPlayer() == null) throw ERROR_NO_PLAYER.create();
        return exitStation(source, zone, source.getPlayer(), fare_evasion_handling);
    }
}
