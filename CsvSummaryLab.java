import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Lab: Read a CSV file and compute simple summaries:
 * - number of rows
 * - average age
 * - unique cities
 *
 * INTENTIONALLY FLAWED "AI-style" implementation :)
 * - naive split(",") breaks on quoted commas (e.g., "Bob, Jr.")
 * - doesn't correctly unescape quotes (e.g., Eve ""The Hacker"")
 * - doesn't robustly trim/clean quoted fields
 * - may silently mis-parse rows instead of failing loudly
 */
public class CsvSummaryLab {

    // ---- Intentionally flawed CSV parser ----
    // Returns list of rows, each row is a list of fields.
    public static List<List<String>> readCsv(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // "AI-style": ignore empty lines
                if (line.trim().isEmpty()) continue;

                // BUG: naive split does not respect quoted commas
                String[] parts = line.split(",");

                List<String> row = new ArrayList<>();
                for (String p : parts) {
                    // "AI-style cleanup": trim, strip surrounding quotes
                    String s = p.trim();
                    if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
                        s = s.substring(1, s.length() - 1);
                    }
                    row.add(s);
                }
                rows.add(row);
            }
        }
        return rows;
    }

    public static Map<String, Object> summarisePeopleCsv(String path) throws IOException {
        List<List<String>> rows = readCsv(path);
        if (rows.isEmpty()) throw new IllegalArgumentException("No data");

        // header assumed to be first row
        List<String> header = rows.get(0);
        int idxAge = header.indexOf("age");
        int idxCity = header.indexOf("city");

        // "AI-style": assume columns exist
        int count = 0;
        int totalAge = 0;
        Set<String> cities = new HashSet<>();

        for (int i = 1; i < rows.size(); i++) {
            List<String> r = rows.get(i);
            // BUG: if row split goes wrong, indexes might be off -> may throw or misread.
            int age = Integer.parseInt(r.get(idxAge).trim());
            String city = r.get(idxCity).trim();
            cities.add(city);

            totalAge += age;
            count++;
        }

        Map<String, Object> out = new HashMap<>();
        out.put("rows", count);
        out.put("avgAge", count == 0 ? 0.0 : (totalAge * 1.0 / count));
        out.put("cities", cities);
        return out;
    }

    // ---- Simple test harness ----
    private static int passed = 0;
    private static int failed = 0;

    private static void assertTrue(String name, boolean condition, String detailIfFail) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + detailIfFail);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running CsvSummaryLab...\n");

        String path = "people.csv"; // expected alongside this .java file

        try {
            Map<String, Object> summary = summarisePeopleCsv(path);
            System.out.println("Computed summary: " + summary + "\n");

            // These *should* be true for a correct parser.
            // With the intentional flaws, you will typically get wrong results or exceptions.
            assertTrue("rows==5", ((int) summary.get("rows")) == 5, "Expected 5 data rows");
            double avgAge = (double) summary.get("avgAge");
            assertTrue("avgAge approx 20.0", Math.abs(avgAge - 20.0) < 0.0001, "Expected avg 20.0, got " + avgAge);

            @SuppressWarnings("unchecked")
            Set<String> cities = (Set<String>) summary.get("cities");
            assertTrue("cities contains New York", cities.contains("New York"), "Missing New York; got " + cities);
            assertTrue("cities contains San Francisco", cities.contains("San Francisco"), "Missing San Francisco; got " + cities);

        } catch (Exception ex) {
            // This is also a valid teaching outcome: naive parsers often crash.
            failed++;
            System.out.println("[FAIL] Exception thrown (this is expected for the flawed parser): " + ex);
        }

        System.out.println("\nSummary: passed=" + passed + " failed=" + failed);

        // Instructor note: ask students to (1) fix parser or (2) replace with a robust approach,
        // and add tests for quoted commas and escaped quotes.
        if (failed > 0) {
            System.exit(1);
        }
    }
}