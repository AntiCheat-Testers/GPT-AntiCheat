package gpt.ac.util

import gpt.ac.Main
import gpt.ac.util.CheckManager.checkClasses
import gpt.ac.check.Check
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.reflections.Reflections

object CheckManager {
     var checkClasses: MutableList<Check> = mutableListOf()

    fun init() {
        val reflections = Reflections("gpt.ac.check")
        val subTypes = reflections.getSubTypesOf(Check::class.java)
        var inittime=0L

        var finishtime=0L

        for (checkClass in subTypes) {
            if(subTypes.indexOf(checkClass)==0){
                inittime=System.currentTimeMillis()
            }

            checkClasses.add(checkClass.getDeclaredConstructor().newInstance())
            val check = checkClass.getDeclaredConstructor().newInstance()
            Main.log("Added ${check.name} to Check List")
            if(subTypes.indexOf(checkClass)==subTypes.size){
                Main.log("Finished Loading Checks in  ${System.currentTimeMillis()-inittime} ms")

            }
        }

        Main.log("Finished Loading Checks in  ${System.currentTimeMillis()-inittime} ms")

    }

    @JvmStatic
     fun registerChecks(plugin:Plugin) {
        val pluginManager: PluginManager = Bukkit.getPluginManager()
        for (check in checkClasses) {

                pluginManager.registerEvents(check as org.bukkit.event.Listener, plugin)
            Main.log("Registered  Check: "+check.name)
            }
        }
    }

    fun getCheckByClass(checkClass :Check): Check? {
        for (clazz in checkClasses) {
            if (clazz == checkClass) {
                return clazz

            }
        }
        return null
    }
