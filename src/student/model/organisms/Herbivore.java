/* ============================================================================
 * Path: src/student/model/organisms/Herbivore.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Herbivore animal seeking high-energy plants and fleeing nearby carnivores.
 * ========================================================================== */
package student.model.organisms;

import student.model.behaviors.Edible;
import student.model.behaviors.Fleeing;
import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;

/**
 * Herbivore organism that:
 * <ul>
 *   <li>Flees carnivores when detected</li>
 *   <li>Prefers moving toward the highest-energy plant in range</li>
 *   <li>Reproduces when energy threshold is met</li>
 * </ul>
 */
public class Herbivore extends Animal implements Fleeing, Edible {
//=============================================================================
//                                   Constants
//=============================================================================
private static final int MAX_ENERGY = 10;
private static final int REPRODUCTION_THRESHOLD = 7;
private static final int VISION_RANGE = 2; // 3x3
private static final int MOVEMENT_RANGE = 2; // 3x3

//=============================================================================
//                               Construction
//=============================================================================
/**
 * Construct a herbivore with base energy 3.
 */
public Herbivore() {
	super(3);
}

/**
 * Construct a herbivore with clamped initial energy.
 *
 * @param energy requested starting energy
 */
public Herbivore(int energy) {
	super(Math.min(Math.max(1, energy), MAX_ENERGY));
}

//=============================================================================
//                                Perception
//=============================================================================
/**
 * Return herbivore vision range.
 *
 * @return vision range radius
 */
@Override
public int visionRange() {
    return VISION_RANGE;
}

//=============================================================================
//                                   Fleeing
//=============================================================================
/**
 * Choose a position maximizing distance from a nearby carnivore if one is perceived.
 *
 * @param world world context
 * @return flee destination or {@code null} if no threat detected
 */
@Override
public Position chooseFlee(World world) {
	// TODO - Implémenter la méthode chooseFlee pour Herbivore
	return null;
}

//=============================================================================
//                                  Movement
//=============================================================================
/**
 * Choose movement toward the highest-energy plant; fallback to random empty cell.
 *
 * @param world world context
 * @param pos current position
 * @return destination cell or {@code null}
 */
@Override
public Cell chooseMove(World world, Position pos) {
	// TODO - Implémenter la méthode chooseMove pour Herbivore
	return null;
}

//=============================================================================
//                                   Feeding
//=============================================================================
/**
 * Return whether the herbivore can eat the plant in the cell.
 *
 * @param cell target cell
 * @return {@code true} if a plant is present
 */
@Override
public boolean canEat(Cell cell) {
	// TODO - Implémenter la vérification de la présence d'une plante
	return false;
}

/**
 * Consume a plant if present.
 *
 * @param cell target cell
 * @param world world context
 */
@Override
public void eat(Cell cell, World world) {
	// TODO - Implémenter la consommation de la plante
}

//=============================================================================
//                                   Edible
//=============================================================================
/**
 * Return nutrition value (current energy).
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
 * Return whether reproduction threshold is reached.
 *
 * @param world world context
 * @return {@code true} if energy permits reproduction
 */
@Override
public boolean canReproduce(World world) {
	// TODO - Implémenter la vérification des conditions de reproduction
	return false;
}

/**
 * Create a child herbivore with base energy.
 *
 * @return new herbivore
 */
@Override
public Organism reproduce() {
	return new Herbivore(3);
}

//=============================================================================
//                            Energy Management
//=============================================================================
/**
 * Add energy clamped to {@code MAX_ENERGY}.
 *
 * @param amount increment amount
 */
@Override
public void addEnergy(int amount) {
	setEnergy(Math.min(MAX_ENERGY, getEnergy() + amount));
}
}
