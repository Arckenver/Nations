package com.arckenver.nations.bukkit.geometry

private const val MIN_NODE_LENGTH = 4
private const val DEFAULT_BOUND_LENGTH = 16

class QuadTreeIndex<T : Any> private constructor(
    private var root: Node<T>
) : Index<T>() {
    constructor(bound: Rectangle) : this(Node(bound))

    constructor() : this(Rectangle(0, 0, DEFAULT_BOUND_LENGTH, DEFAULT_BOUND_LENGTH))

    private enum class Quadrant(val index: Int) {
        TOP_LEFT(0),
        TOP_RIGHT(1),
        BOTTOM_LEFT(2),
        BOTTOM_RIGHT(3),
    }

    private class Node<T : Any>(
        var bound: Rectangle,
        var entries: MutableList<Entry<T>> = mutableListOf(),
        val children: Array<Node<T>?> = arrayOf(null, null, null, null),
        var parent: Node<T>? = null,
    ) {
        val length = bound.width
        val hx get() = avg(bound.x1, bound.x2)
        val hz get() = avg(bound.z1, bound.z2)

        fun isEmpty() = entries.isEmpty() && children.all { it == null }

        fun upperQuadrant(rect: Rectangle): Quadrant? {
            val right = rect.x1 < bound.x1
            val left = rect.x2 > bound.x2
            val bottom = rect.z1 < bound.z1
            val top = rect.z2 > bound.z2

            if (left) {
                return if (top) Quadrant.TOP_LEFT else Quadrant.BOTTOM_LEFT
            }
            if (right) {
                return if (top) Quadrant.TOP_RIGHT else Quadrant.BOTTOM_RIGHT
            }
            if (top) {
                return Quadrant.TOP_RIGHT
            }
            if (bottom) {
                return Quadrant.BOTTOM_RIGHT
            }
            return null
        }

        fun upperBound(quadrant: Quadrant) = when (quadrant) {
            Quadrant.TOP_LEFT -> bound.move(-bound.width, 0, 0, bound.height)
            Quadrant.TOP_RIGHT -> bound.move(0, 0, bound.width, bound.height)
            Quadrant.BOTTOM_LEFT -> bound.move(0, -bound.height, bound.width, 0)
            Quadrant.BOTTOM_RIGHT -> bound.move(-bound.width, -bound.height, 0, 0)
        }

        fun lowerQuadrant(rect: Rectangle): Quadrant? {
            if (length <= MIN_NODE_LENGTH) {
                return null
            }
            if (rect.x2 <= hx) {
                if (rect.z2 <= hz) {
                    return Quadrant.TOP_LEFT
                }
                if (rect.z1 >= hz) {
                    return Quadrant.BOTTOM_LEFT
                }
            }
            if (rect.x1 >= hx) {
                if (rect.z2 <= hz) {
                    return Quadrant.TOP_RIGHT
                }
                if (rect.z1 >= hz) {
                    return Quadrant.BOTTOM_RIGHT
                }
            }
            return null
        }

        fun lowerBound(quadrant: Quadrant) = when (quadrant) {
            Quadrant.TOP_LEFT -> Rectangle(bound.x1, bound.z1, hx, hz)
            Quadrant.TOP_RIGHT -> Rectangle(hx, bound.z1, bound.x2, hz)
            Quadrant.BOTTOM_LEFT -> Rectangle(bound.x1, hz, hx, bound.z2)
            Quadrant.BOTTOM_RIGHT -> Rectangle(hx, hz, bound.x2, bound.z2)
        }
    }

    override fun insert(entry: Entry<T>) {
        var upperQuad = root.upperQuadrant(entry.rect)
        while (upperQuad != null) {
            val parent = Node<T>(root.upperBound(upperQuad))
            if (!root.isEmpty()) {
                parent.children[upperQuad.index] = root
                root.parent = parent
            }
            root = parent

            upperQuad = root.upperQuadrant(entry.rect)
            if (upperQuad == null) {
                root.entries.add(entry)
                return
            }
        }

        var lowerQuad = root.lowerQuadrant(entry.rect)
        var current = root
        while (lowerQuad != null) {
            val child = current.children[lowerQuad.index] ?: Node(current.lowerBound(lowerQuad), parent = current)
            current.children[lowerQuad.index] = child
            current = child

            lowerQuad = current.lowerQuadrant(entry.rect)
        }

        current.entries.add(entry)
    }

    override fun search(query: Query<T>): MutableIterable<Entry<T>> = Iterable(root, query)

    private class Iterable<T : Any>(
        val root: Node<T>,
        val query: Query<T>,
    ) : MutableIterable<Entry<T>> {
        private data class EntryRef<T : Any>(val node: Node<T>, val index: Int)

        override fun iterator() = object : MutableIterator<Entry<T>> {
            private val stack = mutableListOf<Node<T>>(root)
            private var current: EntryRef<T>? = null
            private var removed = false
            private var next: EntryRef<T>? = null

            override fun hasNext(): Boolean {
                search()
                return next != null
            }

            override fun next(): Entry<T> {
                search()
                val ref = next ?: throw NoSuchElementException()
                current = ref
                removed = false
                next = null
                return ref.node.entries[ref.index]
            }

            fun search() {
                if (next != null) {
                    return
                }

                current?.let {
                    val node = it.node
                    var index = it.index + 1
                    while (index < node.entries.size) {
                        if (query.testEntry(node.entries[index])) {
                            next = EntryRef(node, index)
                            return
                        }
                        index++
                    }
                }

                while (stack.isNotEmpty()) {
                    val node = stack.removeLast()
                    if (!query.testBound(node.bound)) {
                        continue
                    }
                    stack.addAll(node.children.filterNotNull())

                    for (index in node.entries.indices) {
                        if (query.testEntry(node.entries[index])) {
                            next = EntryRef(node, index)
                            return
                        }
                    }
                }
            }

            override fun remove() {
                if (removed) throw IllegalStateException()
                var (node, index) = current ?: throw IllegalStateException()

                node.entries.removeAt(index)

                if (node.entries.isEmpty()) {
                    current = null
                } else {
                    current = EntryRef(node, index - 1)
                }

                if (next?.node === node) {
                    next = EntryRef(node, index - 1)
                }

                removed = true

                while (node.isEmpty()) {
                    val parent = node.parent ?: return
                    val quadrant = parent.children.indexOfFirst { it === node }
                    parent.children[quadrant] = null
                    node = parent
                }
            }
        }
    }
}

private fun avg(a: Int, b: Int) = (a and b) + ((a xor b) shr 1)
