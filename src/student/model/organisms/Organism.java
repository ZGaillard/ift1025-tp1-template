/* ============================================================================
 * Path: src/student/model/organisms/Organism.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Abstract base for all organisms handling position, energy, and life state.
 * ========================================================================== */
package student.model.organisms;

import student.model.behaviors.Energetic;
import student.model.core.Position;

/**
 * Base abstract organism holding shared state: {@link Position}, energy, and alive flag.
 * <p>Specialized behavior (movement, feeding, reproduction) is defined in subtypes.</p>
 */
public abstract class Organism implements Energetic {
//=============================================================================
//                                   Fields
//=============================================================================
protected Position position;
protected int energy;
protected boolean alive = true;

//=============================================================================
//                               Construction
//=============================================================================
/**
 * Construct an organism with initial energy.
 *
 * @param energy starting energy value
 */
public Organism(int energy) {
	this.energy = energy;
}

//=============================================================================
//                               Accessors
//=============================================================================
/**
 * Return current position (may be {@code null} if not placed).
 *
 * @return position or {@code null}
 */
public Position getPosition() {
	return position;
}

/**
 * Set organism position reference.
 *
 * @param pos new position (may be {@code null})
 */
public void setPosition(Position pos) {
	this.position = pos;
}

//=============================================================================
//                            Energetic Contract
//=============================================================================
/**
 * Return current energy.
 *
 * @return energy value
 */
@Override
public int getEnergy() {
	return energy;
}

/**
 * Set energy; marks organism dead if energy <= 0.
 *
 * @param value new energy value
 */
@Override
public void setEnergy(int value) {
	// TODO: Implement energy setting logic with alive state update.
}

/**
 * Return whether organism is alive.
 *
 * @return {@code true} if alive
 */
@Override
public boolean isAlive() {
	return alive;
}

}
