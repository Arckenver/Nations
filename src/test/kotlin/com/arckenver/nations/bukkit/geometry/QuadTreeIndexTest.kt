package com.arckenver.nations.bukkit.geometry

import se.lovef.assert.v1.shouldBeEmpty
import se.lovef.assert.v1.shouldContain
import kotlin.test.Test

class QuadTreeIndexTest {
    @Test
    fun test() {
        val index = QuadTreeIndex<String>()

        index.shouldContain(emptyList(), exactly = true)

        val entryA = Index.Entry(Rectangle(-6, -8, 3, 6), "A")
        index.insertCut(entryA)
        index.shouldContain(listOf(entryA), exactly = true)

        val entryB = Index.Entry(Rectangle(0, -6, 7, -2), "B")
        index.insertCut(entryB)
        index.shouldContain(listOf(entryA, entryB), exactly = true)

        val entryC = Index.Entry(Rectangle(0, 1, 4, 3), "C")
        index.insertCut(entryC)
        index.shouldContain(listOf(entryA, entryB, entryC), exactly = true)

        index.cut(Rectangle(2, -4, 12, 4))
        val newEntries = listOf(
            Index.Entry(Rectangle(-6, -8, 2, 6), "A"),
            Index.Entry(Rectangle(2, 4, 3, 6), "A"),
            Index.Entry(Rectangle(2, -8, 3, -4), "A"),
            Index.Entry(Rectangle(2, -6, 7, -4), "B"),
            Index.Entry(Rectangle(0, -6, 2, -2), "B"),
            Index.Entry(Rectangle(0, 1, 2, 3), "C"),
        )
        index.shouldContain(newEntries, exactly = true)
    }

    @Test
    fun test2() {
        val index = QuadTreeIndex<String>()

        val claimRects = listOf(
            Rectangle(8, 5, 14, 17),
            Rectangle(4, 11, 8, 17),
            Rectangle(4, 5, 6, 9),
        )
        val unclaimRect = Rectangle(11, 8, 14, 13)

        for (rect in claimRects) {
            index.insertCut(Index.Entry(rect, "A"))
        }
        index.cut(unclaimRect)

        index.shouldContainRectangles(
            listOf(
                Rectangle(4, 11, 8, 17),
                Rectangle(8, 5, 11, 17),
                Rectangle(11, 13, 14, 17),
                Rectangle(4, 5, 6, 9),
                Rectangle(11, 5, 14, 8),
            )
        )

        val cutAllRect = Rectangle(1, 1, 26, 26)
        index.cut(cutAllRect)
        index.shouldContainRectangles(emptyList(), exactly = true)
    }

    @Test
    fun test3() {
        val index = QuadTreeIndex<String>()

        index.insertCut(Index.Entry(Rectangle(4, 5, 14, 17), "A"))

        val atOrigin = index.search(Index.Query {
            contains(Vector(0, 0))
        }).toList()
        atOrigin.shouldBeEmpty()

        val atCenter = index.search(Index.Query {
            contains(Vector(9, 11))
        }).toList()
        atCenter.shouldContain(Index.Entry(Rectangle(4, 5, 14, 17), "A"))
    }

    fun <T : Any> QuadTreeIndex<T>.shouldContain(expected: List<Index.Entry<T>>, exactly: Boolean = false) {
        val entries = search().toList()
        for (e in expected) {
            entries.shouldContain(e)
        }
        if (exactly && entries.size > expected.size) {
            for (e in entries) {
                if (e !in expected) {
                    throw AssertionError("Should have ${expected.size} entries, has ${entries.size}, unexpected entry: $e")
                }
            }
        }
    }

    fun <T : Any> QuadTreeIndex<T>.shouldContainRectangles(expected: List<Rectangle>, exactly: Boolean = false) {
        val rects = search().toList().map { it.rect }
        for (r in expected) {
            rects.shouldContain(r)
        }
        if (exactly && rects.size > expected.size) {
            for (r in rects) {
                if (r !in expected) {
                    throw AssertionError("Should have ${expected.size} entries, has ${rects.size}, unexpected entry rectangle: $r")
                }
            }
        }
    }
}