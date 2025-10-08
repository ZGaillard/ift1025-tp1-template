package prof.view;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.IntConsumer;

/**
 * Panel de contrôle pour la simulation avec design moderne.
 * Contient les boutons de chargement de fichier et de contrôle de simulation.
 */
public class ControlPanel extends JPanel {

// Composants de chargement de fichier
private JButton loadButton;
private JLabel fileLabel;

// Composants de contrôle de simulation
private JButton playPauseButton;
private JButton stepButton;
private JButton nextPhaseButton;
private JButton resetButton;
private JSpinner tickSpinner;
private JLabel tickLabel;
private JLabel phaseLabel;

// Boutons pour les phases individuelles
private JButton phase1Button;
private JButton phase2Button;
private JButton phase3Button;
private JButton phase4Button;
private JButton phase5Button;

private JPanel filePanel;
private JPanel centerPanel;
private JPanel controlsPanel;
private JPanel phasesPanel;

// État
private boolean simulationRunning = false;
private String loadedFileName = "Aucun fichier chargé";

// Listeners
private ActionListener fileLoadListener;
private ActionListener playPauseListener;
private ActionListener stepListener;
private ActionListener nextPhaseListener;
private ActionListener resetListener;
private IntConsumer tickChangeListener;

// Listeners pour les phases
private ActionListener phase1Listener;
private ActionListener phase2Listener;
private ActionListener phase3Listener;
private ActionListener phase4Listener;
private ActionListener phase5Listener;

// Échelle pour le redimensionnement dynamique
private double lastScale = 1.0;

public ControlPanel() {
	initializeComponents();
	setupLayout();
	setupStyling();
	setupEventHandlers();
}

private void initializeComponents() {
	// Boutons sans émojis
	loadButton = new JButton("Charger configuration");
	playPauseButton = new JButton("Démarrer");
	stepButton = new JButton("Tour complet");
	nextPhaseButton = new JButton("Prochaine phase");
	resetButton = new JButton("Réinitialiser");
	
	// Boutons de phases individuelles
	phase1Button = new JButton("Phase 1");
	phase2Button = new JButton("Phase 2");
	phase3Button = new JButton("Phase 3");
	phase4Button = new JButton("Phase 4");
	phase5Button = new JButton("Phase 5");
	
	// Label pour le fichier chargé
	fileLabel = new JLabel(loadedFileName);
	phaseLabel = new JLabel("Phase: --");
	tickSpinner = new JSpinner(new SpinnerNumberModel(600, 50, 5000, 50));
	tickLabel = new JLabel("Tick (ms)");
	
	// Configuration initiale
	updateButtonStates();
}

private void setupLayout() {
	setLayout(new BorderLayout(0, 5));
	
	// Panel du haut : fichier + contrôles principaux
	// Panels pour layout responsive
	JPanel topRow = new JPanel(new BorderLayout());
	topRow.setOpaque(false);
	
	filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
	filePanel.setOpaque(false);
	filePanel.add(loadButton);
	filePanel.add(new JLabel(" | "));
	filePanel.add(fileLabel);
	
	centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	centerPanel.setOpaque(false);
	centerPanel.add(tickLabel);
	centerPanel.add(tickSpinner);
	centerPanel.add(Box.createHorizontalStrut(10));
	centerPanel.add(phaseLabel);
	
	controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
	controlsPanel.setOpaque(false);
	controlsPanel.add(playPauseButton);
	controlsPanel.add(nextPhaseButton);
	controlsPanel.add(stepButton);
	controlsPanel.add(resetButton);
	
	topRow.add(filePanel, BorderLayout.WEST);
	topRow.add(centerPanel, BorderLayout.CENTER);
	topRow.add(controlsPanel, BorderLayout.EAST);
	
	// Panel du bas : phases individuelles (aligné à droite)
	phasesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
	phasesPanel.setOpaque(false);
	phasesPanel.add(new JLabel("Phases: "));
	phasesPanel.add(phase1Button);
	phasesPanel.add(phase2Button);
	phasesPanel.add(phase3Button);
	phasesPanel.add(phase4Button);
	phasesPanel.add(phase5Button);
	
	add(topRow, BorderLayout.NORTH);
	add(phasesPanel, BorderLayout.SOUTH);
}

private void setupStyling() {
	setBackground(new Color(248, 249, 250));
	setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
	styleAll();
}

private void styleAll() {
	styleButton(loadButton, new Color(46, 204, 113));
	styleButton(playPauseButton, new Color(52, 152, 219));
	styleButton(nextPhaseButton, new Color(155, 89, 182));
	styleButton(stepButton, new Color(241, 196, 15));
	styleButton(resetButton, new Color(231, 76, 60));
	
	// Style des boutons de phase
	stylePhaseButton(phase1Button, new Color(46, 204, 113));
	stylePhaseButton(phase2Button, new Color(52, 152, 219));
	stylePhaseButton(phase3Button, new Color(230, 126, 34));
	stylePhaseButton(phase4Button, new Color(155, 89, 182));
	stylePhaseButton(phase5Button, new Color(149, 165, 166));
	
	fileLabel.setFont(scaledFont(Font.ITALIC, 12));
	phaseLabel.setFont(scaledFont(Font.BOLD, 13));
	phaseLabel.setForeground(new Color(41, 128, 185));
	tickLabel.setFont(scaledFont(Font.PLAIN, 12));
	tickSpinner.setFont(scaledFont(Font.PLAIN, 13));
	
	int spinnerW = (int) Math.round(100 * lastScale);
	int spinnerH = (int) Math.round(32 * lastScale);
	Dimension d = new Dimension(spinnerW, spinnerH);
	tickSpinner.setPreferredSize(d);
	tickSpinner.setMinimumSize(new Dimension(80, spinnerH));
	tickSpinner.setMaximumSize(new Dimension(120, spinnerH));
	
	fileLabel.setForeground(new Color(90, 98, 105));
	
	// Ajuster les espacements des panels
	int hgap = (int) Math.round(8 * lastScale);
	((FlowLayout) filePanel.getLayout()).setHgap(hgap);
	((FlowLayout) centerPanel.getLayout()).setHgap((int) Math.round(10 * lastScale));
	((FlowLayout) controlsPanel.getLayout()).setHgap((int) Math.round(6 * lastScale));
	((FlowLayout) phasesPanel.getLayout()).setHgap(hgap);
	
	int padding = (int) Math.round(8 * lastScale);
	int hpadding = (int) Math.round(12 * lastScale);
	setBorder(BorderFactory.createEmptyBorder(padding, hpadding, padding, hpadding));
}

private Font scaledFont(int style, int base) {
	return new Font(Font.SANS_SERIF, style, (int) Math.round(base * lastScale));
}

private void styleButton(JButton button, Color color) {
	int h = (int) Math.round(36 * lastScale);
	int minW = (int) Math.round(120 * lastScale);
	int maxW = (int) Math.round(180 * lastScale);
	button.setBackground(color);
	button.setForeground(Color.WHITE);
	button.setFocusPainted(false);
	button.setBorderPainted(false);
	button.setFont(scaledFont(Font.BOLD, 13));
	button.setPreferredSize(new Dimension(minW, h));
	button.setMinimumSize(new Dimension((int) (minW * 0.8), h));
	button.setMaximumSize(new Dimension(maxW, h));
	button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	button.setMargin(new Insets(4, 10, 4, 10));
}

private void stylePhaseButton(JButton button, Color color) {
	int h = (int) Math.round(28 * lastScale);
	int w = (int) Math.round(85 * lastScale);
	button.setBackground(color);
	button.setForeground(Color.WHITE);
	button.setFocusPainted(false);
	button.setBorderPainted(false);
	button.setFont(scaledFont(Font.BOLD, 11));
	button.setPreferredSize(new Dimension(w, h));
	button.setMinimumSize(new Dimension((int) (w * 0.8), h));
	button.setMaximumSize(new Dimension((int) (w * 1.2), h));
	button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	button.setMargin(new Insets(3, 6, 3, 6));
}

private void setupEventHandlers() {
	loadButton.addActionListener(e -> {
		if (fileLoadListener != null) fileLoadListener.actionPerformed(e);
	});
	playPauseButton.addActionListener(e -> {
		if (playPauseListener != null) playPauseListener.actionPerformed(e);
	});
	stepButton.addActionListener(e -> {
		if (stepListener != null) stepListener.actionPerformed(e);
	});
	nextPhaseButton.addActionListener(e -> {
		if (nextPhaseListener != null) nextPhaseListener.actionPerformed(e);
	});
	resetButton.addActionListener(e -> {
		if (resetListener != null) resetListener.actionPerformed(e);
	});
	ChangeListener cl = _ -> {
		if (tickChangeListener != null) tickChangeListener.accept((Integer) tickSpinner.getValue());
	};
	tickSpinner.addChangeListener(cl);
	
	// Listeners pour les phases
	phase1Button.addActionListener(e -> {
		if (phase1Listener != null) phase1Listener.actionPerformed(e);
	});
	phase2Button.addActionListener(e -> {
		if (phase2Listener != null) phase2Listener.actionPerformed(e);
	});
	phase3Button.addActionListener(e -> {
		if (phase3Listener != null) phase3Listener.actionPerformed(e);
	});
	phase4Button.addActionListener(e -> {
		if (phase4Listener != null) phase4Listener.actionPerformed(e);
	});
	phase5Button.addActionListener(e -> {
		if (phase5Listener != null) phase5Listener.actionPerformed(e);
	});
}

private void updateButtonStates() {
	stepButton.setEnabled(!simulationRunning);
	nextPhaseButton.setEnabled(!simulationRunning);
	phase1Button.setEnabled(!simulationRunning);
	phase2Button.setEnabled(!simulationRunning);
	phase3Button.setEnabled(!simulationRunning);
	phase4Button.setEnabled(!simulationRunning);
	phase5Button.setEnabled(!simulationRunning);
	resetButton.setEnabled(true);
}

// === Setters pour les listeners ===

public void setFileLoadListener(ActionListener listener) {
	this.fileLoadListener = listener;
}

public void setPlayPauseListener(ActionListener listener) {
	this.playPauseListener = listener;
}

public void setStepListener(ActionListener listener) {
	this.stepListener = listener;
}

public void setNextPhaseListener(ActionListener listener) {
	this.nextPhaseListener = listener;
}

public void setResetListener(ActionListener listener) {
	this.resetListener = listener;
}

public void setTickChangeListener(IntConsumer consumer) {
	this.tickChangeListener = consumer;
}

// Setters pour les listeners de phase
public void setPhase1Listener(ActionListener listener) {
	this.phase1Listener = listener;
}

public void setPhase2Listener(ActionListener listener) {
	this.phase2Listener = listener;
}

public void setPhase3Listener(ActionListener listener) {
	this.phase3Listener = listener;
}

public void setPhase4Listener(ActionListener listener) {
	this.phase4Listener = listener;
}

public void setPhase5Listener(ActionListener listener) {
	this.phase5Listener = listener;
}

// === Méthodes publiques pour mise à jour ===

public void setSimulationRunning(boolean running) {
	this.simulationRunning = running;
	playPauseButton.setText(running ? "Pause" : "Démarrer");
	updateButtonStates();
}

public void setLoadedFileName(String fileName) {
	this.loadedFileName = fileName;
	fileLabel.setText(fileName);
}

public void setCurrentPhase(String phaseName) {
	if (phaseName == null || phaseName.isEmpty()) {
		phaseLabel.setText("Phase: --");
	} else {
		phaseLabel.setText("Phase: " + phaseName);
	}
}

public void applyScale(double scale) {
	this.lastScale = scale;
	styleAll();
	revalidate();
	repaint();
}

public void setTickMs(int ms) {
	tickSpinner.setValue(Math.max(50, Math.min(5000, ms)));
}
}
