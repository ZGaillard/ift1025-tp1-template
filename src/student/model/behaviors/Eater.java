/* ============================================================================
 * Path: src/student/model/behaviors/Eater.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for organisms that can consume plants or animals.
 * ========================================================================== */
package student.model.behaviors;

import student.model.core.Cell;
import student.model.core.World;

/**
 * Defines an organism that can consume other edible entities present in a {@link Cell}.
 * <p>This interface provides helper default methods to consume plants or animals while
 * transferring nutritional energy to the current energetic organism when applicable.</p>
 */
public interface Eater {
//=============================================================================
//                                   Contract
//=============================================================================

    /**
     * Determine whether this organism can eat something located in the provided cell.
     *
     * @param cell cell to inspect (may be {@code null})
     * @return {@code true} if there is edible content this eater can consume
     */
    boolean canEat(Cell cell);

    /**
     * Consume the appropriate edible entity in the given cell according to the diet.
     * Implementations decide which target to prioritize when multiple are present.
     *
     * @param cell  cell containing potential food (never modified here except through helpers)
     * @param world current world context
     */
    void eat(Cell cell, World world);

    //=============================================================================
    //                               Helpers
    //=============================================================================

    /**
     * Helper that consumes a plant if present and edible, adding its nutritional value to this
     * eater if it is also {@link Energetic}.
     *
     * @param cell  cell potentially containing a plant
     * @param world current world context (unused but kept for symmetry / future needs)
     */
    default void consumePlant(Cell cell, World world) { // DONOTTOUCH[behavior] (MAINTAINER, 2025-10-06): Logic must stay side-effect equivalent.
        if (cell.hasPlant()) {
            var plant = cell.getPlant();
            if (plant instanceof Edible edible) {
                // Gain nutritional energy if this eater stores energy.
                if (this instanceof Energetic energetic) {
                    energetic.addEnergy(edible.nutrition());
                }
                // Remove the consumed plant from the cell.
                cell.removePlant();
            }
        }
    }

    /**
     * Helper that consumes an animal if present and edible, adding its nutritional value to this
     * eater if it is also {@link Energetic}.
     *
     * @param cell  cell potentially containing an animal
     * @param world current world context (unused but kept for symmetry / future needs)
     */
    default void consumeAnimal(Cell cell, World world) { // DONOTTOUCH[behavior] (MAINTAINER, 2025-10-06): Logic must stay side-effect equivalent.
        if (cell.hasAnimal()) {
            var animal = cell.getAnimal();
            if (animal instanceof Edible edible) {
                // Gain nutritional energy if this eater stores energy.
                if (this instanceof Energetic energetic) {
                    energetic.addEnergy(edible.nutrition());
                }
                // Remove the consumed animal from the cell.
                cell.removeAnimal();
            }
        }
    }
}
