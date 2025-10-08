package prof.view;

import student.controller.SimulationController;
import student.model.core.World;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Interface graphique principale de la simulation d'écosystème.
 * Chaque cellule est divisée en deux zones : animal (haut) et plante (bas).
 */
public class GUI extends JFrame {

private static final int BASE_WIDTH = 1400;
private final SimulationController controller; // contrôleur de simulation MVC
// Composants principaux
private JPanel topPanel;
private JPanel centerPanel;
private JPanel bottomPanel;
private JSplitPane splitPane;
// Composants spécialisés
private GridPanel gridPanel;
private LoggerPanel loggerPanel;
private ControlPanel controlPanel;
private StatusBar statusBar;
// Données
private double uiScale = 1.0;

public GUI() {
	initializeComponents();
	controller = new SimulationController();
	attachControllerListeners();
	// Branchement tick_ms (valeur initiale + écoute des changements)
	controlPanel.setTickMs(controller.getTickIntervalMs());
	controlPanel.setTickChangeListener(controller::setTickIntervalMs);
	setupLayout();
	setupStyling();
	setupEventHandlers();
	setTitle("Simulation Écosystème");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setSize(1400, 900);
	setLocationRelativeTo(null);
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception ignored) {
	}
	addComponentListener(new java.awt.event.ComponentAdapter() {
		@Override
		public void componentResized(java.awt.event.ComponentEvent e) {
			recomputeScale();
		}
	});
	recomputeScale();
}

// Point d'entrée pour test
public static void main(String[] args) {
	SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
}

private void initializeComponents() {
	// Panels principaux
	topPanel = new JPanel(new BorderLayout());
	centerPanel = new JPanel(new BorderLayout());
	bottomPanel = new JPanel(new BorderLayout());
	
	// Composants spécialisés
	controlPanel = new ControlPanel();
	gridPanel = new GridPanel();
	loggerPanel = new LoggerPanel();
	statusBar = new StatusBar();
	
	// Split pane pour grille + logger
	splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, loggerPanel);
	splitPane.setDividerLocation(800);
	splitPane.setResizeWeight(0.75);
}

private void attachControllerListeners() {
	controller.addListener(new SimulationController.SimulationListener() {
		@Override
		public void onWorldChanged(student.model.core.World world) {
			SwingUtilities.invokeLater(() -> {
				gridPanel.setWorld(world);
				updateWorldStatistics();
				gridPanel.repaint();
			});
		}
		
		@Override
		public void onTurnAdvanced(int turn) {
			SwingUtilities.invokeLater(() -> {
				statusBar.updateTurn(turn);
				updateWorldStatistics();
			});
		}
		
		@Override
		public void onSimulationStateChanged(boolean running) {
			SwingUtilities.invokeLater(() -> controlPanel.setSimulationRunning(running));
		}
		
		@Override
		public void onLog(String message) {
			SwingUtilities.invokeLater(() -> loggerPanel.log(message));
		}
		
		@Override
		public void onPhaseChanged(SimulationController.Phase phase) {
			SwingUtilities.invokeLater(() -> {
				if (phase == null) {
					controlPanel.setCurrentPhase(null);
					statusBar.updatePhase(null);
				} else {
					controlPanel.setCurrentPhase(phase.getPhaseName());
					statusBar.updatePhase(phase.getPhaseName());
				}
			});
		}
	});
}

private void setupLayout() {
	setLayout(new BorderLayout());
	
	topPanel.add(controlPanel, BorderLayout.CENTER);
	centerPanel.add(splitPane, BorderLayout.CENTER);
	bottomPanel.add(statusBar, BorderLayout.CENTER);
	
	add(topPanel, BorderLayout.NORTH);
	add(centerPanel, BorderLayout.CENTER);
	add(bottomPanel, BorderLayout.SOUTH);
}

private void setupStyling() {
	Color backgroundColor = new Color(240, 242, 245);
	
	topPanel.setBorder(new EmptyBorder(8, 16, 8, 16));
	bottomPanel.setBorder(new EmptyBorder(4, 16, 8, 16));
	
	topPanel.setBackground(backgroundColor);
	centerPanel.setBackground(backgroundColor);
	bottomPanel.setBackground(backgroundColor);
	
	splitPane.setBorder(null);
	splitPane.setBackground(backgroundColor);
}

// === Gestionnaires d'événements ===

private void setupEventHandlers() {
	controlPanel.setFileLoadListener(_ -> onFileLoad());
	controlPanel.setPlayPauseListener(_ -> controller.toggle());
	controlPanel.setStepListener(_ -> controller.step());
	controlPanel.setNextPhaseListener(_ -> controller.stepNextPhase());
	controlPanel.setResetListener(_ -> controller.reset());
	
	// Listeners pour les phases individuelles
	controlPanel.setPhase1Listener(_ -> controller.executePhase(SimulationController.Phase.PLANT_GROWTH));
	controlPanel.setPhase2Listener(_ -> controller.executePhase(SimulationController.Phase.HERBIVORES));
	controlPanel.setPhase3Listener(_ -> controller.executePhase(SimulationController.Phase.CARNIVORES));
	controlPanel.setPhase4Listener(_ -> controller.executePhase(SimulationController.Phase.REPRODUCTION));
	controlPanel.setPhase5Listener(_ -> controller.executePhase(SimulationController.Phase.CLEANUP));
}

private void onFileLoad() {
	JFileChooser fileChooser = new JFileChooser("world_config");
	fileChooser.setAcceptAllFileFilterUsed(true);
	int result = fileChooser.showOpenDialog(this);
	if (result == JFileChooser.APPROVE_OPTION) {
		File loadedConfigFile = fileChooser.getSelectedFile();
		controlPanel.setLoadedFileName(loadedConfigFile.getName());
		controller.loadWorld(loadedConfigFile); // déclenche les callbacks
	}
}

private void updateWorldStatistics() {
	var w = controller.getWorld();
	if (w == null) return;
	int plants = 0, herbivores = 0, carnivores = 0;
	for (int y = 0; y < w.getHeight(); y++) {
		for (int x = 0; x < w.getWidth(); x++) {
			var cell = w.getCell(new student.model.core.Position(x, y));
			if (cell == null) continue;
			if (cell.hasPlant()) plants++;
			if (cell.hasAnimal()) {
				if (cell.getAnimal() instanceof Herbivore) herbivores++;
				else if (cell.getAnimal() instanceof Carnivore) carnivores++;
			}
		}
	}
	statusBar.updatePopulations(plants, herbivores, carnivores);
}

public void setWorld(World world) { // conserve méthode mais délègue
	controller.setWorld(world);
}

@Override
public void dispose() {
	super.dispose();
	if (controller != null) controller.dispose();
}

private void recomputeScale() {
	int w = getWidth();
	double newScale = Math.max(0.85, Math.min(1.6, w / (double) BASE_WIDTH));
	if (Math.abs(newScale - uiScale) > 0.03) { // éviter maj trop fréquentes
		uiScale = newScale;
		applyScale();
	}
}

private void applyScale() {
	controlPanel.applyScale(uiScale);
	loggerPanel.applyScale(uiScale);
	statusBar.applyScale(uiScale);
	revalidate();
	repaint();
}
}
