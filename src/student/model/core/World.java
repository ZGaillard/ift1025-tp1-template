/* ============================================================================
 * Path: src/student/model/core/World.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Simulation world grid composed of immutable dimensions and addressable cells.
 * ========================================================================== */
package student.model.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the simulation world as a rectangular grid of {@link Cell} instances.
 * <p>World is a simple container offering coordinate validation and neighbor lookup. Higher-level
 * behaviors (movement, spawning, cleanup) are orchestrated by controllers or organisms.</p>
 */
public class World {
//=============================================================================
//                                   Fields
//=============================================================================
private final int width;
private final int height;
private final Cell[][] grid;

//=============================================================================
//                               Construction
//=============================================================================

/**
 * Create a new rectangular world of the given dimensions.
 *
 * @param width  grid width (columns)
 * @param height grid height (rows)
 */
public World(int width, int height) {
	this.width = width;
	this.height = height;
	this.grid = new Cell[height][width];
	
	// Initialize every cell (row-major). Kept explicit for clarity over streams.
	for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
			grid[y][x] = new Cell(new Position(x, y));
		}
	}
}

//=============================================================================
//                               Accessors
//=============================================================================

/**
 * Return world width in cells.
 *
 * @return number of columns
 */
public int getWidth() {
	return width;
}

/**
 * Return world height in cells.
 *
 * @return number of rows
 */
public int getHeight() {
	return height;
}

//=============================================================================
//                              Cell Retrieval
//=============================================================================

/**
 * Return the cell at the given position or {@code null} if out of bounds.
 *
 * @param pos target position
 * @return cell reference or {@code null} if invalid
 */
public Cell getCell(Position pos) {
	if (!isValidPosition(pos)) return null;
	return grid[pos.y()][pos.x()];
}

//=============================================================================
//                               Validation
//=============================================================================

/**
 * Check whether a position lies within world bounds.
 *
 * @param pos position to validate
 * @return {@code true} if coordinates are inside the grid
 */
public boolean isValidPosition(Position pos) {
	return pos.x() >= 0 && pos.x() < width && pos.y() >= 0 && pos.y() < height;
}

//=============================================================================
//                               Transfers (Legacy)
//=============================================================================

/**
 * Transfer an organism from one cell to another (legacy alias).
 * <p>Deprecated in favor of explicit {@link #transferAnimal(Cell, Cell)} and
 * {@link #transferPlant(Cell, Cell)} for clarity. Only attempts moving the animal slot to
 * preserve historical behavior.</p>
 *
 * @param from source cell
 * @param to   destination cell
 * @deprecated Use {@link #transferAnimal(Cell, Cell)} / {@link #transferPlant(Cell, Cell)}.
 */
@Deprecated
public void transferOrganism(Cell from, Cell to) { // DONOTTOUCH[world] (MAINTAINER, 2025-10-06): Legacy semantics.
	if (from.hasAnimal()) {
		transferAnimal(from, to);
	}
}

/**
 * Transfer an animal between cells if the destination animal slot is empty.
 *
 * @param from source cell
 * @param to   destination cell
 */
public void transferAnimal(Cell from, Cell to) {
	if (from.hasAnimal() && to.isEmptyAnimal()) {
		var animal = from.getAnimal();
		from.removeAnimal();
		to.setAnimal(animal);
		animal.setPosition(to.getPosition());
	}
}

/**
 * Transfer a plant between cells if the destination plant slot is empty.
 *
 * @param from source cell
 * @param to   destination cell
 */
public void transferPlant(Cell from, Cell to) {
	if (from.hasPlant() && to.isEmptyPlant()) {
		var plant = from.getPlant();
		from.removePlant();
		to.setPlant(plant);
		plant.setPosition(to.getPosition());
	}
}

//=============================================================================
//                               Neighborhood
//=============================================================================

/**
 * Return neighboring cells of a position (4-way or 8-way depending on {@code includeDiagonals}).
 *
 * @param pos              origin position
 * @param includeDiagonals include diagonals when {@code true}
 * @return list of neighboring cells (never {@code null})
 */
public List<Cell> getNeighbors(Position pos, boolean includeDiagonals) {
	List<Cell> neighbors = new ArrayList<>();
	
	int[][] deltas;
	if (includeDiagonals) {
		// 8 directions including diagonals.
		deltas = new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
	} else {
		// Cardinal directions only.
		deltas = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
	}
	
	for (int[] delta : deltas) {
		int nx = pos.x() + delta[0];
		int ny = pos.y() + delta[1];
		if (nx < 0 || ny < 0 || nx >= width || ny >= height) continue; // Avoid invalid Position creation.
		Position neighborPos = new Position(nx, ny); // Safe now.
		neighbors.add(getCell(neighborPos));
	}
	
	return neighbors;
}
}
