package classifier.utils;

import classifier.crawlers.Channels;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Writer {

    private static final String dataDirectory = "data/";

    public static void write(Channels channel, List<String> content) throws IOException {
        for (int i = 0; i < content.size(); i++) {
            Path path = Paths.get(dataDirectory + channel.name() + "/" + String.valueOf(i) + ".txt");

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(content.get(i));
            }
        }
    }

}
