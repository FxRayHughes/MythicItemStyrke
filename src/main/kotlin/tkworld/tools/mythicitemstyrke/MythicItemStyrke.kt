package tkworld.tools.mythicitemstyrke

import io.lumine.xikage.mythicmobs.io.MythicConfig
import org.bukkit.Bukkit
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.*
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir

object MythicItemStyrke : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }

    private fun MythicConfig.getAction(key: String): List<String> {
        return this.getStringList("Styrke.action.${key}")
    }

    //破坏方块
    @SubscribeEvent
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val item = event.player.inventory.itemInMainHand
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            mmi.config.getAction("onBlockBreak").ketherEval(event.player)
        }
    }

    @Awake(LifeCycle.ACTIVE)
    fun onTimer() {
        submit(period = 200) {
            Bukkit.getOnlinePlayers().forEach { player ->
                player.inventory.toList().forEach { item ->
                    if (item != null && item.isNotAir()) {
                        val mmi = item.toMythicItem() ?: return@submit
                        mmi.config.getAction("onTimer").ketherEval(player)
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
            mmi.config.getAction("onItemBreak").ketherEval(event.player)
        }
    }

    //物品消耗
    @SubscribeEvent
    fun onPlayerItemConsumeEvent(event: PlayerItemConsumeEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            mmi.config.getAction("onItemConsume").ketherEval(event.player)
        }
    }

    //物品捡起
    @SubscribeEvent
    fun onEntityPickupItemEvent(event: PlayerPickupItemEvent) {
        val player = event.player
        val item = event.item.itemStack
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            mmi.config.getAction("onPickUp").ketherEval(player)
        }
    }

    //物品丢弃
    @SubscribeEvent
    fun onEntityDropItemEvent(event: PlayerDropItemEvent) {
        val player = event.player
        val item = event.itemDrop.itemStack
        if (!item.isAir()) {
            val mmi = item.toMythicItem() ?: return
            mmi.config.getAction("onDrop").ketherEval(player)
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
        if (mmi.config.getBoolean("Styrke.food.enable", false)) {
            //设置饥饿值
            val add = mmi.config.getInteger("Styrke.food.add")
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
        val player = event.player
        if (event.offHandItem.isNotAir()) {
            val mmi = event.offHandItem!!.toMythicItem() ?: return
            mmi.config.getAction("onSwapToOffhand").ketherEval(player)
        }
        if (event.mainHandItem.isNotAir()) {
            val mmi = event.offHandItem!!.toMythicItem() ?: return
            mmi.config.getAction("onSwapToMainHand").ketherEval(player)
        }
    }

    //物品点击
    @SubscribeEvent
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.item.isNotAir()) {
            val player = event.player
            val mmi = event.item!!.toMythicItem() ?: return
            when (event.action) {
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                    mmi.config.getAction("onLeftClick").ketherEval(player)
                }
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                    mmi.config.getAction("onRightClick").ketherEval(player)
                }
                else -> {
                    mmi.config.getAction("onStyrkeClick").ketherEval(player)
                }
            }
        }
    }
}