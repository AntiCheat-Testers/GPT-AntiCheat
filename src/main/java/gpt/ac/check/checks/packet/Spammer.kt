package gpt.ac.check.checks.packet

import gpt.ac.check.Check
import org.bukkit.entity.Player
import kotlin.math.pow
import kotlin.math.sqrt

private val messages = mutableListOf<Double>()

class Spammer: Check("Chat STD Check","Checks if the Standard Deviation between messages is too low",Category.PACKET,2) {


    override fun onRecieveChat(perpetrator : Player, message : String) {
        if(message.isNotEmpty()){
            messages.add(message.length.toDouble())

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
        val mean = messages.map { it }.average()
        val variance = messages.map { (it - mean).pow(2) }.average()
        val stdDev = sqrt(variance)
        println(stdDev)
        return stdDev <9000 // adjust threshold as needed
    }
}