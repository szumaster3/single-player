package core.tools

import core.game.world.map.Location

/**
 * Represents a 3-dimensional vector with [x], [y], and [z] components.
 *
 * Provides common vector operations such as addition, subtraction, dot/cross products, normalization, and angle calculations.
 */
class Vector3d {

    var x: Double
    var y: Double
    var z: Double

    /**
     * Constructs a vector with specified components.
     *
     * @param x x-component
     * @param y y-component
     * @param z z-component
     */
    constructor(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * Constructs a vector from a double array [t].
     *
     * @param t array of size 3 containing [x, y, z]
     */
    constructor(t: DoubleArray) {
        this.x = t[0]
        this.y = t[1]
        this.z = t[2]
    }

    /**
     * Constructs a vector as a copy of another vector [v].
     *
     * @param v vector to copy
     */
    constructor(v: Vector3d) {
        this.x = v.x
        this.y = v.y
        this.z = v.z
    }

    /**
     * Constructs a vector from a [Location] object.
     *
     * @param l location to convert
     */
    constructor(l: Location) {
        this.x = l.x.toDouble()
        this.y = l.y.toDouble()
        this.z = l.z.toDouble()
    }

    /**
     * Constructs a zero vector (0.0, 0.0, 0.0).
     */
    constructor() {
        this.x = 0.0
        this.y = 0.0
        this.z = 0.0
    }

    override fun toString(): String = "($x, $y, $z)"

    /**
     * Sets this vector to the sum of [v1] and [v2].
     *
     * @return this vector after addition
     */
    fun add(v1: Vector3d, v2: Vector3d): Vector3d {
        this.x = v1.x + v2.x
        this.y = v1.y + v2.y
        this.z = v1.z + v2.z
        return this
    }

    /**
     * Adds [v1] to this vector in-place.
     *
     * @return this vector after addition
     */
    fun add(v1: Vector3d): Vector3d {
        this.x += v1.x
        this.y += v1.y
        this.z += v1.z
        return this
    }

    /**
     * Sets this vector to [v1] minus [v2].
     *
     * @return this vector after subtraction
     */
    fun sub(v1: Vector3d, v2: Vector3d): Vector3d {
        this.x = v1.x - v2.x
        this.y = v1.y - v2.y
        this.z = v1.z - v2.z
        return this
    }

    /**
     * Subtracts [v1] from this vector in-place.
     *
     * @return this vector after subtraction
     */
    fun sub(v1: Vector3d): Vector3d {
        this.x -= v1.x
        this.y -= v1.y
        this.z -= v1.z
        return this
    }

    /**
     * Sets this vector to the negation of [v1].
     *
     * @return this vector after negation
     */
    fun negate(v1: Vector3d): Vector3d {
        this.x = -v1.x
        this.y = -v1.y
        this.z = -v1.z
        return this
    }

    /**
     * Negates this vector in-place.
     *
     * @return this vector after negation
     */
    fun negate(): Vector3d {
        this.x = -this.x
        this.y = -this.y
        this.z = -this.z
        return this
    }

    /**
     * Sets this vector to the cross product of this vector and [v2].
     *
     * @return this vector after cross product
     */
    fun cross(v2: Vector3d): Vector3d {
        this.cross(Vector3d(this), v2)
        return this
    }

    /**
     * Sets this vector to the cross product of [v1] and [v2].
     *
     * @return this vector after cross product
     */
    fun cross(v1: Vector3d, v2: Vector3d): Vector3d {
        this.x = v1.y * v2.z - v1.z * v2.y
        this.y = v1.z * v2.x - v1.x * v2.z
        this.z = v1.x * v2.y - v1.y * v2.x
        return this
    }

    /**
     * Computes the dot product with [v2].
     *
     * @return dot product as Double
     */
    fun dot(v2: Vector3d): Double = this.x * v2.x + this.y * v2.y + this.z * v2.z

    /**
     * Computes the Euclidean (L2) norm of this vector.
     *
     * @return vector magnitude
     */
    fun l2norm(): Double = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z)

    /**
     * Normalizes this vector in-place (makes it unit length).
     *
     * @return normalized vector
     */
    fun normalize(): Vector3d {
        val norm = this.l2norm()
        this.x /= norm
        this.y /= norm
        this.z /= norm
        return this
    }

    companion object {
        /**
         * Computes the signed angle between [v1] and [v2] around the normal [n].
         *
         * @return angle in radians
         */
        fun signedAngle(v1: Vector3d, v2: Vector3d, n: Vector3d): Double =
            Math.atan2(Vector3d().cross(v1, v2).dot(n), v1.dot(v2))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector3d) return false
        return this.x == other.x && this.y == other.y && this.z == other.z
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    /**
     * Compares this vector with [v1] using a tolerance [epsilon].
     *
     * @return true if each component differs by at most [epsilon]
     */
    fun epsilonEquals(v1: Vector3d, epsilon: Double): Boolean {
        var diff = this.x - v1.x
        if (diff.isNaN() || Math.abs(diff) > epsilon) return false
        diff = this.y - v1.y
        if (diff.isNaN() || Math.abs(diff) > epsilon) return false
        diff = this.z - v1.z
        if (diff.isNaN() || Math.abs(diff) > epsilon) return false
        return true
    }
}
