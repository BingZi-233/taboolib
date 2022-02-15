package taboolib.common.platform.service

import taboolib.common.platform.PlatformService
import taboolib.common.platform.event.EventOrder
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.PostOrder
import taboolib.common.platform.event.ProxyListener

/**
 * @author sky
 * @since 2021/6/17 12:04 上午
 */
@PlatformService
interface PlatformListener {

    /**
     * bukkit & nukkit
     */
    fun <T> registerListener(
        event: Class<T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        func: (T) -> Unit
    ): ProxyListener

    /**
     * bungeecord
     */
    fun <T> registerListener(
        event: Class<T>,
        level: Int,
        ignoreCancelled: Boolean = false,
        func: (T) -> Unit
    ): ProxyListener {
        error("Unsupported")
    }

    /**
     * velocity
     */
    fun <T> registerListener(
        event: Class<T>,
        postOrder: PostOrder = PostOrder.NORMAL,
        func: (T) -> Unit
    ): ProxyListener {
        error("Unsupported")
    }

    /**
     * sponge
     */
    fun <T> registerListener(
        event: Class<T>,
        order: EventOrder = EventOrder.DEFAULT,
        beforeModifications: Boolean = false,
        func: (T) -> Unit
    ): ProxyListener {
        error("Unsupported")
    }

    fun unregisterListener(proxyListener: ProxyListener)
}
