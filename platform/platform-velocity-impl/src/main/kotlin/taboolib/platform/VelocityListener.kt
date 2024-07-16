package taboolib.platform

import com.velocitypowered.api.event.EventHandler
import taboolib.common.Inject
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.PostOrder
import taboolib.common.platform.event.ProxyListener
import taboolib.common.platform.service.PlatformListener
import taboolib.common.util.unsafeLazy

/**
 * TabooLib
 * taboolib.platform.VelocityAdapter
 *
 * @author CziSKY
 * @since 2021/6/21 14:28
 */
@Awake
@Inject
@PlatformSide(Platform.VELOCITY)
class VelocityListener : PlatformListener {

    val plugin by unsafeLazy { VelocityPlugin.getInstance() }

    override fun <T> registerListener(event: Class<T>, priority: EventPriority, ignoreCancelled: Boolean, func: (T) -> Unit): ProxyListener {
        error("Unsupported")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> registerListener(event: Class<T>, postOrder: PostOrder, func: (T) -> Unit): ProxyListener {
        val listener = VelocityListener(event) { func(it as T) }
        plugin.server.eventManager.register(plugin, event as Class<Any>, com.velocitypowered.api.event.PostOrder.values()[postOrder.ordinal], listener)
        return listener
    }

    override fun unregisterListener(proxyListener: ProxyListener) {
        plugin.server.eventManager.unregister(plugin, proxyListener as EventHandler<*>)
    }

    class VelocityListener(private val clazz: Class<*>, val consumer: (Any) -> Unit) : ProxyListener, EventHandler<Any> {

        override fun execute(event: Any) {
            if (clazz.isAssignableFrom(event.javaClass)) {
                consumer(event)
            }
        }
    }
}