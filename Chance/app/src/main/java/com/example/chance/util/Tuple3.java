package com.example.chance.util;

public class Tuple3<X, Y, Z> {
    public final X x;
    public final Y y;
    public final Z z;
    public Tuple3(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

/**
 * ==================== Tuple3.java Comments ====================
 *
 * This file defines the Tuple3 class, a generic container for holding three
 * objects of potentially different types. It serves as a simple data structure
 * to group three related values together, often used for returning multiple
 * values from a method.
 *
 * === Tuple3 Class ===
 * A generic, immutable tuple that holds three elements. The elements are public
 * and final, meaning they can be accessed directly but cannot be changed after
 * the tuple is created.
 *
 * --- Generic Type Parameters ---
 * @param <X> The type of the first element.
 * @param <Y> The type of the second element.
 * @param <Z> The type of the third element.
 *
 * --- public final X x; ---
 * The first element of the tuple.
 *
 * --- public final Y y; ---
 * The second element of the tuple.
 *
 * --- public final Z z; ---
 * The third element of the tuple.
 *
 * --- Tuple3 Constructor ---
 * Constructs a new Tuple3 with the specified values.
 * @param x The value for the first element.
 * @param y The value for the second element.
 * @param z The value for the third element.
 */