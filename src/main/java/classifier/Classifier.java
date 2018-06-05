package classifier;

import classifier.crawlers.Channels;
import classifier.utils.Reader;
import classifier.utils.Writer;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Classifier {

    private int N = 30;                 // number of lexems to pick that are farthest from neutral probability
    private double neutralProb = 0.5;   // neutral probability
    private int minCharacters = 4;      // minimum number of characters a word should have to not be ignored
    private int trainSize = 4000;       // train dataset size for each category
    private int testSize = 500;        // test dataset size for each category
    private String regex = "[^a-zA-ZąčęėįšųūžĄČĘĖĮŠŲŪŽ$'\"]";  // regular expression for tokenizing the string into lexems
    private List<LexemProbabilities> probabilities;

    // list of words that should be ignored
    private List<String> wordsToIgnore =
            Arrays.asList("kaip", "buvo", "taip", "tačiau", "apie", "gali", "savo", "tiek",
                    "todėl", "nėra", "turi", "kurie", "nors", "arba", "kuriuos", "šiuo",
                    "prie", "daug", "tarp", "kuri", "šios", "kiek", "toks", "jūsų");

    public Classifier() {
        this.probabilities = Reader.readProbabilities();
    }

    /**
     * Test this classifier
     */
    public List<LexemProbabilities> test() {

        // lists that contains articles texts
        List<String> autoArticles    = Reader.read(Channels.Auto, trainSize, testSize);
        List<String> sportasArticles = Reader.read(Channels.Sportas, trainSize, testSize);
        List<String> verslasArticles = Reader.read(Channels.Verslas, trainSize, testSize);
        List<String> mokslasArticles = Reader.read(Channels.Mokslas, trainSize, testSize);

        LexemProbabilities result;
        int autoCorrectCount = 0;
        int sportasCorrectCount = 0;
        int verslasCorrectCount = 0;
        int mokslasCorrectCount = 0;

        for(String article : autoArticles) {
            result = evaluate(article);
            if (result.isAutoMax()) {
                autoCorrectCount++;
            }
        }

        for(String article : sportasArticles) {
            result = evaluate(article);
            if (result.isSportasMax()) {
                sportasCorrectCount++;
            }
        }

        for(String article : verslasArticles) {
            result = evaluate(article);
            if (result.isVerslasMax()) {
                verslasCorrectCount++;
            }
        }

        for(String article : mokslasArticles) {
            result = evaluate(article);
            if (result.isMokslasMax()) {
                mokslasCorrectCount++;
            }
        }

        System.out.println("Auto correct: " + autoCorrectCount + "/" + testSize);
        System.out.println("Sportas correct: " + sportasCorrectCount + "/" + testSize);
        System.out.println("Verslas correct: " + verslasCorrectCount + "/" + testSize);
        System.out.println("Mokslas correct: " + mokslasCorrectCount + "/" + testSize);

        return null;
    }

    public LexemProbabilities evaluate(String content) {
        List<String> lexems = getLexems(content);
        List<LexemProbabilities> lexemProbabilities = evaluateProbabilities(lexems);

        List<LexemProbabilities> importantAuto    = findImportantLexemsAuto(lexemProbabilities);
        List<LexemProbabilities> importantSportas = findImportantLexemsSportas(lexemProbabilities);
        List<LexemProbabilities> importantVerslas = findImportantLexemsVerslas(lexemProbabilities);
        List<LexemProbabilities> importantMokslas = findImportantLexemsMokslas(lexemProbabilities);

        double probAuto = findProbabilityAuto(importantAuto);
        double probSportas = findProbabilitySportas(importantSportas);
        double probVerslas = findProbabilityVerslas(importantVerslas);
        double probMokslas = findProbabilityMokslas(importantMokslas);

        LexemProbabilities result = new LexemProbabilities();
        result.setAuto(probAuto);
        result.setSportas(probSportas);
        result.setVerslas(probVerslas);
        result.setMokslas(probMokslas);

        return result;
    }

    private List<String> getLexems(String content) {
        return Pattern.compile(regex)
                      .splitAsStream(content)
                      .filter(lexem -> !lexem.isEmpty())
                      .collect(Collectors.toList());
    }

    private List<LexemProbabilities> evaluateProbabilities(List<String> lexems) {
        List<LexemProbabilities> result = new ArrayList<>();

        for (String lexem : lexems) {
            Optional<LexemProbabilities> optional = probabilities.stream().filter(prob -> prob.getLexem().equals(lexem)).findFirst();

            if (optional.isPresent()) {
                result.add(optional.get());
            } else {
                LexemProbabilities temp = new LexemProbabilities();
                temp.setLexem(lexem);
                result.add(temp);
            }
        }

        return result;
    }

    private List<LexemProbabilities> findImportantLexemsAuto(List<LexemProbabilities> probabilities) {
        return probabilities.stream()
                .sorted((first, second) -> {
                    double firstAuto = first.getAuto();
                    double secondAuto = second.getAuto();
                    return Double.compare(Math.abs(neutralProb - secondAuto), Math.abs(neutralProb - firstAuto));
                })
                .limit(N)
                .collect(Collectors.toList());
    }

    private List<LexemProbabilities> findImportantLexemsSportas(List<LexemProbabilities> probabilities) {
        return probabilities.stream()
                .sorted((first, second) -> {
                    double firstSportas = first.getSportas();
                    double secondSportas = second.getSportas();
                    return Double.compare(Math.abs(neutralProb - secondSportas), Math.abs(neutralProb - firstSportas));
                })
                .limit(N)
                .collect(Collectors.toList());
    }

    private List<LexemProbabilities> findImportantLexemsVerslas(List<LexemProbabilities> probabilities) {
        return probabilities.stream()
                .sorted((first, second) -> {
                    double firstVerslas = first.getVerslas();
                    double secondVerslas = second.getVerslas();
                    return Double.compare(Math.abs(neutralProb - secondVerslas), Math.abs(neutralProb - firstVerslas));
                })
                .limit(N)
                .collect(Collectors.toList());
    }

    private List<LexemProbabilities> findImportantLexemsMokslas(List<LexemProbabilities> probabilities) {
        return probabilities.stream()
                .sorted((first, second) -> {
                    double firstMokslas = first.getMokslas();
                    double secondMokslas = second.getMokslas();
                    return Double.compare(Math.abs(neutralProb - secondMokslas), Math.abs(neutralProb - firstMokslas));
                })
                .limit(N)
                .collect(Collectors.toList());
    }

    private double findProbabilityAuto(List<LexemProbabilities> importantAuto) {
        double numerator = importantAuto.stream()
                .mapToDouble(LexemProbabilities::getAuto)
                .reduce((a, b) -> a * b).getAsDouble();

        double denominator = numerator +
                importantAuto.stream()
                    .mapToDouble(LexemProbabilities::getAuto)
                    .reduce((a, b) -> (1 - a) * (1 - b)).getAsDouble();

        return numerator / denominator;
    }

    private double findProbabilitySportas(List<LexemProbabilities> importantSportas) {
        double numerator = importantSportas.stream()
                .mapToDouble(LexemProbabilities::getSportas)
                .reduce((a, b) -> a * b).getAsDouble();

        double denominator = numerator +
                importantSportas.stream()
                        .mapToDouble(LexemProbabilities::getSportas)
                        .reduce((a, b) -> (1 - a) * (1 - b)).getAsDouble();

        return numerator / denominator;
    }

    private double findProbabilityVerslas(List<LexemProbabilities> importantVerslas) {
        double numerator = importantVerslas.stream()
                .mapToDouble(LexemProbabilities::getVerslas)
                .reduce((a, b) -> a * b).getAsDouble();

        double denominator = numerator +
                importantVerslas.stream()
                        .mapToDouble(LexemProbabilities::getVerslas)
                        .reduce((a, b) -> (1 - a) * (1 - b)).getAsDouble();

        return numerator / denominator;
    }

    private double findProbabilityMokslas(List<LexemProbabilities> importantMokslas) {
        double numerator = importantMokslas.stream()
                .mapToDouble(LexemProbabilities::getMokslas)
                .reduce((a, b) -> a * b).getAsDouble();

        double denominator = numerator +
                importantMokslas.stream()
                        .mapToDouble(LexemProbabilities::getMokslas)
                        .reduce((a, b) -> (1 - a) * (1 - b)).getAsDouble();

        return numerator / denominator;
    }

    public List<LexemProbabilities> train() {

        // lists that contains articles texts
        List<String> autoArticles    = Reader.read(Channels.Auto, trainSize);
        List<String> sportasArticles = Reader.read(Channels.Sportas, trainSize);
        List<String> verslasArticles = Reader.read(Channels.Verslas, trainSize);
        List<String> mokslasArticles = Reader.read(Channels.Mokslas, trainSize);

        // maps of lexems and their repetition counts
        Map<String, Long> auto = process(autoArticles);
        Map<String, Long> sportas = process(sportasArticles);
        Map<String, Long> verslas = process(verslasArticles);
        Map<String, Long> mokslas = process(mokslasArticles);

        // a set of all unique lexems
        Set<String> lexems = getLexems(auto, sportas, verslas, mokslas);

        // a list of lexem probabilities
        List<LexemProbabilities> lexemProbabilities = findProbabilities(lexems, auto, sportas, verslas, mokslas);

        Writer.writeProbabilities(lexemProbabilities);

        return lexemProbabilities;
    }

    /**
     * Splits each article text into lexems and finds their repetition counts
     */
    private Map<String, Long> process(List<String> contents) {
        Map<String, Long> result = new HashMap<>();

        contents.stream()
                .forEach(content -> {
                    Pattern.compile(regex)
                            .splitAsStream(content)
                            .filter(word -> !word.isEmpty() && word.length() >= minCharacters && !wordsToIgnore.contains(word))
                            .collect(Collectors.groupingBy(Function.identity(), HashMap::new, Collectors.counting()))
                            .forEach((k, v) -> {
                                result.merge(k, v, Long::sum);
                            });
                });

        return result;
    }

    /**
     * Finds all unique lexems in the provided maps
     */
    private Set<String> getLexems(Map<String, Long>... maps) {
        Set<String> lexems = new HashSet<>();

        for (Map<String, Long> map : maps) {
            Set<String> temp = map.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
            lexems.addAll(temp);
        }

        return lexems;
    }

    /**
     * Finds the probabilities of each lexem
     */
    private List<LexemProbabilities> findProbabilities(Set<String> lexems,
                                                       Map<String, Long> auto,
                                                       Map<String, Long> sportas,
                                                       Map<String, Long> verslas,
                                                       Map<String, Long> mokslas) {
        List<LexemProbabilities> result = new ArrayList<>();

        for (String lexem : lexems) {

            LexemProbabilities lexemProbabilities = new LexemProbabilities(lexem);

            double probWAuto = findPWC(lexem, auto);
            double probWNotAuto = findPWN(lexem, sportas, verslas, mokslas);
            if (probWAuto == 0) {
                lexemProbabilities.setAuto(0.01);
            } else if (probWNotAuto == 0) {
                lexemProbabilities.setAuto(0.99);
            } else {
                lexemProbabilities.setAuto(probWAuto / (probWAuto + probWNotAuto));
            }

            double probWSportas = findPWC(lexem, sportas);
            double probWNotSportas = findPWN(lexem, auto, verslas, mokslas);
            if (probWSportas == 0) {
                lexemProbabilities.setSportas(0.01);
            } else if (probWNotSportas == 0) {
                lexemProbabilities.setSportas(0.99);
            } else {
                lexemProbabilities.setSportas(probWSportas / (probWSportas + probWNotSportas));
            }

            double probWVerslas = findPWC(lexem, verslas);
            double probWNotVerslas = findPWN(lexem, auto, sportas, mokslas);
            if (probWVerslas == 0) {
                lexemProbabilities.setVerslas(0.01);
            } else if (probWNotVerslas == 0) {
                lexemProbabilities.setVerslas(0.99);
            } else {
                lexemProbabilities.setVerslas(probWVerslas / (probWVerslas + probWNotVerslas));
            }

            double probWMokslas = findPWC(lexem, mokslas);
            double probWNotMokslas = findPWN(lexem, auto, sportas, verslas);
            if (probWMokslas == 0) {
                lexemProbabilities.setMokslas(0.01);
            } else if (probWNotMokslas == 0) {
                lexemProbabilities.setMokslas(0.99);
            } else {
                lexemProbabilities.setMokslas(probWMokslas / (probWMokslas + probWNotMokslas));
            }

            result.add(lexemProbabilities);
        }

        return result;
    }

    /**
     * finds probability that a given lexem belongs to a certain category
     */
    private double findPWC(String lexem, Map<String, Long> category) {
        double result = 0;
        if (category.containsKey(lexem)) {
            result = category.get(lexem) / (double) category.size();
        }
        return result;
    }

    /**
     *
     */
    private double findPWN(String lexem, Map<String, Long>... categories) {
        int totalNumberOfLexems = 0;
        long repetitions = 0;

        for (Map<String, Long> category : categories) {
            if (category.containsKey(lexem)) {
                repetitions += category.get(lexem);
            }
            totalNumberOfLexems += category.size();
        }

        if (repetitions == 0) {
            return 0;
        } else {
            return (double) repetitions / (double) totalNumberOfLexems;
        }
    }

}
