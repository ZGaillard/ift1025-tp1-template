// path: prof/test/open/Phase1Test.java
// author: Zakary Gaillard-D.
// date: 2025-10-06
// purpose: Public tests for phase 1 (plant growth)
package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.controller.SimulationController;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Plant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Public tests for phase 1 (plant growth).
 * These tests validate the plant growth phase of the simulation,
 * ensuring plants can grow properly under various conditions.
 */
public class Phase1Test {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int INITIAL_PLANT_ENERGY = 2;
private static final int MAX_PLANT_ENERGY = 3;

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
 * Verifies plant growth phase with multiple plants.
 * Tests that plants can grow and maintain their energy levels during the growth phase.
 */
@Test
public void testPhasePlantGrowthWithSomePlants() {
	final Position position1 = new Position(2, 2);
	final Position position2 = new Position(5, 5);
	final Plant plant1 = new Plant(INITIAL_PLANT_ENERGY - 1); // 1 -> doit devenir 2
	plant1.setPosition(position1);
	final Plant plant2 = new Plant(MAX_PLANT_ENERGY); // déjà au max
	plant2.setPosition(position2);
	world.getCell(position1).setPlant(plant1);
	world.getCell(position2).setPlant(plant2);
	controller.phasePlantGrowth();
	assertEquals(2, plant1.getEnergy(), "Une plante < max doit croître exactement de +1");
	assertEquals(MAX_PLANT_ENERGY, plant2.getEnergy(), "Une plante au max ne doit pas dépasser le plafond");
}

/**
 * Verifies plant growth phase with empty world.
 * Tests that the growth phase executes safely when no plants are present.
 */
@Test
public void testPhasePlantGrowthEmptyWorld() {
	assertDoesNotThrow(() -> controller.phasePlantGrowth());
}

/**
 * Verifies plant growth phase with mature plant.
 * Tests that mature plants maintain energy constraints during growth.
 */
@Test
public void testPhasePlantGrowthMaturePlant() {
	final Position position = new Position(4, 4);
	final Plant plant = new Plant(MAX_PLANT_ENERGY);
	plant.setPosition(position);
	world.getCell(position).setPlant(plant);
	controller.phasePlantGrowth();
	assertEquals(MAX_PLANT_ENERGY, plant.getEnergy(), "Plante mature doit rester au plafond");
}

@Test
public void testPhasePlantGrowthDeadPlantDoesNotGrow() {
	final Position position = new Position(3, 3);
	final Plant plant = new Plant(2);
	plant.setPosition(position);
	world.getCell(position).setPlant(plant);
	plant.setEnergy(0); // morte
	controller.phasePlantGrowth();
	assertEquals(0, plant.getEnergy(), "Plante morte ne doit pas pousser");
}

@Test
public void testPhasePlantGrowthClampAfterMultipleCycles() {
	final Position position = new Position(6, 6);
	final Plant plant = new Plant(1);
	plant.setPosition(position);
	world.getCell(position).setPlant(plant);
	for (int i = 0; i < 10; i++) controller.phasePlantGrowth();
	assertEquals(MAX_PLANT_ENERGY, plant.getEnergy(), "Croissance répétée doit rester clampée à max");
}
}