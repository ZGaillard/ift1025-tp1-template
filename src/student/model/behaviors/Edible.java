/* ============================================================================
 * Path: src/student/model/behaviors/Edible.java
 * Author: Zakary Gaillard-D.
 * Date: 2025-10-06
 * Description: Behavior interface for entities that can be eaten for nutritional energy.
 * ========================================================================== */
package student.model.behaviors;

/**
 * Defines an entity that can be consumed to yield a nutritional energy value.
 */
public interface Edible {
    /**
     * Return the nutritional energy granted when this entity is consumed.
     *
     * @return positive energy value transferred to the consumer
     */
    int nutrition();
}
