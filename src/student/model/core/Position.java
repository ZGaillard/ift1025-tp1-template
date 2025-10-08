/* ============================================================================
 * Path: src/student/model/core/Position.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Immutable 2D grid position record with basic validation and Manhattan distance.
 * ========================================================================== */
package student.model.core;

/**
 * Immutable grid position defined by non-negative {@code x} and {@code y} coordinates.
 * <p>Coordinates are validated to be >= 0 at construction time and cannot be modified
 * afterwards.</p>
 */
public record Position(int x, int y) {
//=============================================================================
//                               Construction
//=============================================================================
    /**
     * Canonical constructor validating that both coordinates are non-negative.
     *
     * @throws IllegalArgumentException if {@code x < 0} or {@code y < 0}
     */
    public Position {
        if (x < 0) {
            throw new IllegalArgumentException("La coordonnée x ne peut pas être négative: " + x); // runtime string preserved
        }
        if (y < 0) {
            throw new IllegalArgumentException("La coordonnée y ne peut pas être négative: " + y); // runtime string preserved
        }
    }

//=============================================================================
//                                Utilities
//=============================================================================
    /**
     * Compute the Manhattan distance to another position.
     *
     * @param other other position (may be {@code null})
     * @return {@code Integer.MAX_VALUE} if {@code other} is {@code null}; otherwise {@code |x1-x2| + |y1-y2|}
     */
    public int distanceTo(Position other) {
        if (other == null) return Integer.MAX_VALUE; // DONOTTOUCH[core] (MAINTAINER, 2025-10-06): Legacy null handling.
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }
}
