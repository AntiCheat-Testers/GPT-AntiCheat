package gpt.ac.vpnshit

import org.bukkit.entity.Player
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class ISPChecker {

    companion object{
        fun check(player :Player){
            try {
                val address = player.address

                val inetAddress = address!!.address

                val  publicAddress =inetAddress.hostAddress
                // Create a URL object from the string URL
                val url = URL("https://ipinfo.io/$publicAddress")

                val connection : HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // Read all lines from the response into a string
                val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
                val responseBuilder = StringBuilder()
                var line : String?
                while (reader.readLine().also { line = it } != null) {
                    responseBuilder.append(line)
                }
                reader.close()

                // Check if the response string contains the target string
                val response = responseBuilder.toString()
                if (!response.contains("ISP") && !player.address!!.address.hostAddress.startsWith("127.0.0.1") && !player.address!!.address.hostAddress.startsWith(
                        "192.168"
                    )
                ) {
                    player.kickPlayer("You are not authorized to join this server. $publicAddress")
                    System.out.println(response)
                } else {
                    System.out.println(player.name + "has passed the isp check")

                }

                // Disconnect the connection
                connection.disconnect()
            } catch (e : IOException) {
                // Handle any errors that occur during the request
                e.printStackTrace()
                player.kickPlayer("An error occurred while trying to join the server.")
            }
        }

    }
}