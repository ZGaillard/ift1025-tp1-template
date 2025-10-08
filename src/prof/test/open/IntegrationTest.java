// author: Zakary Gaillard-D.
// date: 2025-10-06
// purpose: Public integration tests for simulation phases
package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prof.utils.RandomGenerator;
import student.controller.SimulationController;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;
import student.model.organisms.Plant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public integration tests for simulation phases.
 * These tests validate that different components work together correctly
 * and that the simulation can execute complete turns without errors.
 */
public class IntegrationTest {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int SIMULATION_STEPS = 100;

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
 * Scénario intégral d'un tour complet avec effets vérifiés (croissance -> herbivore mange -> carnivore chasse).
 */
@Test
public void testFullTurnExecutionStrict() {
	RandomGenerator.reseed(12345L);
	// Positions choisies pour forcer l'enchaînement déterministe
	// Phase 1: la plante (énergie 2) passe à 3
	// Phase 2: l'herbivore (énergie 5) se déplace sur la plante et la mange : 5 -1 +3 = 7
	// Phase 3: le carnivore adjacent à la nouvelle position de l'herbivore le chasse et le mange : 10 -1 +7 = 16
	
	final Plant plant = new Plant(2);
	final Position plantPos = new Position(3, 4);
	plant.setPosition(plantPos);
	world.getCell(plantPos).setPlant(plant);
	
	final Herbivore herbivore = new Herbivore(5);
	final Position herbPos = new Position(3, 3); // au-dessus de la plante
	herbivore.setPosition(herbPos);
	world.getCell(herbPos).setAnimal(herbivore);
	
	final Carnivore carnivore = new Carnivore(10);
	final Position carnPos = new Position(3, 5); // sous la plante -> adjacent à l'herbivore après phase 2
	carnivore.setPosition(carnPos);
	world.getCell(carnPos).setAnimal(carnivore);
	
	controller.step(); // exécute toutes les phases
	
	// Plante mangée pendant la phase herbivores
	assertFalse(world.getCell(plantPos).hasPlant(), "Plante doit être consommée (phase herbivores)");
	// Herbivore devrait avoir été mangé pendant phase carnivores
	boolean herbStillThere = world.getCell(plantPos).hasAnimal() && world.getCell(plantPos).getAnimal() instanceof Herbivore;
	assertFalse(herbStillThere, "Herbivore doit être consommé (phase carnivores)");
	// Carnivore doit maintenant occuper la case de l'ancienne plante ou de la proie
	boolean carnAtPrey = world.getCell(plantPos).hasAnimal() && world.getCell(plantPos).getAnimal() instanceof Carnivore;
	assertTrue(carnAtPrey, "Carnivore doit se déplacer sur la proie (phase carnivores)");
	// Énergie attendue carnivore (si logique appliquée) : 16 (10 -1 +7) ou clamp <= 20
	int energy = ((Carnivore) world.getCell(plantPos).getAnimal()).getEnergy();
	assertTrue(energy >= 11 && energy <= 20, "Énergie carnivore doit refléter chasse (>= 11 après -1 + nutrition)");
}

/**
 * Scénario multi-tours : reproduction attendue puis nettoyage après mort artificielle.
 */
@Test
public void testMultiTurnReproductionAndCleanupIntegration() {
	RandomGenerator.reseed(2024L);
	// Préparer une plante max énergie, herbivore et carnivore haute énergie
	Plant plant = new Plant(3);
	Position plantPos = new Position(2,2);
	plant.setPosition(plantPos);
	world.getCell(plantPos).setPlant(plant);
	Herbivore herb = new Herbivore(12); // au-dessus seuil reproduction (>7)
	Position herbPos = new Position(4,4);
	herb.setPosition(herbPos);
	world.getCell(herbPos).setAnimal(herb);
	Carnivore carn = new Carnivore(18); // au-dessus seuil reproduction (>=14)
	Position carnPos = new Position(6,6);
	carn.setPosition(carnPos);
	world.getCell(carnPos).setAnimal(carn);
	int plantsBefore = countPlants();
	int herbsBefore = countHerbivores();
	int carnBefore = countCarnivores();
	
	controller.step(); // reproduction se produit dans ce tour (phase 4)
	
	assertTrue(countPlants() >= plantsBefore, "Plantes >= avant (reproduction possible)");
	assertTrue(countHerbivores() >= herbsBefore, "Herbivores >= avant (reproduction possible)");
	assertTrue(countCarnivores() >= carnBefore, "Carnivores >= avant (reproduction possible)");
	
	// Forcer la mort de toutes les instances initiales et relancer un tour pour cleanup
	plant.setEnergy(0);
	herb.setEnergy(0);
	carn.setEnergy(0);
	controller.step(); // phase 5 devrait les retirer
	
	assertFalse(world.getCell(plantPos).hasPlant(), "Plante morte nettoyée (integration cleanup)");
	assertFalse(world.getCell(herbPos).hasAnimal(), "Herbivore mort nettoyé (integration cleanup)");
	assertFalse(world.getCell(carnPos).hasAnimal(), "Carnivore mort nettoyé (integration cleanup)");
}

/**
 * Vérifie que plusieurs steps consécutifs respectent les invariants globaux (pas d'énergie > max plante, monde dimensions constantes).
 */
@Test
public void testMultipleTurnsInvariantsStrict() {
	RandomGenerator.reseed(9999L);
	// Seed minimal : quelques plantes et herbivores
	for (int x = 1; x <= 3; x++) {
		Plant p = new Plant(2);
		Position pos = new Position(x, 1);
		p.setPosition(pos);
		world.getCell(pos).setPlant(p);
	}
	for (int x = 1; x <= 2; x++) {
		Herbivore h = new Herbivore(6);
		Position pos = new Position(x, 3);
		h.setPosition(pos);
		world.getCell(pos).setAnimal(h);
	}
	
	for (int i = 0; i < 8; i++) controller.step();
	
	assertEquals(WORLD_WIDTH, world.getWidth(), "Largeur monde invariant");
	assertEquals(WORLD_HEIGHT, world.getHeight(), "Hauteur monde invariant");
	// Énergie plantes dans borne
	for (int y = 0; y < world.getHeight(); y++) {
		for (int x = 0; x < world.getWidth(); x++) {
			if (world.getCell(new Position(x,y)).hasPlant()) {
				int e = world.getCell(new Position(x,y)).getPlant().getEnergy();
				assertTrue(e >= 0 && e <= 3, "Énergie plante bornée 0..3");
			}
		}
	}
}

// ---------------- Helpers (copiés pour éviter dépendances croisées) ----------------
private int countPlants() {
	int c = 0;
	for (int y = 0; y < world.getHeight(); y++)
		for (int x = 0; x < world.getWidth(); x++)
			if (world.getCell(new Position(x, y)).hasPlant()) c++;
	return c;
}
private int countHerbivores() {
	int c = 0;
	for (int y = 0; y < world.getHeight(); y++)
		for (int x = 0; x < world.getWidth(); x++)
			if (world.getCell(new Position(x, y)).hasAnimal() && world.getCell(new Position(x, y)).getAnimal() instanceof Herbivore) c++;
	return c;
}
private int countCarnivores() {
	int c = 0;
	for (int y = 0; y < world.getHeight(); y++)
		for (int x = 0; x < world.getWidth(); x++)
			if (world.getCell(new Position(x, y)).hasAnimal() && world.getCell(new Position(x, y)).getAnimal() instanceof Carnivore) c++;
	return c;
}
}

