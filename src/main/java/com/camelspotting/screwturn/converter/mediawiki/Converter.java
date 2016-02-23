package com.camelspotting.screwturn.converter.mediawiki;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Converter {
    private static final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

    private Converter() {
    }

    public static void main(String[] args) throws Exception {
        Path folder = Paths.get(args[0]);

        Path outputFolder = folder.resolveSibling("output");
        Files.createDirectories(outputFolder);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "The-Wall.xml")) {
            stream.forEach(f -> convertFile(outputFolder, f));
        }
    }

    private static void convertFile(Path outputFolder, Path f)  {
        String name = f.getFileName().toString();
        System.out.println("Processing " + name);
        Path outputFile = outputFolder.resolve(name);

        try (OutputStream os = Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             OutputStreamWriter pw = new OutputStreamWriter(os, StandardCharsets.UTF_8)){

            Files.createDirectories(outputFolder);


            Document doc = new SAXBuilder().build(f.toFile());
            List<Element> pages = doc.getRootElement().getChildren("page");
            pages.forEach(Converter::convertPage);

            pw.write(outputter.outputString(doc));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void convertPage(Element page) {
        List<Element> revisions = page.getChildren("revision");
        revisions.forEach(Converter::convertRevision);
    }

    private static void convertRevision(Element element) {
        Element text = element.getChild("text");

        new InlineCodeBlockFilter().filter(text);
        new CodeBlockFilter().filter(text);

    }
}
