package taboolib.common.util

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer

/**
 * 是否为控制台对象
 */
fun ProxyCommandSender?.isConsole(): Boolean {
    return this !is ProxyPlayer
}

/**
 * 是否为玩家对象
 */
fun ProxyCommandSender?.isPlayer(): Boolean {
    return this is ProxyPlayer
}