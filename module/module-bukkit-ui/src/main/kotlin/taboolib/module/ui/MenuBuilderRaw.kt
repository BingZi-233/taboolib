package taboolib.module.ui

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import taboolib.module.chat.Source

inline fun <reified T : Menu> buildMenu(title: Source, builder: T.() -> Unit): Inventory {
    return buildMenu(title.toRawMessage(), builder)
}

inline fun <reified T : Menu> HumanEntity.openMenu(title: Source, builder: T.() -> Unit) {
    openMenu(title.toRawMessage(), builder)
}