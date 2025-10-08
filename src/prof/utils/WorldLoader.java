/* ============================================================================
 * Path: src/prof/utils/WorldLoader.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-01-06
 * Description: Minimal JSON loader using regex patterns to initialize World
 *              instances with cohabitation support for plants and animals.
 * ========================================================================== */
package prof.utils;

import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Animal;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;
import student.model.organisms.Plant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//=============================================================================
//                              WorldLoader
//=============================================================================

/**
 * Minimal JSON loader using regex patterns to initialize World instances
 * with cohabitation support between plants and animals.
 *
 * <p>Expected JSON format (whitespace flexible):</p>
 * <pre>{@code
 * {
 *   "width": 10,
 *   "height": 8,
 *   "plants":     [ {"energy":2,"posx":3,"posy":4}, ... ],
 *   "herbivores": [ {"energy":50,"posx":1,"posy":2}, ... ],
 *   "carnivores": [ {"energy":80,"posx":7,"posy":6}, ... ]
 * }
 * }</pre>
 *
 * <p><strong>Warning:</strong> This is NOT a generic JSON parser.
 * Use only for simple configuration files.</p>
 */
public final class WorldLoader {

//----------------------------- Constructor -------------------------------

/**
 * Private constructor to prevent instantiation of utility class.
 */
private WorldLoader() {
	throw new UnsupportedOperationException("Utility class cannot be instantiated");
}

//----------------------------- Public API --------------------------------

/**
 * Loads a world from a minimal JSON configuration file.
 *
 * @param filePath the path to the JSON configuration file
 * @return the loaded world instance, or {@code null} if loading fails
 */
public static World loadFromJson(final String filePath) {
	try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
		final StringBuilder jsonContent = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonContent.append(line.trim());
		}
		return parseSimpleJson(jsonContent.toString());
	} catch (IOException e) {
		System.err.println("Error loading file: " + e.getMessage());
		return null;
	} catch (Exception e) {
		System.err.println("Error parsing JSON: " + e.getMessage());
		return null;
	}
}

//----------------------------- JSON Parsing ------------------------------

/**
 * Parses a simple JSON string to create a World instance.
 *
 * @param json the JSON string to parse
 * @return the created world instance, or {@code null} if parsing fails
 */
private static World parseSimpleJson(final String json) {
	final int width = extractNumber(json, "width");
	final int height = extractNumber(json, "height");
	
	if (width <= 0 || height <= 0) {
		System.err.println("Invalid dimensions: " + width + "x" + height);
		return null;
	}
	
	final World world = new World(width, height);
	
	// Parse plants
	final String plantsArray = extractArray(json, "plants");
	if (plantsArray != null && !plantsArray.trim().isEmpty()) {
		parsePlantsSimple(plantsArray, world);
	}
	
	// Parse herbivores
	final String herbivoresArray = extractArray(json, "herbivores");
	if (herbivoresArray != null && !herbivoresArray.trim().isEmpty()) {
		parseHerbivoresSimple(herbivoresArray, world);
	}
	
	// Parse carnivores
	final String carnivoresArray = extractArray(json, "carnivores");
	if (carnivoresArray != null && !carnivoresArray.trim().isEmpty()) {
		parseCarnivoresSimple(carnivoresArray, world);
	}
	
	return world;
}

/**
 * Extracts a numeric value from JSON using regex pattern matching.
 *
 * @param json the JSON string to search
 * @param key  the key to extract the value for
 * @return the extracted number, or 0 if not found or invalid
 */
private static int extractNumber(final String json, final String key) {
	try {
		final String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(json);
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		}
	} catch (NumberFormatException e) {
		System.err.println("Format error for '" + key + "'");
	}
	return 0;
}

/**
 * Extracts an array content from JSON using regex pattern matching.
 *
 * @param json      the JSON string to search
 * @param arrayName the name of the array to extract
 * @return the array content as a string, or {@code null} if not found
 */
private static String extractArray(final String json, final String arrayName) {
	try {
		final String pattern = "\"" + arrayName + "\"\\s*:\\s*\\[([^]]*)]";
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(json);
		if (m.find()) {
			return m.group(1);
		}
	} catch (Exception e) {
		System.err.println("Error extracting array '" + arrayName + "'");
	}
	return null;
}

/**
 * Extracts a numeric value from a JSON object string.
 *
 * @param obj the object string to search
 * @param key the key to extract the value for
 * @return the extracted number, or 0 if not found or invalid
 */
private static int extractNumberFromObject(final String obj, final String key) {
	try {
		final String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(obj);
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		}
	} catch (NumberFormatException e) {
		System.err.println("Format error for '" + key + "' in: " + obj);
	}
	return 0;
}

