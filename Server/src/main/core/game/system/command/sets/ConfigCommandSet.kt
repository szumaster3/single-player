package core.game.system.command.sets

import core.api.setVarp
import core.game.system.command.Privilege
import core.plugin.Initializable

@Initializable
class ConfigCommandSet : CommandSet(Privilege.ADMIN) {

    override fun defineCommands() {

        /*
         * Command for setting a range of configuration
         * ids (varps) to the maximum integer value for the player.
         */

        define(name = "sconfigrange", Privilege.ADMIN) { player, args ->
            if (args.size < 3) {
                reject(player, "usage: sconfigrange idlo idhi")
            }
            val idlo = args[1].toIntOrNull() ?: reject(player, "INCORRECT ID LOW")
            val idhi = args[2].toIntOrNull() ?: reject(player, "INCORRECT ID HIGH")
            for (idsend in (idlo as Int) until (idhi as Int)) {
                setVarp(player, idsend, Integer.MAX_VALUE)
                notify(player, "Config: $idsend value: " + Integer.MAX_VALUE)
            }
        }

        /*
         * Command for setting a range of configuration
         * ids (varps) to 0 for the player.
         */

        define(name = "sconfigrange0", Privilege.ADMIN) { player, args ->
            if (args.size < 3) {
                reject(player, "usage: sconfigrange0 idlo idhi")
            }
            val idlo = args[1].toIntOrNull() ?: reject(player, "INCORRECT ID LOW")
            val idhi = args[2].toIntOrNull() ?: reject(player, "INCORRECT ID HIGH")
            for (idsend in (idlo as Int) until (idhi as Int)) {
                setVarp(player, idsend, 0)
                notify(player, "Config: $idsend value: 0")
            }
        }

        /*
         * Command for opening interface.
         */

        define(
            name = "iface",
            privilege = Privilege.ADMIN,
            usage = "::iface <lt>Interface ID<gt>",
            description = "Opens the interface with the given ID.",
        ) { player, args ->
            if (args.size < 2) {
                reject(player, "usage: iface id")
                return@define
            }
            val id = args[1].toIntOrNull() ?: reject(player, "INVALID INTERFACE ID")
            player.interfaceManager.openComponent(id as Int)
        }
    }
}
