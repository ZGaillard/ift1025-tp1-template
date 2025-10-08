/* ============================================================================
 * Path: src/student/model/behaviors/Reproducible.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for entities capable of reproduction and spawning offspring.
 * ========================================================================== */
package student.model.behaviors;

import student.model.core.World;
import student.model.organisms.Organism;

/**
 * Defines an entity able to reproduce, creating a new offspring organism that can then be placed
 * into the world if spawning succeeds.
 */
public interface Reproducible {
//=============================================================================
//                                   Contract
//=============================================================================

/**
 * Determine whether current reproduction conditions are satisfied.
 *
 * @param world current world context
 * @return {@code true} if reproduction may proceed
 */
boolean canReproduce(World world);

/**
 * Create a new offspring organism (not yet placed in the world). Implementations decide the
 * initialization parameters of the child.
 *
 * @return newly created organism instance (never placed automatically)
 */
Organism reproduce();

/**
 * Attempt to place the previously created offspring into a free neighboring cell.
 *
 * @param world current world context
 * @return {@code true} if placement succeeds
 */
boolean spawn(World world);
}
