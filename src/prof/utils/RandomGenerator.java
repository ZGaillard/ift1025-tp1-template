/* ============================================================================
 * Path: src/prof/utils/RandomGenerator.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-01-06
 * Description: Deterministic random generator centralizing seed management
 *              and exposing utilities for choice, shuffling, and neighborhood.
 * ========================================================================== */
package prof.utils;

import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//=============================================================================
//                            RandomGenerator
//=============================================================================

/**
 * Deterministic random generator that centralizes seed management and provides
 * utilities for random choice, shuffling, and neighborhood selection.
 *
 * <p>This class uses a centralized {@link Random} instance with a configurable
 * seed to ensure reproducible random behavior across the simulation.</p>
 */
public final class RandomGenerator {

//----------------------------- Constants ---------------------------------

/**
 * Default seed value for the random generator.
 */
private static final long DEFAULT_SEED = 42L;

//----------------------------- Fields ------------------------------------

/**
 * The centralized random number generator instance.
 */
private static Random random = new Random(DEFAULT_SEED);

//----------------------------- Constructor -------------------------------

/**
 * Private constructor to prevent instantiation of utility class.
 */
private RandomGenerator() {
	throw new UnsupportedOperationException("Utility class cannot be instantiated");
}

//----------------------------- Seed Management ---------------------------

/**
 * Resets the random generator with a new seed value.
 *
 * @param seed the new seed value for deterministic random generation
 */
public static void reseed(final long seed) {
	random = new Random(seed);
}

//----------------------------- Basic Random Methods ----------------------

/**
 * Returns a random integer between 0 (inclusive) and the specified bound (exclusive).
 *
 * @param bound the upper bound (exclusive), must be positive
 * @return a random integer between 0 and bound-1
 * @throws IllegalArgumentException if bound is not positive
 */
public static int nextInt(final int bound) {
	return random.nextInt(bound);
}

/**
 * Returns a random boolean value.
 *
 * @return {@code true} or {@code false} with equal probability
 */
public static boolean nextBoolean() {
	return random.nextBoolean();
}

/**
 * Returns a random double value between 0.0 (inclusive) and 1.0 (exclusive).
 *
 * @return a random double in the range [0.0, 1.0)
 */
public static double nextDouble() {
	return random.nextDouble();
}

/**
 * Returns {@code true} with the specified probability.
 *
 * @param probability the probability of returning {@code true}, between 0.0 and 1.0
 * @return {@code true} if a random event with the given probability occurs
 */
public static boolean chance(final double probability) {
	return probability > 0 && (probability >= 1 || random.nextDouble() < probability);
}

//----------------------------- Collection Utilities ----------------------

/**
 * Randomly selects an element from the given list.
 *
 * @param <T>  the type of elements in the list
 * @param list the list to choose from
 * @return a randomly selected element, or {@code null} if the list is null or empty
 */
public static <T> T choose(final List<T> list) {
	if (list == null || list.isEmpty()) {
		return null;
	}
	return list.get(random.nextInt(list.size()));
}

/**
 * Randomly selects an element from the given array.
 *
 * @param <T>   the type of elements in the array
 * @param array the array to choose from
 * @return a randomly selected element, or {@code null} if the array is null or empty
 */
public static <T> T choose(final T[] array) {
	if (array == null || array.length == 0) {
		return null;
	}
	return array[random.nextInt(array.length)];
}

/**
 * Shuffles the elements in the given list using Fisher-Yates algorithm.
 *
 * @param <T>  the type of elements in the list
 * @param list the list to shuffle, modified in place
 */
public static <T> void shuffleList(final List<T> list) {
	if (list != null) {
		Collections.shuffle(list, random);
	}
}

/**
 * Shuffles the elements in the given array using Fisher-Yates algorithm.
 *
 * @param <T>   the type of elements in the array
 * @param array the array to shuffle, modified in place
 */
public static <T> void shuffleArray(final T[] array) {
	if (array == null) {
		return;
	}
	for (int i = array.length - 1; i > 0; i--) {
		final int j = random.nextInt(i + 1);
		final T temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
}

//----------------------------- Neighborhood Methods ----------------------

/**
 * Builds the neighboring positions according to the vision range.
 *
 * <p>Vision ranges:</p>
 * <ul>
 *   <li>1 → cross pattern (4 adjacent cells)</li>
 *   <li>2 → 3×3 grid (8 surrounding cells)</li>
 *   <li>3 → 5×5 grid (24 surrounding cells)</li>
 * </ul>
 *
 * @param position    the center position
 * @param world       the world containing the grid
 * @param visionRange the vision range (1, 2, or 3)
 * @return a list of valid neighboring positions within bounds
 * @throws IllegalArgumentException if visionRange is not 1, 2, or 3
 */
private static List<Position> buildNeighborhood(final Position position,
                                                final World world,
                                                final int visionRange) {
	if (visionRange < 1 || visionRange > 3) {
		throw new IllegalArgumentException(
			"visionRange must be 1 (cross), 2 (3x3), or 3 (5x5), but was: " + visionRange);
	}
	
	final List<Position> neighbors = new ArrayList<>();
	
	if (visionRange == 1) {
		// Cross pattern: only 4 adjacent cells
		final int[][] crossDeltas = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
		for (final int[] delta : crossDeltas) {
			addValidPosition(position, world, neighbors, delta[0], delta[1]);
		}
	} else {
		// Grid pattern: 3x3 for vision=2, 5x5 for vision=3
		final int radius = (visionRange == 2) ? 1 : 2;
		for (int deltaX = -radius; deltaX <= radius; deltaX++) {
			for (int deltaY = -radius; deltaY <= radius; deltaY++) {
				if (deltaX == 0 && deltaY == 0) {
					continue; // Skip center position
				}
				addValidPosition(position, world, neighbors, deltaX, deltaY);
			}
		}
	}
	return neighbors;
}

/**
 * Adds a position to the neighbors list if it's within world bounds.
 *
 * @param center    the center position
 * @param world     the world containing the grid
 * @param neighbors the list to add valid positions to
 * @param deltaX    the X offset from center
 * @param deltaY    the Y offset from center
 */
private static void addValidPosition(final Position center,
                                     final World world,
                                     final List<Position> neighbors,
                                     final int deltaX,
                                     final int deltaY) {
	final int newX = center.x() + deltaX;
	final int newY = center.y() + deltaY;
	
	if (newX >= 0 && newY >= 0 && newX < world.getWidth() && newY < world.getHeight()) {
		neighbors.add(new Position(newX, newY));
	}
}

/**
 * Selects a random neighboring position based on vision range and filter criteria.
 *
 * @param position    the center position
 * @param world       the world containing the grid
 * @param visionRange the vision range (1, 2, or 3)
 * @param filter      the filter criteria for selecting neighbors
 * @return a randomly selected neighbor position, or {@code null} if none match
 * @throws IllegalArgumentException if visionRange is not 1, 2, or 3
 */
public static Position randomNeighbor(final Position position,
                                      final World world,
                                      final int visionRange,
                                      final NeighborFilter filter) {
	final List<Position> candidates = buildNeighborhood(position, world, visionRange);
	final List<Position> filteredCandidates = new ArrayList<>();
	
	for (final Position candidatePosition : candidates) {
		final Cell cell = world.getCell(candidatePosition);
		if (cell == null) {
			continue;
		}
		
		if (matchesFilter(cell, filter)) {
			filteredCandidates.add(candidatePosition);
		}
	}
	
	return choose(filteredCandidates);
}

/**
 * Checks if a cell matches the specified filter criteria.
 *
 * @param cell   the cell to check
 * @param filter the filter criteria
 * @return {@code true} if the cell matches the filter
 */
private static boolean matchesFilter(final Cell cell, final NeighborFilter filter) {
	return switch (filter) {
		case EMPTY -> cell.isCompletelyEmpty();
		case EMPTY_ANIMAL -> cell.isEmptyAnimal();
		case EMPTY_PLANT -> cell.isEmptyPlant();
		case ORGANISM -> cell.hasPlant() || cell.hasAnimal();
		case PLANT -> cell.hasPlant();
		case ANIMAL -> cell.hasAnimal();
		case HERBIVORE -> cell.getAnimal() instanceof Herbivore;
		case CARNIVORE -> cell.getAnimal() instanceof Carnivore;
	};
}

/**
 * Selects a random neighboring position with default vision range 1 and EMPTY filter.
 *
 * @param position the center position
 * @param world    the world containing the grid
 * @return a randomly selected empty neighbor position, or {@code null} if none available
 */
public static Position randomNeighbor(final Position position, final World world) {
	return randomNeighbor(position, world, 1, NeighborFilter.EMPTY);
}

/**
 * Selects a random neighboring position with default vision range 1 and specified filter.
 *
 * @param position the center position
 * @param world    the world containing the grid
 * @param filter   the filter criteria for selecting neighbors
 * @return a randomly selected neighbor position, or {@code null} if none match
 */
public static Position randomNeighbor(final Position position,
                                      final World world,
                                      final NeighborFilter filter) {
	return randomNeighbor(position, world, 1, filter);
}

/**
 * Selects a random neighboring position with specified vision range and EMPTY filter.
 *
 * @param position    the center position
 * @param world       the world containing the grid
 * @param visionRange the vision range (1, 2, or 3)
 * @return a randomly selected empty neighbor position, or {@code null} if none available
 * @throws IllegalArgumentException if visionRange is not 1, 2, or 3
 */
public static Position randomNeighbor(final Position position,
                                      final World world,
                                      final int visionRange) {
	return randomNeighbor(position, world, visionRange, NeighborFilter.EMPTY);
}

//=============================================================================
//                              NeighborFilter
//=============================================================================

/**
 * Filter criteria for neighbor selection based on cell contents.
 *
 * <p>These filters determine which neighboring cells are considered
 * valid candidates for random selection.</p>
 */
public enum NeighborFilter {
	/**
	 * Cell is completely empty (no plant and no animal).
	 */
	EMPTY,
	
	/**
	 * Animal slot is empty (may contain a plant).
	 */
	EMPTY_ANIMAL,
	
	/**
	 * Plant slot is empty (may contain an animal).
	 */
	EMPTY_PLANT,
	
	/**
	 * Cell contains at least one organism (plant or animal).
	 */
	ORGANISM,
	
	/**
	 * Cell contains a plant.
	 */
	PLANT,
	
	/**
	 * Cell contains an animal.
	 */
	ANIMAL,
	
	/**
	 * Cell contains a herbivore animal.
	 */
	HERBIVORE,
	
	/**
	 * Cell contains a carnivore animal.
	 */
	CARNIVORE
}
}
