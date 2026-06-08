package percobaan;

import com.toedter.calendar.JDateChooser;
import connection.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;

public class pembelianObat extends JFrame {
    private JPanel mainPanel;
    private JTextField txtno;
    private JPanel jpTanggal;
    private JComboBox cmbSupplier;
    private JButton tambahPembelianButton;
    private JTable tblObat;
    private JTextField txttotal;
    private JButton cancelButton;
    private JButton simpanButton;

    DefaultTableModel model;
    DBConnect connection = new DBConnect();
    JDateChooser datechos = new JDateChooser();

    public pembelianObat() {
        setContentPane(mainPanel);
        setTitle("Pembelian Obat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 500);

        jpTanggal.setLayout(new BorderLayout());
        jpTanggal.add(datechos);

        model = new DefaultTableModel();
        tblObat.setModel(model);
        model.addColumn("Kode Obat");
        model.addColumn("Nama Obat");
        model.addColumn("Merk");
        model.addColumn("Harga Beli");
        model.addColumn("Jumlah");

        tampilSupplier();

        tambahPembelianButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{"", "", "", "", ""});
            }
        });

        tblObat.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                String kode, nama, merk, harga;
                int i = tblObat.getSelectedRow();
                if (i == -1) {
                    return;
                }

                kode = (String) model.getValueAt(i, 0);
                try {
                    connection.stat = connection.conn.createStatement();

                    String sql = "SELECT namaObat, merkObat, hargaBeliObat, stock " +
                            "FROM tbObat WHERE idObat = '" + kode + "'";
                    connection.result = connection.stat.executeQuery(sql);
                    while (connection.result.next()) {
                        nama  = connection.result.getString("namaObat");
                        merk  = connection.result.getString("merkObat");
                        harga = connection.result.getString("hargaBeliObat");
                        model.setValueAt(nama,  i, 1); // set nama di tabel
                        model.setValueAt(merk,  i, 2); // set merk di tabel
                        model.setValueAt(harga, i, 3); // set harga di tabel
                    }
                    connection.stat.close();
                    connection.result.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat mengambil data nama dan harga obat " + ex);
                }
            }
        });

        txttotal.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                double temp, total = 0.0;
                int i = tblObat.getSelectedRow();
                if (i == -1) {
                    return;
                }
                int j = tblObat.getModel().getRowCount();
                for (int k = 0; k < j; k++) {
                    temp = (Double.parseDouble((String) model.getValueAt(k, 3)))
                            * (Double.parseDouble((String) model.getValueAt(k, 4)));
                    total = total + temp;
                }
                txttotal.setText(String.valueOf(total));
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });

        simpanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idTrsPembelian, tanggal, kodeSupplier, total;
                double harga;
                Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                int j = tblObat.getModel().getRowCount();
                idTrsPembelian = txtno.getText();
                tanggal        = formatter.format(datechos.getDate());
                total          = txttotal.getText();
                kodeSupplier   = "";

                try {
                    connection.stat = connection.conn.createStatement();
                    String sql = "SELECT kodeSupplier FROM tbSupplier " +
                            "WHERE namaSupplier = '" + cmbSupplier.getSelectedItem() + "'";
                    connection.result = connection.stat.executeQuery(sql);
                    while (connection.result.next()) {
                        kodeSupplier = (String) connection.result.getString("kodeSupplier");
                    }

                    String sql2 = "INSERT INTO trsPembelian VALUES (?, ?, ?, ?)";
                    connection.pstat = connection.conn.prepareStatement(sql2);
                    connection.pstat.setString(1, idTrsPembelian);
                    connection.pstat.setString(2, tanggal);
                    connection.pstat.setString(3, kodeSupplier);
                    connection.pstat.setString(4, total);
                    connection.pstat.executeUpdate();

                    for (int k = 0; k < j; k++) {
                        harga = (Double.parseDouble((String) model.getValueAt(k, 3)))
                                * (Double.parseDouble((String) model.getValueAt(k, 4)));
                        String sql3 = "INSERT INTO detilBeli VALUES (?, ?, ?, ?)";
                        connection.pstat = connection.conn.prepareStatement(sql3);
                        connection.pstat.setString(1, idTrsPembelian);
                        connection.pstat.setString(2, (String) model.getValueAt(k, 0));
                        connection.pstat.setString(3, (String) model.getValueAt(k, 4));
                        connection.pstat.setString(4, String.valueOf(harga));
                        connection.pstat.executeUpdate();
                    }

                    connection.pstat.close();
                    clear();
                    JOptionPane.showMessageDialog(null, "Insert data Obat Berhasil");

                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }
            }
        });
    }

    public void tampilSupplier() {
        try {
            connection.stat = connection.conn.createStatement();
            String sql = "SELECT kodeSupplier, namaSupplier FROM tbSupplier";
            connection.result = connection.stat.executeQuery(sql);
            while (connection.result.next()) {
                cmbSupplier.addItem(connection.result.getString("namaSupplier"));
            }
            connection.stat.close();
            connection.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data supplier " + ex);
        }
    }

    public void clear() {
        txtno.setText("");
        txttotal.setText("");
        datechos.setDate(null);
        cmbSupplier.setSelectedIndex(0);
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
    }

    public static void main(String[] args) {
        new pembelianObat().setVisible(true);
    }
}