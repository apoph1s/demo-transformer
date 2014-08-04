package eu.fusepoolp3.demotransformer;

import com.memetix.mst.translate.Translate;
import eu.fusepool.extractor.server.ExtractorServer;
import org.wymiwyg.commons.util.arguments.ArgumentHandler;

public class Main {

    public static void main(String[] args) throws Exception {
        Arguments arguments = ArgumentHandler.readArguments(Arguments.class, args);
        if (arguments != null) {
            start(arguments);
        }
    }

    private static void start(Arguments arguments) throws Exception {
        ExtractorServer server = new ExtractorServer(arguments.getPort());
        // set client id and secret
        Translate.setClientId("cf64ca27-db0d-4b86-bc86-1a8561adca49");
        Translate.setClientSecret("4j2jJnOE2v94MD1sDzeKpwddqckr1/PR1SRotmyJQnU=");

        server.start(new DemoTransformer());

        server.join();
    }
}
