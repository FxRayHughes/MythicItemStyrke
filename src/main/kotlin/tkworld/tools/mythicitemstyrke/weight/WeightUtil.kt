package tkworld.tools.mythicitemstyrke.weight

import java.util.*

object WeightUtil{

    fun <T> getWeightRandom(categories: List<WeightCategory<T>>): T? {
        var weightSum = 0
        for (wc in categories) {
            weightSum += wc.getWeight()
        }
        if (weightSum <= 0) {
            return null
        }
        val random = Random()
        val n = random.nextInt(weightSum)
        var m = 0
        for (wc in categories) {
            if (m <= n && n < m + wc.getWeight()) {
                return wc.getCategory()
            }
            m += wc.getWeight()
        }
        return null
    }

}