package gpt.ac


import com.maxmind.geoip2.DatabaseReader
import gpt.ac.util.*
import io.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.utils.server.ServerVersion
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


private const val DATA_FOLDER = "plugins/GPT-AntiCheat/"


class Main : JavaPlugin(), Listener {

    companion object {
        lateinit var plugin : Plugin
        fun log(any: Any) {
            plugin.logger.info("$any")
        }

    }
    private val currentDate : LocalDate = LocalDate.now()
    private val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    private val formattedDate : String = currentDate.format(formatter)
    override fun onLoad() {

        PacketEvents.create(this@Main)
        val settings = PacketEvents.get().settings
        settings
            .fallbackServerVersion(ServerVersion.v_1_7_10)
            .checkForUpdates(false)
            .bStats(true)
        PacketEvents.get().load()
        PacketEvents.get().registerListener(PacketListener())
    }

    override fun onEnable() {
        plugin = this@Main
        val messenger = Bukkit.getMessenger()



        println("_________________________________________________________________\n")
        CheckManager.init()
        server.pluginManager.registerEvents(this@Main, this@Main)
        PacketEvents.get().init()
        logger.info(
            (if (PacketEvents.get().isInitialized) "✔️" else "❌") + " PacketEvents " + PacketEvents.get().version + " was " + (if (PacketEvents.get().isInitialized) "" else "not ") + "initialized successfully"
        )
        logger.info("❀GPT AntiCheat was Loaded ✔️ ")

        println("_________________________________________________________________")
        if (!Files.exists(Paths.get("${DATA_FOLDER}country-db-${formattedDate}.mmdb"))) {
            Files.deleteIfExists(Paths.get(DATA_FOLDER))

            Files.createDirectory(Paths.get(DATA_FOLDER))
            logger.info("GPT Data Folder was Created✔️")
            downloadGeoIPDB()
        }

    }

    @EventHandler
    fun onPlayerJoin(e : PlayerJoinEvent) {
        CheckManager.checkClasses.forEach { check -> check.onJoin(e) }

        val playerIp = e.player.address?.address?.hostAddress
        if (!playerIp.isNullOrEmpty() && !playerIp.startsWith("127") && !playerIp.startsWith("192.168")) {
            // Player joined from outside the local network
            server.operators.filter { op -> op.isOnline }
                .forEach { op -> op.player?.sendMessage("§2[❀GPT] §6${e.player.name}§2 joined from outside the local network") }
        }

        // Player joined from external network
        Thread {
            val databaseFile = File("${DATA_FOLDER}country-db-${formattedDate}.mmdb")

            runCatching {
                DatabaseReader.Builder(databaseFile).build().country(e.player.address!!.address)
            }.onSuccess { response ->
                if (!e.player.isOp) {
                    IPChecker.check(e.player)
                }
                server.operators.filter { it.isOnline }.forEach { op ->
                    op.player?.sendMessage("§2[❀GPT] §6${e.player.name}§2 joined with the IP §a${playerIp} (${response.traits.isp}/${response.country})")
                }
            }.onFailure { exception ->
                logger.warning("Failed to get country information for player ${e.player.name}: ${exception.message}")
            }
        }.start()
    }


    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        CheckManager.checkClasses.forEach { it.bukkitOnMove(e) }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEntityEvent) {
        CheckManager.checkClasses.forEach { it.bukkitOnInteractEntity(e) }
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        CheckManager.checkClasses.forEach { it.onLeave(e) }
    }

    @EventHandler
    fun onPlayerInteractAt(e: PlayerInteractAtEntityEvent) {
        CheckManager.checkClasses.forEach { it.bukkitOnInteractAtEntity(e) }
    }

    @EventHandler
    fun onBlockBreaking(e: BlockBreakEvent) {
        CheckManager.checkClasses.forEach { it.bukkitOnBreaking(e) }
    }

    @EventHandler
    fun onPlayerAnimation(e: PlayerAnimationEvent) {
        CheckManager.checkClasses.forEach { it.bukkitOnAnimation(e) }
    }

    @EventHandler
    fun onPlayerVelocity(e: PlayerVelocityEvent) {
        CheckManager.checkClasses.forEach { it.bukkitOnVelocity(e) }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        CheckManager.checkClasses.forEach { it.bukkitOnInteract(e) }
    }


private fun downloadGeoIPDB(){


    val url = URL("https://github.com/P3TERX/GeoLite.mmdb/releases/download/2023.03.13/GeoLite2-Country.mmdb")
    val connection = url.openConnection() as HttpURLConnection

    connection.apply {
        requestMethod = "GET"
        connect()
    }

    val contentLength = connection.contentLength
    var downloadedBytes = 0
    connection.inputStream.use { input ->
        BufferedInputStream(input).use { bufferedInput ->
            FileOutputStream("${DATA_FOLDER}country-db-${formattedDate}.mmdb").use { output ->
                val buffer = ByteArray(4096)

                while (true) {
                    val bytesRead = bufferedInput.read(buffer)
                    if (bytesRead == -1) break
                    output.write(buffer, 0, bytesRead)

                    downloadedBytes += bytesRead
                    val progress = (downloadedBytes.toDouble() / contentLength * 100).toInt()
                    logger.info("Downloading... [$progress%]")
                }
            }
        }
    }

    logger.info("Country Database Downloaded")
}

}
