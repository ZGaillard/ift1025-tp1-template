/* ============================================================================
 * Path: src/student/model/behaviors/Hunting.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for predators that can select a target prey position.
 * ========================================================================== */
package student.model.behaviors;

import student.model.core.Position;
import student.model.core.World;

/**
 * Defines an entity capable of selecting a prey target position in order to pursue it.
 * Implementations decide prey identification, tie-breaking, and pathing heuristics.
 */
public interface Hunting {
    //=============================================================================
    //                                   Contract
    //=============================================================================

    /**
     * Choose a position toward which the hunter will move to reach prey. Returning {@code null}
     * indicates no suitable prey was found or that staying in place is preferable this turn.
     *
     * @param world current world context
     * @return prey position or {@code null} if no target exists
     */
    Position chooseHunt(World world);
}
