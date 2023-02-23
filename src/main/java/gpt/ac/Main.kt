package gpt.ac


import com.maxmind.geoip2.DatabaseReader
import gpt.ac.util.CheckManager
import gpt.ac.util.PacketListener
import gpt.ac.vpnshit.TorChecker
import io.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.utils.server.ServerVersion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
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
    }
    @JvmStatic
    var plugin: Plugin? = null

    }

    override fun onLoad() {
        PacketEvents.create(this)
        val settings = PacketEvents.get().settings
        settings
            .fallbackServerVersion(ServerVersion.v_1_7_10)
            .checkForUpdates(false)
            .bStats(true)
        PacketEvents.get().load()
        PacketEvents.get().registerListener(PacketListener())

    }
  override fun onEnable() {
      Thread{
    logger.info("GPT AntiCheat was Loaded")
CheckManager.init()
plugin=this
      server.pluginManager.registerEvents(this, this);

      PacketEvents.get().init()
      logger.info((if(PacketEvents.get().isInitialized) "✔️" else "❌") + " PacketEvents " + PacketEvents.get().version + " was " + (if(PacketEvents.get().isInitialized) "" else "not ") + "initialized successfully")
if(!Files.exists(Paths.get("plugins/GPT-AntiCheat/"))) {
    Files.createDirectory(Paths.get("plugins/GPT-AntiCheat"))
    logger.info("GPT Data Folder was Created")
    downloadGeoIPDB()
}
}.start()
  }
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        for(Check in CheckManager.checkClasses){
            Check.onJoin(e)
        }
        val playerIp = e.player.address?.address?.hostAddress
        if (playerIp == null || playerIp == "127.0.0.1" || playerIp.startsWith("192.168")) {
            // Player joined via local network
            for (op in server.operators) {
                if (op.isOnline) {
                    op.player?.sendMessage("§2[❀GPT] §6${e.player.name}§2 joined via the local network")
                }
            }
            return
        }

        // Player joined from external network
        Thread {
            val databaseFile = File("plugins/GPT-AntiCheat/country.mmdb")
            val dbReader = DatabaseReader.Builder(databaseFile).build()
            val response = dbReader.country(e.player.address!!.address)
            if (!e.player.isOp) {
                TorChecker.check(e.player)
            }
            for (op in server.operators) {
                if (op.isOnline) {
                    op.player?.sendMessage("§2[❀GPT] §6${e.player.name}§2 joined with the IP §a${playerIp} (${response.traits.isp}/${response.country})")
                }
            }
        }.start()
    }


    @EventHandler
    fun onPlayerMove(e:PlayerMoveEvent){

for(Check in CheckManager.checkClasses){
  Check.bukkitOnMove(e)
}

    }
    @EventHandler
    fun onPlayerInteract(e:PlayerInteractEntityEvent){

        for(Check in CheckManager.checkClasses){
            Check.bukkitOnInteractEntity(e)
        }

    }
    @EventHandler
    fun onPlayeLeave(e:PlayerQuitEvent){

        for(Check in CheckManager.checkClasses){
            Check.onLeave(e)
        }

    }
    @EventHandler
    fun onPlayerInteractAt(e:PlayerInteractAtEntityEvent){

        for(Check in CheckManager.checkClasses){
            Check.bukkitOnInteractAtEntity(e)
        }

    }
    @EventHandler
    fun onBlockBreaking(e:BlockBreakEvent){

        for(Check in CheckManager.checkClasses){
            Check.bukkitOnBreaking(e)
        }

    }
    @EventHandler
    fun onPlayerAnimation(e:PlayerAnimationEvent){

        for(Check in CheckManager.checkClasses){
            Check.bukkitOnAnimation(e)
        }

    }
    @EventHandler
    fun onPlayerVelocity(e:PlayerVelocityEvent){

        for(Check in CheckManager.checkClasses){
            Check.bukkitOnVelocity(e)
        }

    }
    @EventHandler
    fun onInteract(e:PlayerInteractEvent){

        for(Check in CheckManager.checkClasses){
            Check.bukkitOnInteract(e)
        }

    }

    override fun onDisable() {
        PacketEvents.get().unregisterAllListeners()
        PacketEvents.get().terminate()
    }
fun downloadGeoIPDB(){
    val url = URL("https://github.com/P3TERX/GeoLite.mmdb/releases/download/2023.02.22/GeoLite2-Country.mmdb")
    val connection = url.openConnection() as HttpURLConnection

    connection.apply {
        requestMethod = "GET"
        connect()
    }

    val contentLength = connection.contentLength
    var downloadedBytes = 0

    connection.inputStream.use { input ->
        FileOutputStream("plugins/GPT-AntiCheat/country.mmdb").use { output ->
            val buffer = ByteArray(4096)

            while (true) {
                val bytesRead = input.read(buffer)
                if (bytesRead == -1) break
                output.write(buffer, 0, bytesRead)

                downloadedBytes += bytesRead
                val progress = (downloadedBytes.toDouble() / contentLength * 100).toInt()
                print("\rDownloading... [$progress%]")
            }
        }
    }

    println("\nFile downloaded")
}

}
