package classifier;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Classifier classifier = new Classifier();
        //List<LexemProbabilities> lexemProbabilities = classifier.train();
        classifier.test();
        //classifier.evaluate("");

        System.out.println();
    }

}
