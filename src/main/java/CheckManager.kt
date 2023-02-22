import gpt.ac.check.Check
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.reflections.Reflections
import kotlin.reflect.KClass

object CheckManager {
     var checkClasses: MutableList<Class<out Check>> = mutableListOf()

    fun init() {
        val reflections = Reflections("gpt.ac.check")
        val subTypes = reflections.getSubTypesOf(Check::class.java)
        val inittime=System.currentTimeMillis()
        for (checkClass in subTypes) {
            checkClasses.add(checkClass)
            val check = checkClass.getDeclaredConstructor().newInstance()
            println("Added ${check.name} to Check List")
        }

println("Finished Loading in ${System.currentTimeMillis()-inittime} ms")

    }

    @JvmStatic
     fun registerChecks(plugin:Plugin) {
        val pluginManager: PluginManager = Bukkit.getPluginManager()
        for (check in checkClasses) {
            val sex=check.getDeclaredConstructor().newInstance()

            if (sex is Listener) {
                pluginManager.registerEvents(sex as org.bukkit.event.Listener, plugin)
                System.out.println("Registered "+sex.name)
            }
        }
    }

    fun getCheckByClass(checkClass: Class<out Check>): Check? {
        for (clazz in checkClasses) {
            if (clazz == checkClass) {
                return clazz.getDeclaredConstructor().newInstance()
            }
        }
        return null
    }
}