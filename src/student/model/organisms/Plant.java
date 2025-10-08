/* ============================================================================
 * Path: src/student/model/organisms/Plant.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Concrete plant organism handling growth, nutrition value, and simple reproduction.
 * ========================================================================== */
package student.model.organisms;

import prof.utils.RandomGenerator;
import student.model.behaviors.Edible;
import student.model.behaviors.Growable;
import student.model.behaviors.Reproducible;
import student.model.core.Cell;
import student.model.core.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a simple plant with bounded energy and reproduction on saturation.
 * <p>Rules:
 * <ul>
 *   <li>Energy range: 1..3</li>
 *   <li>{@link #grow(World)} increases energy by 1 up to the maximum</li>
 *   <li>When energy == 3 and a free neighboring plant slot exists, spawns a new plant and resets
 *   parent energy to 1</li>
 * </ul>
 * Behavior strictly limited to local growth and reproduction logic.
 */
public class Plant extends Organism implements Growable, Edible, Reproducible {
//=============================================================================
//                                   Constants
//=============================================================================
private static final int MAX_ENERGY = 3;

//=============================================================================
//                               Construction
//=============================================================================
/**
 * Construct a plant with initial energy 1.
 */
public Plant() {
	// Default energy is 1.
	super(1);
}

/**
 * Construct a plant with a given initial energy clamped to [1, MAX_ENERGY].
 *
 * @param energy requested starting energy
 */
public Plant(int energy) {
	super(Math.min(Math.max(1, energy), MAX_ENERGY));
}

//=============================================================================
//                                   Growth
//=============================================================================
/**
 * Increase energy by 1 up to {@code MAX_ENERGY} if alive.
 *
 * @param world world context (ignored, required by interface)
 */
@Override
public void grow(World world) {
	// TODO: Implement growth logic.
}

//=============================================================================
//                                  Edible
//=============================================================================
/**
 * Return current energy as nutritional value.
 *
 * @return nutrition points
 */
@Override
public int nutrition() {
	return energy;
}

//=============================================================================
//                               Reproduction
//=============================================================================
/**
 * Determine whether reproduction can occur (alive, saturated energy, free neighbor).
 *
 * @param world world providing neighborhood lookup
 * @return {@code true} if a spawn is possible
 */
@Override
public boolean canReproduce(World world) {
	// TODO - Implement reproduction condition check.
	return false;
}

/**
 * Produce a new plant instance with base energy.
 *
 * @return child organism
 */
@Override
public Organism reproduce() {
	return new Plant();
}

/**
 * Attempt to spawn a child into a neighboring free plant slot.
 * <p>On success resets parent energy to 1.</p>
 *
 * @param world world context
 * @return {@code true} if a child was placed
 */
@Override
public boolean spawn(World world) {
	// TODO - Implement spawning logic.
	return false;
}

//=============================================================================
//                            Energy Management
//=============================================================================
/**
 * Add energy clamped to {@code MAX_ENERGY}.
 *
 * @param amount increment value
 */
@Override
public void addEnergy(int amount) {
	// TODO: Implement energy addition logic.
}

/**
 * Subtract energy and mark dead if depleted.
 *
 * @param amount decrement value
 */
@Override
public void subEnergy(int amount) {
	// TODO: Implement energy subtraction logic.
}
}
