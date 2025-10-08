/* ============================================================================
 * Path: src/student/controller/SimulationController.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Controller (MVC C layer) driving simulation ticks, phases, and
 *              listener notifications for the ecosystem world.
 * ========================================================================== */
package student.controller;

import prof.utils.WorldLoader;
import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//=============================================================================
//                              SimulationController
//=============================================================================

/**
 * Drive the simulation by advancing turns and their internal ordered phases.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Load and reset worlds</li>
 *   <li>Advance full turns or single phases</li>
 *   <li>Run incremental updates via a Swing {@link Timer}</li>
 *   <li>Notify registered {@link SimulationListener}s of state changes</li>
 * </ul>
 *
 * <p>Phases are executed in this strict order for a full turn:
 * PLANT_GROWTH → HERBIVORES → CARNIVORES → REPRODUCTION → CLEANUP.</p>
 */
public class SimulationController {

//=============================================================================
//                                 Configuration
//=============================================================================
/**
 * Default delay (milliseconds) between automatic simulation steps.
 */
public static final int DEFAULT_TICK_MS = 600; // Public constant (documented)

/**
 * Registered listeners receiving simulation event callbacks.
 */
private final List<SimulationListener> listeners = new ArrayList<>();

//=============================================================================
//                                   State
//=============================================================================
/**
 * Active world instance (may be {@code null} if none loaded).
 */
private World world;
/**
 * Current turn counter (starts at 0, increments before first phase of a turn).
 */
private int turn = 0;
/**
 * Whether the simulation auto-advances using the timer.
 */
private boolean running = false;
/**
 * Current phase, {@code null} when not in the middle of a partial turn.
 */
private Phase currentPhase = null; // null = no phase in progress (full turn idle)

/**
 * Swing timer used for periodic advancement (GUI control).
 */
private Timer timer;
/**
 * Current tick interval in milliseconds (clamped to >= 50).
 */
private int tickIntervalMs = DEFAULT_TICK_MS;

/**
 * Last loaded config file (used for reset).
 */
private File configFile = null;

//=============================================================================
//                               Construction
//=============================================================================

/**
 * Construct a controller with no world loaded.
 */
public SimulationController() {
}

/**
 * Construct a controller with an initial world.
 *
 * @param world initial world to manage
 */
public SimulationController(final World world) {
	setWorld(world);
}

//=============================================================================
//                               Listeners API
//=============================================================================

/**
 * Adds a listener if non-null.
 *
 * @param l listener to add
 */
public void addListener(final SimulationListener l) {
	if (l != null) listeners.add(l);
}

/**
 * Logs a message to all listeners. (Runtime strings intentionally untranslated.)
 *
 * @param msg message text
 */
private void log(final String msg) {
	for (SimulationListener l : listeners) l.onLog(msg);
}

/**
 * Notifies listeners that the world instance changed.
 */
private void fireWorldChanged() {
	for (SimulationListener l : listeners) l.onWorldChanged(world);
}

/**
 * Notifies listeners the turn counter advanced.
 */
private void fireTurnAdvanced() {
	for (SimulationListener l : listeners) l.onTurnAdvanced(turn);
}

/**
 * Notifies listeners that running/paused state changed.
 */
private void fireStateChanged() {
	for (SimulationListener l : listeners) l.onSimulationStateChanged(running);
}

/**
 * Notifies listeners that the active phase changed.
 */
private void firePhaseChanged() {
	for (SimulationListener l : listeners) l.onPhaseChanged(currentPhase);
}

//=============================================================================
//                               State Accessors
//=============================================================================

/**
 * Returns the current world.
 *
 * @return current world or {@code null}
 */
public World getWorld() {
	return world;
}

/**
 * Sets the active world, resets turn and phase indicators, and notifies listeners.
 *
 * @param world new world (may be {@code null})
 */
public void setWorld(final World world) {
	this.world = world;
	this.turn = 0;
	this.currentPhase = null;
	fireWorldChanged();
	fireTurnAdvanced();
	firePhaseChanged();
	log("Nouveau monde chargé: " + (world != null ? world.getWidth() + "x" + world.getHeight() : "<null>"));
}

/**
 * Returns current tick interval in milliseconds.
 *
 * @return interval ms
 */
public int getTickIntervalMs() {
	return tickIntervalMs;
}

/**
 * Sets tick interval (clamped to at least 50 ms) and updates timer if active.
 *
 * @param ms requested interval
 */
public void setTickIntervalMs(final int ms) {
	this.tickIntervalMs = Math.max(50, ms);
	if (timer != null) timer.setDelay(tickIntervalMs);
}

/**
 * Loads a world from the given JSON configuration file.
 *
 * @param file JSON file
 * @return {@code true} if loaded successfully
 */
public boolean loadWorld(final File file) {
	if (file == null) return false;
	final World loaded = WorldLoader.loadFromJson(file.getAbsolutePath());
	if (loaded == null) {
		log("Échec chargement: " + file.getName());
		return false;
	}
	setWorld(loaded);
	this.configFile = file;
	return true;
}

//=============================================================================
//                               Turn Control
//=============================================================================

/**
 * Advances the simulation by a full turn executing all phases in order.
 * If already mid-turn (partial phase stepping), completes remaining phases.
 */
public void step() {
	if (world == null) {
		log("Step ignoré: world nul");
		return;
	}
	
	// Complete remaining phases if mid-turn.
	if (currentPhase != null) {
		stepRemainingPhases();
		return;
	}
	
	// Start a brand new turn.
	turn++;
	log("Step: " + turn);
	currentPhase = Phase.PLANT_GROWTH;
	firePhaseChanged();
	
	phasePlantGrowth();
	phaseHerbivores();
	phaseCarnivores();
	phaseReproduction();
	phaseCleanup();
	
	currentPhase = null;
	firePhaseChanged();
	fireTurnAdvanced();
	fireWorldChanged();
}

/**
 * Executes the current phase only (or initializes a new turn), then advances to next phase.
 */
public void stepNextPhase() {
	if (world == null) {
		log("Step phase ignoré: world nul");
		return;
	}
	
	// Initialize a new turn if none in progress.
	if (currentPhase == null) {
		turn++;
		log("Step: " + turn);
		currentPhase = Phase.PLANT_GROWTH;
		firePhaseChanged();
		fireTurnAdvanced();
	}
	
	// Execute the active phase.
	executeCurrentPhase();
	
	// Move to next.
	currentPhase = currentPhase.next();
	firePhaseChanged();
	
	if (currentPhase == null) {
		log("Tour " + turn + " terminé");
	}
	
	fireWorldChanged();
}

//=============================================================================
//                         Partial Phase Progression
//=============================================================================

/**
 * Executes all remaining phases of the current turn until completion.
 */
public void stepRemainingPhases() {
	if (world == null || currentPhase == null) return;
	
	while (currentPhase != null) {
		executeCurrentPhase();
		currentPhase = currentPhase.next();
		firePhaseChanged();
	}
	
	log("Tour " + turn + " terminé");
	fireWorldChanged();
}

/**
 * Executes whatever phase is currently active (no advancement after execution).
 */
private void executeCurrentPhase() {
	if (currentPhase == null) return;
	
	switch (currentPhase) {
		case PLANT_GROWTH -> phasePlantGrowth();
		case HERBIVORES -> phaseHerbivores();
		case CARNIVORES -> phaseCarnivores();
		case REPRODUCTION -> phaseReproduction();
		case CLEANUP -> phaseCleanup();
	}
}

/**
 * Executes a specific phase in isolation (e.g., triggered by dedicated UI button).
 * Does not auto-advance to subsequent phases.
 *
 * @param phase phase to execute (ignored if {@code null})
 */
public void executePhase(final Phase phase) {
	if (world == null || phase == null) return;
	
	// Start a new turn if idle.
	if (currentPhase == null) {
		turn++;
		log("Step: " + turn);
		fireTurnAdvanced();
	}
	
	currentPhase = phase;
	firePhaseChanged();
	log("Exécution phase: " + phase.getPhaseName());
	
	executeCurrentPhase();
	
	// No automatic advancement or turn completion here.
	fireWorldChanged();
}

//=============================================================================
//                                   Phases
//=============================================================================

/**
 * Plant growth phase: each plant performs its growth behavior.
 */
public void phasePlantGrowth() {
	// TODO : Implement plant growth phase logic.
	
	// Keep fireWorldChanged() at the end of the method.
	fireWorldChanged();
}

/**
 * Herbivore movement, fleeing, and eating phase.
 */
public void phaseHerbivores() {
	// TODO : Implement herbivore movement, fleeing, and eating phase logic.
	
	// Keep fireWorldChanged() at the end of the method.
	fireWorldChanged();
}

/**
 * Carnivore movement, hunting, and eating phase.
 */
public void phaseCarnivores() {
	// TODO : Implement carnivore movement, hunting, and eating phase logic.
	
	// Keep fireWorldChanged() at the end of the method.
	fireWorldChanged();
}

/**
 * Reproduction phase for all organism types.
 */
public void phaseReproduction() {
	// TODO : Implement reproduction phase logic.
	
	// Keep fireWorldChanged() at the end of the method.
	fireWorldChanged();
}

/**
 * Cleanup phase: removes dead plants and animals from their cells.
 */
public void phaseCleanup() {
	// TODO : Implement cleanup phase logic.
	
	// Keep fireWorldChanged() at the end of the method.
	fireWorldChanged();
}

//=============================================================================
//                               Run Control
//=============================================================================

/**
 * Starts automatic simulation stepping (no-op if already running).
 */
public void start() {
	if (running) return;
	if (world == null) {
		log("Aucun monde à simuler");
		return;
	}
	running = true;
	fireStateChanged();
	if (timer == null) {
		timer = new Timer(tickIntervalMs, _ -> step());
	}
	timer.setDelay(tickIntervalMs);
	timer.start();
	log("Simulation démarrée");
}

/**
 * Pauses automatic stepping (no-op if already paused).
 */
public void pause() {
	if (!running) return;
	running = false;
	fireStateChanged();
	if (timer != null) timer.stop();
	log("Simulation en pause");
}

//=============================================================================
//                             Control Utilities
//=============================================================================

/**
 * Toggles running/paused state.
 */
public void toggle() {
	if (running) pause();
	else start();
}

/**
 * Resets the world to its original configuration file if available; otherwise creates
 * an empty world of current dimensions. Turn and phase tracking are cleared.
 */
public void reset() {
	pause();
	if (configFile != null) {
		final boolean ok = loadWorld(configFile);
		if (ok) {
			log("Monde réinitialisé depuis: " + configFile.getName());
		} else {
			if (world != null) {
				setWorld(new World(world.getWidth(), world.getHeight()));
				log("Échec reload; monde réinitialisé vide de mêmes dimensions");
			} else {
				log("Échec reload; aucun monde existant pour dimensions");
			}
		}
	} else if (world != null) {
		setWorld(new World(world.getWidth(), world.getHeight()));
		log("Monde réinitialisé");
	}
	turn = 0;
	currentPhase = null;
	fireTurnAdvanced();
	firePhaseChanged();
}

/**
 * Returns an iterable snapshot of all cells (row-major order). Used internally by phases.
 *
 * @return iterable of cells (possibly empty)
 */
private Iterable<Cell> allCells() {
	final List<Cell> cells = new ArrayList<>();
	if (world == null) return cells;
	for (int y = 0; y < world.getHeight(); y++) {
		for (int x = 0; x < world.getWidth(); x++) {
			cells.add(world.getCell(new Position(x, y)));
		}
	}
	return cells;
}

/**
 * Disposes resources and clears listeners (idempotent).
 */
public void dispose() {
	pause();
	if (timer != null) {
		timer.stop();
		timer = null;
	}
	listeners.clear();
}

//=============================================================================
//                                   Phase Enum
//=============================================================================

/**
 * Ordered simulation phases forming a complete turn.
 */
public enum Phase {
	/**
	 * Plant growth stage.
	 */
	PLANT_GROWTH("Croissance plantes"),
	/**
	 * Herbivore actions stage.
	 */
	HERBIVORES("Herbivores"),
	/**
	 * Carnivore actions stage.
	 */
	CARNIVORES("Carnivores"),
	/**
	 * Reproduction of organisms stage.
	 */
	REPRODUCTION("Reproduction"),
	/**
	 * Cleanup of dead entities stage.
	 */
	CLEANUP("Nettoyage");
	
