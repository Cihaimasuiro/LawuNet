/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 *
 * @author LENOVO
 */
public class Main extends javax.swing.JFrame {

    private DefaultTableModel modelPelanggan;
    private DefaultTableModel modelSales;
    private DefaultTableModel modelJaringan;
    private DefaultTableModel modelPaket;
    private int selectedIdForEdit = -1;

    /**
     * Creates new form main
     */
    public Main() {
        initComponents();
        this.setLocationRelativeTo(null);

        // Inisialisasi semua model tabel
        initTableModels();
        
        // Memuat semua data saat aplikasi dimulai
        loadAllData();
    }
    
    // Inisialisasi semua model tabel dengan kolomnya
    private void initTableModels() {
        // Tabel Pelanggan
        modelPelanggan = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(modelPelanggan);
        modelPelanggan.addColumn("ID");
        modelPelanggan.addColumn("Nama");
        modelPelanggan.addColumn("Alamat");
        modelPelanggan.addColumn("Tanggal Pemasangan");
        modelPelanggan.addColumn("Sales");
        modelPelanggan.addColumn("Jaringan");
        modelPelanggan.addColumn("Paket");
        modelPelanggan.addColumn("Status Bulan Ini");

        // Tabel Sales
        modelSales = new DefaultTableModel() {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable2.setModel(modelSales);
        modelSales.addColumn("ID");
        modelSales.addColumn("Nama Sales");

        // Tabel Jaringan
        modelJaringan = new DefaultTableModel(){
             @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable3.setModel(modelJaringan);
        modelJaringan.addColumn("ID");
        modelJaringan.addColumn("Nama Jaringan");
        
        // Tabel Paket
        modelPaket = new DefaultTableModel(){
             @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable4.setModel(modelPaket);
        modelPaket.addColumn("ID");
        modelPaket.addColumn("Nama Paket");
    }
    
    private void loadAllData() {
        loadDataPelanggan(""); 
        loadDataSales();
        loadDataJaringan();
        loadDataPaket();
        
        loadComboBoxSales();
        loadComboBoxJaringan();
        loadComboBoxPaket();
    }
    
    // Method baru untuk menampilkan dialog pembayaran
    private void showPaymentDialog(int idPelanggan, String namaPelanggan, double hargaPaket, double tunggakan) {
        String bulanIniStr = new SimpleDateFormat("MMMM yyyy").format(new Date());

        // Opsi untuk ComboBox
        java.util.Vector<String> options = new java.util.Vector<>();
        options.add("Bayar Bulan Ini (" + bulanIniStr + ")");
        if (tunggakan > 0) {
            options.add("Bayar Tunggakan (" + String.format("Rp %,.0f", tunggakan) + ")");
        }

        JComboBox<String> bulanComboBox = new JComboBox<>(options);
        JTextField jumlahBayarField = new JTextField(String.format("%.0f", hargaPaket));

        Object[] message = {
            "Pelanggan: " + namaPelanggan,
            "Tagihan Bulan Ini: Rp " + String.format("%,.0f", hargaPaket),
            (tunggakan > 0 ? "Tunggakan: Rp " + String.format("%,.0f", tunggakan) : "Tidak ada tunggakan."),
            "Pilih Pembayaran:",
            bulanComboBox,
            "Jumlah Bayar:",
            jumlahBayarField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Proses Pembayaran", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                double jumlahBayar = Double.parseDouble(jumlahBayarField.getText());
                boolean bayarTunggakan = bulanComboBox.getSelectedIndex() == 1 && tunggakan > 0;

                prosesPembayaran(idPelanggan, jumlahBayar, hargaPaket, bayarTunggakan);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah bayar tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateCustomerReportPDF(String filePath) throws DocumentException, IOException {
        // 1. Definisikan Font
        Font fontJudul = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font fontHeader = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Font fontIsi = new Font(Font.HELVETICA, 11, Font.NORMAL);

        // 2. Buat Dokumen
        Document document = new Document(PageSize.A4.rotate()); 
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // 3. Buat Judul Laporan
        Paragraph judul = new Paragraph("Laporan Data Pelanggan LAWU.NET", fontJudul);
        judul.setAlignment(Element.ALIGN_CENTER);
        judul.setSpacingAfter(20f);
        document.add(judul);

        // Tambahkan tanggal pembuatan
        Paragraph tanggal = new Paragraph("Dicetak pada: " + new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss").format(new Date()), fontIsi);
        tanggal.setSpacingAfter(10f);
        document.add(tanggal);

        // 4. Buat Tabel PDF
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
        pdfTable.setWidthPercentage(100);

        // 5. Buat Header Tabel
        for (int i = 0; i < model.getColumnCount(); i++) {
            PdfPCell headerCell = new PdfPCell(new Phrase(model.getColumnName(i), fontHeader));
            headerCell.setBackgroundColor(new Color(37, 35, 60));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(5);
            pdfTable.addCell(headerCell);
        }

        // 6. Isi Data Tabel dari JTable
        for (int baris = 0; baris < model.getRowCount(); baris++) {
            for (int kolom = 0; kolom < model.getColumnCount(); kolom++) {
                // Ambil nilai dan pastikan tidak null
                Object nilaiObj = model.getValueAt(baris, kolom);
                String nilaiStr = (nilaiObj == null) ? "" : nilaiObj.toString();

                PdfPCell dataCell = new PdfPCell(new Phrase(nilaiStr, fontIsi));
                dataCell.setPadding(5);
                pdfTable.addCell(dataCell);
            }
        }

        // 7. Tambahkan Tabel ke Dokumen
        document.add(pdfTable);

        // 8. Tutup Dokumen
        document.close();
    }
    
    // Method baru untuk memproses data ke DB
    private void prosesPembayaran(int idPelanggan, double jumlahBayar, double hargaPaket, boolean bayarTunggakan) {
        Date tanggalSekarang = new Date();
        String bulanTagihan;

        if (bayarTunggakan) {
            // Ambil tanggal 1 bulan lalu
            bulanTagihan = new SimpleDateFormat("yyyy-MM-01").format(new Date(System.currentTimeMillis() - 2592000000L));
        } else {
            // Ambil tanggal 1 bulan ini
            bulanTagihan = new SimpleDateFormat("yyyy-MM-01").format(tanggalSekarang);
        }

        String sqlCheck = "SELECT id, jumlah_bayar FROM pembayaran WHERE id_pelanggan = ? AND bulan_tagihan = ?";
        String sqlInsert = "INSERT INTO pembayaran (id_pelanggan, bulan_tagihan, jumlah_tagihan, jumlah_bayar, tanggal_bayar, status) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE pembayaran SET jumlah_bayar = ?, tanggal_bayar = ?, status = ? WHERE id = ?";

        try (Connection conn = Koneksi.getConnection()) {
            int idBayar = -1;
            double bayarSebelumnya = 0;

            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setInt(1, idPelanggan);
                pstmtCheck.setString(2, bulanTagihan);
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        idBayar = rs.getInt("id");
                        bayarSebelumnya = rs.getDouble("jumlah_bayar");
                    }
                }
            }

            double totalBayarBaru = bayarSebelumnya + jumlahBayar;
            String status = (totalBayarBaru >= hargaPaket) ? "Lunas" : "Belum Lunas";

            if (idBayar != -1) { // Data sudah ada, UPDATE
                try(PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setDouble(1, totalBayarBaru);
                    pstmtUpdate.setDate(2, new java.sql.Date(tanggalSekarang.getTime()));
                    pstmtUpdate.setString(3, status);
                    pstmtUpdate.setInt(4, idBayar);
                    pstmtUpdate.executeUpdate();
                }
            } else { // Data belum ada, INSERT
                try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                    pstmtInsert.setInt(1, idPelanggan);
                    pstmtInsert.setString(2, bulanTagihan);
                    pstmtInsert.setDouble(3, hargaPaket);
                    pstmtInsert.setDouble(4, jumlahBayar);
                    pstmtInsert.setDate(5, new java.sql.Date(tanggalSekarang.getTime()));
                    pstmtInsert.setString(6, status);
                    pstmtInsert.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Pembayaran berhasil diproses!");
            loadDataPelanggan("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memproses pembayaran: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // --- METODE UNTUK MEMUAT DATA KE TABEL ---
    
    private void loadDataPelanggan(String keyword) {
        modelPelanggan.setRowCount(0);
        // Dapatkan tanggal 1 bulan ini untuk filter status pembayaran
        String bulanIni = new SimpleDateFormat("yyyy-MM-01").format(new Date());

        String orderByClause;
        String urutan = (String) cburut.getSelectedItem();
        orderByClause = switch (urutan) {
            case "Nama - Naik" -> " ORDER BY p.nama ASC";
            case "Nama - Menurun" -> " ORDER BY p.nama DESC";
            case "Tanggal - Naik" -> " ORDER BY p.tanggal_pemasangan ASC";
            case "Tanggal - Menurun" -> " ORDER BY p.tanggal_pemasangan DESC";
            default -> " ORDER BY p.id ASC";
        };

        // INI ADALAH QUERY
        String sql = "SELECT p.id, p.nama, p.alamat, p.tanggal_pemasangan, s.nama_sales, j.nama_jaringan, pk.nama_paket, "
                + "CASE "
                + "  WHEN bayar.status = 'Lunas' THEN 'Lunas' "
                + "  ELSE 'Belum Lunas' "
                + "END AS status_bulan_ini "
                + "FROM pelanggan p "
                + "LEFT JOIN sales s ON p.id_sales = s.id "
                + "LEFT JOIN jaringan j ON p.id_jaringan = j.id "
                + "LEFT JOIN paket pk ON p.id_paket = pk.id "
                + "LEFT JOIN pembayaran bayar ON p.id = bayar.id_pelanggan AND bayar.bulan_tagihan = ? " // <-- JOIN ke tabel pembayaran
                + "WHERE p.nama LIKE ? OR p.alamat LIKE ?" + orderByClause;

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameter untuk query
            pstmt.setString(1, bulanIni);
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    modelPelanggan.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getDate("tanggal_pemasangan"),
                        rs.getString("nama_sales"),
                        rs.getString("nama_jaringan"),
                        rs.getString("nama_paket"),
                        rs.getString("status_bulan_ini")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pelanggan: " + e.getMessage());
            e.printStackTrace(); // Tampilkan error di console untuk debug
        }
    }

    // Method untuk memuat data ke tabel Sales, Jaringan, Paket
    private void loadSimpleTableData(DefaultTableModel tableModel, String tableName, String nameColumn) {
        tableModel.setRowCount(0);
        String sql = "SELECT id, " + nameColumn + " FROM " + tableName + " ORDER BY " + nameColumn + " ASC";
        try (Connection conn = Koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString(nameColumn)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data " + tableName + ": " + e.getMessage());
        }
    }

    private void loadDataSales() {
        loadSimpleTableData(modelSales, "sales", "nama_sales");
    }

    private void loadDataJaringan() {
        loadSimpleTableData(modelJaringan, "jaringan", "nama_jaringan");
    }

    private void loadDataPaket() {
        loadSimpleTableData(modelPaket, "paket", "nama_paket");
    }
    
    // --- METODE UNTUK MEMUAT DATA KE COMBO BOX ---
    
    // Method generik untuk memuat data ke combo box
    private void loadComboBoxData(JComboBox<String> comboBox, String tableName, String columnName) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        String sql = "SELECT " + columnName + " FROM " + tableName;
        try (Connection conn = Koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addElement(rs.getString(columnName));
            }
            comboBox.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data " + tableName + " ke combo box: " + e.getMessage());
        }
    }

    private void loadComboBoxSales() {
        loadComboBoxData(cbsales, "sales", "nama_sales");
    }

    private void loadComboBoxJaringan() {
        loadComboBoxData(cbjaringan, "jaringan", "nama_jaringan");
    }

    private void loadComboBoxPaket() {
        loadComboBoxData(cbpaket, "paket", "nama_paket");
    }

    // --- METODE BANTU ---

    private int getIdByName(String tableName, String columnName, String name) {
        int id = -1;
        String sql = "SELECT id FROM " + tableName + " WHERE " + columnName + " = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mendapatkan ID untuk '" + name + "': " + e.getMessage());
        }
        return id;
    }
    
    private void clearFormPelanggan() {
        txtnama.setText("");
        txtalamat.setText("");
        ccbtanggal.setDate(new Date()); 
        if (cbsales.getItemCount() > 0) cbsales.setSelectedIndex(0);
        if (cbjaringan.getItemCount() > 0) cbjaringan.setSelectedIndex(0);
        if (cbpaket.getItemCount() > 0) cbpaket.setSelectedIndex(0);
        selectedIdForEdit = -1;
        bbuatpelanggan.setText("Buat");
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelatas = new javax.swing.JPanel();
        PanelHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        bkeluar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        PanelBaru = new javax.swing.JPanel();
        PanelMasukkanPelanggan = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtnama = new javax.swing.JTextField();
        txtalamat = new javax.swing.JTextField();
        ccbtanggal = new de.wannawork.jcalendar.JCalendarComboBox();
        cbsales = new javax.swing.JComboBox<>();
        cbjaringan = new javax.swing.JComboBox<>();
        cbpaket = new javax.swing.JComboBox<>();
        bbuatpelanggan = new javax.swing.JButton();
        bbersih = new javax.swing.JButton();
        PanelPelanggan = new javax.swing.JPanel();
        txtcari = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        bbuatredirect = new javax.swing.JButton();
        bhapustabel = new javax.swing.JButton();
        bprint = new javax.swing.JButton();
        bubahtabel = new javax.swing.JButton();
        bbayar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        bcari = new javax.swing.JButton();
        cburut = new javax.swing.JComboBox<>();
        PanelPengaturan = new javax.swing.JPanel();
        PanelMasukkanPelanggan1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtsales = new javax.swing.JTextField();
        txtpaket = new javax.swing.JTextField();
        txtjaringan = new javax.swing.JTextField();
        bbersihpaket = new javax.swing.JButton();
        bbuatsales = new javax.swing.JButton();
        bbuatpaket = new javax.swing.JButton();
        bbersihsales = new javax.swing.JButton();
        bbersihjaringan = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        bbuatjaringan = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        bhapusadmin = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        bubahsales = new javax.swing.JButton();
        bubahjaringan = new javax.swing.JButton();
        bubahpaket = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelatas.setBackground(new java.awt.Color(234, 237, 247));
        panelatas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PanelHeader.setBackground(new java.awt.Color(37, 35, 60));

        jLabel1.setFont(new java.awt.Font("Georgia", 0, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("LAWU.NET");

        bkeluar.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        bkeluar.setText("Keluar");
        bkeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bkeluarActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Aplikasi Pencatatan Pelanggan Internet");

        javax.swing.GroupLayout PanelHeaderLayout = new javax.swing.GroupLayout(PanelHeader);
        PanelHeader.setLayout(PanelHeaderLayout);
        PanelHeaderLayout.setHorizontalGroup(
            PanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHeaderLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addGap(27, 27, 27)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 450, Short.MAX_VALUE)
                .addComponent(bkeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PanelHeaderLayout.setVerticalGroup(
            PanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bkeluar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelHeaderLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(PanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelatas.add(PanelHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 990, 50));

        PanelBaru.setBackground(new java.awt.Color(255, 255, 255));

        PanelMasukkanPelanggan.setBackground(new java.awt.Color(255, 255, 255));
        PanelMasukkanPelanggan.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Pelanggan Baru", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Georgia", 0, 18))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jLabel4.setText("Nama");

        jLabel5.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jLabel5.setText("Alamat");

        jLabel6.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jLabel6.setText("Tanggal");

        jLabel7.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jLabel7.setText("Sales");

        jLabel8.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jLabel8.setText("Jaringan");

        jLabel16.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jLabel16.setText("Paket");

        txtnama.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        txtnama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnamaActionPerformed(evt);
            }
        });

        txtalamat.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N

        cbsales.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        cbsales.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbjaringan.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        cbjaringan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbpaket.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        cbpaket.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        bbuatpelanggan.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        bbuatpelanggan.setText("Buat");
        bbuatpelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbuatpelangganActionPerformed(evt);
            }
        });

        bbersih.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        bbersih.setText("Bersih");
        bbersih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbersihActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelMasukkanPelangganLayout = new javax.swing.GroupLayout(PanelMasukkanPelanggan);
        PanelMasukkanPelanggan.setLayout(PanelMasukkanPelangganLayout);
        PanelMasukkanPelangganLayout.setHorizontalGroup(
            PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMasukkanPelangganLayout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelMasukkanPelangganLayout.createSequentialGroup()
                        .addComponent(bbuatpelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bbersih, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMasukkanPelangganLayout.createSequentialGroup()
                        .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ccbtanggal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbpaket, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbjaringan, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbsales, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtalamat, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtnama, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27))))
        );
        PanelMasukkanPelangganLayout.setVerticalGroup(
            PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMasukkanPelangganLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelMasukkanPelangganLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtnama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtalamat)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ccbtanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbsales)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbjaringan)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbpaket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addGroup(PanelMasukkanPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bbuatpelanggan)
                    .addComponent(bbersih))
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout PanelBaruLayout = new javax.swing.GroupLayout(PanelBaru);
        PanelBaru.setLayout(PanelBaruLayout);
        PanelBaruLayout.setHorizontalGroup(
            PanelBaruLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBaruLayout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(PanelMasukkanPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(118, Short.MAX_VALUE))
        );
        PanelBaruLayout.setVerticalGroup(
            PanelBaruLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBaruLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(PanelMasukkanPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(92, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pelanggan Baru", PanelBaru);

        PanelPelanggan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtcari.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        txtcari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcariActionPerformed(evt);
            }
        });
        PanelPelanggan.add(txtcari, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 49, 350, -1));

        jTable1.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        PanelPelanggan.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 82, 978, 467));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bbuatredirect.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bbuatredirect.setText("Buat");
        bbuatredirect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbuatredirectActionPerformed(evt);
            }
        });
        jPanel4.add(bbuatredirect, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        bhapustabel.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bhapustabel.setText("Hapus");
        bhapustabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bhapustabelActionPerformed(evt);
            }
        });
        jPanel4.add(bhapustabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, -1, -1));

        bprint.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bprint.setText("Print");
        bprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bprintActionPerformed(evt);
            }
        });
        jPanel4.add(bprint, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, -1, -1));

        bubahtabel.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bubahtabel.setText("Ubah");
        bubahtabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bubahtabelActionPerformed(evt);
            }
        });
        jPanel4.add(bubahtabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, -1));

        bbayar.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bbayar.setText("Bayar");
        bbayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbayarActionPerformed(evt);
            }
        });
        jPanel4.add(bbayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, -1, -1));

        PanelPelanggan.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 990, 43));

        jLabel2.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel2.setText("Cari Pelanggan");
        PanelPelanggan.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 50, -1, -1));

        jLabel9.setText("Urut Menurut");
        PanelPelanggan.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 50, 70, 20));

        bcari.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bcari.setText("Cari");
        bcari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bcariActionPerformed(evt);
            }
        });
        PanelPelanggan.add(bcari, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 50, -1, -1));

        cburut.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama - Naik", "Nama - Menurun", "Tanggal - Naik", "Tanggal - Menurun" }));
        cburut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cburutActionPerformed(evt);
            }
        });
        PanelPelanggan.add(cburut, new org.netbeans.lib.awtextra.AbsoluteConstraints(832, 50, 150, -1));

        jTabbedPane1.addTab("Detail pelanggan", PanelPelanggan);

        PanelMasukkanPelanggan1.setBackground(new java.awt.Color(255, 255, 255));
        PanelMasukkanPelanggan1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Pengaturan", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Georgia", 0, 18))); // NOI18N

        jLabel10.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel10.setText("Sales");

        jLabel11.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel11.setText("Paket");

        txtsales.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        txtsales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtsalesActionPerformed(evt);
            }
        });

        txtpaket.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N

        txtjaringan.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        txtjaringan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtjaringanActionPerformed(evt);
            }
        });

        bbersihpaket.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bbersihpaket.setText("Bersih");
        bbersihpaket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbersihpaketActionPerformed(evt);
            }
        });

        bbuatsales.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bbuatsales.setText("Buat");
        bbuatsales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbuatsalesActionPerformed(evt);
            }
        });

        bbuatpaket.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bbuatpaket.setText("Buat");
        bbuatpaket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbuatpaketActionPerformed(evt);
            }
        });

        bbersihsales.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bbersihsales.setText("Bersih");
        bbersihsales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbersihsalesActionPerformed(evt);
            }
        });

        bbersihjaringan.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bbersihjaringan.setText("Bersih");
        bbersihjaringan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbersihjaringanActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel12.setText("Jaringan");

        bbuatjaringan.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bbuatjaringan.setText("Buat");
        bbuatjaringan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bbuatjaringanActionPerformed(evt);
            }
        });

        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        jTable4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable4MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable4);

        bhapusadmin.setFont(new java.awt.Font("Georgia", 0, 24)); // NOI18N
        bhapusadmin.setText("Hapus");
        bhapusadmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bhapusadminActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel13.setText("Sales");

        jLabel14.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel14.setText("Jaringan");

        jLabel15.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel15.setText("Paket");

        bubahsales.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bubahsales.setText("Ubah");
        bubahsales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bubahsalesActionPerformed(evt);
            }
        });

        bubahjaringan.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bubahjaringan.setText("Ubah");
        bubahjaringan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bubahjaringanActionPerformed(evt);
            }
        });

        bubahpaket.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        bubahpaket.setText("Ubah");
        bubahpaket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bubahpaketActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelMasukkanPelanggan1Layout = new javax.swing.GroupLayout(PanelMasukkanPelanggan1);
        PanelMasukkanPelanggan1.setLayout(PanelMasukkanPelanggan1Layout);
        PanelMasukkanPelanggan1Layout.setHorizontalGroup(
            PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bhapusadmin)
                            .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addComponent(bbersihsales)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bubahsales))
                            .addComponent(jLabel12)
                            .addComponent(txtjaringan, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                                .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                                        .addComponent(bbuatjaringan)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bbersihjaringan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(txtpaket, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelMasukkanPelanggan1Layout.createSequentialGroup()
                                        .addComponent(bbuatpaket)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bbersihpaket)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bubahpaket)
                                    .addComponent(bubahjaringan))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMasukkanPelanggan1Layout.createSequentialGroup()
                        .addContainerGap(21, Short.MAX_VALUE)
                        .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(bbuatsales))
                            .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addGap(10, 10, 10))
                                .addComponent(txtsales, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)))
                .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        PanelMasukkanPelanggan1Layout.setVerticalGroup(
            PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelMasukkanPelanggan1Layout.createSequentialGroup()
                        .addComponent(txtsales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bbuatsales)
                            .addComponent(bbersihsales)
                            .addComponent(bubahsales))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtjaringan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bbuatjaringan)
                            .addComponent(bbersihjaringan)
                            .addComponent(bubahjaringan))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtpaket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelMasukkanPelanggan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bbuatpaket)
                            .addComponent(bbersihpaket)
                            .addComponent(bubahpaket))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bhapusadmin))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PanelPengaturanLayout = new javax.swing.GroupLayout(PanelPengaturan);
        PanelPengaturan.setLayout(PanelPengaturanLayout);
        PanelPengaturanLayout.setHorizontalGroup(
            PanelPengaturanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelPengaturanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelMasukkanPelanggan1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        PanelPengaturanLayout.setVerticalGroup(
            PanelPengaturanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPengaturanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelMasukkanPelanggan1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Pengaturan", PanelPengaturan);

        panelatas.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 990, 590));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelatas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelatas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bkeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bkeluarActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin keluar dari aplikasi?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_bkeluarActionPerformed

    private void bubahpaketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bubahpaketActionPerformed
        int selectedRow = jTable4.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data paket yang akan diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedIdForEdit = (int) jTable4.getValueAt(selectedRow, 0);
        String nama = (String) jTable4.getValueAt(selectedRow, 1);
        txtpaket.setText(nama);
        bbuatpaket.setText("Ubah");
    }//GEN-LAST:event_bubahpaketActionPerformed

    private void bubahjaringanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bubahjaringanActionPerformed
        int selectedRow = jTable3.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data jaringan yang akan diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedIdForEdit = (int) jTable3.getValueAt(selectedRow, 0);
        String nama = (String) jTable3.getValueAt(selectedRow, 1);
        txtjaringan.setText(nama);
        bbuatjaringan.setText("Ubah");
    }//GEN-LAST:event_bubahjaringanActionPerformed

    private void bubahsalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bubahsalesActionPerformed
        int selectedRow = jTable2.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data sales yang akan diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedIdForEdit = (int) jTable2.getValueAt(selectedRow, 0);
        String nama = (String) jTable2.getValueAt(selectedRow, 1);
        txtsales.setText(nama);
        bbuatsales.setText("Ubah");
    }//GEN-LAST:event_bubahsalesActionPerformed

    private void bhapusadminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bhapusadminActionPerformed
        JTable targetTable = null;
        String tableName = "";

        if(jTable2.getSelectedRow() != -1) {
            targetTable = jTable2;
            tableName = "sales";
        } else if (jTable3.getSelectedRow() != -1) {
            targetTable = jTable3;
            tableName = "jaringan";
        } else if (jTable4.getSelectedRow() != -1) {
            targetTable = jTable4;
            tableName = "paket";
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data dari salah satu tabel (Sales/Jaringan/Paket) untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = targetTable.getSelectedRow();
        int idToDelete = (int) targetTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Koneksi.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {
                pstmt.setInt(1, idToDelete);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadAllData(); // Reload semua data
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage() + "\nPastikan tidak ada pelanggan yang terkait dengan data ini.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_bhapusadminActionPerformed

    private void jTable4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable4MouseClicked
        if(evt.getClickCount() == 2) {
            bubahpaketActionPerformed(null);
        }
    }//GEN-LAST:event_jTable4MouseClicked

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        if(evt.getClickCount() == 2) {
            bubahjaringanActionPerformed(null);
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        if(evt.getClickCount() == 2) {
            bubahsalesActionPerformed(null);
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void txtjaringanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtjaringanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtjaringanActionPerformed

    private void bbuatjaringanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbuatjaringanActionPerformed
        String namaJaringan = txtjaringan.getText().trim();
        if (namaJaringan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Jaringan tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query;
        if(bbuatjaringan.getText().equals("Buat")) {
            query = "INSERT INTO jaringan (nama_jaringan) VALUES (?)";
        } else {
            query = "UPDATE jaringan SET nama_jaringan = ? WHERE id = ?";
        }

        try (Connection conn = Koneksi.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaJaringan);
            if(!bbuatjaringan.getText().equals("Buat")) {
                pstmt.setInt(2, selectedIdForEdit);
            }
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data Jaringan berhasil disimpan.");
            loadDataJaringan();
            loadComboBoxJaringan();
            bbersihjaringanActionPerformed(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data jaringan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bbuatjaringanActionPerformed

    private void bbersihjaringanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbersihjaringanActionPerformed
        txtjaringan.setText("");
        bbuatjaringan.setText("Buat");
        selectedIdForEdit = -1;
    }//GEN-LAST:event_bbersihjaringanActionPerformed

    private void bbersihsalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbersihsalesActionPerformed
        txtsales.setText("");
        bbuatsales.setText("Buat");
        selectedIdForEdit = -1;
    }//GEN-LAST:event_bbersihsalesActionPerformed

    private void bbuatpaketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbuatpaketActionPerformed
        String namaPaket = txtpaket.getText().trim();
        if (namaPaket.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Paket tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query;
        if(bbuatpaket.getText().equals("Buat")) {
            query = "INSERT INTO paket (nama_paket, harga) VALUES (?, 0)";
        } else {
            query = "UPDATE paket SET nama_paket = ? WHERE id = ?";
        }

        try (Connection conn = Koneksi.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaPaket);
            if(!bbuatpaket.getText().equals("Buat")) {
                pstmt.setInt(2, selectedIdForEdit);
            }
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data Paket berhasil disimpan.");
            loadDataPaket();
            loadComboBoxPaket();
            bbersihpaketActionPerformed(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data paket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bbuatpaketActionPerformed

    private void bbuatsalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbuatsalesActionPerformed
        String namaSales = txtsales.getText().trim();
        if (namaSales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Sales tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query;
        if(bbuatsales.getText().equals("Buat")) {
            query = "INSERT INTO sales (nama_sales) VALUES (?)";
        } else {
            query = "UPDATE sales SET nama_sales = ? WHERE id = ?";
        }

        try (Connection conn = Koneksi.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaSales);
            if(!bbuatsales.getText().equals("Buat")) {
                pstmt.setInt(2, selectedIdForEdit);
            }
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data Sales berhasil disimpan.");
            loadDataSales();
            loadComboBoxSales();
            bbersihsalesActionPerformed(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data sales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bbuatsalesActionPerformed

    private void bbersihpaketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbersihpaketActionPerformed
        txtpaket.setText("");
        bbuatpaket.setText("Buat");
        selectedIdForEdit = -1;
    }//GEN-LAST:event_bbersihpaketActionPerformed

    private void txtsalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsalesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsalesActionPerformed

    private void cburutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cburutActionPerformed
        String keyword = txtcari.getText();
        loadDataPelanggan(keyword);
    }//GEN-LAST:event_cburutActionPerformed

    private void bcariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bcariActionPerformed
        String keyword = txtcari.getText();
        loadDataPelanggan(keyword);
    }//GEN-LAST:event_bcariActionPerformed

    private void bubahtabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bubahtabelActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris pelanggan yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedIdForEdit = (int) jTable1.getValueAt(selectedRow, 0);

        // Ambil data dari tabel untuk diisi ke form
        String nama = (String) jTable1.getValueAt(selectedRow, 1);
        String alamat = (String) jTable1.getValueAt(selectedRow, 2);
        Date tanggal = (Date) jTable1.getValueAt(selectedRow, 3);
        String sales = (String) jTable1.getValueAt(selectedRow, 4);
        String jaringan = (String) jTable1.getValueAt(selectedRow, 5);
        String paket = (String) jTable1.getValueAt(selectedRow, 6);

        // Isi form di tab "Baru"
        txtnama.setText(nama);
        txtalamat.setText(alamat);
        ccbtanggal.setDate(tanggal);
        cbsales.setSelectedItem(sales);
        cbjaringan.setSelectedItem(jaringan);
        cbpaket.setSelectedItem(paket);

        // Ubah teks tombol dan pindah tab
        bbuatpelanggan.setText("Ubah");
        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_bubahtabelActionPerformed

    private void bprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bprintActionPerformed
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tabel kosong, tidak ada data untuk dicetak.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Membuat nama file yang unik dengan timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = "Laporan_Pelanggan_" + timeStamp + ".pdf";

        try {
            // Panggil method pembuat PDF
            generateCustomerReportPDF(filePath);

            JOptionPane.showMessageDialog(this, "Laporan PDF berhasil dibuat:\n" + filePath, "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Coba buka file PDF yang baru dibuat secara otomatis
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(filePath));
            }

        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_bprintActionPerformed

    private void bhapustabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bhapustabelActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris pelanggan yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPelanggan = (int) jTable1.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus pelanggan ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM pelanggan WHERE id = ?";
            // Gunakan try-with-resources
            try (Connection conn = Koneksi.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idPelanggan);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Pelanggan berhasil dihapus.");
                loadDataPelanggan(""); // Refresh tabel

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pelanggan: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_bhapustabelActionPerformed

    private void bbuatredirectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbuatredirectActionPerformed
        jTabbedPane1.setSelectedIndex(0);
        clearFormPelanggan();
    }//GEN-LAST:event_bbuatredirectActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {
            bubahtabelActionPerformed(null);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void txtcariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcariActionPerformed
        String keyword = txtcari.getText();
        loadDataPelanggan(keyword);
    }//GEN-LAST:event_txtcariActionPerformed

    private void bbuatpelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbuatpelangganActionPerformed
        String nama = txtnama.getText();
        String alamat = txtalamat.getText();
        Date tanggal = ccbtanggal.getDate();
        String salesName = (String) cbsales.getSelectedItem();
        String jaringanName = (String) cbjaringan.getSelectedItem();
        String paketName = (String) cbpaket.getSelectedItem();

        if (nama.isEmpty() || alamat.isEmpty() || tanggal == null) {
            JOptionPane.showMessageDialog(this, "Nama, Alamat, dan Tanggal harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tanggalSql = sdf.format(tanggal);

        int idSales = getIdByName("sales", "nama_sales", salesName);
        int idJaringan = getIdByName("jaringan", "nama_jaringan", jaringanName);
        int idPaket = getIdByName("paket", "nama_paket", paketName);

        if(idSales == -1 || idJaringan == -1 || idPaket == -1) {
            JOptionPane.showMessageDialog(this, "Data Sales/Jaringan/Paket tidak valid. Pastikan ada data di tab Sales & Paket.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = Koneksi.getConnection();
            String query;
            PreparedStatement pstmt;

            if (selectedIdForEdit == -1) { // Mode Buat
                query = "INSERT INTO pelanggan (nama, alamat, tanggal_pemasangan, id_sales, id_jaringan, id_paket) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(query);
            } else { // Mode Ubah
                query = "UPDATE pelanggan SET nama = ?, alamat = ?, tanggal_pemasangan = ?, id_sales = ?, id_jaringan = ?, id_paket = ? WHERE id = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(7, selectedIdForEdit);
            }

            pstmt.setString(1, nama);
            pstmt.setString(2, alamat);
            pstmt.setString(3, tanggalSql);
            pstmt.setInt(4, idSales);
            pstmt.setInt(5, idJaringan);
            pstmt.setInt(6, idPaket);

            pstmt.executeUpdate();

            if (selectedIdForEdit == -1) {
                JOptionPane.showMessageDialog(this, "Pelanggan baru berhasil ditambahkan!");
            } else {
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diubah!");
            }

            loadDataPelanggan("");
            clearFormPelanggan();
            jTabbedPane1.setSelectedIndex(1);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bbuatpelangganActionPerformed

    private void bbersihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbersihActionPerformed
        clearFormPelanggan();
    }//GEN-LAST:event_bbersihActionPerformed

    private void txtnamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnamaActionPerformed

    private void bbayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbayarActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPelanggan = (int) jTable1.getValueAt(selectedRow, 0);
        String namaPelanggan = (String) jTable1.getValueAt(selectedRow, 1);

        // Dapatkan detail tagihan dan tunggakan dari database
        try (Connection conn = Koneksi.getConnection()) {
            // 1. Dapatkan harga paket pelanggan
            String sqlHarga = "SELECT pk.harga FROM pelanggan p JOIN paket pk ON p.id_paket = pk.id WHERE p.id = ?";
            double hargaPaket = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlHarga)) {
                pstmt.setInt(1, idPelanggan);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        hargaPaket = rs.getDouble("harga");
                    }
                }
            }

            // 2. Cek tunggakan bulan lalu
            String bulanLalu = new SimpleDateFormat("yyyy-MM-01").format(new Date(System.currentTimeMillis() - 2592000000L)); 
            String sqlTunggakan = "SELECT (jumlah_tagihan - jumlah_bayar) as sisa FROM pembayaran WHERE id_pelanggan = ? AND bulan_tagihan = ? AND status = 'Belum Lunas'";
            double tunggakan = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTunggakan)) {
                pstmt.setInt(1, idPelanggan);
                pstmt.setString(2, bulanLalu);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        tunggakan = rs.getDouble("sisa");
                    }
                }
            }

            // Membuat popup pembayaran
            showPaymentDialog(idPelanggan, namaPelanggan, hargaPaket, tunggakan);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil detail tagihan: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_bbayarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelBaru;
    private javax.swing.JPanel PanelHeader;
    private javax.swing.JPanel PanelMasukkanPelanggan;
    private javax.swing.JPanel PanelMasukkanPelanggan1;
    private javax.swing.JPanel PanelPelanggan;
    private javax.swing.JPanel PanelPengaturan;
    private javax.swing.JButton bbayar;
    private javax.swing.JButton bbersih;
    private javax.swing.JButton bbersihjaringan;
    private javax.swing.JButton bbersihpaket;
    private javax.swing.JButton bbersihsales;
    private javax.swing.JButton bbuatjaringan;
    private javax.swing.JButton bbuatpaket;
    private javax.swing.JButton bbuatpelanggan;
    private javax.swing.JButton bbuatredirect;
    private javax.swing.JButton bbuatsales;
    private javax.swing.JButton bcari;
    private javax.swing.JButton bhapusadmin;
    private javax.swing.JButton bhapustabel;
    private javax.swing.JButton bkeluar;
    private javax.swing.JButton bprint;
    private javax.swing.JButton bubahjaringan;
    private javax.swing.JButton bubahpaket;
    private javax.swing.JButton bubahsales;
    private javax.swing.JButton bubahtabel;
    private javax.swing.JComboBox<String> cbjaringan;
    private javax.swing.JComboBox<String> cbpaket;
    private javax.swing.JComboBox<String> cbsales;
    private javax.swing.JComboBox<String> cburut;
    private de.wannawork.jcalendar.JCalendarComboBox ccbtanggal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JPanel panelatas;
    private javax.swing.JTextField txtalamat;
    private javax.swing.JTextField txtcari;
    private javax.swing.JTextField txtjaringan;
    private javax.swing.JTextField txtnama;
    private javax.swing.JTextField txtpaket;
    private javax.swing.JTextField txtsales;
    // End of variables declaration//GEN-END:variables
}
