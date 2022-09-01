package tkworld.tools.mythicitemstyrke

import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.items.MythicItem
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getName
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.giveItem
import taboolib.platform.util.inventoryCenterSlots
import taboolib.platform.util.replaceName
import java.util.*

object MythicItemUI {

    fun MythicItem.isThis(value: String): Boolean {
        val item = this.getItemStackM()
        if (item.isThis(value)) {
            return true
        }
        if (this.internalName.contains(value)) {
            return true
        }
        if (this.file.contains(value)) {
            return true
        }
        return false
    }

    val data = HashMap<UUID, String>()

    fun open(player: Player) {
        player.openMenu<Linked<MythicItem>>("MythicItem物品列表 (${MythicMobs.inst().itemManager.items.toMutableList().size})") {
            rows(6)
            slots(inventoryCenterSlots)
            elements {
                val list = mutableListOf<MythicItem>()
                val keys = data[player.uniqueId] ?: "无"
                if (keys.isEmpty() || keys == "无") {
                    list.addAll(MythicMobs.inst().itemManager.items.toMutableList())
                } else {
                    list.addAll(MythicMobs.inst().itemManager.items.toMutableList().filter { it.isThis(keys) })
                }
                list.sortedBy { it.internalName }
            }
            onGenerate { _, element, _, _ ->
                try {
                    val name = element.getItemStackM().getName()
                    element.getItemStackM().replaceName(name, "${name}§7 (${element.internalName})")
                } catch (_: Exception) {
                    buildItem(Material.STONE) {
                        name = element.internalName
                    }
                }
            }
            onClick { event, element ->
                if (event.rawSlot == 49) {
                    player.inputSign(arrayOf("", "", "第一行输入查询的内容")) { len ->
                        if (len[0].isEmpty()) {
                            data.remove(player.uniqueId)
                        }
                        data[player.uniqueId] = len[0]
                        return@inputSign
                    }
                }
                if (event.clickEvent().isLeftClick) {
                    player.giveItem(element.getItemStackM())
                    return@onClick
                }
                player.inputSign(arrayOf("", "", "第一行输入数量", element.internalName)) { len ->
                    val amount = len[0].toIntOrNull() ?: 1
                    player.giveItem(element.getItemStackM(), amount)
                }
            }
            onBuild { inventory ->
                val key = data[player.uniqueId] ?: "无"
                inventory.setItem(
                    49, buildItem(XMaterial.OAK_SIGN) {
                        name = "&f查询物品"
                        lore.add("&7当前关键字: &f$key")
                        lore.add(" ")
                        lore.add("&7点击通过输入关键字查询物品")
                        colored()
                    }
                )
            }
            setNextPage(51) { page, hasNextPage ->
                if (hasNextPage) {
                    buildItem(XMaterial.SPECTRAL_ARROW) {
                        name = "§f下一页"
                    }
                } else {
                    buildItem(XMaterial.ARROW) {
                        name = "§7下一页"
                    }
                }
            }
            setPreviousPage(47) { page, hasPreviousPage ->
                if (hasPreviousPage) {
                    buildItem(XMaterial.SPECTRAL_ARROW) {
                        name = "§f上一页"
                    }
                } else {
                    buildItem(XMaterial.ARROW) {
                        name = "§7上一页"
                    }
                }
            }
        }
    }

}