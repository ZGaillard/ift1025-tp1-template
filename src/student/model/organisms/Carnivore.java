/* ============================================================================
 * Path: src/student/model/organisms/Carnivore.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Carnivore animal that hunts herbivores with directed movement and reproduction.
 * ========================================================================== */
package student.model.organisms;

import student.model.behaviors.Hunting;
import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;

/**
 * Carnivore organism that:
 * <ul>
 *   <li>Searches for herbivores within an extended vision range</li>
 *   <li>Moves directionally toward detected prey</li>
 *   <li>Reproduces at a higher energy threshold</li>
 * </ul>
 */
public class Carnivore extends Animal implements Hunting {
//=============================================================================
//                                   Constants
//=============================================================================
private static final int MAX_ENERGY = 20;
private static final int REPRODUCTION_THRESHOLD = 14;
private static final int VISION_RANGE = 3; // 5x5
private static final int MOVEMENT_RANGE = 2; // 3x3

//=============================================================================
//                               Construction
//=============================================================================

/**
 * Construct a carnivore with base energy 5.
 */
public Carnivore() {
	super(5);
}

/**
 * Construct a carnivore with clamped initial energy.
 *
 * @param energy requested starting energy
 */
public Carnivore(int energy) {
	super(Math.min(Math.max(1, energy), MAX_ENERGY));
}

//=============================================================================
//                                Perception
//=============================================================================

/**
 * Return carnivore vision range (greater than herbivores).
 *
 * <p>Vision ranges:</p>
 * <ul>
 *   <li>1 → cross pattern (4 adjacent cells)</li>
 *   <li>2 → 3×3 grid (8 surrounding cells)</li>
 *   <li>3 → 5×5 grid (24 surrounding cells)</li>
 * </ul>
 *
 * @return vision range radius (logical level, not direct square radius)
 */
@Override
public int visionRange() {
	return VISION_RANGE;
}

//=============================================================================
//                                  Hunting
//=============================================================================

/**
 * Choose a prey position (herbivore) inside vision if any.
 * Uses the same semantic as neighborhood construction:
 * vision 1: cross (4)
 * vision 2: full 3x3 (radius 1 square excluding center)
 * vision 3: full 5x5 (radius 2 square excluding center)
 *
 * @param world world context
 * @return prey position or {@code null}
 */
@Override
public Position chooseHunt(World world) {
	// TODO - Implémenter la détection de proies
	return null;
}

//=============================================================================
//                                  Movement
//=============================================================================

/**
 * Move toward prey if detected; else random adjacent move.
 *
 * @param world world context
 * @param pos   current position
 * @return destination cell or {@code null}
 */
@Override
public Cell chooseMove(World world, Position pos) {
	// TODO - Implémenter le déplacement dirigé vers la proie
	return null;
}

//=============================================================================
//                                   Feeding
//=============================================================================

/**
 * Return whether the cell contains an herbivore to consume.
 *
 * @param cell target cell
 * @return {@code true} if herbivore present
 */
@Override
public boolean canEat(Cell cell) {
	// TODO - Implémenter la vérification de la présence d'un herbivore
	return false;
}

/**
 * Consume herbivore if present.
 *
 * @param cell  target cell
 * @param world world context
 */
@Override
public void eat(Cell cell, World world) {
	// TODO - Implémenter la consommation de l'herbivore
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
 * Create a child carnivore with base energy.
 *
 * @return new carnivore
 */
@Override
public Organism reproduce() {
	return new Carnivore(5);
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
