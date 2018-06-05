package classifier.utils;

import classifier.LexemProbabilities;
import classifier.crawlers.Channels;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Reader {

    private static final String dataDirectory = "data/";
    private static final String probabilitiesFile = "probabilities.txt";

    public static List<String> read(Channels channels) {
        List<String> fileContents = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(dataDirectory + channels.name()))) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            String content = new String(Files.readAllBytes(path));
                            if (!content.isEmpty()){
                                fileContents.add(content.toLowerCase());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {

        }

        return fileContents;
    }

    public static List<String> read(Channels channels, int limit) {
        List<String> fileContents = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(dataDirectory + channels.name()))) {
            paths.filter(Files::isRegularFile)
                    .limit(limit)
                    .forEach(path -> {
                        try {
                            String content = new String(Files.readAllBytes(path));
                            if (!content.isEmpty()){
                                fileContents.add(content.toLowerCase());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {

        }

        return fileContents;
    }

    public static List<String> read(Channels channels, int skip, int limit) {
        List<String> fileContents = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(dataDirectory + channels.name()))) {
            paths.filter(Files::isRegularFile)
                    .skip(skip)
                    .limit(limit)
                    .forEach(path -> {
                        try {
                            String content = new String(Files.readAllBytes(path));
                            if (!content.isEmpty()){
                                fileContents.add(content.toLowerCase());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {

        }

        return fileContents;
    }

    public static List<LexemProbabilities> readProbabilities() {
        List<LexemProbabilities> probabilities = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(probabilitiesFile))) {
            stream.forEach(
                    line -> {
                        if (!line.isEmpty()) {
                            String[] fields = line.split(";");

                            LexemProbabilities prob = new LexemProbabilities(fields[0]);
                            prob.setAuto(Double.valueOf(fields[1]));
                            prob.setSportas(Double.valueOf(fields[2]));
                            prob.setVerslas(Double.valueOf(fields[3]));
                            prob.setMokslas(Double.valueOf(fields[4]));

                            probabilities.add(prob);
                        }
                    }
            );

        } catch (IOException e) {
        }

        return probabilities;
    }

}
