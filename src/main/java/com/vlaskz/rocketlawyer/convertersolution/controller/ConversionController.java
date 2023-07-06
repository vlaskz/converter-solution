package com.vlaskz.rocketlawyer.convertersolution.controller;

import cz.zcu.kiv.md2odt.MD2odt;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Scanner;

@Controller
@RequestMapping("/")
public class ConversionController {

    @GetMapping("/file")
    public String getFileUploadPage() {
        return "fileInputUploadPage";
    }

    @PostMapping("/convert_md_odf")
    public ResponseEntity<byte[]> convertMdOdf(@RequestBody String requestBody) throws IOException {
        System.out.println("Received data: " + requestBody);

        byte[] fileBytes = convertMdtoOdf(requestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "converted_file.odt");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(fileBytes);
    }

    @PostMapping("/convert_tsv_csv_odf")
    public ResponseEntity<byte[]> convertTsvCsvOdf(@RequestBody String requestBody) throws Exception {
        System.out.println("Received data: " + requestBody);

        byte[] fileBytes = convertTsvCsvToOdf(requestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "converted_file.odt");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(fileBytes);
    }

    public byte[] convertTsvCsvToOdf(String inputData) throws Exception {

        Scanner scanner = new Scanner(new StringReader(inputData));

        // Create a new Writer document
        TextDocument textDoc = TextDocument.newTextDocument();

        // Create a table in the Writer document
        Table table = textDoc.addTable(1, 1);

        // Loop through each line in the input data
        int rowIndex = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] cells = line.split("\t");  // Use "," for CSV

            // Ensure the table has enough rows
            while (table.getRowCount() <= rowIndex) {
                table.appendRow();
            }
            Row row = table.getRowByIndex(rowIndex);

            // Ensure the table has enough columns
            if (table.getColumnCount() < cells.length) {
                table.appendColumns(cells.length - table.getColumnCount());
            }

            // Loop through each cell in the line
            for (int i = 0; i < cells.length; i++) {
                // Add the cell data to the table
                Cell cell = row.getCellByIndex(i);
                cell.setStringValue(cells[i]);
            }

            rowIndex++;
        }

        // Save the Writer document to a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        textDoc.save(outputStream);

        // Convert ByteArrayOutputStream to byte[] and return
        return outputStream.toByteArray();
    }

    private byte[] convertMdtoOdf(String data) throws IOException {
        ByteArrayOutputStream convertedData = new ByteArrayOutputStream();
        MD2odt.converter()
                .setInput(data)
                .setTemplate(Paths.get("src/main/resources/static/template.odt"))
                .setOutput(convertedData)
                .enableAllExtensions()
                .convert();

        return convertedData.toByteArray();
    }

}
