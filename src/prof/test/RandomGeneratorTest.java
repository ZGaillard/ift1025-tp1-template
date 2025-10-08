// path: prof/test/RandomGeneratorTest.java
// author: Zakary Gaillard-D.
// date: 2025-10-06
// purpose: Tests déterministes et utilitaires pour RandomGenerator

package prof.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import prof.utils.RandomGenerator;
import prof.utils.RandomGenerator.NeighborFilter;
import student.model.core.Position;
import student.model.core.World;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// Section: Classe de test
public class RandomGeneratorTest {

private static final int WORLD_SIZE = 10;
private World testWorld;

// Section: Setup
@BeforeEach
void setUp() {
	// Réinitialise la graine pour assurer la reproductibilité
	RandomGenerator.reseed(42);
	// Crée un monde de test
	testWorld = new World(WORLD_SIZE, WORLD_SIZE);
}

// Section: Tests base RNG
@Test
@DisplayName("Test du déterminisme avec la même graine")
void testDeterminism() {
	// Premier passage
	RandomGenerator.reseed(123);
	int val1 = RandomGenerator.nextInt(100);
	boolean bool1 = RandomGenerator.nextBoolean();
	double double1 = RandomGenerator.nextDouble();
	// Deuxième passage avec la même graine
	RandomGenerator.reseed(123);
	int val2 = RandomGenerator.nextInt(100);
	boolean bool2 = RandomGenerator.nextBoolean();
	double double2 = RandomGenerator.nextDouble();
	// Les valeurs doivent être identiques
	assertEquals(val1, val2, "nextInt doit être déterministe");
	assertEquals(bool1, bool2, "nextBoolean doit être déterministe");
	assertEquals(double1, double2, 0.0001, "nextDouble doit être déterministe");
}

@Test
@DisplayName("Test de nextInt avec différentes bornes")
void testNextInt() {
	for (int i = 0; i < 100; i++) {
		int value = RandomGenerator.nextInt(10);
		assertTrue(value >= 0 && value < 10,
			"nextInt(10) doit retourner une valeur entre 0 et 9");
	}
	// Test avec borne 1
	for (int i = 0; i < 50; i++) {
		assertEquals(0, RandomGenerator.nextInt(1),
			"nextInt(1) doit toujours retourner 0");
	}
}

@Test
@DisplayName("Test de la méthode chance")
void testChance() {
	// Probabilité 0 : toujours false
	for (int i = 0; i < 50; i++) {
		assertFalse(RandomGenerator.chance(0.0), "chance(0.0) doit toujours retourner false");
	}
	// Probabilité 1 : toujours true
	for (int i = 0; i < 50; i++) {
		assertTrue(RandomGenerator.chance(1.0), "chance(1.0) doit toujours retourner true");
	}
	// Probabilité négative : toujours false
	assertFalse(RandomGenerator.chance(-0.5), "chance négative doit retourner false");
	// Probabilité > 1 : toujours true
	assertTrue(RandomGenerator.chance(1.5), "chance > 1 doit retourner true");
}

// Section: Tests choose
@Test
@DisplayName("Test de choose avec liste")
void testChooseList() {
	List<String> items = Arrays.asList("A", "B", "C", "D");
	// Test avec liste normale
	Set<String> results = new HashSet<>();
	for (int i = 0; i < 100; i++) {
		String chosen = RandomGenerator.choose(items);
		assertNotNull(chosen, "choose ne doit pas retourner null");
		assertTrue(items.contains(chosen), "Élément hors liste");
		results.add(chosen);
	}
	// Vérifie que tous les éléments peuvent être choisis
	assertTrue(results.size() > 1, "Diversité insuffisante");
	// Test avec liste vide
	assertNull(RandomGenerator.choose(new ArrayList<>()), "Liste vide -> null");
	// Test avec liste null
	assertNull(RandomGenerator.choose((List<String>) null), "Liste null -> null");
}

@Test
@DisplayName("Test de choose avec tableau")
void testChooseArray() {
	String[] items = {"X", "Y", "Z"};
	// Test avec tableau normal
	Set<String> results = new HashSet<>();
	for (int i = 0; i < 60; i++) {
		String chosen = RandomGenerator.choose(items);
		assertNotNull(chosen, "choose ne doit pas retourner null");
		assertTrue(Arrays.asList(items).contains(chosen), "Élément hors tableau");
		results.add(chosen);
	}
	// Test avec tableau vide
	assertNull(RandomGenerator.choose(new String[0]), "Tableau vide -> null");
	// Test avec tableau null
	assertNull(RandomGenerator.choose((String[]) null), "Tableau null -> null");
}

// Section: Tests shuffle
@Test
@DisplayName("Test de shuffleList")
void testShuffleList() {
	List<Integer> original = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	List<Integer> toShuffle = new ArrayList<>(original);
	RandomGenerator.shuffleList(toShuffle);
	// La liste mélangée doit contenir les mêmes éléments
	assertEquals(original.size(), toShuffle.size(), "Taille incohérente");
	assertTrue(toShuffle.containsAll(original), "Éléments manquants");
	// Test avec liste null (ne doit pas planter)
	assertDoesNotThrow(() -> RandomGenerator.shuffleList(null), "Null non toléré");
}

@Test
@DisplayName("Test de shuffleArray")
void testShuffleArray() {
	Integer[] original = {1, 2, 3, 4, 5, 6, 7, 8};
	Integer[] toShuffle = Arrays.copyOf(original, original.length);
	RandomGenerator.shuffleArray(toShuffle);
	// Le tableau mélangé doit contenir les mêmes éléments
	assertEquals(original.length, toShuffle.length, "Taille incohérente");
	assertTrue(Arrays.asList(toShuffle).containsAll(Arrays.asList(original)), "Éléments manquants");
	// Test avec tableau null (ne doit pas planter)
	assertDoesNotThrow(() -> RandomGenerator.shuffleArray((Integer[]) null), "Null non toléré");
}

// Section: Tests voisinage
@Test
@DisplayName("Test de randomNeighbor avec world simple")
void testRandomNeighborBasic() {
	Position center = new Position(5, 5);
	// Test avec vision 1 (croix)
	Set<Position> neighbors = new HashSet<>();
	for (int i = 0; i < 50; i++) {
		Position neighbor = RandomGenerator.randomNeighbor(center, testWorld, 1);
		if (neighbor != null) {
			neighbors.add(neighbor);
			// Vérifie que c'est bien un voisin adjacent
			assertEquals(1, Math.abs(neighbor.x() - center.x()) + Math.abs(neighbor.y() - center.y()), "Voisin invalide");
		}
	}
	// Test au bord du monde
	Position corner = new Position(0, 0);
	for (int i = 0; i < 20; i++) {
		Position neighbor = RandomGenerator.randomNeighbor(corner, testWorld, 1);
		if (neighbor != null) {
			assertTrue(testWorld.isValidPosition(neighbor), "Position hors limites");
		}
	}
}

@Test
@DisplayName("Test de randomNeighbor avec différentes visions")
void testRandomNeighborVisionRanges() {
	Position center = new Position(5, 5);
	// Test vision 2 (3x3)
	Set<Position> vision2 = new HashSet<>();
	for (int i = 0; i < 100; i++) {
		Position n = RandomGenerator.randomNeighbor(center, testWorld, 2);
		if (n != null) {
			vision2.add(n);
			int dx = Math.abs(n.x() - center.x());
			int dy = Math.abs(n.y() - center.y());
			assertTrue(dx <= 1 && dy <= 1 && (dx + dy) > 0, "Hors 3x3");
		}
	}
	// Test vision 3 (5x5)
	Set<Position> vision3 = new HashSet<>();
	for (int i = 0; i < 100; i++) {
		Position n = RandomGenerator.randomNeighbor(center, testWorld, 3);
		if (n != null) {
			vision3.add(n);
			int dx = Math.abs(n.x() - center.x());
			int dy = Math.abs(n.y() - center.y());
			assertTrue(dx <= 2 && dy <= 2 && (dx + dy) > 0, "Hors 5x5");
		}
	}
	// Vision 3 doit avoir plus de voisins que vision 2
	assertTrue(vision3.size() >= vision2.size(), "Vision 3 devrait >= vision 2");
}

@Test
@DisplayName("Test des exceptions pour visionRange invalide")
void testInvalidVisionRange() {
	Position center = new Position(5, 5);
	// Les visions invalides doivent lever une exception
	assertThrows(IllegalArgumentException.class, () -> RandomGenerator.randomNeighbor(center, testWorld, 0));
	assertThrows(IllegalArgumentException.class, () -> RandomGenerator.randomNeighbor(center, testWorld, 4));
	assertThrows(IllegalArgumentException.class, () -> RandomGenerator.randomNeighbor(center, testWorld, -1));
}

@Test
@DisplayName("Test des surcharges de randomNeighbor")
void testRandomNeighborOverloads() {
	Position center = new Position(5, 5);
	// Test surcharge par défaut (vision=1, filtre=EMPTY)
	assertDoesNotThrow(() -> RandomGenerator.randomNeighbor(center, testWorld));
	// Test surcharge avec filtre seulement (vision=1)
	assertDoesNotThrow(() -> RandomGenerator.randomNeighbor(center, testWorld, NeighborFilter.EMPTY));
	// Test surcharge avec vision seulement (filtre=EMPTY)
	assertDoesNotThrow(() -> RandomGenerator.randomNeighbor(center, testWorld, 2));
}

// Section: Reproductibilité séquence
@Test
@DisplayName("Test de reproductibilité complète")
void testFullReproducibility() {
	// Premier run
	RandomGenerator.reseed(999);
	List<Integer> firstRun = new ArrayList<>();
	for (int i = 0; i < 20; i++) {
		firstRun.add(RandomGenerator.nextInt(100));
	}
	// Deuxième run avec même graine
	RandomGenerator.reseed(999);
	List<Integer> secondRun = new ArrayList<>();
	for (int i = 0; i < 20; i++) {
		secondRun.add(RandomGenerator.nextInt(100));
	}
	assertEquals(firstRun, secondRun, "Séquences différentes");
}
}
