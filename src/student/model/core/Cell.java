/* ============================================================================
 * Path: src/student/model/core/Cell.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Grid cell holding at most one animal and/or one plant along with its position.
 * ========================================================================== */
package student.model.core;

import student.model.organisms.Animal;
import student.model.organisms.Organism;
import student.model.organisms.Plant;

/**
 * Represents a single grid cell that may contain one {@link Animal} and one {@link Plant}.
 * <p>Cells are simple containers; they perform no validation beyond slot occupancy. Higher-level
 * logic (movement, spawning, cleanup) lives in controller or organism code.</p>
 */
public class Cell {
//=============================================================================
//                                   Fields
//=============================================================================
    private final Position position;
    private Animal animal;
    private Plant plant;

//=============================================================================
//                               Construction
//=============================================================================
    /**
     * Construct a cell at the provided immutable {@link Position}.
     *
     * @param position non-null grid coordinates reference
     */
    public Cell(final Position position) {
        this.position = position; // DONOTTOUCH[core] (MAINTAINER, 2025-10-06): Positional reference only.
    }

//=============================================================================
//                               Accessors
//=============================================================================
    /**
     * Return this cell's position.
     *
     * @return immutable position handle
     */
    public Position getPosition() {
        return position;
    }

    //=============================================================================
    //                                 Animals
    //=============================================================================
    /**
     * Return whether an animal occupies the animal slot.
     *
     * @return {@code true} if an animal is present
     */
    public boolean hasAnimal() {
        return animal != null;
    }

    /**
     * Return the resident animal or {@code null}.
     *
     * @return current animal or {@code null}
     */
    public Animal getAnimal() {
        return animal;
    }

    /**
     * Set (overwrite) the resident animal reference.
     *
     * @param animal animal to store (may be {@code null})
     */
    public void setAnimal(final Animal animal) {
        this.animal = animal;
    }

    /**
     * Remove the resident animal (if any).
     */
    public void removeAnimal() {
        this.animal = null;
    }

    /**
     * Return whether the animal slot is currently empty.
     *
     * @return {@code true} if no animal reference
     */
    public boolean isEmptyAnimal() {
        return animal == null;
    }

    //=============================================================================
    //                                  Plants
    //=============================================================================
    /**
     * Return whether a plant occupies the plant slot.
     *
     * @return {@code true} if a plant is present
     */
    public boolean hasPlant() {
        return plant != null;
    }

    /**
     * Return the resident plant or {@code null}.
     *
     * @return current plant or {@code null}
     */
    public Plant getPlant() {
        return plant;
    }

    /**
     * Set (overwrite) the resident plant reference.
     *
     * @param plant plant to store (may be {@code null})
     */
    public void setPlant(final Plant plant) {
        this.plant = plant;
    }

    /**
     * Remove the resident plant (if any).
     */
    public void removePlant() {
        this.plant = null;
    }

    /**
     * Return whether the plant slot is currently empty.
     *
     * @return {@code true} if no plant reference
     */
    public boolean isEmptyPlant() {
        return plant == null;
    }

    //=============================================================================
    //                                  General
    //=============================================================================
    /**
     * Return whether both animal and plant slots are empty.
     *
     * @return {@code true} if cell has no occupants
     */
    public boolean isCompletelyEmpty() {
        return animal == null && plant == null;
    }

    /**
     * Alias for {@link #isCompletelyEmpty()} kept for backward compatibility.
     *
     * @return {@code true} if cell has no occupants
     */
    public boolean isEmpty() {
        return isCompletelyEmpty();
    }

    /**
     * Return whichever organism is present prioritizing the animal slot, or {@code null} if empty.
     * <p>This compatibility helper is used by legacy code expecting a single occupant abstraction.</p>
     *
     * @return {@link Animal}, {@link Plant}, or {@code null}
     */
    public Organism getOrganism() {
        if (animal != null) return animal;
        if (plant != null) return plant;
        return null;
    }
}
