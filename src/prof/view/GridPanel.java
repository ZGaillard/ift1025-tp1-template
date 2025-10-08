package prof.view;

import student.model.core.Cell;
import student.model.core.Position;
import student.model.core.World;
import student.model.organisms.Animal;
import student.model.organisms.Carnivore;
import student.model.organisms.Herbivore;
import student.model.organisms.Plant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel d'affichage de la grille de simulation.
 * Chaque cellule est divisée en deux zones : animal (haut) et plante (bas).
 */
public class GridPanel extends JPanel {

// Couleurs pour les différents types d'organismes
private static final Color EMPTY_COLOR = new Color(250, 250, 250);
private static final Color GRID_LINE_COLOR = new Color(70, 70, 70); // lignes externes foncées
private static final Color SUBDIV_LINE_COLOR = new Color(170, 170, 170); // séparation plante/animal plus clair
// Remplacement des tableaux par une couleur unique par type
private static final Color PLANT_COLOR = new Color(56, 180, 50);      // Vert
private static final Color HERBIVORE_COLOR = new Color(25, 118, 210); // Bleu
private static final Color CARNIVORE_COLOR = new Color(198, 40, 40);  // Rouge
private World world;
private int gridWidth = 20;
private int gridHeight = 20;
private int currentCellSize = 30; // recalculé dynamiquement

public GridPanel() {
	// Enlever la taille fixe pour permettre l'expansion adaptative
	setBackground(EMPTY_COLOR);
	setBorder(null); // look plus moderne
	
	// Ajouter un listener pour afficher les infos au clic
	addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			showCellInfo(e.getX(), e.getY());
		}
	});
}

public void setWorld(World world) {
	this.world = world;
	if (world != null) {
		this.gridWidth = world.getWidth();
		this.gridHeight = world.getHeight();
		updateSize();
	}
}

private void updateSize() {
	// Ne plus forcer preferred size -> layout flex
	revalidate();
}

@Override
protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2d = (Graphics2D) g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	if (gridWidth > 0 && gridHeight > 0) {
		int w = getWidth();
		int h = getHeight();
		currentCellSize = Math.max(12, Math.min(w / gridWidth, h / gridHeight));
	}
	// Dessin du fond de la grille (optionnel pour contraste)
	g2d.setColor(new Color(255, 255, 255));
	g2d.fillRect(0, 0, getWidth(), getHeight());
	// Dessin contenu
	drawOrganisms(g2d);
	// Dessin des grilles par-dessus (pour bien voir les limites)
	drawGrid(g2d);
}

private void drawGrid(Graphics2D g2d) {
	int totalW = currentCellSize * gridWidth;
	int totalH = currentCellSize * gridHeight;
	int offsetX = (getWidth() - totalW) / 2;
	int offsetY = (getHeight() - totalH) / 2;
	
	float strokeW = Math.max(1f, Math.min(3f, currentCellSize / 14f));
	g2d.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
	g2d.setColor(GRID_LINE_COLOR);
	
	// Lignes verticales
	for (int x = 0; x <= gridWidth; x++) {
		int px = offsetX + x * currentCellSize;
		g2d.drawLine(px, offsetY, px, offsetY + totalH);
	}
	// Lignes horizontales
	for (int y = 0; y <= gridHeight; y++) {
		int py = offsetY + y * currentCellSize;
		g2d.drawLine(offsetX, py, offsetX + totalW, py);
	}
}

private void drawCell(Graphics2D g2d, Cell cell, int gridX, int gridY) {
	int totalW = currentCellSize * gridWidth;
	int totalH = currentCellSize * gridHeight;
	int offsetX = (getWidth() - totalW) / 2;
	int offsetY = (getHeight() - totalH) / 2;
	int pixelX = offsetX + gridX * currentCellSize;
	int pixelY = offsetY + gridY * currentCellSize;
	
	boolean hasAnimal = cell.hasAnimal();
	boolean hasPlant = cell.hasPlant();
	
	// Aucun organisme
	if (!hasAnimal && !hasPlant) {
		g2d.setColor(EMPTY_COLOR);
		g2d.fillRect(pixelX + 1, pixelY + 1, currentCellSize - 2, currentCellSize - 2);
		return;
	}
	
	// Un seul organisme : occupe toute la cellule
	if (hasAnimal ^ hasPlant) { // XOR => exactement un présent
		Rectangle full = new Rectangle(pixelX + 1, pixelY + 1, currentCellSize - 2, currentCellSize - 2);
		if (hasAnimal) {
			drawAnimal(g2d, cell.getAnimal(), full);
		} else {
			drawPlant(g2d, cell.getPlant(), full);
		}
		return;
	}
	
	// Deux organismes : division (animal haut 2/3, plante bas 1/3)
	int animalHeight = (currentCellSize * 2) / 3;
	Rectangle animalZone = new Rectangle(pixelX + 1, pixelY + 1, currentCellSize - 2, animalHeight - 1);
	int plantHeight = currentCellSize - animalHeight;
	Rectangle plantZone = new Rectangle(pixelX + 1, pixelY + animalHeight, currentCellSize - 2, plantHeight - 1);
	
	drawAnimal(g2d, cell.getAnimal(), animalZone);
	drawPlant(g2d, cell.getPlant(), plantZone);
	
	// Ligne de séparation interne
	g2d.setColor(SUBDIV_LINE_COLOR);
	float sepStroke = Math.max(1f, Math.min(2f, currentCellSize / 20f));
	Stroke old = g2d.getStroke();
	g2d.setStroke(new BasicStroke(sepStroke));
	g2d.drawLine(pixelX + 1, pixelY + animalHeight, pixelX + currentCellSize - 1, pixelY + animalHeight);
	g2d.setStroke(old);
}

