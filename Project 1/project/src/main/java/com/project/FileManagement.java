package com.project;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

class FileManagement {

    // Variables
    String fileName;
    FileWriter writer;

    // Constructor
    public FileManagement(String fileName) {
        this.fileName = fileName;
    }

    // Functions
    // This function is to write in a file
    public void Write(String Data) {
        // txt
        try {
            writer = new FileWriter(fileName);
            writer.write(Data);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This function is to read in a file
    public void read() {
        // Txt
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String Linea;
            while ((Linea = br.readLine()) != null) {
                System.out.println(Linea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}