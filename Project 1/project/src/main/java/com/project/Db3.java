package com.project;

import java.io.IOException;

public class Db3 {

    public static void main(String[] args) {
        DataBase2 database = new DataBase2(50063);
        try {
            database.server.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}