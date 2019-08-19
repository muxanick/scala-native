package java.util.concurrent

import java.io.Serializable
import java.lang.{Object, String}
import java.util.{Collection, Iterator, Set, Spliterator}
import java.util.function.Consumer
import scala.scalanative.annotation.stub

/** A view of a ConcurrentHashMap as a Set of keys, in
 *  which additions may optionally be enabled by mapping to a
 *  common value.  This class cannot be directly instantiated.
 *  See keySet(),
 *  keySet(V),
 *  newKeySet(),
 *  newKeySet(int).
 */
trait ConcurrentHashMap_KeySetView[K, V] extends Object with Set[K] with Serializable {

    /** Adds the specified key to this set view by mapping the key to
     *  the default mapped value in the backing map, if defined.
     */
    @stub
    def add(e: K): Boolean = ???

    /** Adds all of the elements in the specified collection to this set,
     *  as if by calling add(K) on each one.
     */
    @stub
    def addAll(c: Collection[_ <: K]): Boolean = ???

    /** Removes all of the elements from this view, by removing all
     *  the mappings from the map backing this view.
     */
    @stub
    def clear(): Unit = ???

    /** Returns true if this collection contains the specified element. */
    @stub
    def contains(o: Any): Boolean = ???

    /** Returns true if this collection contains all of the elements
     *  in the specified collection.
     */
    @stub
    def containsAll(c: Collection[_]): Boolean = ???

    /** Indicates whether some other object is "equal to" this one. */
    @stub
    override def equals(o: Any): Boolean = ???

    /** Performs the given action for each element of the Iterable
     *  until all elements have been processed or the action throws an
     *  exception.
     */
    @stub
    def forEach(action: Consumer[_ >: K]): Unit = ???

    /** Returns the map backing this view. */
    @stub
    def getMap(): ConcurrentHashMap[K, V] = ???

    /** Returns the default mapped value for additions,
     *  or null if additions are not supported.
     */
    @stub
    def getMappedValue(): V = ???

    /** Returns a hash code value for the object. */
    @stub
    override def hashCode(): Int = ???

    /** Returns true if this collection contains no elements. */
    @stub
    def isEmpty(): Boolean = ???

    /** Returns an iterator over the elements in this collection. */
    @stub
    def iterator(): Iterator[K] = ???

    /** Removes the key from this map view, by removing the key (and its
     *  corresponding value) from the backing map.
     */
    @stub
    def remove(o: Any): Boolean = ???

    /** Removes all of this collection's elements that are also contained in the
     *  specified collection (optional operation).
     */
    @stub
    def removeAll(c: Collection[_]): Boolean = ???

    /** Retains only the elements in this collection that are contained in the
     *  specified collection (optional operation).
     */
    @stub
    def retainAll(c: Collection[_]): Boolean = ???

    /** Returns the number of elements in this collection. */
    @stub
    def size(): Int = ???

    /** Creates a Spliterator over the elements in this set. */
    @stub
    def spliterator(): Spliterator[K] = ???

    /** Returns an array containing all of the elements in this collection. */
    @stub
    def toArray(): Array[Object] = ???

    /** Returns an array containing all of the elements in this collection;
     *  the runtime type of the returned array is that of the specified array.
     */
    @stub
    def toArray[T](a: Array[T]): Array[T] = ???

    /** Returns a string representation of this collection. */
    @stub
    override def toString(): String = ???
}

object ConcurrentHashMap_KeySetView {
    @stub
    def method(): Boolean = ???
}