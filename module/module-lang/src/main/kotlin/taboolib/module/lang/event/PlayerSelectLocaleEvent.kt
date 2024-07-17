package taboolib.module.lang.event

import taboolib.common.event.InternalEvent
import taboolib.common.platform.ProxyPlayer

/**
 * TabooLib
 * taboolib.module.lang.event.PlayerSelectLocaleEvent
 *
 * @author sky
 * @since 2021/6/18 11:05 下午
 */
class PlayerSelectLocaleEvent(val player: ProxyPlayer, var locale: String) : InternalEvent()