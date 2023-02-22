package gpt.ac

import CheckManager
import gpt.ac.check.Check
import gpt.ac.vpnshit.ISPChecker
import gpt.ac.vpnshit.TorChecker
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerVelocityEvent
import org.bukkit.plugin.java.JavaPlugin


class App : JavaPlugin(), Listener {

  override fun onEnable() {
    logger.info("AntiHake 1.0 Was Loaded")
    getServer().getPluginManager().registerEvents(this, this);
CheckManager.init()
CheckManager.registerChecks(this)




  }
@EventHandler
fun onPlayerJoin(e:PlayerJoinEvent){
if(!e.player.isOp) {
    TorChecker.check(e.player)
    ISPChecker.check(e.player)
}
}

    @EventHandler
    fun onPlayerMove(e:PlayerMoveEvent){
for(Check in CheckManager.checkClasses){
    Check.getDeclaredConstructor().newInstance().onMove(e)
}

    }
    @EventHandler
    fun onPlayerInteract(e:PlayerInteractEntityEvent){
        for(Check in CheckManager.checkClasses){
            Check.getDeclaredConstructor().newInstance().onInteractEntity(e)
        }

    }

    @EventHandler
    fun onPlayerInteractAt(e:PlayerInteractAtEntityEvent){
        for(Check in CheckManager.checkClasses){
            Check.getDeclaredConstructor().newInstance().onInteractAtEntity(e)
        }

    }
    @EventHandler
    fun onPlayerAnimation(e:PlayerAnimationEvent){
        for(Check in CheckManager.checkClasses){
            Check.getDeclaredConstructor().newInstance().onAnimation(e)
        }

    }
    @EventHandler
    fun onPlayerVelocity(e:PlayerVelocityEvent){
        for(Check in CheckManager.checkClasses){
            Check.getDeclaredConstructor().newInstance().onVelocity(e)
        }

    }
    @EventHandler
    fun onInteract(e:PlayerInteractEvent){
        for(Check in CheckManager.checkClasses){
            Check.getDeclaredConstructor().newInstance().onInteract(e)
        }

    }
}
