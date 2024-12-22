package com.arckenver.nations.bukkit.geometry

import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min

@Serializable
data class Rectangle(val x1: Int, val z1: Int, val x2: Int, val z2: Int) {
    constructor(v1: Vector, v2: Vector) : this(
        min(v1.x, v2.x),
        min(v1.z, v2.z),
        max(v1.x, v2.x) + 1,
        max(v1.z, v2.z) + 1,
    )

    val width get() = x2 - x1

    val height get() = z2 - z1

    val area get() = width * height

    fun intersects(r: Rectangle) = x1 < r.x2 && x2 > r.x1 && z1 < r.z2 && z2 > r.z1

    fun contains(r: Rectangle) = x1 <= r.x1 && x2 >= r.x2 && z1 <= r.z1 && z2 >= r.z2

    fun contains(v: Vector) = v.x in x1..<x2 && v.z in z1..<z2

    fun expand(hn: Int) = Rectangle(
        x1 - hn,
        z1 - hn,
        x2 + hn,
        z2 + hn
    )

    fun move(dx1: Int, dz1: Int, dx2: Int, dz2: Int) = Rectangle(
        x1 + dx1,
        z1 + dz1,
        x2 + dx2,
        z2 + dz2,
    )

    fun distanceSquared(r: Rectangle): Double {
        val dx = if (r.x2 < x1)
            (x1 - r.x2).toDouble()
        else if (r.x1 > x2)
            (r.x1 - x2).toDouble()
        else
            0.0

        val dz = if (r.z2 < z1)
            (z1 - r.z2).toDouble()
        else if (r.z1 > z2)
            (r.z1 - z2).toDouble()
        else
            0.0

        return if (dx != 0.0 && dz != 0.0)
            dx * dx + dz * dz
        else
            max(dx, dz)
    }

    fun cutBy(r: Rectangle): Array<Rectangle> {
        if (!intersects(r)) {
            return arrayOf(this)
        }

        val result = mutableListOf<Rectangle>()

        if (x1 < r.x1) {
            result.add(Rectangle(x1, z1, r.x1, z2));
        }
        if (x2 > r.x2) {
            result.add(Rectangle(r.x2, z1, x2, z2));
        }
        if (z1 < r.z1) {
            result.add(Rectangle(max(x1, r.x1), z1, min(x2, r.x2), r.z1));
        }
        if (z2 > r.z2) {
            result.add(Rectangle(max(x1, r.x1), r.z2, min(x2, r.x2), z2));
        }

        return result.toTypedArray()
    }

    operator fun compareTo(r: Rectangle): Int {
        var v = x1 - r.x1
        if (v != 0) {
            return v
        }
        v = z1 - r.z1
        if (v != 0) {
            return v
        }
        v = x2 - r.x2
        if (v != 0) {
            return v
        }
        return z2 - r.z2
    }
}

class SweepLineArea(val rectangles: List<Rectangle>) {
    private data class Event(
        val x: Int,
        val z1: Int,
        val z2: Int,
        val type: Type
    ) {
        enum class Type(val precedence: Int) { START(1), END(-1) }
    }

    fun compute(): Int {
        val events = mutableListOf<Event>()
        for (rect in rectangles) {
            events.add(Event(rect.x1, rect.z1, rect.z2, Event.Type.START))
            events.add(Event(rect.x2, rect.z1, rect.z2, Event.Type.END))
        }
        if (events.isEmpty()) {
            return 0
        }

        events.sortWith(compareBy({ it.x }, { -it.type.precedence }))

        var totalArea = 0
        var prevX = events.first().x
        val activeIntervals = mutableListOf<Pair<Int, Int>>()

        for (event in events) {
            val coveredZ = calculateCoveredZ(activeIntervals)
            totalArea += coveredZ * (event.x - prevX)

            when (event.type) {
                Event.Type.START -> activeIntervals.add(Pair(event.z1, event.z2))
                Event.Type.END -> activeIntervals.remove(Pair(event.z1, event.z2))
            }

            prevX = event.x
        }

        return totalArea
    }

    private fun calculateCoveredZ(intervals: List<Pair<Int, Int>>): Int {
        if (intervals.isEmpty()) return 0

        val sortedIntervals = intervals.sortedWith(compareBy { it.first })

        var coveredZ = 0
        var currentStart = sortedIntervals[0].first
        var currentEnd = sortedIntervals[0].second

        for (interval in sortedIntervals) {
            if (interval.first > currentEnd) {
                coveredZ += currentEnd - currentStart
                currentStart = interval.first
                currentEnd = interval.second
            } else {
                currentEnd = maxOf(currentEnd, interval.second)
            }
        }

        coveredZ += currentEnd - currentStart
        return coveredZ
    }
}


