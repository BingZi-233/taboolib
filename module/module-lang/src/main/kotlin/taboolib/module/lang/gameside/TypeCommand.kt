package taboolib.module.lang.gameside

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.common.util.asList
import taboolib.common.util.replaceWithOrder
import taboolib.module.lang.Type

/**
 * TabooLib
 * taboolib.module.lang.gameside.TypeCommand
 *
 * @author sky
 * @since 2021/6/20 10:55 下午
 */
class TypeCommand : Type {

    var command: List<String>? = null

    override fun init(source: Map<String, Any>) {
        command = source["command"]?.asList()
    }

    override fun send(sender: ProxyCommandSender, vararg args: Any) {
        command?.forEach { console().performCommand(it.replace("@p", sender.name).translate(sender, *args).replaceWithOrder(*args)) }
    }

    override fun toString(): String {
        return "TypeCommand(command='$command')"
    }
}