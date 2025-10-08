package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.controller.SimulationController;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;
import student.model.organisms.Plant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public tests for phase 5 (cleanup).
 * These tests validate the cleanup phase of the simulation,
 * ensuring dead organisms are properly removed from the world.
 */
public class Phase5Test {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int ZERO_ENERGY = 0;

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
 * Verifies cleanup phase with dead organisms.
 * Tests that dead plants and animals are properly removed from the world.
 */
@Test
public void testPhaseCleanupWithDeadOrganisms() {
	final Position plantPosition = new Position(2, 2);
	final Position herbivorePosition = new Position(5, 5);
	final Position carnivorePosition = new Position(7, 7);
	
	// Construct with valid positive energy, then set to 0 to mark as dead.
	final Plant deadPlant = new Plant(1);
	deadPlant.setPosition(plantPosition);
	deadPlant.setEnergy(ZERO_ENERGY); // now dead
	
	final Herbivore deadHerbivore = new Herbivore(1);
	deadHerbivore.setPosition(herbivorePosition);
	deadHerbivore.setEnergy(ZERO_ENERGY); // now dead
	
	final Carnivore deadCarnivore = new Carnivore(1);
	deadCarnivore.setPosition(carnivorePosition);
	deadCarnivore.setEnergy(ZERO_ENERGY); // now dead
	
	world.getCell(plantPosition).setPlant(deadPlant);
	world.getCell(herbivorePosition).setAnimal(deadHerbivore);
	world.getCell(carnivorePosition).setAnimal(deadCarnivore);
	
	controller.phaseCleanup();
	
	assertFalse(world.getCell(plantPosition).hasPlant());
	assertFalse(world.getCell(herbivorePosition).hasAnimal());
	assertFalse(world.getCell(carnivorePosition).hasAnimal());
}

/**
 * Nettoyage strict : seuls les organismes/plantes morts doivent disparaître, les vivants restent.
 */
@Test
public void testCleanupRemovesOnlyDead() {
	// Positions
	Position deadPlantPos = new Position(2, 2);
	Position livePlantPos = new Position(2, 3);
	Position deadHerbPos = new Position(4, 4);
	Position liveHerbPos = new Position(4, 5);
	Position deadCarnPos = new Position(6, 6);
	Position liveCarnPos = new Position(6, 5);
	
	// Dead plant
	Plant deadPlant = new Plant(2);
	deadPlant.setPosition(deadPlantPos);
	deadPlant.setEnergy(0); // mort
	world.getCell(deadPlantPos).setPlant(deadPlant);
	
	// Live plant
	Plant livePlant = new Plant(2);
	livePlant.setPosition(livePlantPos);
	world.getCell(livePlantPos).setPlant(livePlant);
	
	// Dead herbivore
	Herbivore deadHerb = new Herbivore(3);
	deadHerb.setPosition(deadHerbPos);
	deadHerb.setEnergy(0);
	world.getCell(deadHerbPos).setAnimal(deadHerb);
	
	// Live herbivore
	Herbivore liveHerb = new Herbivore(5);
	liveHerb.setPosition(liveHerbPos);
	world.getCell(liveHerbPos).setAnimal(liveHerb);
	
	// Dead carnivore
	Carnivore deadCarn = new Carnivore(4);
	deadCarn.setPosition(deadCarnPos);
	deadCarn.setEnergy(0);
	world.getCell(deadCarnPos).setAnimal(deadCarn);
	
	// Live carnivore
	Carnivore liveCarn = new Carnivore(8);
	liveCarn.setPosition(liveCarnPos);
	world.getCell(liveCarnPos).setAnimal(liveCarn);
	
	controller.phaseCleanup();
	
	// Dead removed
	assertFalse(world.getCell(deadPlantPos).hasPlant(), "Plante morte doit être retirée (implémentez phaseCleanup)");
	assertFalse(world.getCell(deadHerbPos).hasAnimal(), "Herbivore mort doit être retiré");
	assertFalse(world.getCell(deadCarnPos).hasAnimal(), "Carnivore mort doit être retiré");
	// Alive preserved
	assertTrue(world.getCell(livePlantPos).hasPlant(), "Plante vivante ne doit pas être retirée");
	assertTrue(world.getCell(liveHerbPos).hasAnimal(), "Herbivore vivant ne doit pas être retiré");
	assertTrue(world.getCell(liveCarnPos).hasAnimal(), "Carnivore vivant ne doit pas être retiré");
}

/**
 * Idempotence : un second appel ne change plus rien après la première purge.
 */
@Test
public void testCleanupIdempotent() {
	Position pPos = new Position(1, 1);
	Plant deadPlant = new Plant(2);
	deadPlant.setPosition(pPos);
	deadPlant.setEnergy(0);
	world.getCell(pPos).setPlant(deadPlant);
	Position aPos = new Position(2, 2);
	Herbivore deadHerb = new Herbivore(3);
	deadHerb.setPosition(aPos);
	deadHerb.setEnergy(0);
	world.getCell(aPos).setAnimal(deadHerb);
	
	controller.phaseCleanup();
	// Snapshot after first cleanup
	boolean plantGoneFirst = !world.getCell(pPos).hasPlant();
	boolean herbGoneFirst = !world.getCell(aPos).hasAnimal();
	assertTrue(plantGoneFirst && herbGoneFirst, "Première passe doit retirer les morts");
	
	controller.phaseCleanup(); // seconde passe
	// Doit rester identique
	assertEquals(plantGoneFirst, !world.getCell(pPos).hasPlant(), "Aucun changement sur plante après seconde passe");
	assertEquals(herbGoneFirst, !world.getCell(aPos).hasAnimal(), "Aucun changement sur animal après seconde passe");
}

/**
 * Sécurité monde vide : ne lance aucune exception et n'altère rien.
 */
@Test
public void testCleanupEmptyWorldSafe() {
	assertDoesNotThrow(() -> controller.phaseCleanup(), "Phase cleanup ne doit pas lever d'exception sur monde vide");
}
}