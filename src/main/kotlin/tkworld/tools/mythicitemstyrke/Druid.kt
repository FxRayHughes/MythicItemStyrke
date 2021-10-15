package tkworld.tools.mythicitemstyrke

import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.items.MythicItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Baffle
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.nms.getI18nName
import taboolib.platform.util.hasLore
import taboolib.platform.util.hasName
import tkworld.tools.mythicitemstyrke.serializer.Serializer
import java.util.*



fun List<String>.ketherEval(senderPlayer: Player) {
    try {
        KetherShell.eval(this) {
            sender = adaptPlayer(senderPlayer)
        }
    } catch (e: LocalizedException) {
        e.printKetherErrorMessage()
    } catch (e: Throwable) {
        e.printKetherErrorMessage()
    }
}

fun ItemStack.name(): String {
    return if (this.hasName()) {
        this.itemMeta!!.displayName
    } else {
        this.getI18nName()
    }
}

fun String.center(prefix: String, suffix: String): String? {
    val start = this.indexOfLast { it.toString() == prefix }
    val end = this.indexOfFirst { it.toString() == suffix }
    if (start == -1 || end == -1) {
        return null
    }
    return this.subSequence(start + 1, end).toString()

}

fun String.range(): String {
    if (this.center("{", "}") != null) {
        val info = this.center("{", "}")!!.split("-")
        // {10-20}
        return this.replace(
            "{${this.center("{", "}")}}",
            ((info[0].toInt())..(info[1].toInt())).random().toString()
        )
    } else {
        return this
    }
}

fun ItemStack.isThis(value: String): Boolean {
    if (this.name().contains(value, true)) {
        return true
    }
    if (this.hasLore(value)) {
        return true
    }
    if (this.type.toString().contains(value, true)) {
        return true
    }
    if (this.toMythicItem()?.internalName == value) {
        return true
    }
    return false
}

fun MythicItem.getItemStackM(): ItemStack {
    return MythicItemNatur.getItemStack(this)
}

fun ItemStack.toMythicItem(): MythicItem? {
    return MythicItemNatur.getMythicItem(this)
}

fun String.getItemStackM(): ItemStack {
    return MythicItemNatur.getItemStack(MythicMobs.inst().itemManager.items.firstOrNull { it.internalName == this }!!)
}

fun ItemStack.toSerializerM(): String {
    if (MythicItemNatur.getMythicItem(this) == null) {
        return this.toSerializer()
    }
    return "MythicItem::${MythicItemNatur.getMythicItem(this)!!.internalName}::${this.amount}"
}

fun String.toItemStackM(): ItemStack {
    if (this.startsWith("MythicItem::")) {
        val args = this.split("::")
        val itemStack =
            MythicItemNatur.getItemStack(MythicMobs.inst().itemManager.getItem(args[1]).get()).clone()
        itemStack.amount = args[2].toIntOrNull() ?: 1
        return itemStack
    }
    return this.toItemStack() ?: ItemStack(Material.AIR)
}

fun ItemStack.toSerializer(): String {
    return Serializer.fromItemStack(this)
}

fun String.toItemStack(): ItemStack {
    return Serializer.toItemStack(this)
}


fun Player.info(vararg block: String) {
    block.forEach {
        toInfo(this, it)
    }
}

fun Player.error(vararg block: String) {
    block.forEach {
        toError(this, it)
    }
}

fun debug(vararg block: String) {
    val player = Bukkit.getPlayerExact("Ray_Hughes") ?: return
    block.forEach {
        toError(player, it)
    }
}

fun toInfo(sender: CommandSender, message: String) {
    sender.sendMessage("§8[§a Natur §8] §7${message.replace("&", "§")}")
    if (sender is Player && !cooldown.hasNext(sender.name)) {
        sender.playSound(sender.location, Sound.UI_BUTTON_CLICK, 1f, (1..2).random().toFloat())
    }
}

fun toError(sender: CommandSender, message: String) {
    sender.sendMessage("§8[§4 Natur §8] §7${message.replace("&", "§")}")
    if (sender is Player && !cooldown.hasNext(sender.name)) {
        sender.playSound(sender.location, Sound.ENTITY_VILLAGER_NO, 1f, (1..2).random().toFloat())
    }
}

fun toDone(sender: CommandSender, message: String) {
    sender.sendMessage("§8[§6 Natur §8] §7${message.replace("&", "§")}")
    if (sender is Player && !cooldown.hasNext(sender.name)) {
        sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, (1..2).random().toFloat())
    }
}

fun toConsole(message: String) {
    Bukkit.getConsoleSender().sendMessage("§8[§e Natur §8] §7${message.replace("&", "§")}")
}

val cooldown = Baffle.of(100)
