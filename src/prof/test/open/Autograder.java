package prof.test.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Lightweight autograder without dependency on JUnit Platform launcher.
 * Provides colored output with emojis for test execution results,
 * including detailed test status, timing, and failure details.
 *
 * <p>Features include:
 * <ul>
 * <li>Detailed test status per test (✅ / ❌) with ANSI colors</li>
 * <li>Duration per test execution</li>
 * <li>Summary per class and global summary</li>
 * <li>Failure details with abbreviated stack traces</li>
 * </ul>
 */
public class Autograder {

//=============================================================================
//                              ANSI Color Constants
//=============================================================================

private static final String RESET = "\u001B[0m";
private static final String BOLD = "\u001B[1m";
private static final String DIM = "\u001B[2m";
private static final String GREEN = "\u001B[32m";
private static final String RED = "\u001B[31m";
private static final String YELLOW = "\u001B[33m";
private static final String CYAN = "\u001B[36m";
private static final String MAGENTA = "\u001B[35m";

//=============================================================================
//                               Test Configuration
//=============================================================================

/**
 * Weight assigned to public tests in final score calculation.
 */
private static final double PUBLIC_WEIGHT = 50.0;

/**
 * Array of public test classes to be executed.
 */
private static final Class<?>[] PUBLIC_TEST_CLASSES = {
	// Basic organism-specific tests
	PlantTest.class,
	HerbivoreTest.class,
	CarnivoreTest.class,
	// Simulation tests (all controller phases)
	Phase1Test.class,
	Phase2Test.class,
	Phase3Test.class,
	Phase4Test.class,
	Phase5Test.class,
	ControllerPhasesTest.class,
	// Integration tests
	IntegrationTest.class
};

//=============================================================================
//                                Main Execution
//=============================================================================

/**
 * Main entry point for the autograder.
 *
 * @param args command line arguments (not used)
 */
public static void main(final String[] args) {
	run();
}

/**
 * Executes the autograder test suite.
 * Runs all configured test classes and displays results with colored output.
 */
public static void run() {
	System.out.println(BOLD + CYAN + "Starting Autograder..." + RESET);
	System.out.println();
	
	int totalTests = 0;
	int totalPassed = 0;
	final long startTime = System.currentTimeMillis();
	
	for (final Class<?> testClass : PUBLIC_TEST_CLASSES) {
		final TestResult result = runTestClass(testClass);
		totalTests += result.totalTests;
		totalPassed += result.passedTests;
		
		displayClassResult(testClass, result);
	}
	
	final long endTime = System.currentTimeMillis();
	displayFinalSummary(totalTests, totalPassed, endTime - startTime);
}

//------------------------------ Reset Environnement ---------------------------------
private static void resetEnvironment() {
    // Liste de classes candidates disposant potentiellement d\'un reset() statique
    String[] candidates = {
        "prof.utils.RandomGenerator",
        "student.model.core.WorldContext",
        "student.model.core.GlobalState"
    };
    for (String name : candidates) {
        try {
            Class<?> cls = Class.forName(name);
            try {
                Method m = cls.getDeclaredMethod("reset");
                m.setAccessible(true);
                m.invoke(null);
            } catch (NoSuchMethodException ignored) {
                // pas de reset() -> ignorer
            }
        } catch (ClassNotFoundException ignored) {
            // classe inexistante -> ignorer
        } catch (Exception e) {
            System.err.println("Reset warning (" + name + "): " + e.getMessage());
        }
    }
	// Garbage collector
    System.gc();
}

//------------------------------ Test Execution ------------------------------

/**
 * Runs all test methods in the specified test class.
 *
 * @param testClass the test class to execute
 * @return {@link TestResult} containing execution statistics
 */
private static TestResult runTestClass(final Class<?> testClass) {
	final TestResult result = new TestResult();
	final String className = testClass.getSimpleName();
	System.out.println(BOLD + MAGENTA + "📋 " + className + RESET);
	
	Method[] methods = testClass.getDeclaredMethods();
	for (Method method : methods) {
		if (isTestMethod(method)) {
			result.totalTests++;
			try {
				
				// Réinitialiser l'environnement avant chaque test
				resetEnvironment();
				
				// Nouvelle instance par test (comme JUnit Jupiter)
				Object testInstance = testClass.getDeclaredConstructor().newInstance();
				
				// Exécuter @BeforeEach pour cette instance
				executeBeforeEach(testInstance, testClass);
				
				// Exécuter le test
				method.setAccessible(true);
				method.invoke(testInstance);
				System.out.println(GREEN + "  ✅ " + method.getName() + RESET);
				result.passedTests++;
			} catch (InvocationTargetException ite) {
				Throwable cause = ite.getCause();
				System.out.println(RED + "  ❌ " + method.getName() + " - " +
					                   cause.getClass().getSimpleName() + ": " + cause.getMessage() + RESET);
			} catch (Exception e) {
				System.out.println(RED + "  ❌ " + method.getName() + " - " +
					                   e.getClass().getSimpleName() + ": " + e.getMessage() + RESET);
			}
		}
	}
	return result;
}

/**
 * Executes methods annotated with {@code @BeforeEach}.
 *
 * @param testInstance the test class instance
 * @param testClass    the test class
 */
private static void executeBeforeEach(final Object testInstance, final Class<?> testClass) {
	for (final Method method : testClass.getDeclaredMethods()) {
		if (method.isAnnotationPresent(BeforeEach.class)) {
			try {
				method.setAccessible(true);
				method.invoke(testInstance);
			} catch (final Exception e) {
				System.err.println(YELLOW + "Warning: BeforeEach method failed: " + e.getMessage() + RESET);
			}
		}
	}
}

//------------------------------ Utility Methods ------------------------------

/**
 * Checks if a method is a test method.
 *
 * @param method the method to check
 * @return {@code true} if method is annotated with {@code @Test} and not disabled
 */
private static boolean isTestMethod(final Method method) {
	return method.isAnnotationPresent(Test.class) &&
		       !method.isAnnotationPresent(Disabled.class);
}

/**
 * Displays the test results for a specific class.
 *
 * @param testClass the test class
 * @param result    the test execution result
 */
private static void displayClassResult(final Class<?> testClass, final TestResult result) {
	final double successRate = result.totalTests > 0 ?
		                           (double) result.passedTests / result.totalTests * 100.0 : 0.0;
	
	System.out.println();
	System.out.printf("   %s%d/%d tests passed (%.1f%%)%s%n",
		successRate == 100.0 ? GREEN : (successRate >= 50.0 ? YELLOW : RED),
		result.passedTests, result.totalTests, successRate, RESET);
	System.out.println();
}

/**
 * Displays the final summary of all test executions.
 *
 * @param totalTests  total number of tests executed
 * @param totalPassed total number of tests that passed
 * @param duration    total execution time in milliseconds
 */
private static void displayFinalSummary(final int totalTests, final int totalPassed, final long duration) {
	final double overallSuccessRate = totalTests > 0 ?
		                                  (double) totalPassed / totalTests * 100.0 : 0.0;
	final double score = overallSuccessRate * PUBLIC_WEIGHT / 100.0;
	
	System.out.println(BOLD + CYAN + "=".repeat(60) + RESET);
	System.out.println(BOLD + CYAN + "📊 FINAL SUMMARY" + RESET);
	System.out.println(BOLD + CYAN + "=".repeat(60) + RESET);
	
	System.out.printf("Total tests: %d%n", totalTests);
	System.out.printf("Passed: %s%d%s%n", GREEN, totalPassed, RESET);
	System.out.printf("Failed: %s%d%s%n", RED, totalTests - totalPassed, RESET);
	System.out.printf("Success rate: %s%.1f%%%s%n",
		overallSuccessRate >= 75.0 ? GREEN : (overallSuccessRate >= 50.0 ? YELLOW : RED),
		overallSuccessRate, RESET);
	System.out.printf("Score: %s%.1f/%.1f%s%n",
		score >= PUBLIC_WEIGHT * 0.75 ? GREEN : (score >= PUBLIC_WEIGHT * 0.5 ? YELLOW : RED),
		score, PUBLIC_WEIGHT, RESET);
	System.out.printf("Duration: %s%d ms%s%n", DIM, duration, RESET);
	
	System.out.println(BOLD + CYAN + "=".repeat(60) + RESET);
}

//------------------------------ Inner Classes ------------------------------

/**
 * Holds test execution results for a single test class.
 */
private static class TestResult {
	int totalTests = 0;
	int passedTests = 0;
}
}
