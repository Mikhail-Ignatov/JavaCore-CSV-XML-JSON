package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<Employee> parseCSV(String[] columnMapping, String fileName){
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String listToJson(List<Employee> list){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }
    public static void writeString(String json, String jsonFileName){
        try (FileWriter file = new
                FileWriter(jsonFileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileNameXML) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employeeList = new ArrayList<>();
        List<String> employeeElements = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileNameXML));

        Node root = doc.getDocumentElement();
        System.out.println("Корневой элемент: " + root.getNodeName());
        NodeList nodeList = root.getChildNodes();
        System.out.println(nodeList);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            System.out.println("Teкyщий элeмeнт: " + node.getNodeName());
            if (node.getNodeName().equals("employee")){
                NodeList nodeEmployeeList = node.getChildNodes();
                for (int j = 0; j < nodeEmployeeList.getLength(); j++){
                    Node employeeNode = nodeEmployeeList.item(j);
                    if (Node.ELEMENT_NODE == employeeNode.getNodeType()) {
                        employeeElements.add(employeeNode.getTextContent());
                        System.out.println(employeeElements);
                    }
                }
                employeeList.add(new Employee(
                        Long.parseLong(employeeElements.get(0)),
                        employeeElements.get(1),
                        employeeElements.get(2),
                        employeeElements.get(3),
                        Integer.parseInt(employeeElements.get(4))));
                employeeElements.clear();
            }
        }
        System.out.println(employeeElements);
        return employeeList;
    }



    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        // CSV-JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);

        String jsonFileName = "data.json";
        writeString(json, jsonFileName);

        // XML-JSON
        String fileNameXML = "data.xml";
        List<Employee> listXML = parseXML(fileNameXML);
        String jsonXML = listToJson(listXML);

        String jsonFileNameXML = "dataXML.json";
        writeString(jsonXML, jsonFileNameXML);

    }

}