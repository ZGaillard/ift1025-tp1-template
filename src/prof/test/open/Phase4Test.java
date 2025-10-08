// path: prof/test/open/Phase4Test.java
// author: Zakary Gaillard-D.
// date: 2025-10-06
// purpose: Public tests for phase 4 (reproduction)
package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.controller.SimulationController;
import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;
import student.model.organisms.Plant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public tests for phase 4 (animal reproduction).
 * These tests validate the animal reproduction phase of the simulation,
 * ensuring animals can reproduce when conditions are met.
 */
public class Phase4Test {

private static final int WORLD_WIDTH = 10;
private static final int WORLD_HEIGHT = 10;
private static final int HIGH_ENERGY_FOR_REPRODUCTION = 20;
private static final int LOW_ENERGY = 5;

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
 * Reproduction animaux (herbivore & carnivore) :
 * - Chaque parent doit produire exactement 1 offspring
 * - L'offspring doit apparaître dans une case cardinale libre
 * - L'énergie du parent est réduite (>=1 et < énergie initiale)
 */
@Test
public void testAnimalsReproduction() {
    final int parentEnergy = HIGH_ENERGY_FOR_REPRODUCTION; // 20
    Herbivore herbParent = new Herbivore(parentEnergy);
    Carnivore carnParent = new Carnivore(parentEnergy);
    Position hPos = new Position(2,2);
    Position cPos = new Position(7,7);
    herbParent.setPosition(hPos);
    carnParent.setPosition(cPos);
    world.getCell(hPos).setAnimal(herbParent);
    world.getCell(cPos).setAnimal(carnParent);
    int herbBefore = countHerbivores();
    int carnBefore = countCarnivores();

    controller.phaseReproduction();

    // Offspring counts
    assertEquals(herbBefore + 1, countHerbivores(), "Exactement 1 nouvel herbivore attendu (implémentez spawn herbivore)");
    assertEquals(carnBefore + 1, countCarnivores(), "Exactement 1 nouveau carnivore attendu (implémentez spawn carnivore)");

    // Énergie parent réduite (demi ou autre logique mais < énergie initiale)
    assertTrue(herbParent.getEnergy() < parentEnergy, "Énergie parent herbivore doit diminuer");
    assertTrue(carnParent.getEnergy() < parentEnergy, "Énergie parent carnivore doit diminuer");

    // Vérifier position cardinal d'au moins un enfant (cardinal = distance Manhattan 1)
    boolean herbChildOk = hasCardinalNewHerbivoreAround(hPos, herbParent);
    boolean carnChildOk = hasCardinalNewCarnivoreAround(cPos, carnParent);
    assertTrue(herbChildOk, "Enfant herbivore doit apparaître dans une case cardinale libre (implémentez placement reproduction)");
    assertTrue(carnChildOk, "Enfant carnivore doit apparaître dans une case cardinale libre (implémentez placement reproduction)");
}

/**
 * Reproduction animaux interdite si énergie insuffisante :
 * - Aucun offspring
 * - Énergie parents inchangée
 */
@Test
public void testAnimalsNoReproductionInsufficientEnergy() {
    Herbivore herbParent = new Herbivore(LOW_ENERGY); // 5
    Carnivore carnParent = new Carnivore(LOW_ENERGY); // 5
    Position hPos = new Position(3,3);
    Position cPos = new Position(6,6);
    herbParent.setPosition(hPos);
    carnParent.setPosition(cPos);
    world.getCell(hPos).setAnimal(herbParent);
    world.getCell(cPos).setAnimal(carnParent);
    int herbBefore = countHerbivores();
    int carnBefore = countCarnivores();

    controller.phaseReproduction();

    assertEquals(herbBefore, countHerbivores(), "Aucun nouvel herbivore attendu (énergie insuffisante)");
    assertEquals(carnBefore, countCarnivores(), "Aucun nouveau carnivore attendu (énergie insuffisante)");
    assertEquals(LOW_ENERGY, herbParent.getEnergy(), "Énergie parent herbivore ne doit pas changer");
    assertEquals(LOW_ENERGY, carnParent.getEnergy(), "Énergie parent carnivore ne doit pas changer");
}

/**
 * Reproduction plante :
 * - Une plante énergie 3 produit exactement 1 offspring (cardinal)
 * - Énergie parent réinitialisée à 1
 */
@Test
public void testPlantReproduction() {
    Plant parent = new Plant(3);
    Position pos = new Position(4,4);
    parent.setPosition(pos);
    world.getCell(pos).setPlant(parent);
    int plantsBefore = countPlants();

    controller.phaseReproduction();

    assertEquals(plantsBefore + 1, countPlants(), "Exactement 1 nouvelle plante attendue (implémentez reproduction plante)");
    assertEquals(1, parent.getEnergy(), "Énergie parent plante doit être réinitialisée à 1");
    assertTrue(hasCardinalPlantChild(pos, parent), "Enfant plante doit apparaître dans une case cardinale");
}

/**
 * Reproduction plante bloquée :
 * - Aucun offspring si toutes les cardinales sont occupées
 * - Énergie inchangée
 */
@Test
public void testPlantNoReproductionNoSpace() {
    Plant parent = new Plant(3);
    Position center = new Position(1,1);
    parent.setPosition(center);
    world.getCell(center).setPlant(parent);
    // Bloquer cardinaux
    for (Position p : new Position[]{new Position(1,0), new Position(1,2), new Position(0,1), new Position(2,1)}) {
        Plant blocker = new Plant(2);
        blocker.setPosition(p);
        world.getCell(p).setPlant(blocker);
    }
    int before = countPlants();
    controller.phaseReproduction();
    assertEquals(before, countPlants(), "Aucune nouvelle plante (pas d'espace)");
    assertEquals(3, parent.getEnergy(), "Énergie plante inchangée faute de reproduction");
}

// ---------------- Helpers stricts supplémentaires ----------------
private boolean hasCardinalNewHerbivoreAround(Position parentPos, Herbivore parent) {
    return world.getNeighbors(parentPos, false).stream()
        .filter(Cell::hasAnimal)
        .anyMatch(c -> c.getAnimal() != parent && c.getAnimal() instanceof Herbivore);
}
private boolean hasCardinalNewCarnivoreAround(Position parentPos, Carnivore parent) {
    return world.getNeighbors(parentPos, false).stream()
        .filter(Cell::hasAnimal)
        .anyMatch(c -> c.getAnimal() != parent && c.getAnimal() instanceof Carnivore);
}
private boolean hasCardinalPlantChild(Position parentPos, Plant parent) {
    return world.getNeighbors(parentPos, false).stream()
        .filter(Cell::hasPlant)
        .anyMatch(c -> c.getPlant() != parent);
}
// Helpers ajoutés pour compter les plantes et animaux
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

