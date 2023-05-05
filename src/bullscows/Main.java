package bullscows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Main {


    private static final String CODE_ALLOWED_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";

    /**
     * <pre>GENERATED_CODE</pre> and <pre>GENERATED_CODE_POSITIONS</pre> are here to support in the future codes
     * With repeated digits
     */
    private static final Map<String, List<Integer>> GENERATED_CODE = new HashMap<>();

    private static final Map<Integer, String> GENERATED_CODE_POSITIONS = new HashMap<>();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        processSecretCode(scanner);

        int turn = 0;
        boolean guessed = false;

        System.out.println("Okay, let's start a game!");

        while (!guessed) {
            System.out.printf("Turn %d:%n", ++turn);

            var result = new Object() {
                long numBulls = 0;
                long numCows = 0;
            };

            var input = scanner.next();

            Map<String, List<Integer>> indexedInput = IntStream.range(0, input.length())
                    .boxed()
                    .collect(Collectors.groupingBy(i -> String.valueOf(input.charAt(i))));

            indexedInput.forEach((k, v) -> {
                result.numBulls += countParam(k, () -> v.stream().filter(GENERATED_CODE.get(k)::contains).count());
                result.numCows += countParam(k, () -> v.size() - v.stream().filter(GENERATED_CODE.get(k)::contains).count());
            });

            long totalCows = result.numCows;
            long totalBulls = result.numBulls;

            System.out.println(buildResultGradeString(totalCows, totalBulls));

            if (totalBulls == GENERATED_CODE.size()) {
                guessed = true;
            }
        }

        System.out.println("Congratulations! You guessed the secret code.");
    }

    private static void processSecretCode(Scanner scanner) {
        System.out.println("Please, enter the secret code's length:");

        int length = readLength(scanner);

        System.out.println("Input the number of possible symbols in the code:");
        int numberPossibleSymbols = scanner.nextInt() - 1;

        validateInput(length, numberPossibleSymbols);

        String code = generateSecretCode(length, numberPossibleSymbols);
        indexSecretCode(code);

        char[] stars = new char[length];
        Arrays.fill(stars, '*');
        String starsStr = Stream.of(stars).map(String::valueOf).collect(Collectors.joining(""));

        if (numberPossibleSymbols >= 10) {
            System.out.printf("The secret is prepared: %s (0-9, a-%c).%n", starsStr, CODE_ALLOWED_CHARS.charAt(numberPossibleSymbols));
        } else {
            System.out.printf("The secret is prepared: %s (0-%c).%n", starsStr, CODE_ALLOWED_CHARS.charAt(numberPossibleSymbols));
        }
    }

    private static int readLength(Scanner scanner) {
        String input = scanner.next();

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) < '0' || input.charAt(i) > '9') {
                System.out.printf("Error: \"%s\" isn't a valid number.", input);
                System.exit(0);
            }
        }

        int length = Integer.parseInt(input);

        if (length <= 0) {
            System.out.println("Error: the length of the secret code should be greater than 0");
            System.exit(0);
        }

        return length;
    }

    private static void validateInput(int length, int numberPossibleSymbols) {
        numberPossibleSymbols++;

        if (numberPossibleSymbols > 36) {
            System.out.printf("Error: it's not possible to generate a code with a length of %d with %d unique symbols.", length, numberPossibleSymbols);
            System.exit(0);
        }

        if (length > numberPossibleSymbols) {
            System.out.printf("Error: maximum number of possible symbols in the code is 36 (0-9, a-z)", length, numberPossibleSymbols);
            System.exit(0);
        }
    }

    private static void indexSecretCode(String s) {
        IntStream.range(0, s.length()).boxed().forEach(i -> {
            GENERATED_CODE.put(String.valueOf(s.charAt(i)), List.of(i));
            GENERATED_CODE_POSITIONS.put(i, String.valueOf(s.charAt(i)));
        });
    }

    private static String buildResultGradeString(long totalCows, long totalBulls) {
        StringBuilder sb = new StringBuilder();

        if ((totalCows + totalBulls) == 0) {
            sb.append("None");
        } else if (totalBulls > 0 && totalCows > 0) {
            sb.append("%d bull(s) and %d cow(s)".formatted(totalBulls, totalCows));
        } else if (totalBulls > 0) {
            sb.append("%d bull(s)".formatted(totalBulls));
        } else sb.append("%d cow(s)".formatted(totalCows));

        return "Grand: %s".formatted(sb.toString());
    }

    private static long countParam(String key, LongSupplier s) {
        if (GENERATED_CODE.containsKey(key)) {
            return s.getAsLong();
        }

        return 0;
    }

    private static String generateSecretCode(int length, int numberPossibleSymbols) {
        Random r = new Random();
        char[] secretCode = new char[length];

        for (int i = 0; i < length; i++) {
            secretCode[i] = CODE_ALLOWED_CHARS.charAt(r.nextInt(numberPossibleSymbols));
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = length - 1; i >= 0; i--) {
            if (sb.indexOf(String.valueOf(secretCode[i])) != -1) {
                return generateSecretCode(length, numberPossibleSymbols);
            }
            sb.append(secretCode[i]);
        }

        return sb.reverse().toString();
    }
}
