package team.dovecotmc.metropolis.command;

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

    public static void initialize() {
        Metropolis.LOGGER.info("Initializing Commands");
        MTRTicketSystemCommands.register(Metropolis.commandDispatcher);
    }
}
