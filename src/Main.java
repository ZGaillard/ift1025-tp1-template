/* ============================================================================
 * Path: src/Main.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Provides CLI and command-line entry points for launching the GUI
 *              or executing public autograder tests for the ecosystem simulation.
 * ========================================================================== */

import prof.test.open.Autograder;
import prof.view.GUI;

import javax.swing.*;
import java.util.Scanner;

//=============================================================================
//                                  Main
//=============================================================================

/**
 * Provides the main entry point for the ecosystem simulation application.
 *
 * <p>Supports multiple launch modes:</p>
 * <ul>
 *   <li>Command-line arguments for direct mode selection</li>
 *   <li>Interactive CLI menu for mode selection</li>
 *   <li>GUI mode for visual simulation</li>
 *   <li>Autograder mode for running public tests</li>
 * </ul>
 */
public class Main {

//----------------------------- Constants ---------------------------------

/**
 * Command-line argument for GUI mode.
 */
private static final String GUI_MODE_COMMAND = "gui";

/**
 * Command-line argument for autograder mode.
 */
private static final String AUTOGRADE_MODE_COMMAND = "autograde";

//----------------------------- Main Method -------------------------------

/**
 * Starts the application.
 *
 * <p>Recognized command-line arguments:</p>
 * <ul>
 *   <li>{@code gui} - Launch GUI directly</li>
 *   <li>{@code autograde} - Run autograder tests</li>
 * </ul>
 *
 * <p>If no arguments are provided, an interactive menu is displayed.</p>
 *
 * @param args command-line arguments for mode selection
 */
public static void main(final String[] args) {
	// Quick modes via command-line arguments
	if (args.length > 0) {
		final String mode = args[0].toLowerCase();
		if (GUI_MODE_COMMAND.equals(mode)) {
			launchGui();
			return;
		} else if (AUTOGRADE_MODE_COMMAND.equals(mode)) {
			runAutograder();
			return;
		}
	}
	
	// Interactive CLI menu
	runInteractiveMenu();
}

//----------------------------- Launch Methods ----------------------------

/**
 * Launches the graphical user interface asynchronously on the EDT.
 */
private static void launchGui() {
	SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
}

/**
 * Runs the public autograder tests.
 */
private static void runAutograder() {
	Autograder.run();
}

/**
 * Runs the interactive command-line menu loop until user exits.
 */
private static void runInteractiveMenu() {
	final Scanner scanner = new Scanner(System.in);
	
	while (true) {
		displayMenu();
		final String choice = scanner.nextLine().trim();
		
		switch (choice) {
			case "1" -> {
				launchGui();
				return; // Exit menu after launching GUI
			}
			case "2" -> runAutograder();
			case "3" -> {
				System.out.println("Goodbye.");
				return;
			}
			default -> System.out.println("Invalid choice. Please try again.\n");
		}
	}
}

/**
 * Displays the interactive menu options (non-internationalized).
 */
private static void displayMenu() {
	System.out.println("=== Main Menu ===");
	System.out.println("1) Launch GUI");
	System.out.println("2) Run Autograder (public tests)");
	System.out.println("3) Exit");
	System.out.print("> ");
}
}