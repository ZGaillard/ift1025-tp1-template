/* ============================================================================
 * Path: src/student/model/behaviors/Growable.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for entities that can increase stored resources (grow step).
 * ========================================================================== */
package student.model.behaviors;

import student.model.core.World;

/**
 * Defines an entity that performs a growth step, typically increasing internal energy or size.
 */
public interface Growable {
//=============================================================================
//                                   Contract
//=============================================================================

/**
 * Apply one growth step for this entity within the provided world context.
 * Implementations decide resource constraints and caps.
 *
 * @param world current world context
 */
void grow(World world);
}
