// path: prof/test/open/Phase3Test.java
// author: Zakary Gaillard-D.
// date: 2025-10-06
// purpose: Public tests for phase 3 (carnivore movement)

package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.controller.SimulationController;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public tests for phase 3 (carnivore movement).
 * These tests validate the carnivore movement phase of the simulation,
 * ensuring carnivores can move, hunt, and interact properly with their environment.
 */
public class Phase3Test {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int DEFAULT_CARNIVORE_ENERGY = 12;
private static final int HIGH_CARNIVORE_ENERGY = 15;
private static final int DEFAULT_HERBIVORE_ENERGY = 8;

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
 * Verifies carnivore movement phase.
 * Tests that carnivores consume energy during movement and maintain proper state.
 */
@Test
public void testPhaseCarnivoresMovement() {
	final Position start = new Position(5, 5);
	final Carnivore c = new Carnivore(DEFAULT_CARNIVORE_ENERGY); // 12
	c.setPosition(start);
	world.getCell(start).setAnimal(c);
	// Aucun herbivore dans la vision => devrait tenter un déplacement (si espace) et perdre 1 énergie
	controller.phaseCarnivores();
	assertNotEquals(start, c.getPosition(), "Carnivore devrait se déplacer en absence de proie (implémentez phaseCarnivores)");
	assertEquals(DEFAULT_CARNIVORE_ENERGY - 1, c.getEnergy(), "Énergie doit diminuer de 1 après déplacement (implémentez coût énergie)");
}

/**
 * Verifies carnivore phase with empty world.
 * Tests that the carnivore phase executes safely when no carnivores are present.
 */
@Test
public void testPhaseCarnivoresEmptyWorld() {
	assertDoesNotThrow(() -> controller.phaseCarnivores());
}

/**
 * Verifies carnivore hunting behavior during movement phase.
 * Tests that carnivores can detect and pursue herbivores during their movement phase.
 */
@Test
public void testPhaseCarnivoresHunting() {
	final Position carnivorePosition = new Position(5, 5);
	final Position herbivorePosition = new Position(6, 6); // Dans la vision (diagonale)
	final Carnivore carnivore = new Carnivore(HIGH_CARNIVORE_ENERGY); // 15
	carnivore.setPosition(carnivorePosition);
	final Herbivore herbivore = new Herbivore(DEFAULT_HERBIVORE_ENERGY); // 8
	herbivore.setPosition(herbivorePosition);
	world.getCell(carnivorePosition).setAnimal(carnivore);
	world.getCell(herbivorePosition).setAnimal(herbivore);
	controller.phaseCarnivores();
	// Attendu : mouvement d'une case vers la proie (pas de téléportation), distance réduite de 1
	int newDist = carnivore.getPosition().distanceTo(herbivorePosition);
	assertTrue(newDist < carnivorePosition.distanceTo(herbivorePosition), "La distance à la proie doit diminuer (implémentez déplacement dirigé)");
}

@Test
public void testPhaseCarnivoresAdjacencyHuntConsumes() {
	final Carnivore c = new Carnivore(14); // énergie 14
	final Herbivore h = new Herbivore(6);  // nutrition 6
	final Position preyPos = new Position(4, 4);
	final Position cPos = new Position(4, 3); // Adjacent
	c.setPosition(cPos);
	h.setPosition(preyPos);
	world.getCell(cPos).setAnimal(c);
	world.getCell(preyPos).setAnimal(h);
	controller.phaseCarnivores();
	// Énergie attendue : 14 -1 +6 = 19 (clamp < 20 toujours ici)
	assertEquals(19, c.getEnergy(), "Énergie après chasse adjacente attendue 14 -1 + 6 = 19 (implémentez chasse + alimentation)");
	assertEquals(preyPos, c.getPosition(), "Carnivore doit se déplacer sur la proie adjacente (implémentez mouvement sur proie)");
	assertFalse(world.getCell(preyPos).hasAnimal() && world.getCell(preyPos).getAnimal() instanceof Herbivore, "Herbivore doit être retiré après consommation (implémentez remove)");
}

@Test
public void testPhaseCarnivoresBlockedLoseEnergy() {
	final Carnivore c = new Carnivore(10);
	final Position corner = new Position(0, 0);
	c.setPosition(corner);
	world.getCell(corner).setAnimal(c);
	// Bloquer 3 voisins
	for (Position p : new Position[]{new Position(1, 0), new Position(0, 1), new Position(1, 1)}) {
		Carnivore blocker = new Carnivore(5);
		blocker.setPosition(p);
		world.getCell(p).setAnimal(blocker);
	}
	controller.phaseCarnivores();
	assertEquals(corner, c.getPosition(), "Bloqué: pas de déplacement (implémentez détection blocage)");
	assertEquals(9, c.getEnergy(), "Bloqué: énergie -1 (implémentez décrément énergie)");
}
}