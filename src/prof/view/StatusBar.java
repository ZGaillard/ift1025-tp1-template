package prof.view;

import javax.swing.*;
import java.awt.*;

/**
 * Barre de statut affichant les informations de la simulation.
 * Montre le tour actuel, les populations et l'état de la simulation.
 */
public class StatusBar extends JPanel {

private JLabel statusLabel;
private JLabel turnLabel;
private JLabel phaseLabel;
private JLabel plantsLabel;
private JLabel herbivoresLabel;
private JLabel carnivoresLabel;
private JLabel simulationStateLabel;

private JPanel leftPanel;
private JPanel centerPanel;
private JPanel rightPanel;

private double lastScale = 1.0;

public StatusBar() {
	initializeComponents();
	setupLayout();
	setupStyling();
	updateStatus("Prêt", 0, 0, 0, 0);
}

private void initializeComponents() {
	statusLabel = new JLabel("Prêt");
	turnLabel = new JLabel("Tour: 0");
	phaseLabel = new JLabel("Phase: --");
	plantsLabel = new JLabel("Plantes: 0");
	herbivoresLabel = new JLabel("Herbivores: 0");
	carnivoresLabel = new JLabel("Carnivores: 0");
	simulationStateLabel = new JLabel("Arrêtée");
}

private void setupLayout() {
	setLayout(new BorderLayout());
	
	// Panel gauche : statut général
	leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
	leftPanel.setOpaque(false);
	leftPanel.add(statusLabel);
	leftPanel.add(createSeparator());
	leftPanel.add(turnLabel);
	leftPanel.add(createSeparator());
	leftPanel.add(phaseLabel);
	
	// Panel centre : populations
	centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
	centerPanel.setOpaque(false);
	centerPanel.add(plantsLabel);
	centerPanel.add(herbivoresLabel);
	centerPanel.add(carnivoresLabel);
	
	// Panel droit : état simulation
	rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
	rightPanel.setOpaque(false);
	rightPanel.add(simulationStateLabel);
	
	add(leftPanel, BorderLayout.WEST);
	add(centerPanel, BorderLayout.CENTER);
	add(rightPanel, BorderLayout.EAST);
}

private void setupStyling() {
	setBackground(new Color(248, 249, 250));
	setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	restyle();
}

private void restyle() {
	int base = (int) Math.round(12 * lastScale);
	int baseBold = (int) Math.round(13 * lastScale);
	Font statusFont = new Font(Font.SANS_SERIF, Font.PLAIN, base);
	Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, baseBold);
	
	statusLabel.setFont(boldFont);
	turnLabel.setFont(statusFont);
	phaseLabel.setFont(statusFont);
	phaseLabel.setForeground(new Color(155, 89, 182));
	plantsLabel.setFont(statusFont);
	herbivoresLabel.setFont(statusFont);
	carnivoresLabel.setFont(statusFont);
	simulationStateLabel.setFont(boldFont);
	
	// Ajuster les espacements des panels en fonction de l'échelle
	int hgap = (int) Math.round(10 * lastScale);
	int vgap = (int) Math.round(5 * lastScale);
	((FlowLayout) leftPanel.getLayout()).setHgap(hgap);
	((FlowLayout) leftPanel.getLayout()).setVgap(vgap);
	((FlowLayout) centerPanel.getLayout()).setHgap((int) Math.round(15 * lastScale));
	((FlowLayout) centerPanel.getLayout()).setVgap(vgap);
	((FlowLayout) rightPanel.getLayout()).setHgap(hgap);
	((FlowLayout) rightPanel.getLayout()).setVgap(vgap);
	
	int padding = (int) Math.round(5 * lastScale);
	int hpadding = (int) Math.round(10 * lastScale);
	setBorder(BorderFactory.createEmptyBorder(padding, hpadding, padding, hpadding));
	
	int minHeight = (int) Math.round(35 * lastScale);
	setMinimumSize(new Dimension(0, minHeight));
	setPreferredSize(new Dimension(getWidth(), minHeight));
}

public void applyScale(double scale) {
	this.lastScale = scale;
	restyle();
	revalidate();
	repaint();
}

private JLabel createSeparator() {
	JLabel separator = new JLabel("|");
	separator.setForeground(new Color(173, 181, 189));
	return separator;
}

/**
 * Met à jour toutes les informations de statut.
 */
public void updateStatus(String status, int turn, int plants, int herbivores, int carnivores) {
	SwingUtilities.invokeLater(() -> {
		statusLabel.setText(status);
		updateTurn(turn);
		updatePopulations(plants, herbivores, carnivores);
	});
}

/**
 * Met à jour uniquement le numéro de tour.
 */
public void updateTurn(int turn) {
	SwingUtilities.invokeLater(() -> turnLabel.setText("Tour: " + turn));
}

/**
 * Met à jour la phase courante.
 */
public void updatePhase(String phaseName) {
	SwingUtilities.invokeLater(() -> {
		if (phaseName == null || phaseName.isEmpty()) {
			phaseLabel.setText("Phase: --");
		} else {
			phaseLabel.setText("Phase: " + phaseName);
		}
	});
}

/**
 * Met à jour les populations d'organismes.
 */
public void updatePopulations(int plants, int herbivores, int carnivores) {
	SwingUtilities.invokeLater(() -> {
		plantsLabel.setText("Plantes: " + plants);
		herbivoresLabel.setText("Herbivores: " + herbivores);
		carnivoresLabel.setText("Carnivores: " + carnivores);
	});
}

}
