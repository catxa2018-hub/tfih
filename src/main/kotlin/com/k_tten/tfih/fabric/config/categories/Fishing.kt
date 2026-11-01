package com.k_tten.tfih.fabric.config.categories

class Fishing {
    var enabled: Boolean = false
    var jumpMode: Boolean = false
    var bobberCheckAverageSeconds: Double = 60.0

    var timings = FishingTimings()

    class FishingTimings {
        var distribution = 0.3

        var waterAverage: Float = 300f
        var lavaAverage: Float = 250f
        var castAverage: Float = 225f
    }
}