package ru.nsu.borodun;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OSMProcessor {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("OSMProcessor");
        logger.info("Hello, world!");

        if (args.length == 0) {
            System.out.println("Usage: OSMProcessor <filename>");
            return;
        }

        String fileName = args[0];

        Map<String, Integer> userEdits = new HashMap<>();
        Map<String, Integer> keyCount = new HashMap<>();

        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
             BZip2CompressorInputStream bz2InputStream = new BZip2CompressorInputStream(inputStream)) {

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(bz2InputStream);

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("node")) {
                    String user = reader.getAttributeValue(null, "user");
                    userEdits.put(user, userEdits.getOrDefault(user, 0) + 1);

                    while (reader.hasNext()) {
                        int subEvent = reader.next();
                        if (subEvent == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("node")) {
                            break;
                        } else if (subEvent == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("tag")) {
                            String key = reader.getAttributeValue(null, "k");
                            keyCount.put(key, keyCount.getOrDefault(key, 0) + 1);
                        }
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sort user edits by descending order
        Map<String, Integer> sortedUserEdits = new TreeMap<>(Comparator.comparingInt(userEdits::get).reversed());
        sortedUserEdits.putAll(userEdits);
        System.out.println("User Edits:");
        for (Map.Entry<String, Integer> entry : sortedUserEdits.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.println("Key Counts:");
        for (Map.Entry<String, Integer> entry : keyCount.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}
