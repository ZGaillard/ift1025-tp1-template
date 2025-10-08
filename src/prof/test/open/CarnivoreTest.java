// path: prof/test/open/CarnivoreTest.java
// author: Zakary Gaillard-D.
// date: 2025-10-06
// purpose: Public tests for Carnivore behavior
package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;
import prof.utils.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public tests for the {@link Carnivore} class.
 * These tests are visible to students and validate carnivore behavior
 * including construction, hunting, eating, movement, and reproduction.
 */
public class CarnivoreTest {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int DEFAULT_CARNIVORE_ENERGY = 15;
private static final int DEFAULT_HERBIVORE_ENERGY = 8;
private static final int HIGH_ENERGY_FOR_REPRODUCTION = 20;

private World world;

/**
 * Initializes a 10x10 world before each test.
 */
@BeforeEach
public void setUp() {
	world = new World(WORLD_WIDTH, WORLD_HEIGHT);
}

/**
 * Verifies carnivore construction with initial energy.
 * Tests that the carnivore is correctly created with position, energy, and alive state.
 */
@Test
public void testCarnivoreConstruction() {
	final Carnivore carnivore = new Carnivore(DEFAULT_CARNIVORE_ENERGY);
	final Position position = new Position(5, 5);
	carnivore.setPosition(position);
	
	assertNotNull(carnivore);
	assertEquals(position, carnivore.getPosition());
	assertEquals(DEFAULT_CARNIVORE_ENERGY, carnivore.getEnergy());
	assertTrue(carnivore.isAlive());
}

/**
 * Verifies carnivore eating behavior.
 * Tests that a carnivore can eat a herbivore and gain energy.
 */
@Test
public void testCarnivoreEatHerbivore() {
	final Carnivore carnivore = new Carnivore(10);
	final Position carnivorePosition = new Position(5, 5);
	carnivore.setPosition(carnivorePosition);
	
	final Herbivore herbivore = new Herbivore(DEFAULT_HERBIVORE_ENERGY);
	final Position herbivorePosition = new Position(5, 6);
	herbivore.setPosition(herbivorePosition);
	
	world.getCell(carnivorePosition).setAnimal(carnivore);
	world.getCell(herbivorePosition).setAnimal(herbivore);
	
	final int initialEnergy = carnivore.getEnergy();
	final Cell targetCell = world.getCell(herbivorePosition);
	
	assertTrue(carnivore.canEat(targetCell), "canEat devrait retourner true pour une proie adjacente (implémentez Carnivore.canEat)");
	carnivore.eat(targetCell, world);
	assertTrue(carnivore.getEnergy() > initialEnergy, "L'énergie du carnivore devrait augmenter après avoir mangé (implémentez Carnivore.eat)");
	assertFalse(targetCell.hasAnimal(), "La cellule de la proie devrait être vide après consommation (implémentez remove)" );
}

/**
 * Verifies carnivore movement behavior.
 * Tests that a carnivore can choose a valid move target.
 */
@Test
public void testCarnivoreMove() {
	final Carnivore carnivore = new Carnivore(12);
	final Position startPosition = new Position(5, 5);
	carnivore.setPosition(startPosition);
	world.getCell(startPosition).setAnimal(carnivore);
	
	final Cell targetCell = carnivore.chooseMove(world, startPosition);
	assertNotNull(targetCell);
	assertFalse(targetCell.hasAnimal());
}

/**
 * Verifies carnivore alive state based on energy level.
 * Tests that a carnivore with energy &gt; 0 is alive and with energy = 0 is dead.
 */
@Test
public void testCarnivoreIsAlive() {
	final Carnivore carnivore = new Carnivore(10);
	final Position position = new Position(3, 3);
	carnivore.setPosition(position);
	assertTrue(carnivore.isAlive());
	carnivore.setEnergy(0);
	assertFalse(carnivore.isAlive());
}

/**
 * Verifies carnivore reproduction behavior.
 * Tests that a carnivore with sufficient energy can reproduce.
 */
@Test
public void testCarnivoreReproduction() {
	final Carnivore carnivore = new Carnivore(HIGH_ENERGY_FOR_REPRODUCTION);
	final Position position = new Position(5, 5);
	carnivore.setPosition(position);
	world.getCell(position).setAnimal(carnivore);
	
	assertTrue(carnivore.canReproduce(world), "canReproduce devrait être true avec énergie suffisante (implémentez Carnivore.canReproduce)");
	final int initialEnergy = carnivore.getEnergy();
	final boolean spawned = carnivore.spawn(world);
	assertTrue(spawned, "spawn devrait réussir (implémentez Carnivore.spawn)");
	assertTrue(carnivore.getEnergy() < initialEnergy, "L'énergie du parent doit diminuer après reproduction");
}

/**
 * Verifies carnivore hunting behavior.
 * Tests that a carnivore can choose a hunting target when prey is available.
 */
@Test
public void testCarnivoreHunting() {
	final Carnivore carnivore = new Carnivore(DEFAULT_CARNIVORE_ENERGY);
	final Position carnivorePosition = new Position(5, 5);
	carnivore.setPosition(carnivorePosition);
	
	final Herbivore herbivore = new Herbivore(DEFAULT_HERBIVORE_ENERGY);
	final Position herbivorePosition = new Position(6, 6);
	herbivore.setPosition(herbivorePosition);
	
	world.getCell(carnivorePosition).setAnimal(carnivore);
	world.getCell(herbivorePosition).setAnimal(herbivore);
	
	final Position huntTarget = carnivore.chooseHunt(world);
	assertNotNull(huntTarget, "chooseHunt ne doit pas retourner null quand une proie est dans le champ de vision (implémentez Carnivore.chooseHunt)");
	assertEquals(herbivorePosition, huntTarget, "La cible de chasse devrait être la position de l'herbivore" );
}

@Test
public void testAddEnergyClamp() {
    RandomGenerator.reseed(2024L);
    Carnivore c = new Carnivore(19);
    c.addEnergy(10); // dépasse max 20
    assertEquals(20, c.getEnergy(), "Énergie carnivore plafonnée à 20");
}

@Test
public void testReproductionHalvesEnergyOnSpawn() {
    RandomGenerator.reseed(2024L);
    Carnivore c = new Carnivore(16); // >= threshold 14
    Position pos = new Position(3, 3);
    world.getCell(pos).setAnimal(c);
    c.setPosition(pos);
    int before = c.getEnergy();
    boolean spawned = c.spawn(world);
    assertTrue(spawned, "Spawn carnivore devrait réussir avec énergie suffisante");
    assertEquals(before - before / 2, c.getEnergy(), "Énergie parent réduite de moitié");
}

@Test
public void testSpawnFailsNoEmptyNeighbor() {
    RandomGenerator.reseed(2024L);
    Carnivore c = new Carnivore(18);
    Position center = new Position(2, 2);
    world.getCell(center).setAnimal(c);
    c.setPosition(center);
    for (Position p : new Position[]{new Position(2, 1), new Position(2, 3), new Position(1, 2), new Position(3, 2)}) {
        Carnivore blocker = new Carnivore(5);
        world.getCell(p).setAnimal(blocker);
        blocker.setPosition(p);
    }
    assertTrue(c.canReproduce(world), "Condition reproduction vraie");
    assertFalse(c.spawn(world), "Pas d'emplacement vide -> échec");
    assertEquals(18, c.getEnergy(), "Énergie inchangée après échec spawn");
}

@Test
public void testEatHerbivoreEnergyClamp() {
    RandomGenerator.reseed(2024L);
    Carnivore c = new Carnivore(19); // proche max
    Herbivore h = new Herbivore(6);
    Position preyPos = new Position(4, 4);
    world.getCell(preyPos).setAnimal(h);
    h.setPosition(preyPos);
    Position cPos = new Position(4, 3);
    world.getCell(cPos).setAnimal(c);
    c.setPosition(cPos);
    assertTrue(c.canEat(world.getCell(preyPos)));
    c.eat(world.getCell(preyPos), world);
    assertEquals(20, c.getEnergy(), "Énergie doit être plafonnée à 20 après repas");
    assertFalse(world.getCell(preyPos).hasAnimal(), "Herbivore consommé doit être retiré");
}

@Test
public void testDirectionalMoveTowardPrey() {
    RandomGenerator.reseed(2024L);
    Carnivore c = new Carnivore(10);
    Position cPos = new Position(2, 2);
    world.getCell(cPos).setAnimal(c);
    c.setPosition(cPos);
    Herbivore h = new Herbivore(4);
    Position preyPos = new Position(2, 4);
    world.getCell(preyPos).setAnimal(h);
    h.setPosition(preyPos);
    Cell dest = c.chooseMove(world, cPos);
    assertNotNull(dest);
    assertEquals(new Position(2, 3), dest.getPosition(), "Déplacement directionnel vers la proie");
}

@Test
public void testRandomMoveWhenNoPrey() {
    RandomGenerator.reseed(2024L);
    Carnivore c = new Carnivore(8);
    Position cPos = new Position(5, 5);
    world.getCell(cPos).setAnimal(c);
    c.setPosition(cPos);
    Cell dest = c.chooseMove(world, cPos);
    assertNotNull(dest, "Doit choisir un déplacement même sans proie");
    assertEquals(1, dest.getPosition().distanceTo(cPos), "Déplacement doit être adjacent");
}
}