package gpt.ac.vpnshit

import org.bukkit.entity.Player
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class TorChecker {
    companion object{
@JvmStatic
fun check(player:Player) {
    try {
        // Check if the IP address is a Tor exit node
        val url = URL("https://check.torproject.org/torbulkexitlist")
        val stream = url.openStream()
        val reader = BufferedReader(InputStreamReader(stream))
        var line : String?
        while (reader.readLine().also { line = it } != null) {
            if (player.address!!.address.hostAddress!! == line) {
                player.kickPlayer("You are not allowed to join using a Tor exit node.")
                return
            }


        }
    } catch (e : IOException) {
        e.printStackTrace()
    }
}
}

}