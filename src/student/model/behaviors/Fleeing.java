/* ============================================================================
 * Path: src/student/model/behaviors/Fleeing.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for organisms that can select an escape position when threatened.
 * ========================================================================== */
package student.model.behaviors;

import student.model.core.Position;
import student.model.core.World;

/**
 * Defines an entity capable of choosing an escape position (flee behavior) when a threat is
 * detected. Implementations decide what constitutes a predator and the heuristic for safety.
 */
public interface Fleeing {
    //=============================================================================
    //                                   Contract
    //=============================================================================

    /**
     * Choose a position to move to in order to flee, or return {@code null} to remain in place
     * when no better option is available.
     *
     * @param world current world context
     * @return target position or {@code null} if staying is chosen
     */
    Position chooseFlee(World world);
}
