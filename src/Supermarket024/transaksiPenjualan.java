package Supermarket024;

import com.toedter.calendar.JDateChooser;
import connection024.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;

public class transaksiPenjualan extends JFrame {
    private JPanel mainPanel;
    private JTextField txtIdTrs;
    private JPanel jpTanggal;
    private JButton tambahItemButton;
    private JTable tblItem;
    private JTextField txttotal;
    private JButton cancelButton;
    private JButton simpanButton;
    private JButton hitungButton;

    DefaultTableModel model;
    DBConnect connection = new DBConnect();
    JDateChooser datechos = new JDateChooser();

    public transaksiPenjualan() {
        setContentPane(mainPanel);
        setTitle("Transaksi Penjualan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 530);
        setLocationRelativeTo(null);

        jpTanggal.setLayout(new BorderLayout());
        jpTanggal.add(datechos);

        txtIdTrs.setEnabled(false);
        txttotal.setEnabled(false);

        txtIdTrs.setText(generateKodeTrs());

        model = new DefaultTableModel();
        tblItem.setModel(model);
        model.addColumn("Kode Item");
        model.addColumn("Nama Item");
        model.addColumn("Harga");
        model.addColumn("Qty");


        tambahItemButton.addActionListener(e -> {
            model.addRow(new Object[]{"", "", "", ""});
        });

        hitungButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitungTotal();
            }
        });

        tblItem.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                int i = tblItem.getSelectedRow();
                if (i == -1) return;
                String kode = model.getValueAt(i, 0) != null ? model.getValueAt(i, 0).toString() : "";
                try {
                    connection.stat = connection.conn.createStatement();
                    String sql = "SELECT itm_nama, itm_harga FROM tblItem WHERE itm_id = '" + kode + "'";
                    connection.result = connection.stat.executeQuery(sql);
                    while (connection.result.next()) {
                        model.setValueAt(connection.result.getString("itm_nama"), i, 1);
                        model.setValueAt(connection.result.getString("itm_harga"), i, 2);
                    }
                    connection.stat.close();
                    connection.result.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat mengambil data item: " + ex);
                }
            }
        });

        txttotal.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                double temp, total = 0.0;
                int j = tblItem.getModel().getRowCount();
                for (int k = 0; k < j; k++) {
                    Object hargaObj = model.getValueAt(k, 2);
                    Object qtyObj = model.getValueAt(k, 3);
                    if (hargaObj == null || qtyObj == null) continue;
                    if (hargaObj.toString().isEmpty() || qtyObj.toString().isEmpty()) continue;
                    temp = Double.parseDouble(hargaObj.toString())
                            * Double.parseDouble(qtyObj.toString());
                    total = total + temp;
                }
                txttotal.setText(String.valueOf(total));
            }
        });

        cancelButton.addActionListener(e -> clear());
        simpanButton.addActionListener(e -> simpan());
    }

    public String generateKodeTrs() {
        try {
            DBConnect conn2 = new DBConnect();
            conn2.result = conn2.stat.executeQuery(
                    "SELECT dbo.fnGenerateKodeTrs() AS kode"
            );
            if (conn2.result.next()) {
                String kode = conn2.result.getString("kode");
                conn2.result.close();
                conn2.stat.close();
                return kode;
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat generate kode transaksi: " + e);
        }
        return "TR0001";
    }

    public void hitungTotal() {
        double total = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object hargaObj = model.getValueAt(i, 2);
            Object qtyObj = model.getValueAt(i, 3);

            if (hargaObj == null || qtyObj == null)
                continue;

            if (hargaObj.toString().trim().isEmpty()
                    || qtyObj.toString().trim().isEmpty())
                continue;

            double harga = Double.parseDouble(hargaObj.toString());
            int qty = Integer.parseInt(qtyObj.toString());

            total += harga * qty;
        }

        txttotal.setText(String.valueOf(total));
    }

    public void simpan() {
        int j = tblItem.getModel().getRowCount();
        if (j == 0) {
            JOptionPane.showMessageDialog(null, "Tambahkan item terlebih dahulu!");
            return;
        }

        if (datechos.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Pilih tanggal terlebih dahulu!");
            return;
        }

        if (txttotal.getText().isEmpty() || txttotal.getText().equals("0.0")) {
            JOptionPane.showMessageDialog(null, "Hitung total terlebih dahulu!");
            return;
        }

        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String idTrs  = txtIdTrs.getText();
        String tanggal = formatter.format(datechos.getDate());
        String total  = txttotal.getText();

        try {
            // insert ke tabel master
            String sql2 = "INSERT INTO tblTrsPenjualan VALUES (?, ?, ?, ?)";
            connection.pstat = connection.conn.prepareStatement(sql2);
            connection.pstat.setString(1, idTrs);
            connection.pstat.setString(2, tanggal);
            connection.pstat.setDouble(3, Double.parseDouble(total));
            connection.pstat.setInt(4, 1);
            connection.pstat.executeUpdate();

            // insert detail + update stock per baris
            for (int k = 0; k < j; k++) {
                Object itmIdObj = model.getValueAt(k, 0);
                Object qtyObj   = model.getValueAt(k, 3);

                if (itmIdObj == null || qtyObj == null) continue;
                if (itmIdObj.toString().isEmpty() || qtyObj.toString().isEmpty()) continue;

                // insert detail
                String sql3 = "INSERT INTO tblDetilPenjualan VALUES (?, ?, ?)";
                connection.pstat = connection.conn.prepareStatement(sql3);
                connection.pstat.setString(1, idTrs);
                connection.pstat.setString(2, itmIdObj.toString());
                connection.pstat.setInt(3, Integer.parseInt(qtyObj.toString()));
                connection.pstat.executeUpdate();

            }

            connection.pstat.close();
            clear();
            JOptionPane.showMessageDialog(null, "Transaksi berhasil disimpan!");

        } catch (SQLException ex) {
            System.out.println("Terjadi error saat simpan transaksi: " + ex);
        }
    }

    public void clear() {
        txtIdTrs.setText(generateKodeTrs());
        txttotal.setText("");
        datechos.setDate(null);
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
    }

    public static void main(String[] args) {
        new transaksiPenjualan().setVisible(true);
    }
}
