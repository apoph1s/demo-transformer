/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fusepoolp3.demotransformer;

import eu.fusepool.extractor.HttpRequestEntity;
import eu.fusepool.extractor.RdfGeneratingExtractor;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.clerezza.rdf.core.BNode;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.commons.io.IOUtils;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.clerezza.rdf.ontologies.SIOC;

/**
 *
 * @author Gabor
 */
public class DemoTransformer extends RdfGeneratingExtractor {


    
    @Override
    protected TripleCollection generateRdf(HttpRequestEntity entity) throws IOException {
        final String queryString = entity.getRequest().getQueryString();
        final String original = IOUtils.toString(entity.getData(), "UTF-8");
        final TripleCollection result = new SimpleMGraph();  
        final GraphNode node = new GraphNode(new BNode(), result);
        GraphNode nodes;
        
//        System.out.println(queryString);
//        System.out.println(original);
       
        HashMap<String,String> queryParams = new HashMap<>();
        
        if(queryString != null){
            String[] params = queryString.split("&");
            String[] param;
            for (int i = 0; i < params.length; i++) {
                param = params[i].split("=", 2);
                queryParams.put(param[0], param[1]);
            }
        }        

        // language string to translate from
        String from = queryParams.get("from");
        
        // language string to translate to
        String to = queryParams.get("to");
        
        // language to translate from
        Language fromLanguage = Language.fromString(from);
        if(fromLanguage == null) {
            fromLanguage = Language.AUTO_DETECT;
        }
        
        // language to translate to
        Language toLanguage = Language.fromString(to);
        if(toLanguage == null) {
            throw new RuntimeException("No language was supplied to translate to!");
        }
        
        // translated text
        String translation;
        
        // tanslate original text
        try {
            translation = Translate.execute(original, fromLanguage, toLanguage);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

//        System.out.println("Original phrase: " + original);
//        System.out.println("Translated phrase: " + translation);
        

        if (original != null && !original.isEmpty()) {
            node.addProperty(RDF.type, new UriRef("http://example.org/ontology#TextDescription"));
            node.addPropertyValue(SIOC.content, original);
            node.addPropertyValue(new UriRef("http://example.org/ontology#textLength"), original.length());
            
            nodes = new GraphNode(new BNode(), result);
            nodes.addProperty(RDF.type, new UriRef("http://example.org/ontology#LanguageAnnotation"));
            nodes.addPropertyValue(new UriRef("http://example.org/ontology#fromLanguage"), fromLanguage.toString());
            nodes.addPropertyValue(new UriRef("http://example.org/ontology#toLanguage"), toLanguage.toString());
            nodes.addPropertyValue(SIOC.content, translation);
            nodes.addPropertyValue(new UriRef("http://example.org/ontology#textLength"), translation.length());
        }
        
        return result;
    }


    @Override
    public Set<MimeType> getSupportedInputFormats() {
        try {
            MimeType mimeType = new MimeType("text/plain;charset=UTF-8");
            return Collections.singleton(mimeType);
        } catch (MimeTypeParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public boolean isLongRunning() {
        return false;
    }
}