private void drawOrganisms(Graphics2D g2d) {
	if (world == null) return;
	for (int x = 0; x < gridWidth; x++) {
		for (int y = 0; y < gridHeight; y++) {
			Position pos = new Position(x, y);
			Cell cell = world.getCell(pos);
			if (cell != null) {
				drawCell(g2d, cell, x, y);
			}
		}
	}
}

private void drawAnimal(Graphics2D g2d, Animal animal, Rectangle zone) {
	Color color = getAnimalColor(animal);
	g2d.setColor(color);
	g2d.fill(zone);
	
	// Dessiner l'énergie au centre
	drawEnergyText(g2d, animal.getEnergy(), zone);
	
	// Bordure plus foncée
	g2d.setColor(color.darker());
	g2d.setStroke(new BasicStroke(1));
	g2d.draw(zone);
}

private void drawPlant(Graphics2D g2d, Plant plant, Rectangle zone) {
	Color color = getPlantColor();
	g2d.setColor(color);
	g2d.fill(zone);
	
	// Dessiner l'énergie au centre
	drawEnergyText(g2d, plant.getEnergy(), zone);
	
	// Bordure plus foncée
	g2d.setColor(color.darker());
	g2d.setStroke(new BasicStroke(1));
	g2d.draw(zone);
}

private void drawEnergyText(Graphics2D g2d, int energy, Rectangle zone) {
	g2d.setColor(Color.WHITE);
	int fontSize = Math.max(10, currentCellSize / 3);
	g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
	
	FontMetrics fm = g2d.getFontMetrics();
	String energyStr = String.valueOf(energy);
	int textWidth = fm.stringWidth(energyStr);
	int textHeight = fm.getAscent();
	
	int x = zone.x + (zone.width - textWidth) / 2;
	int y = zone.y + (zone.height + textHeight) / 2;
	
	g2d.drawString(energyStr, x, y);
}

private Color getAnimalColor(Animal animal) {
	if (animal instanceof Herbivore) {
		return HERBIVORE_COLOR;
	} else if (animal instanceof Carnivore) {
		return CARNIVORE_COLOR;
	}
	return new Color(128, 128, 128); // fallback neutre
}

private Color getPlantColor() {
	return PLANT_COLOR;
}

private void showCellInfo(int mouseX, int mouseY) {
	if (world == null) return;
	int totalW = currentCellSize * gridWidth;
	int totalH = currentCellSize * gridHeight;
	int offsetX = (getWidth() - totalW) / 2;
	int offsetY = (getHeight() - totalH) / 2;
	mouseX -= offsetX;
	mouseY -= offsetY;
	if (mouseX < 0 || mouseY < 0) return;
	int gridX = mouseX / currentCellSize;
	int gridY = mouseY / currentCellSize;
	
	if (gridX >= 0 && gridX < gridWidth && gridY >= 0 && gridY < gridHeight) {
		Position pos = new Position(gridX, gridY);
		Cell cell = world.getCell(pos);
		
		if (cell != null) {
			StringBuilder info = new StringBuilder();
			info.append("Position: (").append(gridX).append(", ").append(gridY).append(")\n");
			
			if (cell.hasAnimal()) {
				Animal animal = cell.getAnimal();
				String type = animal instanceof Herbivore ? "Herbivore" : "Carnivore";
				info.append("Animal: ").append(type).append(" (Énergie: ").append(animal.getEnergy()).append(")\n");
			}
			
			if (cell.hasPlant()) {
				Plant plant = cell.getPlant();
				info.append("Plante: Énergie ").append(plant.getEnergy()).append("\n");
			}
			
			if (!cell.hasAnimal() && !cell.hasPlant()) {
				info.append("Cellule vide");
			}
			
			JOptionPane.showMessageDialog(this, info.toString(), "Info Cellule", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}

}
