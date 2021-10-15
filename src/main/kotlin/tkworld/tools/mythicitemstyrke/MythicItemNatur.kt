package tkworld.tools.mythicitemstyrke

import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.items.MythicItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

object MythicItemNatur {

    fun getMythicItem(itemStack: ItemStack): MythicItem? {
        if (itemStack.type == Material.AIR || !itemStack.hasItemMeta()) {
            return null
        }
        val items = itemStack.clone()
        items.amount = 1
        val nbtId = items.getItemTag()["MythicItem"]
        if (nbtId != null) {
            return MythicMobs.inst().itemManager.items.firstOrNull { it.internalName == nbtId.asString() }
        }
        return MythicMobs.inst().itemManager.items.firstOrNull { it.displayName == itemStack.name() }
    }


    fun getItemStack(mythicItem: MythicItem): ItemStack {
        val itemStack =
            MythicMobs.inst().itemManager.getItemStack(mythicItem.internalName) ?: return ItemStack(Material.STONE)
        val items = itemStack.clone()
        items.amount = 1
        items.amount = itemStack.amount
        val meta = items.itemMeta!!
        val tag = items.getItemTag()
        tag["MythicItem"] = ItemTagData(mythicItem.internalName)
        val lore = mythicItem.lore ?: listOf()
        val newLore = mutableListOf<String>()
        lore.forEachIndexed { _, info ->
            val test = info.center("[", "]")
            if (test != null) {
                val save = info.replace(test, "<-Save->").replace("[\\[\\]]".toRegex(), "")
                newLore.addAll(LoreSteam.eval(test).map { save.replace("<-Save->", it).range() })
            } else {
                newLore.add(info.range())
            }
        }
        meta.lore = newLore
        items.itemMeta = meta
        return items.setItemTag(tag)
    }


}