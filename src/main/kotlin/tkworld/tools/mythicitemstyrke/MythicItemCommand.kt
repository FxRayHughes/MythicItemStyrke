package tkworld.tools.mythicitemstyrke

import io.lumine.xikage.mythicmobs.MythicMobs
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.command
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir

object MythicItemCommand {

    @Awake(LifeCycle.ENABLE)
    fun init() {

        command(name = "itemcommand", aliases = listOf("ic"), permission = "*") {
            literal("give") {
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        Bukkit.getOnlinePlayers().map { it.name }
                    }
                    dynamic {
                        suggestion<ProxyCommandSender> { _, _ ->
                            MythicMobs.inst().itemManager.items.map { it.internalName }.toList()
                        }
                        dynamic(optional = true) {
                            suggestion<ProxyCommandSender>(uncheck = true) { _, _ ->
                                listOf("1", "32", "64")
                            }
                            execute<ProxyCommandSender> { sender, context, _ ->
                                val type = context.argument(-1)
                                val amount = context.argument(0).toIntOrNull() ?: 1
                                val player = Bukkit.getPlayerExact(context.argument(-2)) ?: return@execute
                                if (type.getItemStackM().isAir()) {
                                    if (sender is Player) {
                                        sender.error("物品不存在！")
                                        return@execute
                                    }
                                }
                                if (sender.isOp && sender is Player) {
                                    sender.info("成功给予 &f${player.name} ${type}X${amount}")
                                }
                                type.let { player.giveItem(it.getItemStackM(), amount) }
                            }
                        }
                        execute<Player> { sender, context, _ ->
                            val type = context.argument(0)
                            val amount = 1
                            val player = Bukkit.getPlayerExact(context.argument(-1)) ?: return@execute
                            if (type.getItemStackM().isAir()) {
                                sender.error("物品不存在！")
                                return@execute
                            }
                            if (sender.isOp) {
                                sender.info("成功给予 &f${player.name} ${type}X${amount}")
                            }
                            type.let { player.giveItem(it.getItemStackM(), amount) }
                        }
                    }
                }
            }
            literal("ui") {
                dynamic(optional = true) {
                    suggestion<ProxyCommandSender> { _, _ ->
                        Bukkit.getOnlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { _, context, _ ->
                        val player = Bukkit.getPlayerExact(context.argument(0)) ?: return@execute
                        MythicItemUI.open(player)
                    }
                }
                execute<Player> { sender, _, _ ->
                    MythicItemUI.open(sender)
                }
            }
            literal("run") {
                dynamic(commit = "id") {
                    suggestion<ProxyCommandSender> { _, _ ->
                        MythicMobs.inst().itemManager.items.map { it.internalName }.toList()
                    }
                    dynamic(commit = "target") {
                        suggestion<ProxyCommandSender> { _, _ ->
                            Bukkit.getOnlinePlayers().map { it.name }
                        }
                        execute<CommandSender> { sender, context, argument ->
                            val target = Bukkit.getPlayerExact(context.argument(0)) ?: return@execute
                            val mmi = MythicMobs.inst().itemManager.getItem(context.argument(-1))
                            if (!mmi.isPresent) {
                                return@execute
                            }
                            val action = mmi.get().config.getStringList("Styrke.action.onCommand")
                            action.ketherEval(target)
                        }
                    }
                    execute<Player> { sender, context, argument ->
                        val mmi = MythicMobs.inst().itemManager.getItem(context.argument(0))
                        if (!mmi.isPresent) {
                            return@execute
                        }
                        val action = mmi.get().config.getStringList("Styrke.action.onCommand")
                        action.ketherEval(sender)
                    }
                }
            }
        }
    }

}