	private final String phaseName;
	
	Phase(final String name) {
		this.phaseName = name;
	}
	
	/**
	 * Returns display name (runtime string preserved in original language).
	 *
	 * @return phase display name
	 */
	public String getPhaseName() {
		return phaseName;
	}
	
	/**
	 * Returns the next phase in sequence or {@code null} if this was the last.
	 *
	 * @return next phase or {@code null}
	 */
	public Phase next() {
		final Phase[] phases = values();
		return this.ordinal() < phases.length - 1 ? phases[this.ordinal() + 1] : null;
	}
}

//=============================================================================
//                                Listener API
//=============================================================================

/**
 * Listener interface for simulation events. Default methods allow selective override.
 */
public interface SimulationListener {
	/**
	 * Called whenever the world instance content changes materially.
	 */
	default void onWorldChanged(final World world) {
	}
	
	/**
	 * Called when the turn counter increases.
	 */
	default void onTurnAdvanced(final int turn) {
	}
	
	/**
	 * Called when the running/paused state flips.
	 */
	default void onSimulationStateChanged(final boolean running) {
	}
	
	/**
	 * Called for textual log messages.
	 */
	default void onLog(final String message) {
	}
	
	/**
	 * Called when the active phase pointer changes (may be {@code null}).
	 */
	default void onPhaseChanged(final Phase phase) {
	}
}
}
