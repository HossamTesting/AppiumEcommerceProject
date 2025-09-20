package utility;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * AnnotationTransformer is a TestNG utility class that dynamically attaches a retry analyzer
 * (e.g., {@link RetryAnalyzer}) to all test methods at runtime.
 *
 * <p>This class eliminates the need to annotate each test method individually with
 * <code>@Test(retryAnalyzer = RetryAnalyzer.class)</code>. Instead, it applies the retry analyzer
 * globally to all tests via TestNG's {@link IAnnotationTransformer} interface.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Automatically sets {@link RetryAnalyzer} for all test methods.</li>
 *   <li>Simplifies retry configuration and maintenance.</li>
 *   <li>Integrates seamlessly with TestNG without modifying test classes.</li>
 * </ul>
 *
 * <p>To activate this transformer, it should be registered in the <code>testng.xml</code>
 *
 * <pre>{@code
 * <listeners>
 *     <listener class-name="utility.AnnotationTransformer"/>
 * </listeners>
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
public class AnnotationTransformer implements IAnnotationTransformer {

    /**
     * Modifies the behavior of TestNG test annotations at runtime by setting a retry analyzer.
     *
     * <p>This method is called by TestNG for each test method during the test discovery phase.
     * It injects the specified {@link RetryAnalyzer} into each {@link ITestAnnotation}, enabling
     * automatic retry logic without modifying the test class or method.
     *
     * @param annotation      the TestNG {@link ITestAnnotation} being processed
     * @param testClass       the test class (can be {@code null} if not applicable)
     * @param testConstructor the test constructor (can be {@code null} if not applicable)
     * @param testMethod      the test method (can be {@code null} if not applicable)
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
