import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Lab: passwordStrength(String password) -> "WEAK", "OK", "STRONG"
 *
 * Spec idea (for students to enforce via tests):
 * - Do NOT modify the user's password (no trim!)
 * - Length >= 12 for STRONG, >= 8 for OK
 * - At least: 1 uppercase, 1 lowercase, 1 digit, 1 symbol
 * - Reject common passwords
 *
 * INTENTIONALLY FLAWED "AI-style" implementation:
 * - trims password (changes meaning)
 * - counts length after trimming (can "upgrade" bad passwords)
 * - uses simplistic symbol check (fails many cases)
 * - common password list too small and case-sensitive
 * - allows passwords with spaces after trim changes
 */
public class PasswordStrengthLab {

    private static final Set<String> COMMON = new HashSet<>(Arrays.asList(
            "password", "12345678", "qwerty", "letmein"
    ));

    // ---- Intentionally flawed implementation ----
    public static String passwordStrength(String password) {
        if (password == null) return "WEAK";

        // BUG: modifies password
        password = password.trim();

        if (password.length() < 8) return "WEAK";

        // BUG: case-sensitive common check and too small
        if (COMMON.contains(password)) return "WEAK";

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        // BUG: simplistic "symbol" definition; misses many and includes underscore only
        boolean hasSymbol = password.matches(".*[_!@#$].*");

        int score = 0;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSymbol) score++;

        // "AI-style": simplistic thresholds
        if (password.length() >= 12 && score >= 3) return "STRONG";
        if (score >= 2) return "OK";
        return "WEAK";
    }

    // ---- Test harness ----
    private static int passed = 0;
    private static int failed = 0;

    private static void assertEquals(String name, String expected, String actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("[PASS] " + name + " -> " + actual);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
            System.out.println("  expected: " + expected);
            System.out.println("  actual:   " + actual);
        }
    }

    public static void main(String[] args) {
        System.out.println("Running PasswordStrengthLab tests...\n");

        // These are "reasonable expectations" students can debate/refine.
        assertEquals("null", "WEAK", passwordStrength(null));
        assertEquals("short", "WEAK", passwordStrength("Ab1!"));

        // Common password (should be weak) - this will PASS
        assertEquals("common 'password'", "WEAK", passwordStrength("password"));

        // Case variant of common password (should ALSO be weak) - likely FAIL due to case-sensitivity
        assertEquals("common 'Password' (case variant)", "WEAK", passwordStrength("Password"));

        // Leading/trailing spaces should probably NOT be ignored (depends on spec) - this will FAIL because trim()
        assertEquals("spaces preserved", "WEAK", passwordStrength("   Abcdef1!   "));

        // Strong candidate (should be strong) - may PASS
        assertEquals("strong candidate", "STRONG", passwordStrength("CorrectHorse1!"));

        // Has symbol not in [_!@#$] (e.g. '.') should count as symbol by most specs - will FAIL
        assertEquals("symbol '.' counts", "OK", passwordStrength("Abcdef12.."));

        System.out.println("\nSummary: passed=" + passed + " failed=" + failed);

        // Instructor note: ask students to (1) define precise spec, (2) improve symbol definition,
        // (3) remove trim, (4) improve common-password handling, (5) add more tests.
        if (failed > 0) {
            System.exit(1);
        }
    }
}