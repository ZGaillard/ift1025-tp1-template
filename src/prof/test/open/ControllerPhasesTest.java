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
 * Tests ciblés sur les méthodes de phase de {@link SimulationController} uniquement.
 * Ne teste pas les aspects fournis (listeners, timer, step/stepNextPhase orchestration, etc.).
 */
public class ControllerPhasesTest {

private SimulationController controller;
private World world;

@BeforeEach
public void setup() {
	world = new World(8, 8);
	controller = new SimulationController(world);
}

@Test
public void testPhasePlantGrowthIncrementsAndCaps() {
	Plant p1 = new Plant(2);
	Position pos1 = new Position(2, 2);
	p1.setPosition(pos1);
	world.getCell(pos1).setPlant(p1);
	Plant p2 = new Plant(3);
	Position pos2 = new Position(3, 2);
	p2.setPosition(pos2);
	world.getCell(pos2).setPlant(p2);
	controller.phasePlantGrowth();
	assertEquals(3, p1.getEnergy(), "Croissance +1 jusqu'au plafond 3");
	assertEquals(3, p2.getEnergy(), "Déjà au max -> inchangé");
}

@Test
public void testPhaseHerbivoresMovementEatsPlant() {
	Herbivore h = new Herbivore(4);
	Position hPos = new Position(4, 4);
	world.getCell(hPos).setAnimal(h);
	h.setPosition(hPos);
	Plant p = new Plant(3);
	Position plantPos = new Position(4, 5);
	world.getCell(plantPos).setPlant(p);
	p.setPosition(plantPos);
	controller.phaseHerbivores();
	// L'herbivore doit avoir bougé et mangé la plante: énergie finale = 4 -1 +3 = 6
	assertEquals(6, h.getEnergy(), "Déplacement (-1) + nutrition (+3)");
	assertFalse(world.getCell(plantPos).hasPlant(), "Plante consommée");
	assertEquals(plantPos, h.getPosition(), "Herbivore déplacé sur l'ancienne plante");
}

@Test
public void testPhaseCarnivoresHuntAndConsumeHerbivore() {
	Carnivore c = new Carnivore(9);
	Position cPos = new Position(2, 2);
	world.getCell(cPos).setAnimal(c);
	c.setPosition(cPos);
	Herbivore h = new Herbivore(4);
	Position hPos = new Position(2, 3);
	world.getCell(hPos).setAnimal(h);
	h.setPosition(hPos);
	controller.phaseCarnivores();
	// Énergie attendue : 9 -1 +4 = 12
	assertEquals(12, c.getEnergy());
	assertTrue(world.getCell(hPos).hasAnimal() && world.getCell(hPos).getAnimal() instanceof Carnivore, "Carnivore présent sur la case");
	assertFalse(world.getCell(hPos).hasAnimal() && world.getCell(hPos).getAnimal() instanceof Herbivore, "Herbivore consommé");
	assertEquals(hPos, c.getPosition(), "Carnivore s'est déplacé sur la proie");
}

@Test
public void testPhaseReproductionAllTypes() {
	// Plant prête
	Plant plant = new Plant(3);
	Position pPos = new Position(1, 1);
	plant.setPosition(pPos);
	world.getCell(pPos).setPlant(plant);
	// Herbivore prêt (>=7)
	Herbivore h = new Herbivore(8);
	Position hPos = new Position(3, 3);
	h.setPosition(hPos);
	world.getCell(hPos).setAnimal(h);
	// Carnivore prêt (>=14)
	Carnivore c = new Carnivore(15);
	Position cPos = new Position(5, 5);
	c.setPosition(cPos);
	world.getCell(cPos).setAnimal(c);
	int beforePlants = countPlants();
	int beforeHerb = countHerbivores();
	int beforeCarn = countCarnivores();
	controller.phaseReproduction();
	assertTrue(countPlants() > beforePlants, "Nouvelle plante attendue");
	assertTrue(countHerbivores() > beforeHerb, "Nouvel herbivore attendu");
	assertTrue(countCarnivores() > beforeCarn, "Nouveau carnivore attendu");
	assertEquals(1, plant.getEnergy(), "Énergie plante réinitialisée");
	assertTrue(h.getEnergy() < 7, "Énergie herbivore réduite");
	assertTrue(c.getEnergy() < 14, "Énergie carnivore réduite");
}

@Test
public void testPhaseCleanupRemovesDead() {
	Plant deadPlant = new Plant(2);
	Position pPos = new Position(1, 2);
	deadPlant.setPosition(pPos);
	world.getCell(pPos).setPlant(deadPlant);
	deadPlant.setEnergy(0);
	Herbivore deadHerb = new Herbivore(3);
	Position hPos = new Position(2, 2);
	deadHerb.setPosition(hPos);
	world.getCell(hPos).setAnimal(deadHerb);
	deadHerb.setEnergy(0);
	controller.phaseCleanup();
	assertFalse(world.getCell(pPos).hasPlant(), "Plante morte retirée");
	assertFalse(world.getCell(hPos).hasAnimal(), "Herbivore mort retiré");
}

@Test
public void testPhaseReproductionFailsNoSpaceAnimal() {
	Herbivore h = new Herbivore(9);
	Position center = new Position(4, 4);
	world.getCell(center).setAnimal(h);
	h.setPosition(center);
	// Bloquer les 4 voisins cardinaux
	for (Position p : new Position[]{new Position(4, 3), new Position(4, 5), new Position(3, 4), new Position(5, 4)}) {
		Herbivore blocker = new Herbivore(3);
		world.getCell(p).setAnimal(blocker);
		blocker.setPosition(p);
	}
	int before = countHerbivores();
	controller.phaseReproduction();
	assertEquals(before, countHerbivores(), "Aucun nouvel herbivore faute d'espace");
	assertEquals(9, h.getEnergy(), "Énergie inchangée car reproduction impossible");
}

@Test
public void testPhaseReproductionFailsNoSpacePlant() {
	Plant plant = new Plant(3);
	Position center = new Position(6, 6);
	plant.setPosition(center);
	world.getCell(center).setPlant(plant);
	// Bloquer les 4 cardinaux (nécessaires pour spawn())
	for (Position p : new Position[]{new Position(6, 5), new Position(6, 7 - 1), new Position(5, 6), new Position(7 - 1, 6)}) {
		// reposition fixed for clarity; using existing coords inside bounds
		Plant blocker = new Plant(2);
		blocker.setPosition(p);
		world.getCell(p).setPlant(blocker);
	}
	int before = countPlants();
	controller.phaseReproduction();
	assertEquals(before, countPlants(), "Aucune reproduction faute d'espace cardinal");
	assertEquals(3, plant.getEnergy(), "Énergie plante inchangée (pas reset)");
}

@Test
public void testHerbivoreEnergyDecreasesWhenBlocked() {
	world = new World(8, 8);
	controller = new SimulationController(world);
	
	Herbivore h = new Herbivore(5);
	Position center = new Position(0, 0);
	world.getCell(center).setAnimal(h);
	h.setPosition(center);
	// Bloquer les 3 autres voisins (0,1), (1,0), (1,1)
	for (Position p : new Position[]{new Position(0, 1), new Position(1, 0), new Position(1, 1)}) {
		Herbivore blocker = new Herbivore(3);
		world.getCell(p).setAnimal(blocker);
		blocker.setPosition(p);
	}
	controller.phaseHerbivores();
	assertEquals(4, h.getEnergy(), "Pas de mouvement possible -> énergie -1");
	assertEquals(center, h.getPosition());
	world = null;
}

@Test
public void testCarnivoreEnergyDecreasesWhenBlocked() {
	world = new World(8, 8);
	controller = new SimulationController(world);
	
	Carnivore c = new Carnivore(8);
	Position center = new Position(0, 0);
	world.getCell(center).setAnimal(c);
	c.setPosition(center);
	// Bloquer les 3 autres coins (0,1), (1,0), (1,1)
	for (Position p : new Position[]{new Position(0, 1), new Position(1, 0), new Position(1, 1)}) {
		Carnivore blocker = new Carnivore(5);
		world.getCell(p).setAnimal(blocker);
		blocker.setPosition(p);
	}
	controller.phaseCarnivores();
	assertEquals(7, c.getEnergy(), "Pas de chasse ni mouvement -> énergie -1");
	assertEquals(center, c.getPosition());
}

// ---------------- Helpers -----------------
private int countPlants() {
	int count = 0;
	for (int y = 0; y < world.getHeight(); y++)
		for (int x = 0; x < world.getWidth(); x++) if (world.getCell(new Position(x, y)).hasPlant()) count++;
	return count;
}

private int countHerbivores() {
	int count = 0;
	for (int y = 0; y < world.getHeight(); y++)
		for (int x = 0; x < world.getWidth(); x++)
			if (world.getCell(new Position(x, y)).hasAnimal() && world.getCell(new Position(x, y)).getAnimal() instanceof Herbivore)
				count++;
	return count;
}

private int countCarnivores() {
	int count = 0;
	for (int y = 0; y < world.getHeight(); y++)
		for (int x = 0; x < world.getWidth(); x++)
			if (world.getCell(new Position(x, y)).hasAnimal() && world.getCell(new Position(x, y)).getAnimal() instanceof Carnivore)
				count++;
	return count;
}
}
