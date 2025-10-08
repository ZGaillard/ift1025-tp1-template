/* ============================================================================
 * Path: src/student/model/behaviors/Energetic.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for organisms whose lifecycle depends on an energy pool.
 * ========================================================================== */
package student.model.behaviors;

/**
 * Defines an entity that maintains an energy pool driving survival and actions.
 * <p>Implementations decide depletion rules and death conditions. Helper default methods support
 * incremental addition and subtraction without altering the underlying contract.</p>
 */
public interface Energetic {
    //=============================================================================
    //                                   Accessors
    //=============================================================================

    /**
     * Return current energy value.
     *
     * @return non-negative (unless implementation allows debt) energy amount
     */
    int getEnergy();

    /**
     * Set the current energy value.
     *
     * @param value new absolute energy value
     */
    void setEnergy(int value);

    //=============================================================================
    //                                  Mutators
    //=============================================================================

    /**
     * Add (increase) a positive or negative delta to the energy pool.
     *
     * @param delta energy to add (may be negative for convenience)
     */
    default void addEnergy(int delta) { // DONOTTOUCH[behavior] (MAINTAINER, 2025-10-06): Keep side effects.
        setEnergy(getEnergy() + delta);
    }

    /**
     * Subtract the provided cost from the energy pool.
     *
     * @param cost energy to remove
     */
    default void subEnergy(int cost) { // DONOTTOUCH[behavior] (MAINTAINER, 2025-10-06): Keep side effects.
        setEnergy(getEnergy() - cost);
    }

    //=============================================================================
    //                                   State
    //=============================================================================

    /**
     * Determine whether the entity is still alive according to its energy rules.
     *
     * @return {@code true} if alive and eligible for further updates
     */
    boolean isAlive();
}
