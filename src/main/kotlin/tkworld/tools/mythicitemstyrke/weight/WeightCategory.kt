package tkworld.tools.mythicitemstyrke.weight

class WeightCategory<T>(
    private var category: T,
    private var weight: Int
) {

    fun weightCategory(category: T, weight: Int) {
        this.setCategory(category)
        this.setWeight(weight)
    }

    fun getWeight(): Int {
        return weight
    }

    fun setWeight(weight: Int) {
        this.weight = weight
    }

    fun getCategory(): T {
        return category
    }

    fun setCategory(category: T) {
        this.category = category
    }

}