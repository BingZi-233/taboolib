package taboolib.platform

import cn.nukkit.Server
import cn.nukkit.event.Event
import cn.nukkit.event.HandlerList
import cn.nukkit.event.Listener
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.ProxyListener
import taboolib.common.platform.service.PlatformListener
import taboolib.common.platform.function.getEventClass
import taboolib.internal.Internal

/**
 * TabooLib
 * taboolib.platform.NukkitAdapter
 *
 * @author CziSKY
 * @since 2021/6/20 0:46
 */
@Internal
@Awake
@PlatformSide([Platform.NUKKIT])
class NukkitListener : PlatformListener {

    val plugin: NukkitPlugin
        get() = NukkitPlugin.getInstance()

    @Suppress("UNCHECKED_CAST")
    override fun <T> registerListener(event: Class<T>, priority: EventPriority, ignoreCancelled: Boolean, func: (T) -> Unit): ProxyListener {
        val listener = NukkitRegisteredListener(event as Class<Event>) { func(it as T) }
        val eventClass = event.getEventClass()
        Server.getInstance().pluginManager.registerEvent(eventClass as Class<Event>, listener, priority.toNukkit(), listener, plugin, ignoreCancelled)
        return listener
    }

    override fun unregisterListener(proxyListener: ProxyListener) {
        HandlerList.unregisterAll(proxyListener as Listener)
    }

    fun EventPriority.toNukkit() = when (this) {
        EventPriority.LOWEST -> cn.nukkit.event.EventPriority.LOWEST
        EventPriority.LOW -> cn.nukkit.event.EventPriority.LOW
        EventPriority.NORMAL -> cn.nukkit.event.EventPriority.NORMAL
        EventPriority.HIGH -> cn.nukkit.event.EventPriority.HIGH
        EventPriority.HIGHEST -> cn.nukkit.event.EventPriority.HIGHEST
        EventPriority.MONITOR -> cn.nukkit.event.EventPriority.MONITOR
    }
}