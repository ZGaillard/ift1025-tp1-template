// path: prof/test/open/Phase2Test.java
// author: Zakary Gaillard-D.
// date: 2025-10-06
// purpose: Public tests for phase 2 (herbivore movement)
/*
 * Public tests for phase 2 (herbivore movement).
 * These tests validate the herbivore movement phase of the simulation,
 * ensuring herbivores can move and interact properly with their environment.
 */
package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.controller.SimulationController;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Herbivore;
import student.model.organisms.Plant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public tests for phase 2 (herbivore movement).
 * These tests validate the herbivore movement phase of the simulation,
 * ensuring herbivores can move and interact properly with their environment.
 */
public class Phase2Test {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int DEFAULT_HERBIVORE_ENERGY = 8;
private static final int DEFAULT_PLANT_ENERGY = 3;

private SimulationController controller;
private World world;

/**
 * Initializes a 10x10 world and simulation controller before each test.
 */
@BeforeEach
public void setUp() {
	world = new World(WORLD_WIDTH, WORLD_HEIGHT);
	controller = new SimulationController(world);
}

/**
 * Verifies herbivore movement phase.
 * Tests that herbivores consume energy during movement and maintain proper state.
 */
@Test
public void testPhaseHerbivoresMovement() {
	// Herbivore sans plante autour doit se déplacer (s'il y a de l'espace) et perdre 1 énergie
	final Position start = new Position(5, 5);
	final Herbivore h = new Herbivore(8);
	h.setPosition(start);
	world.getCell(start).setAnimal(h);
	controller.phaseHerbivores();
	assertNotEquals(start, h.getPosition(), "L'herbivore devrait se déplacer s'il y a de l'espace (implémentez phaseHerbivores)");
	assertEquals(7, h.getEnergy(), "Énergie devrait diminuer de 1 après déplacement (implémentez coût énergie phaseHerbivores)");
}

/**
 * Verifies herbivore phase with empty world.
 * Tests that the herbivore phase executes safely when no herbivores are present.
 */
@Test
public void testPhaseHerbivoresEmptyWorld() {
	assertDoesNotThrow(() -> controller.phaseHerbivores());
}

/**
 * Verifies herbivore eating behavior during movement phase.
 * Tests that herbivores can find and consume plants during their movement phase.
 */
@Test
public void testPhaseHerbivoresEating() {
	final Position herbivorePosition = new Position(5, 5);
	final Position plantPosition = new Position(5, 6);
	final Herbivore herbivore = new Herbivore(4); // énergie initiale 4
	herbivore.setPosition(herbivorePosition);
	final Plant plant = new Plant(3); // nutrition 3
	plant.setPosition(plantPosition);
	world.getCell(herbivorePosition).setAnimal(herbivore);
	world.getCell(plantPosition).setPlant(plant);
	controller.phaseHerbivores();
	assertEquals(1, herbivorePosition.distanceTo(plantPosition));
	assertEquals(6, herbivore.getEnergy(), "Énergie finale attendue 4 -1 +3 = 6 (implémentez déplacement + alimentation)");
	assertEquals(plantPosition, herbivore.getPosition(), "Herbivore doit se déplacer sur la plante (implémentez move/eat)");
	assertFalse(world.getCell(plantPosition).hasPlant(), "Plante consommée doit disparaître (implémentez suppression plante)");
}

/**
 * Verifies herbivore behavior when blocked by other herbivores.
 * Tests that herbivores lose energy and do not move when blocked by other herbivores.
 */
@Test
public void testPhaseHerbivoresBlockedLoseEnergy() {
	// Herbivore bloqué sur (0,0) doit perdre 1 énergie et ne pas bouger
	final Herbivore h = new Herbivore(5);
	final Position center = new Position(0, 0);
	h.setPosition(center);
	world.getCell(center).setAnimal(h);
	for (Position p : new Position[]{new Position(1, 0), new Position(0, 1), new Position(1, 1)}) {
		Herbivore blocker = new Herbivore(3);
		blocker.setPosition(p);
		world.getCell(p).setAnimal(blocker);
	}
	controller.phaseHerbivores();
	assertEquals(center, h.getPosition(), "Bloqué: la position ne doit pas changer (implémentez détection blocage)");
	assertEquals(4, h.getEnergy(), "Bloqué: énergie -1 (implémentez décrément énergie)");
}
}