package core.game.system.command.sets

import core.api.refreshAppearance
import core.game.system.command.CommandPlugin.Companion.toInteger
import core.game.system.command.Privilege
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.update.flag.context.Animation
import core.plugin.Initializable

@Initializable
class AnimationCommandSet : CommandSet(Privilege.ADMIN) {

    override fun defineCommands() {
        define(
            name = "anim",
            privilege = Privilege.ADMIN,
            usage = "::anim <lt>Animation ID<gt>",
            description = "Plays the animation with the given ID.",
        ) { player, args ->
            if (args.size < 2) {
                reject(player, "Syntax error: ::anim <Animation ID>")
            }
            val animation = Animation(args[1].toInt())
            player.animate(animation)
        }

        define(
            name = "loopanim",
            privilege = Privilege.ADMIN,
            usage = "::loopanim <lt>startID<gt> <lt>endID<gt> <lt>delay<gt>",
            description = "Plays the animation with the given ID range.",
        ) { player, args ->
            if (args.size < 2) {
                reject(player, "Syntax error: ::loopanim <start ID> <end ID> <delay>")
                return@define
            }

            val start = toInteger(args[1])
            val end = if (args.size > 2) toInteger(args[2]) else 11154
            val delay = if (args.size > 3) toInteger(args[3]) else 3

            GameWorld.Pulser.submit(
                object : Pulse(delay, player) {
                var animId = start

                override fun pulse(): Boolean {
                    if (delay == 1) {
                        refreshAppearance(player)
                    }

                    player.animate(Animation.create(animId))
                    player.debug("Animation id: $animId")

                    animId++
                    return animId >= end
                }
            })
        }

        define(
            name = "ranim",
            privilege = Privilege.ADMIN,
            usage = "::ranim <lt>Render Anim ID<gt>",
            description = "Sets the player's render (walk/idle) animation.",
        ) { player, args ->
            if (args.size < 2) {
                reject(player, "Syntax error: ::ranim <Render Animation ID>")
            }
            if (args.size > 2) {
                GameWorld.Pulser.submit(
                    object : Pulse(3, player) {
                        var id = args[1].toInt()

                        override fun pulse(): Boolean {
                            player.appearance.setAnimations(Animation.create(id))
                            refreshAppearance(player)
                            player.sendChat("Current: $id")
                            return ++id >= args[2].toInt()
                        }
                    },
                )
            } else {
                try {
                    player.appearance.setAnimations(Animation.create(args[1].toInt()))
                    refreshAppearance(player)
                } catch (e: NumberFormatException) {
                    reject(player, "Syntax error: ::ranim <Render Animation ID>")
                }
            }
        }

        define(
            name = "resetanim",
            privilege = Privilege.ADMIN,
            usage = "::resetanim",
            description = "Resets the player's render (walk/idle) animation to default.",
        ) { player, _ ->
            player.appearance.prepareBodyData(player)
            player.appearance.setDefaultAnimations()
            player.appearance.setAnimations()
            refreshAppearance(player)
        }
    }
}
