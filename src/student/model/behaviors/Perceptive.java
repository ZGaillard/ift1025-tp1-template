/* ============================================================================
 * Path: src/student/model/behaviors/Perceptive.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for entities that can perceive surrounding positions.
 * ========================================================================== */
package student.model.behaviors;

import student.model.core.Position;
import student.model.core.World;

import java.util.List;

/**
 * Defines an entity that can perceive positions within a vision range around its current location.
 * <p>Vision levels (suggested semantics, implementations may refine):</p>
 * <ul>
 *   <li>Level 1: cross (N, S, E, W) – 4 cells.</li>
 *   <li>Level 2: full 3x3 square – 8 surrounding cells.</li>
 *   <li>Level 3: full 5x5 square – 24 surrounding cells.</li>
 * </ul>
 */
public interface Perceptive {
//=============================================================================
//                                   Contract
//=============================================================================

/**
 * Return the maximum vision range level used to compute perceivable positions.
 *
 * @return vision level (implementation-defined positive integer)
 */
int visionRange();

/**
 * Return the list of perceivable positions given the world and current position.
 *
 * @param world world context used to validate bounds and contents
 * @param pos   current position of the perceiving entity
 * @return immutable or caller-safe list of visible positions (ordering is implementation-defined)
 */
List<Position> perceive(World world, Position pos);
}
