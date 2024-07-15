package taboolib.common5

import taboolib.common.Inject
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency

@Inject
@RuntimeDependencies(
    RuntimeDependency(value = "!com.google.code.gson:gson:2.8.8", test = "!com.google.gson.JsonElement"),
    RuntimeDependency(value = "!com.google.guava:guava:21.0", test = "!com.google.common.base.Optional"),
    RuntimeDependency(value = "!org.apache.commons:commons-lang3:3.5", test = "!org.apache.commons.lang3.concurrent.BasicThreadFactory")
)
object Common5