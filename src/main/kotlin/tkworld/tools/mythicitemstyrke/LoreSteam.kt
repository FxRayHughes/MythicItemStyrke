package tkworld.tools.mythicitemstyrke

import tkworld.tools.mythicitemstyrke.weight.WeightCategory
import tkworld.tools.mythicitemstyrke.weight.WeightUtil

object LoreSteam {

    fun eval(string: String): List<String> {
        val args = string.split("->")
        //取一->test::ore::over::this
        val info = args[1]
        return when (args[0]) {
            "one", "取一" -> listOf(AOne(info))
            "more", "多个" -> AMore(string)
            "weight", "权重" -> listOf(AWeight(info))
            "more-weight", "多权重" -> AMoreWeight(string)
            else -> listOf(string)
        }.use()
    }

    fun List<String>.use(): List<String> {
        val list = mutableListOf<String>()
        this.forEach {
            if (it.contains("=|=")) {
                list.addAll(it.split("=|=").toList())
            } else {
                list.add(it)
            }
        }
        return list
    }

    fun AWeight(string: String): String {
        val data = mutableListOf<WeightCategory<String>>()
        val list = string.split("::")
        list.forEach {
            val args = it.split("||")
            data.add(WeightCategory(args[0], args[1].toInt()))
        }
        return WeightUtil.getWeightRandom(data) ?: string
    }

    fun AMoreWeight(string: String): List<String> {
        val args = string.split("->")
        val list = mutableListOf<String>()
        //more->5->Info
        if (!args[1].contains("(")) {
            (1..(args[1].toInt())).forEach { _ ->
                list.add(AWeight(args[2]))
            }
        } else {
            val number = args[1].replace("[()]".toRegex(), "").split("-")
            (1..(((number[0].toInt())..(number[1].toInt())).random())).forEach { _ ->
                list.add(AWeight(args[2]))
            }
        }
        return list
    }

    fun AOne(string: String): String {
        val list = string.split("::")
        return list.random()
    }

    fun AMore(string: String): List<String> {
        val args = string.split("->")
        val list = mutableListOf<String>()
        //more->5->Info
        if (!args[1].contains("(")) {
            (1..(args[1].toInt())).forEach { _ ->
                list.add(AOne(args[2]))
            }
        } else {
            val number = args[1].replace("[()]".toRegex(), "").split("-")
            (1..(((number[0].toInt())..(number[1].toInt())).random())).forEach { _ ->
                list.add(AOne(args[2]))
            }
        }
        return list
    }
}