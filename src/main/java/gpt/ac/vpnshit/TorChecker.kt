package gpt.ac.vpnshit

import org.bukkit.entity.Player
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

object TorChecker {
    @JvmStatic
    fun check(player: Player) {
        val ip = player.address?.address?.hostAddress
        if (ip == null || ip.startsWith("127.0.0.1") || ip.startsWith("192.168")) {
            return
        }
        try {
            val url = URL("https://check.torproject.org/torbulkexitlist")
            val stream = url.openStream()
            val reader = BufferedReader(InputStreamReader(stream))
            reader.useLines { lines ->
                if (ip in lines) {
                    player.kickPlayer("You are not allowed to join using a Tor exit node.")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}