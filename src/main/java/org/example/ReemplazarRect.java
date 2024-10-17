package org.example;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReemplazarRect {

    public static void main(String[] args) {

        // Verificar que se pasó un archivo como argumento
        if (args.length != 1) {
            System.out.println("Uso: java ReemplazarRect <archivo_svg>");
            return;
        }

        // Nombre del archivo SVG de entrada
        String archivoEntrada = args[0];

        // Cargar el archivo SVG usando DOM
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            // Parsear el archivo SVG
            doc = db.parse(new File(archivoEntrada));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        // Normalizar el documento
        doc.getDocumentElement().normalize();

        // Obtener todos los elementos <rect>
        NodeList listaRectangulos = doc.getElementsByTagName("rect");

        // Recorremos cada rectángulo y lo reemplazamos por líneas
        for (int i = 0; i < listaRectangulos.getLength(); i++) {
            Element rect = (Element) listaRectangulos.item(i);

            // Obtener las coordenadas y dimensiones del rectángulo
            String x = rect.getAttribute("x");
            String y = rect.getAttribute("y");
            String ancho = rect.getAttribute("width");
            String alto = rect.getAttribute("height");
            String color = rect.getAttribute("fill");

            // Convertir las coordenadas y dimensiones a números
            int x1 = Integer.parseInt(x);
            int y1 = Integer.parseInt(y);
            int x2 = x1 + Integer.parseInt(ancho);
            int y2 = y1 + Integer.parseInt(alto);

            // Crear cuatro líneas para reemplazar el rectángulo
            crearLinea(doc, x1, y1, x2, y1, color);  // Línea superior
            crearLinea(doc, x2, y1, x2, y2, color);  // Línea derecha
            crearLinea(doc, x2, y2, x1, y2, color);  // Línea inferior
            crearLinea(doc, x1, y2, x1, y1, color);  // Línea izquierda

            // Eliminar el rectángulo del documento
            rect.getParentNode().removeChild(rect);
        }

        // Guardar el documento modificado
        guardarDocumento(doc, "svg_modificado.svg");

        System.out.println("Archivo SVG modificado guardado como 'svg_modificado.svg'");
    }

    // Método para crear un elemento <line> en el documento SVG
    public static void crearLinea(Document doc, int x1, int y1, int x2, int y2, String color) {
        Element linea = doc.createElement("line");
        linea.setAttribute("x1", String.valueOf(x1));
        linea.setAttribute("y1", String.valueOf(y1));
        linea.setAttribute("x2", String.valueOf(x2));
        linea.setAttribute("y2", String.valueOf(y2));
        linea.setAttribute("stroke", color);
        linea.setAttribute("stroke-width", "1");
        doc.getDocumentElement().appendChild(linea);  // Agregar la línea al SVG
    }

    // Método para guardar el documento modificado en un archivo
    public static void guardarDocumento(Document doc, String nombreArchivo) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            FileWriter writer = new FileWriter(new File(nombreArchivo));
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);

        } catch (TransformerException | IOException e) {
            e.printStackTrace();
        }
    }
}
