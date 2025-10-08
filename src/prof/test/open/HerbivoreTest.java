// path: prof/test/open/HerbivoreTest.java
// author: Zakary Gaillard-D.
// date: 2025-10-06
// purpose: Public tests for Herbivore behavior
package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prof.utils.RandomGenerator;
import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;
import student.model.organisms.Plant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public tests for the {@link Herbivore} class.
 * These tests are visible to students and validate herbivore behavior
 * including construction, eating, movement, and reproduction.
 */
public class HerbivoreTest {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int DEFAULT_HERBIVORE_ENERGY = 8;
private static final int LOW_ENERGY = 5;
private static final int HIGH_ENERGY_FOR_REPRODUCTION = 10;
private static final int DEFAULT_PLANT_ENERGY = 3;

private World world;

/**
 * Initializes a 10x10 world before each test.
 */
@BeforeEach
public void setUp() {
	world = new World(WORLD_WIDTH, WORLD_HEIGHT);
}

/**
 * Verifies herbivore construction with initial energy.
 * Tests that the herbivore is correctly created with position, energy, and alive state.
 */
@Test
public void testHerbivoreConstruction() {
	final Herbivore herbivore = new Herbivore(DEFAULT_HERBIVORE_ENERGY);
	final Position position = new Position(5, 5);
	herbivore.setPosition(position);
	
	assertNotNull(herbivore);
	assertEquals(position, herbivore.getPosition());
	assertEquals(DEFAULT_HERBIVORE_ENERGY, herbivore.getEnergy());
	assertTrue(herbivore.isAlive());
}

/**
 * Verifies herbivore eating behavior.
 * Tests that a herbivore can eat a plant and gain energy.
 */
@Test
public void testHerbivoreEatPlant() {
	final Herbivore herbivore = new Herbivore(LOW_ENERGY);
	final Position herbivorePosition = new Position(5, 5);
	herbivore.setPosition(herbivorePosition);
	
	final Plant plant = new Plant(DEFAULT_PLANT_ENERGY);
	final Position plantPosition = new Position(5, 6);
	plant.setPosition(plantPosition);
	
	world.getCell(herbivorePosition).setAnimal(herbivore);
	world.getCell(plantPosition).setPlant(plant);
	
	final int initialEnergy = herbivore.getEnergy();
	final Cell targetCell = world.getCell(plantPosition);
	
	assertTrue(herbivore.canEat(targetCell), "canEat devrait retourner true pour une plante adjacente (implémentez Herbivore.canEat)");
	herbivore.eat(targetCell, world);
	assertTrue(herbivore.getEnergy() > initialEnergy, "L'énergie devrait augmenter après avoir mangé (implémentez Herbivore.eat)");
}

/**
 * Verifies herbivore movement behavior.
 * Tests that a herbivore can choose a valid move target.
 */
@Test
public void testHerbivoreMove() {
	final Herbivore herbivore = new Herbivore(DEFAULT_HERBIVORE_ENERGY);
	final Position startPosition = new Position(5, 5);
	herbivore.setPosition(startPosition);
	world.getCell(startPosition).setAnimal(herbivore);
	
	final Cell targetCell = herbivore.chooseMove(world, startPosition);
	assertNotNull(targetCell, "chooseMove ne doit pas retourner null (implémentez Herbivore.chooseMove)");
	assertFalse(targetCell.hasAnimal(), "La case cible devrait être vide (implémentez la logique de déplacement)");
}

/**
 * Verifies herbivore alive state based on energy level.
 * Tests that a herbivore with energy &gt; 0 is alive and with energy = 0 is dead.
 */
@Test
public void testHerbivoreIsAlive() {
	final Herbivore herbivore = new Herbivore(LOW_ENERGY);
	final Position position = new Position(3, 3);
	herbivore.setPosition(position);
	
	assertTrue(herbivore.isAlive());
	
	herbivore.setEnergy(0);
	assertFalse(herbivore.isAlive());
}

/**
 * Verifies herbivore reproduction behavior.
 * Tests that a herbivore with sufficient energy can reproduce.
 */
@Test
public void testHerbivoreReproduction() {
	final Herbivore herbivore = new Herbivore(HIGH_ENERGY_FOR_REPRODUCTION);
	final Position position = new Position(5, 5);
	herbivore.setPosition(position);
	world.getCell(position).setAnimal(herbivore);
	
	assertTrue(herbivore.canReproduce(world), "canReproduce devrait être true avec énergie suffisante (implémentez Herbivore.canReproduce)");
	final int initialEnergy = herbivore.getEnergy();
	final boolean spawned = herbivore.spawn(world);
	assertTrue(spawned, "spawn devrait réussir (implémentez Herbivore.spawn si nécessaire)");
	assertTrue(herbivore.getEnergy() < initialEnergy, "L'énergie du parent devrait diminuer après reproduction");
}

@Test
public void testAddEnergyClamp() {
    RandomGenerator.reseed(1234L);
    Herbivore h = new Herbivore(9); // max 10
    h.addEnergy(10); // dépasse
    assertEquals(10, h.getEnergy(), "Énergie herbivore plafonnée à 10");
}

@Test
public void testReproductionHalvesEnergyOnSpawn() {
    RandomGenerator.reseed(1234L);
    Herbivore h = new Herbivore(9); // >= threshold 7
    Position pos = new Position(2, 2);
    world.getCell(pos).setAnimal(h);
    h.setPosition(pos);
    int before = h.getEnergy();
    boolean spawned = h.spawn(world);
    assertTrue(spawned, "Spawn devrait réussir avec énergie suffisante et voisins libres");
    assertEquals(before - before / 2, h.getEnergy(), "L'énergie doit être réduite de moitié après reproduction");
}

@Test
public void testEatIncreasesEnergyButClamped() {
    RandomGenerator.reseed(1234L);
    Herbivore h = new Herbivore(9); // energy 9
    Position hPos = new Position(1, 1);
    world.getCell(hPos).setAnimal(h);
    h.setPosition(hPos);
    Plant p = new Plant(3); // plante énergie 3
    Position plantPos = new Position(2, 1);
    world.getCell(plantPos).setPlant(p);
    p.setPosition(plantPos);
    Cell dest = h.chooseMove(world, hPos);
    assertNotNull(dest, "chooseMove devrait cibler une plante si présente");
    assertEquals(plantPos, dest.getPosition(), "Doit se déplacer vers la plante");
    assertTrue(h.canEat(dest), "canEat devrait être true sur la plante");
    h.eat(dest, world);
    assertEquals(10, h.getEnergy(), "Gain d'énergie plafonné à 10");
}

@Test
public void testChooseHighestEnergyPlantAmongMany() {
    RandomGenerator.reseed(1234L);
    Herbivore h = new Herbivore(4);
    Position pos = new Position(3, 3);
    world.getCell(pos).setAnimal(h);
    h.setPosition(pos);
    Plant p1 = new Plant(1);
    Position p1Pos = new Position(2, 3);
    world.getCell(p1Pos).setPlant(p1);
    p1.setPosition(p1Pos);
    Plant p2 = new Plant(3);
    Position p2Pos = new Position(3, 4);
    world.getCell(p2Pos).setPlant(p2);
    p2.setPosition(p2Pos);
    Plant p3 = new Plant(3);
    Position p3Pos = new Position(4, 3);
    world.getCell(p3Pos).setPlant(p3);
    p3.setPosition(p3Pos);
    Cell dest = h.chooseMove(world, pos);
    assertNotNull(dest, "chooseMove doit retourner une cellule");
    assertTrue(dest.getPosition().equals(p2Pos) || dest.getPosition().equals(p3Pos), "Doit viser une plante d'énergie maximale");
}

@Test
public void testFleeChoosesFarthest() {
    // Arrange
    RandomGenerator.reseed(1234L);
    World fleeWorld = new World(7, 7);
    Herbivore h = new Herbivore(6); // en dessous reproduction
    Position hPos = new Position(3, 3);
    fleeWorld.getCell(hPos).setAnimal(h);
    h.setPosition(hPos);
    // Placer un carnivore (prédateur) juste au nord pour forcer la fuite
    Carnivore predator = new Carnivore(8);
    Position predatorPos = new Position(3, 2);
    fleeWorld.getCell(predatorPos).setAnimal(predator);
    predator.setPosition(predatorPos);

    // Act
    Position fleeTarget = h.chooseFlee(fleeWorld);

    // Assert
    assertNotNull(fleeTarget, "La fuite ne doit pas être null lorsqu'un prédateur est perçu (implémentez Herbivore.chooseFlee)");
    // Le prédateur est en (3,2). Les positions perçues sont l'anneau 3x3 autour de (3,3).
    // Distances Manhattan au prédateur (3,2) parmi les cellules libres les plus éloignées: (2,4) et (4,4) avec distance 3.
    int dist = fleeTarget.distanceTo(predatorPos);
    assertTrue(dist >= 0, "Distance calculable");
    int maxExpected = 3; // distance maximale possible dans la couronne 3x3
    assertEquals(maxExpected, dist, "La position de fuite doit maximiser la distance au prédateur (implémentez la sélection dans chooseFlee)");
    assertTrue((fleeTarget.equals(new Position(2,4)) || fleeTarget.equals(new Position(4,4))),
        "La fuite doit viser une des positions les plus éloignées (2,4) ou (4,4) – implémentez Herbivore.chooseFlee");
}

@Test
public void testSpawnFailsWhenNoEmptyNeighbor() {
    RandomGenerator.reseed(1234L);
    Herbivore h = new Herbivore(10);
    Position center = new Position(2, 2);
    world.getCell(center).setAnimal(h);
    h.setPosition(center);
    for (Position p : new Position[]{new Position(2, 1), new Position(2, 3), new Position(1, 2), new Position(3, 2)}) {
        Herbivore blocker = new Herbivore(3);
        world.getCell(p).setAnimal(blocker);
        blocker.setPosition(p);
    }
    assertTrue(h.canReproduce(world), "Condition reproduction vraie (énergie suffisante)");
    assertFalse(h.spawn(world), "Spawn doit échouer faute d'emplacement libre");
    assertEquals(10, h.getEnergy(), "Énergie inchangée car reproduction échouée");
}
}