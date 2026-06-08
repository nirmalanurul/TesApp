package percobaan;

import connection.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;

public class ubahObat extends JFrame {
    private JPanel mainPanel;
    private JPanel panelKiri;
    private JPanel panelKanan;
    private JRadioButton rbNama;
    private JRadioButton rbMerk;
    private JTextField txtNama;
    private JTextField txtMerk;
    private JTable tbObat;
    private JTextField txtKode;
    private JTextField txtNamaObat;
    private JTextField txtMerkObat;
    private JTextField txtKemasan;
    private JTextField txtEfek;
    private JTextField txtHBeli;
    private JTextField txtHJual;
    private JTextField txtStock;
    private JButton hapusButton;
    private JButton ubahButton;

    DefaultTableModel model;
    DBConnect connection = new DBConnect();

    String kode, nama, merk, kemasan, efek;
    int hBeli, hJual, stock;

    public ubahObat() {
        setContentPane(mainPanel);
        setTitle("Ubah Data Obat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(900, 500);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbNama);
        bg.add(rbMerk);

        rbNama.setEnabled(false);
        rbMerk.setEnabled(false);

        txtKode.setEnabled(false);

        model = new DefaultTableModel();
        tbObat.setModel(model);
        addColumn();

        rbNama.addActionListener(e -> showByNama());
        rbMerk.addActionListener(e -> showByMerk());

        txtNama.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                rbNama.setEnabled(true);
                rbMerk.setEnabled(false);
            }
        });

        txtMerk.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                rbMerk.setEnabled(true);
                rbNama.setEnabled(false);
            }
        });

        tbObat.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbObat.getSelectedRow();
                if (i == -1) return;
                txtKode.setText((String) model.getValueAt(i, 0));
                txtNamaObat.setText((String) model.getValueAt(i, 1));
                txtMerkObat.setText((String) model.getValueAt(i, 2));
                txtKemasan.setText((String) model.getValueAt(i, 3));
                txtEfek.setText((String) model.getValueAt(i, 4));
                txtHBeli.setText(model.getValueAt(i, 5).toString());
                txtHJual.setText(model.getValueAt(i, 6).toString());
                txtStock.setText(model.getValueAt(i, 8).toString());
            }
        });

        ubahButton.addActionListener(e -> {
            kode    = txtKode.getText();
            nama    = txtNamaObat.getText();
            merk    = txtMerkObat.getText();
            kemasan = txtKemasan.getText();
            efek    = txtEfek.getText();
            hBeli   = Integer.parseInt(txtHBeli.getText());
            hJual   = Integer.parseInt(txtHJual.getText());
            stock   = Integer.parseInt(txtStock.getText());

            try {
                CallableStatement cs = connection.conn.prepareCall(
                        "{call spUpdateObat(?,?,?,?,?,?,?,?)}"
                );
                cs.setString(1, nama);
                cs.setString(2, merk);
                cs.setString(3, kemasan);
                cs.setString(4, efek);
                cs.setInt(5, hBeli);
                cs.setInt(6, hJual);
                cs.setInt(7, stock);
                cs.setString(8, kode);
                cs.executeUpdate();
                cs.close();
                JOptionPane.showMessageDialog(null, "Update data obat berhasil");
                clearDetail();
            } catch (Exception ex) {
                System.out.println("Terjadi error saat update obat: " + ex);
            }
        });

        hapusButton.addActionListener(e -> {
            kode = txtKode.getText();
            if (kode.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus");
                return;
            }
            try {
                CallableStatement cs = connection.conn.prepareCall(
                        "{call spDeleteObat(?)}"
                );
                cs.setString(1, kode);
                cs.executeUpdate();
                cs.close();
                JOptionPane.showMessageDialog(null, "Hapus data berhasil");
                clearDetail();
            } catch (Exception ex) {
                System.out.println("Terjadi error saat menghapus obat: " + ex);
            }
        });
    }

    public void addColumn() {
        model.addColumn("Kode Obat");
        model.addColumn("Nama Obat");
        model.addColumn("Merk");
        model.addColumn("Kemasan");
        model.addColumn("Efek");
        model.addColumn("Harga Beli");
        model.addColumn("Harga Jual");
        model.addColumn("Kadaluarsa");
        model.addColumn("Stock");
    }

    public void showByNama() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            DBConnect conn = new DBConnect();
            conn.stat = conn.conn.createStatement();
            String query = "SELECT * FROM tbObat WHERE namaObat LIKE '%"
                    + txtNama.getText() + "%'";
            conn.result = conn.stat.executeQuery(query);
            while (conn.result.next()) {
                Object[] obj = new Object[9];
                obj[0] = conn.result.getString("idObat");
                obj[1] = conn.result.getString("namaObat");
                obj[2] = conn.result.getString("merkObat");
                obj[3] = conn.result.getString("kemasanObat");
                obj[4] = conn.result.getString("efekObat");
                obj[5] = conn.result.getInt("hargaBeliObat");
                obj[6] = conn.result.getInt("hargaJualObat");
                obj[7] = conn.result.getString("tglKadaluarsaObat");
                obj[8] = conn.result.getInt("stock");
                model.addRow(obj);
            }
            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(null, "Data Obat tidak ditemukan");
            conn.stat.close();
            conn.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data Obat: " + e);
        }
    }

    public void showByMerk() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            DBConnect conn = new DBConnect();
            conn.stat = conn.conn.createStatement();
            String query = "SELECT * FROM tbObat WHERE merkObat LIKE '%"
                    + txtMerk.getText() + "%'";
            conn.result = conn.stat.executeQuery(query);
            while (conn.result.next()) {
                Object[] obj = new Object[9];
                obj[0] = conn.result.getString("idObat");
                obj[1] = conn.result.getString("namaObat");
                obj[2] = conn.result.getString("merkObat");
                obj[3] = conn.result.getString("kemasanObat");
                obj[4] = conn.result.getString("efekObat");
                obj[5] = conn.result.getInt("hargaBeliObat");
                obj[6] = conn.result.getInt("hargaJualObat");
                obj[7] = conn.result.getString("tglKadaluarsaObat");
                obj[8] = conn.result.getInt("stock");
                model.addRow(obj);
            }
            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(null, "Data Obat tidak ditemukan");
            conn.stat.close();
            conn.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data Obat: " + e);
        }
    }

    public void clearDetail() {
        txtKode.setText("");
        txtNamaObat.setText("");
        txtMerkObat.setText("");
        txtKemasan.setText("");
        txtEfek.setText("");
        txtHBeli.setText("");
        txtHJual.setText("");
        txtStock.setText("");
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
    }

    public static void main(String[] args) {
        new ubahObat().setVisible(true);
    }
}