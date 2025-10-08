/* ============================================================================
 * Path: src/student/model/behaviors/Movable.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for entities capable of selecting a movement destination.
 * ========================================================================== */
package student.model.behaviors;

import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;

/**
 * Defines an entity that can select a destination cell to move to for the current turn.
 * Implementations decide collision rules, path constraints, and tie-breaking.
 */
public interface Movable {
//=============================================================================
//                                   Contract
//=============================================================================

/**
 * Choose a destination cell for movement or return {@code null} to stay in place.
 *
 * @param world current world context
 * @param pos   current position of the entity
 * @return chosen destination cell or {@code null} to remain stationary
 */
Cell chooseMove(World world, Position pos);
}