//----------------------------- Organism Placement -----------------------

/**
 * Places a plant in the world if the plant slot is available.
 *
 * <p>The plant slot can be occupied even if an animal is present
 * in the same cell (cohabitation support).</p>
 *
 * @param world the world to place the plant in
 * @param plant the plant to place
 * @param x     the X coordinate
 * @param y     the Y coordinate
 */
private static void placePlantIfPossible(final World world,
                                         final Plant plant,
                                         final int x,
                                         final int y) {
	final Position pos = new Position(x, y);
	if (!world.isValidPosition(pos)) {
		System.err.printf("Invalid position for plant: (%d,%d)%n", x, y);
		return;
	}
	final Cell cell = world.getCell(pos);
	if (!cell.isEmptyPlant()) {
		System.err.printf("Plant slot occupied at (%d,%d)%n", x, y);
		return;
	}
	cell.setPlant(plant);
	plant.setPosition(pos);
}

/**
 * Places an animal in the world if the animal slot is available.
 *
 * <p>The animal slot can be occupied even if a plant is present
 * in the same cell (cohabitation support).</p>
 *
 * @param world  the world to place the animal in
 * @param animal the animal to place
 * @param x      the X coordinate
 * @param y      the Y coordinate
 * @param label  the animal type label for error messages
 */
private static void placeAnimalIfPossible(final World world,
                                          final Animal animal,
                                          final int x,
                                          final int y,
                                          final String label) {
	final Position pos = new Position(x, y);
	if (!world.isValidPosition(pos)) {
		System.err.printf("Invalid position for %s: (%d,%d)%n", label, x, y);
		return;
	}
	final Cell cell = world.getCell(pos);
	if (!cell.isEmptyAnimal()) {
		System.err.printf("Animal slot occupied, cannot place %s at (%d,%d)%n", label, x, y);
		return;
	}
	cell.setAnimal(animal);
	animal.setPosition(pos);
}

//----------------------------- Array Parsing -----------------------------

/**
 * Parses a plants array content and places plants in the world.
 *
 * @param plantsContent the plants array content as a string
 * @param world         the world to place plants in
 */
private static void parsePlantsSimple(final String plantsContent, final World world) {
	if (plantsContent.trim().isEmpty()) {
		return;
	}
	
	final String[] objects = plantsContent.split("},\\s*\\{");
	for (String obj : objects) {
		obj = obj.replaceAll("[{}]", "").trim();
		if (obj.isEmpty()) {
			continue;
		}
		
		try {
			int energy = extractNumberFromObject(obj, "energy");
			final int posx = extractNumberFromObject(obj, "posx");
			final int posy = extractNumberFromObject(obj, "posy");
			
			// CHANGED: ensure minimum energy (2025-01-06)
			if (energy <= 0) {
				energy = 1;
			}
			
			final Plant plant = new Plant(energy);
			placePlantIfPossible(world, plant, posx, posy);
		} catch (Exception e) {
			System.err.println("Error parsing plant: " + e.getMessage());
		}
	}
}

/**
 * Parses a herbivores array content and places herbivores in the world.
 *
 * @param content the herbivores array content as a string
 * @param world   the world to place herbivores in
 */
private static void parseHerbivoresSimple(final String content, final World world) {
	if (content.trim().isEmpty()) {
		return;
	}
	
	final String[] objects = content.split("},\\s*\\{");
	for (String obj : objects) {
		obj = obj.replaceAll("[{}]", "").trim();
		if (obj.isEmpty()) {
			continue;
		}
		
		final int energy = extractNumberFromObject(obj, "energy");
		final int posx = extractNumberFromObject(obj, "posx");
		final int posy = extractNumberFromObject(obj, "posy");
		
		final Herbivore herbivore = new Herbivore(energy);
		placeAnimalIfPossible(world, herbivore, posx, posy, "herbivore");
	}
}

/**
 * Parses a carnivores array content and places carnivores in the world.
 *
 * @param content the carnivores array content as a string
 * @param world   the world to place carnivores in
 */
private static void parseCarnivoresSimple(final String content, final World world) {
	if (content.trim().isEmpty()) {
		return;
	}
	
	final String[] objects = content.split("},\\s*\\{");
	for (String obj : objects) {
		obj = obj.replaceAll("[{}]", "").trim();
		if (obj.isEmpty()) {
			continue;
		}
		
		final int energy = extractNumberFromObject(obj, "energy");
		final int posx = extractNumberFromObject(obj, "posx");
		final int posy = extractNumberFromObject(obj, "posy");
		
		final Carnivore carnivore = new Carnivore(energy);
		placeAnimalIfPossible(world, carnivore, posx, posy, "carnivore");
	}
}
}
