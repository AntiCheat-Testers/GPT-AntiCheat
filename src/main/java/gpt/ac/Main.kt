package gpt.ac

import com.maxmind.geoip2.DatabaseReader
import gpt.ac.util.CheckManager
import gpt.ac.util.PacketEventsPacketListener
import gpt.ac.vpnshit.ISPChecker
import gpt.ac.vpnshit.TorChecker
import io.github.retrooper.packetevents.PacketEvents
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.java.JavaPlugin
import sun.audio.AudioPlayer.player
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths


class Main : JavaPlugin(), Listener {
    companion object{
    @JvmStatic
    fun log(any:Any){
println("[GPT-AntiCheat] $any")
    }}
  override fun onEnable() {
    logger.info("GPT AntiCheat was Loaded")
CheckManager.init()
      server.pluginManager.registerEvents(this, this);

      PacketEvents.create(this).init()
      logger.info((if(PacketEvents.get().isInitialized) "✔️" else "❌") + " PacketEvents " + PacketEvents.get().version + " was " + (if(PacketEvents.get().isInitialized) "" else "not ") + "initialized successfully")
      //PacketEvents.get().registerListener(PacketEventsPacketListener())
if(!Files.exists(Paths.get("plugins/GPT-AntiCheat/"))){
    Files.createDirectory(Paths.get("plugins/GPT-AntiCheat"))
    logger.info("GPT Data Folder was Created")
    val connection = URL("https://github.com/P3TERX/GeoLite.mmdb/releases/download/2023.02.22/GeoLite2-Country.mmdb").openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connect()

    val contentLength = connection.contentLength
    var downloadedBytes = 0

    val inputStream = connection.inputStream
    val outputStream = FileOutputStream("plugins/GPT-AntiCheat/country.mmdb")

    val buffer = ByteArray(4096)
    var bytesRead: Int
    var lastProgress = 0

    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        outputStream.write(buffer, 0, bytesRead)
        downloadedBytes += bytesRead

        // Update progress bar
        val progress = (downloadedBytes.toDouble() / contentLength * 100).toInt()
        if (progress != lastProgress) {
            print("\rDownloading... [$progress%]")
            lastProgress = progress
        }
    }

    outputStream.close()
    inputStream.close()

    println("\nFile downloaded ")
}

  }
@EventHandler
fun onPlayerJoin(e:PlayerJoinEvent) {
    if (e.player.address!!.address.hostAddress != "127.0.0.1" || e.player.address!!.address.hostAddress.startsWith("192.168")) {
        val database = File("plugins/GPT-AntiCheat/country.mmdb")
        val dbReader : DatabaseReader = DatabaseReader.Builder(database).build()
        val response = dbReader.country(e.player.address!!.address)
        if (!e.player.isOp) {
            TorChecker.check(e.player)
            ISPChecker.check(e.player)
        }
        for (op in server.operators) {
            if (op.isOnline) {

                op.player!!.sendMessage("§2[❀GPT] §6${e.player.name}" + "§2 joined with the IP §a${e.player.address!!.address.hostAddress} (${response.traits.isp+"/"+response.country})")
            }
        }
    }
    else{
        for (op in server.operators) {
            if (op.isOnline) {
                op.player!!.sendMessage("§2[❀GPT] §6${e.player.name}" + "§2 joined via the local network")

            }
        }

    }
}


    @EventHandler
    fun onPlayerMove(e:PlayerMoveEvent){
        logger.info("PlayerMoveEvent fired!")

for(Check in CheckManager.checkClasses){
  Check.BukkitonMove(e)
}

    }
    @EventHandler
    fun onPlayerInteract(e:PlayerInteractEntityEvent){
        logger.info("PlayerInteractEntityEvent fired!")

        for(Check in CheckManager.checkClasses){
            Check.BukkitonInteractEntity(e)
        }

    }

    @EventHandler
    fun onPlayerInteractAt(e:PlayerInteractAtEntityEvent){
        logger.info("PlayerInteractAtEntityEvent fired!")

        for(Check in CheckManager.checkClasses){
            Check.BukkitonInteractAtEntity(e)
        }

    }
    @EventHandler
    fun onBlockBreaking(e:BlockBreakEvent){
        logger.info("BlockBreakEvent fired!")

        for(Check in CheckManager.checkClasses){
            Check.BukkitonBreaking(e)
        }

    }
    @EventHandler
    fun onPlayerAnimation(e:PlayerAnimationEvent){
        logger.info("PlayerAnimationEvent fired!")

        for(Check in CheckManager.checkClasses){
            Check.BukkitonAnimation(e)
        }

    }
    @EventHandler
    fun onPlayerVelocity(e:PlayerVelocityEvent){
        logger.info("PlayerVelocityEvent fired!")

        for(Check in CheckManager.checkClasses){
            Check.BukkitonVelocity(e)
        }

    }
    @EventHandler
    fun onInteract(e:PlayerInteractEvent){
        logger.info("PlayerInteractEvent fired!")

        for(Check in CheckManager.checkClasses){
            Check.BukkitonInteract(e)
        }

    }

    override fun onDisable() {
        PacketEvents.get().unregisterAllListeners()
        PacketEvents.get().terminate()
    }

}
