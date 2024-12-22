package com.arckenver.nations.bukkit.geometry

import java.util.function.Predicate

abstract class Index<T : Any> {
    protected abstract fun insert(entry: Entry<T>)

    fun insertCut(entry: Entry<T>) {
        val cut = mutableListOf<Rectangle>(entry.rect)
        for (e in search(Query {
            intersects(entry.rect)
            matchValue(entry.value)
        })) {
            val newCut = mutableListOf<Rectangle>()
            for (r in cut) {
                newCut.addAll(r.cutBy(e.rect))
            }
            cut.clear()
            cut.addAll(newCut)
        }
        // TODO optimize rectangles in cut

        for (rect in cut) {
            insert(Entry(rect, entry.value))
        }
    }

    fun delete(query: Query<T>): List<Entry<T>> {
        val iter = search(query).iterator()
        val entries = mutableListOf<Entry<T>>()
        while (iter.hasNext()) {
            entries.add(iter.next())
            iter.remove()
        }
        return entries
    }

    open fun cut(rect: Rectangle, valuePredicate: Predicate<T>? = null) {
        val removed = delete(Query {
            intersects(rect)
            if (valuePredicate != null) {
                matchValue(valuePredicate)
            }
        })
        val cut = mutableListOf<Entry<T>>()
        for (e in removed) {
            for (r in e.rect.cutBy(rect)) {
                cut.add(Entry(r, e.value))
            }
        }

        // TODO optimize rectangles in cut

        for (entry in cut) {
            insert(entry)
        }
    }

    fun overlap(rect: Rectangle, valuePredicate: Predicate<T>? = null): Int {
        var cut = listOf(rect)
        for (entry in search(Query {
            intersects(rect)
            if (valuePredicate != null) {
                matchValue(valuePredicate)
            }
        })) {
            val newCut = mutableListOf<Rectangle>()
            for (r in cut) {
                newCut.addAll(r.cutBy(entry.rect))
            }
            cut = newCut
            if (cut.isEmpty()) {
                break
            }
        }
        return rect.area - cut.sumOf { it.area }
    }

    abstract fun search(query: Query<T>): MutableIterable<Entry<T>>

    fun search() = search(Query())

    data class Entry<T>(val rect: Rectangle, val value: T)

    class Query<T : Any>() {
        private val boundFilters = mutableListOf<Predicate<Rectangle>>()
        private val entryFilters = mutableListOf<Predicate<Entry<T>>>()

        constructor(builderAction: Builder<T>.() -> Unit) : this() {
            val builder = Builder(this)
            builder.builderAction()
        }

        fun testBound(bound: Rectangle) = boundFilters.all { it.test(bound) }
        fun testEntry(entry: Entry<T>) = entryFilters.all { it.test(entry) }

        class Builder<T : Any> internal constructor(private val query: Query<T>) {
            fun intersects(rect: Rectangle): Builder<T> {
                query.boundFilters.add { it.intersects(rect) }
                query.entryFilters.add { it.rect.intersects(rect) }
                return this
            }

            fun contains(rect: Rectangle): Builder<T> {
                query.boundFilters.add { it.contains(rect) }
                query.entryFilters.add { it.rect.contains(rect) }
                return this
            }

            fun contains(vec: Vector): Builder<T> {
                query.boundFilters.add { it.contains(vec) }
                query.entryFilters.add { it.rect.contains(vec) }
                return this
            }

            fun withinDistance(rect: Rectangle, distance: Int): Builder<T> {
                val distanceSquared = distance * distance
                val expanded = rect.expand(distance)
                query.boundFilters.add { it.intersects(expanded) }
                query.entryFilters.add { it.rect.distanceSquared(rect) <= distanceSquared }
                return this
            }

            fun matchValue(valuePredicate: Predicate<T>): Builder<T> {
                query.entryFilters.add { valuePredicate.test(it.value) }
                return this
            }

            fun matchValue(value: T) = matchValue { it == value }

            fun matchEntry(entry: Entry<T>): Builder<T> {
                query.boundFilters.add { it.contains(entry.rect) }
                query.entryFilters.add { it.rect == entry.rect && it.value == entry.value }
                return this
            }
        }
    }
}
