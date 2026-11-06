package com.k_tten.tfih.fabric.config

import com.k_tten.tfih.fabric.TfihFabric
import com.k_tten.tfih.fabric.config.categories.Fishing


class Config {
    val fishing: Fishing = Fishing()
    var umberella: Boolean = false
    var autoGg: Boolean = false
    var goldenFish: Boolean = false

    class Range(val min: Long, val max: Long) {
        override fun toString(): String = "$min..$max"
        operator fun component1(): Long = min
        operator fun component2(): Long = max

        fun getRandomValue(): Long =
            TfihFabric.RANDOM.nextLong(min, max)
    }
}