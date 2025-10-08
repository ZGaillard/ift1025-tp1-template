package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Plant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public tests for the {@link Plant} class.
 * These tests are visible to students and validate basic plant functionality
 * including construction, growth, energy management, and lifecycle.
 */
public class PlantTest {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int INITIAL_ENERGY = 2;
private static final int MAX_PLANT_ENERGY = 3;
private static final int MATURE_ENERGY_THRESHOLD = 2;

private World world;

/**
 * Initializes a 10x10 world before each test.
 */
@BeforeEach
public void setUp() {
	world = new World(WORLD_WIDTH, WORLD_HEIGHT);
}

/**
 * Verifies plant construction with initial energy.
 * Tests that the plant is correctly created with position, energy, and alive state.
 */
@Test
public void testPlantConstruction() {
	final Plant plant = new Plant(INITIAL_ENERGY);
	final Position position = new Position(5, 5);
	plant.setPosition(position);
	
	assertNotNull(plant, "Plant should not be null");
	assertEquals(position, plant.getPosition(), "Position should be correct");
	assertEquals(INITIAL_ENERGY, plant.getEnergy(), "Initial energy should be correct");
	assertTrue(plant.isAlive(), "Plant should be alive");
}

/**
 * Verifies plant getter methods.
 * Tests that {@code getEnergy()} and {@code getPosition()} return correct values.
 */
@Test
public void testPlantGetters() {
	final Plant plant = new Plant(INITIAL_ENERGY);
	final Position position = new Position(3, 3);
	plant.setPosition(position);
	
	assertEquals(INITIAL_ENERGY, plant.getEnergy(),
		"getEnergy() should return correct energy");
	assertEquals(position, plant.getPosition(),
		"getPosition() should return correct position");
}

/**
 * Verifies plant alive or dead state based on energy level.
 * Tests that a plant with energy &gt; 0 is alive and a plant with energy = 0 is dead.
 */
@Test
public void testPlantIsAlive() {
	final Plant plant = new Plant(INITIAL_ENERGY);
	final Position position = new Position(2, 2);
	plant.setPosition(position);
	
	assertTrue(plant.isAlive(), "Plant with energy > 0 should be alive");
	
	plant.setEnergy(0);
	assertFalse(plant.isAlive(), "Plant with energy = 0 should be dead");
}


/**
 * Verifies the plant growth mechanism.
 * Tests that plant energy increases after a growth cycle.
 */
@Test
public void testPlantGrowth() {
    final Plant plant = new Plant(1); // démarrer au minimum pour observer l'évolution
    final Position position = new Position(5, 5);
    plant.setPosition(position);
    world.getCell(position).setPlant(plant);

    int last = plant.getEnergy();
    int cycles = 10; // suffisamment pour atteindre et tester le plafond
    for (int i = 0; i < cycles; i++) {
        plant.grow(world);
        int current = plant.getEnergy();
        assertTrue(current >= last, "L'énergie ne doit pas diminuer pendant la croissance (itération " + i + ")");
        assertTrue(current <= MAX_PLANT_ENERGY, "L'énergie ne doit jamais dépasser " + MAX_PLANT_ENERGY + " (itération " + i + ")");
        last = current;
    }
    assertEquals(MAX_PLANT_ENERGY, plant.getEnergy(), "Après suffisamment de cycles la plante doit atteindre le maximum exactement");
}

/**
 * Verifies addEnergy clamps at MAX_PLANT_ENERGY.
 */
@Test
public void testPlantAddEnergyClampPublic() {
	Plant p = new Plant(2);
	p.addEnergy(10); // devrait être clampé à 3
	assertEquals(MAX_PLANT_ENERGY, p.getEnergy(), "addEnergy doit plafonner l'énergie (implémentez Plant.addEnergy)");
}

/**
 * Verifies subEnergy can kill the plant when energy depletes.
 */
@Test
public void testPlantSubEnergyDeathPublic() {
	Plant p = new Plant(2);
	p.subEnergy(5); // devrait tuer
	assertFalse(p.isAlive(), "La plante devrait mourir si énergie <= 0 (implémentez Plant.subEnergy)");
}

/**
 * Reproduction should require max energy and a free cardinal neighbor.
 */
@Test
public void testPlantCanReproduceAndSpawnPublic() {
	Plant parent = new Plant(MAX_PLANT_ENERGY);
	Position center = new Position(2, 2);
	parent.setPosition(center);
	world.getCell(center).setPlant(parent);
	// Vérifier condition reproduction
	assertTrue(parent.canReproduce(world), "canReproduce devrait être true avec énergie max et voisin libre (implémentez Plant.canReproduce)");
	boolean spawned = parent.spawn(world);
	assertTrue(spawned, "spawn devrait réussir avec voisin libre (implémentez Plant.spawn)");
	assertEquals(1, parent.getEnergy(), "Après spawn l'énergie devrait être réinitialisée à 1 (implémentez reset dans spawn)");
}

/**
 * Dead plant should not grow.
 */
@Test
public void testDeadPlantDoesNotGrow() {
	Plant p = new Plant(2);
	p.subEnergy(5); // tue la plante
	int before = p.getEnergy();
	p.grow(world);
	assertEquals(before, p.getEnergy(), "Une plante morte ne doit pas pousser (implémentez le garde-fou dans grow)");
}

/**
 * Vérifie la reproduction diagonale seulement.
 * Teste qu'une plante peut se reproduire si une case diagonale est libre,
 * mais l'énergie doit rester inchangée si le spawn échoue.
 */
@Test
public void testPlantCanReproduceDiagonalOnlyButSpawnFails() {
	Position center = new Position(2, 2);
	Plant parent = new Plant(3);
	parent.setPosition(center);
	world.getCell(center).setPlant(parent);
	// Bloquer les 4 cardinaux
	for (Position p : new Position[]{new Position(2,1), new Position(2,3), new Position(1,2), new Position(3,2)}) {
		Plant blocker = new Plant(1);
		blocker.setPosition(p);
		world.getCell(p).setPlant(blocker);
	}
	// Laisser diagonales libres: canReproduce doit refléter la logique attendue (selon impl. étudiant)
	assertTrue(parent.canReproduce(world), "canReproduce devrait être vrai si une diagonale est libre (implémentez Plant.canReproduce)");
	boolean spawned = parent.spawn(world);
	assertFalse(spawned, "spawn doit échouer car aucune case cardinale libre (implémentez Plant.spawn)");
	assertEquals(3, parent.getEnergy(), "Énergie ne doit pas être réinitialisée en cas d'échec (implémentez Plant.spawn)");
}

/**
 * A plant with energy < max (3) must NOT be able to reproduce.
 */
@Test
public void testPlantCannotReproduceLowEnergy() {
    Plant parent = new Plant(2); // énergie insuffisante
    Position center = new Position(3, 3);
    parent.setPosition(center);
    world.getCell(center).setPlant(parent);
    assertFalse(parent.canReproduce(world), "canReproduce doit être false si énergie < 3 (implémentez Plant.canReproduce)");
    boolean spawned = parent.spawn(world);
    assertFalse(spawned, "spawn doit échouer si énergie insuffisante (implémentez Plant.spawn)");
    assertEquals(2, parent.getEnergy(), "Énergie ne doit pas être modifiée après un spawn échoué (implémentez Plant.spawn)");
}
}