package prof.view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Panel de logging pour afficher les messages de la simulation.
 * Affiche les événements avec horodatage et possibilité d'export.
 */
public class LoggerPanel extends JPanel {

private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
private JTextArea logArea;
private JScrollPane scrollPane;
private JButton clearButton;
private JButton exportButton;
private JCheckBox autoScrollCheckBox;
private double lastScale = 1.0;

public LoggerPanel() {
	initializeComponents();
	setupLayout();
	setupStyling();
	setupEventHandlers();
}

private void initializeComponents() {
	// Zone de texte pour les logs
	logArea = new JTextArea();
	logArea.setEditable(false);
	logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
	logArea.setRows(30);
	logArea.setColumns(30);
	
	// Scroll pane pour la zone de texte
	scrollPane = new JScrollPane(logArea);
	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	// Boutons de contrôle
	clearButton = new JButton("Effacer");
	exportButton = new JButton("Exporter");
	autoScrollCheckBox = new JCheckBox("Auto-scroll", true);
	
	// Message initial
	log("Logger initialisé - prêt pour la simulation");
}

private void setupLayout() {
	setLayout(new BorderLayout());
	
	// Panel principal avec la zone de log
	add(scrollPane, BorderLayout.CENTER);
	
	// Panel du bas avec les contrôles
	JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	controlsPanel.add(clearButton);
	controlsPanel.add(exportButton);
	controlsPanel.add(autoScrollCheckBox);
	
	add(controlsPanel, BorderLayout.SOUTH);
}

private void setupStyling() {
	setBorder(null); // style épuré
	setPreferredSize(new Dimension((int) (360 * lastScale), (int) (600 * lastScale)));
	restyle();
}

private void restyle() {
	logArea.setBackground(new Color(248, 249, 250));
	logArea.setForeground(new Color(33, 37, 41));
	logArea.setBorder(BorderFactory.createEmptyBorder((int) (12 * lastScale), (int) (14 * lastScale), (int) (12 * lastScale), (int) (14 * lastScale)));
	int baseFont = (int) Math.round(12 * lastScale);
	logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, Math.max(11, baseFont)));
	styleButton(clearButton, new Color(220, 53, 69));
	styleButton(exportButton, new Color(40, 167, 69));
	autoScrollCheckBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(11, (int) (11 * lastScale))));
}

private void styleButton(JButton button, Color color) {
	int h = (int) Math.round(40 * lastScale);
	int w = (int) Math.round(140 * lastScale);
	button.setBackground(color);
	button.setForeground(Color.WHITE);
	button.setFocusPainted(false);
	button.setBorderPainted(false);
	button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(11, (int) (13 * lastScale))));
	button.setPreferredSize(new Dimension(w, h));
	button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	button.setMargin(new Insets(4, 10, 4, 10));
}

private void setupEventHandlers() {
	clearButton.addActionListener(_ -> clearLog());
	exportButton.addActionListener(_ -> exportLog());
}

/**
 * Ajoute un message au log avec horodatage.
 */
public void log(String message) {
	SwingUtilities.invokeLater(() -> {
		String timestamp = timeFormat.format(new Date());
		String logEntry = String.format("[%s] %s%n", timestamp, message);
		
		logArea.append(logEntry);
		
		// Auto-scroll vers le bas si activé
		if (autoScrollCheckBox.isSelected()) {
			logArea.setCaretPosition(logArea.getDocument().getLength());
		}
	});
}

/**
 * Ajoute un message d'erreur avec formatting spécial.
 */
public void logError(String message) {
	log("ERREUR: " + message);
}

/**
 * Efface tous les logs.
 */
private void clearLog() {
	int result = JOptionPane.showConfirmDialog(
		this,
		"Êtes-vous sûr de vouloir effacer tous les logs ?",
		"Confirmation",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE
	);
	
	if (result == JOptionPane.YES_OPTION) {
		logArea.setText("");
		log("Logs effacés");
	}
}

/**
 * Exporte les logs vers un fichier.
 */
private void exportLog() {
	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setSelectedFile(new java.io.File("simulation_log_" +
		                                             new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
	
	int result = fileChooser.showSaveDialog(this);
	if (result == JFileChooser.APPROVE_OPTION) {
		try {
			java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
			writer.write(logArea.getText());
			writer.close();
			log("Logs exportés vers: " + fileChooser.getSelectedFile().getName());
		} catch (Exception e) {
			logError("Impossible d'exporter les logs: " + e.getMessage());
		}
	}
}

public void applyScale(double scale) {
	this.lastScale = scale;
	setPreferredSize(new Dimension((int) (360 * lastScale), (int) (600 * lastScale)));
	restyle();
	revalidate();
	repaint();
}
}
