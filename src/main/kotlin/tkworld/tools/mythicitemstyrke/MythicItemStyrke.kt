package tkworld.tools.mythicitemstyrke

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import io.lumine.xikage.mythicmobs.io.MythicConfig
import io.lumine.xikage.mythicmobs.utils.config.ConfigurationSection
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.util.random
import taboolib.common5.Coerce
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import taboolib.type.BukkitEquipment

object MythicItemStyrke : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }

    class ActionM(val list: MutableList<String>, val cancelled: Boolean)

    private fun MythicConfig.getAction(key: String): ActionM {
        if (this.getStringList("Styrke.action.${key}!!").size != 0) {
            return ActionM(this.getStringList("Styrke.action.${key}"), true)
        }
        if (this.getStringList("Styrke.action.${key}").size != 0) {
            return ActionM(this.getStringList("Styrke.action.${key}"), false)
        }
        return ActionM(mutableListOf(), false)
    }

    //破坏方块
    @SubscribeEvent
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val item = event.player.inventory.itemInMainHand
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            val actionM = mmi.config.getAction("onBlockBreak")
            actionM.list.ketherEval(event.player)
            event.isCancelled = actionM.cancelled
        }
    }

    @SubscribeEvent
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        val item = event.player.inventory.itemInMainHand
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            val actionM = mmi.config.getAction("onBlockPlace")
            actionM.list.ketherEval(event.player)
            event.isCancelled = actionM.cancelled
            if (!mmi.config.getBoolean("Styrke.setting.place", true)) {
                event.isCancelled = true
            }
        }
    }

    @Awake(LifeCycle.ACTIVE)
    fun onTimer() {
        submit(period = 200) {
            Bukkit.getOnlinePlayers().forEach { player ->
                player.inventory.toList().forEach { item ->
                    if (item != null && item.isNotAir()) {
                        val mmi = item.toMythicItem() ?: return@submit
                        mmi.config.getAction("onTimer").list.ketherEval(player)
                    }
                }
            }
        }
    }

    //物品损坏
    @SubscribeEvent
    fun onPlayerItemBreakEvent(event: PlayerItemBreakEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            mmi.config.getAction("onItemBreak").list.ketherEval(event.player)

        }
    }

    //物品消耗
    @SubscribeEvent
    fun onPlayerItemConsumeEvent(event: PlayerItemConsumeEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            val actionM = mmi.config.getAction("onItemConsume")
            actionM.list.ketherEval(event.player)
            event.isCancelled = actionM.cancelled

        }
    }

    //物品捡起
    @SubscribeEvent
    fun onEntityPickupItemEvent(event: PlayerPickupItemEvent) {
        val player = event.player
        val item = event.item.itemStack
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            val actionM = mmi.config.getAction("onPickUp")
            actionM.list.ketherEval(event.player)
            event.isCancelled = actionM.cancelled
        }
    }

    //物品丢弃
    @SubscribeEvent
    fun onEntityDropItemEvent(event: PlayerDropItemEvent) {
        val player = event.player
        val item = event.itemDrop.itemStack
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            val actionM = mmi.config.getAction("onDrop")
            actionM.list.ketherEval(event.player)
            event.isCancelled = actionM.cancelled
        }
    }

    //吃掉物品
    @SubscribeEvent
    fun onEat(event: PlayerItemConsumeEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (item.isAir()) {
            return
        }
        val mmi = item.toMythicItem() ?: return
        val add = mmi.config.getInteger("Styrke.food.add")
        if (add >= 1) {
            //设置饥饿值
            if (player.foodLevel + add >= 20) {
                player.foodLevel = 20
            } else if (player.foodLevel + add <= 0) {
                player.foodLevel = 0
            } else {
                player.foodLevel = player.foodLevel + add
            }
        }
    }

    //物品切换
    @SubscribeEvent
    fun onPlayerSwapHandItemsEvent(event: PlayerSwapHandItemsEvent) {
        if (event.offHandItem.isNotAir()) {
            val mmi = event.offHandItem!!.toMythicItem() ?: return
            val actionM = mmi.config.getAction("onSwapToOffhand")
            actionM.list.ketherEval(event.player)
            event.isCancelled = actionM.cancelled
        }
        if (event.mainHandItem.isNotAir()) {
            val mmi = event.offHandItem!!.toMythicItem() ?: return
            val actionM = mmi.config.getAction("onSwapToMainHand")
            actionM.list.ketherEval(event.player)
            event.isCancelled = actionM.cancelled
        }
    }

    //物品点击
    @SubscribeEvent
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.item.isNotAir()) {
            val mmi = event.item!!.toMythicItem() ?: return
            if (mmi.config.getInteger("Styrke.setting.consume", 0) != 0) {
                if (event.item!!.amount < mmi.config.getInteger("Styrke.setting.consume", 0)) {
                    event.player.error("缺少物品!! 无法执行!!")
                    return
                } else {
                    event.item!!.amount = event.item!!.amount - mmi.config.getInteger("Styrke.setting.consume", 0)
                }
            }
            val actionMs = mmi.config.getAction("onStyrkeClickAll")
            actionMs.list.ketherEval(event.player)
            event.isCancelled = actionMs.cancelled
            when (event.action) {
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                    val actionM = mmi.config.getAction("onLeftClick")
                    actionM.list.ketherEval(event.player)
                    event.isCancelled = actionM.cancelled
                }
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                    val actionM = mmi.config.getAction("onRightClick")
                    actionM.list.ketherEval(event.player)
                    event.isCancelled = actionM.cancelled
                }
                else -> {
                    val actionM = mmi.config.getAction("onStyrkeClick")
                    actionM.list.ketherEval(event.player)
                    event.isCancelled = actionM.cancelled
                }
            }
        }
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent")
    fun e1(oe: OptionalEvent) {
        val e = oe.get<MythicMobSpawnEvent>()
        val section =
            e.mob.type.config.fileConfiguration.getConfigurationSection(e.mob.type.internalName + ".Styrke.equipments")
                ?: return
        submit(delay = 5) {
            MythicUtil.equipment(section, e.entity as? LivingEntity ?: return@submit)
        }
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent")
    fun e2(oe: OptionalEvent) {
        val e = oe.get<MythicMobDeathEvent>()
        e.mob.type.config.getStringList("Styrke.drops").forEach {
            val args = it.split(" ")
            if (args.size == 3 && !random(Coerce.toDouble(args[2]))) {
                return@forEach
            }
            val item = args[0].getItemStackM()
            val amount = args.getOrElse(1) { "1" }.split("-").map { a -> Coerce.toInteger(a) }
            item.amount = random(amount[0], amount.getOrElse(1) { amount[0] })
            e.drops.add(item)
        }
    }

    object MythicUtil {

        fun equipment(equipment: ConfigurationSection?, entity: LivingEntity) {
            equipment?.getValues(false)?.forEach { (slot, item) ->
                val itemStack = if (item.toString() == "air" && !item.toString().startsWith("S-")) {
                    ItemStack(Material.AIR)
                } else {
                    item.toString().getItemStackM()
                }
                val equipments = asEquipmentSlot(slot)
                if (equipments != null) {
                    equipments.setItem(entity, itemStack)
                    equipments.setItemDropChance(entity, 0f)
                }
            }
        }

        fun asEquipmentSlot(id: String): BukkitEquipment? {
            return BukkitEquipment.fromString(id)
        }
    }
}