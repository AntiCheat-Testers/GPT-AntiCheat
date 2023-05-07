package gpt.ac.check.checks.packet

import gpt.ac.check.Check
import org.bukkit.entity.Player
import kotlin.math.pow
import kotlin.math.sqrt

private val messages = mutableListOf<Long>()

class Spammer: Check("Chat STD Check","Checks if the Standard Deviation between messages is too low",Category.PACKET,2) {


    override fun onRecieveChat(perpetrator : Player, message : String) {
        if(message.isNotEmpty()){
            messages.add(System.currentTimeMillis())

        }
        if(isStdDevTooLow()){
            flag(1,perpetrator,"lol")
            messages.clear()
        }
    }


    fun isStdDevTooLow(): Boolean {
        if (messages.size < 20) {
            // Not enough messages to calculate standard deviation
            return false
        }
        val mean = messages.map { it.toDouble() }.average()
        val variance = messages.map { (it.toDouble() - mean).pow(2) }.average()
        val stdDev = sqrt(variance)
        println(stdDev)
        return stdDev <7000 // adjust threshold as needed
    }
}