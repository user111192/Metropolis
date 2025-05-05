package team.dovecotmc.metropolis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import team.dovecotmc.metropolis.Metropolis;

// getString(ctx, "string")
// word()
// literal("foo")
// argument("bar", word())
// Import everything in the Commands


/**
 * @project Metropolis
 */
@SuppressWarnings("unused")
public class MetroCommands {

    public static void initialize(CommandDispatcher<CommandSourceStack> dispatcher) {
        Metropolis.LOGGER.info("Initializing Commands");
        /*final LiteralCommandNode<CommandSourceStack> MTRTICKET = */
        MTRTicketSystemCommands.register(dispatcher);
    }
}
