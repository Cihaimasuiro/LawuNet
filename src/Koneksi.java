

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author LENOVO
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:8081/lawunet_db"; 
        String user = "root";
        String password = "";

        // Baris ini memastikan driver termuat dengan benar.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver MySQL tidak ditemukan!", "Driver Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Mengembalikan objek koneksi yang baru dibuat setiap kali dipanggil
        return DriverManager.getConnection(url, user, password);
    }
}