package Supermarket024;

import com.toedter.calendar.JDateChooser;
import connection024.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.CallableStatement;
import java.text.Format;
import java.text.SimpleDateFormat;

public class inputItem extends JFrame {
    private JPanel mainPanel;
    private JTextField txtId;
    private JTextField txtNama;
    private JTextField txtStock;
    private JTextField txtHarga;
    private JComboBox<String> cmbJenis;
    private JComboBox<String> cmbSupplier;
    private JComboBox<String> cmbStatus;
    private JPanel jpTanggal;
    private JButton simpanButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JTable tbItem;

    DefaultTableModel model;
    DBConnect connection = new DBConnect();
    JDateChooser datechos = new JDateChooser();
    boolean isEdit = false;

    public inputItem() {
        setContentPane(mainPanel);
        setTitle("Master Item");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(680, 550);
        setLocationRelativeTo(null);

        jpTanggal.setLayout(new BorderLayout());
        jpTanggal.add(datechos);

        txtId.setText(generateKodeItem());
        txtId.setEnabled(false);

        loadKomboJenis();
        loadKomboSupplier();

        model = new DefaultTableModel();
        tbItem.setModel(model);
        model.addColumn("Kode Item");
        model.addColumn("Nama Item");
        model.addColumn("Jenis");
        model.addColumn("Supplier");
        model.addColumn("Harga");
        model.addColumn("Stock");
        model.addColumn("Status");

        loadData();

        tbItem.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = tbItem.getSelectedRow();
                if (i == -1) return;
                isEdit = true;
                txtId.setText(model.getValueAt(i, 0).toString());
                txtNama.setText(model.getValueAt(i, 1).toString());
                cmbJenis.setSelectedItem(model.getValueAt(i, 2).toString());
                cmbSupplier.setSelectedItem(model.getValueAt(i, 3).toString());
                txtHarga.setText(model.getValueAt(i, 4).toString());
                txtStock.setText(model.getValueAt(i, 5).toString());
                cmbStatus.setSelectedItem(
                        model.getValueAt(i, 6).toString().equals("1") ? "Aktif" : "Tidak Aktif"
                );
            }
        });

        simpanButton.addActionListener(e -> simpan());
        hapusButton.addActionListener(e -> hapus());
        batalButton.addActionListener(e -> batal());
    }

    public String generateKodeItem() {
        try {
            connection.result = connection.stat.executeQuery("SELECT dbo.fnGenerateKodeItem() AS kode");
            if (connection.result.next()) {
                String kode = connection.result.getString("kode");
                connection.result.close();
                return kode;
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat generate kode item: " + e);
        }
        return "ITM001";
    }

    public void loadKomboJenis() {
        try {
            connection.result = connection.stat.executeQuery(
                    "SELECT jn_id, jn_nama FROM tblJenisItem WHERE jn_status=1"
            );
            while (connection.result.next()) {
                cmbJenis.addItem(connection.result.getString("jn_nama"));
            }
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load jenis: " + e);
        }
    }

    public void loadKomboSupplier() {
        try {
            connection.result = connection.stat.executeQuery(
                    "SELECT sp_id, sp_nama FROM tblSupplier"
            );
            while (connection.result.next()) {
                cmbSupplier.addItem(connection.result.getString("sp_nama"));
            }
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load supplier: " + e);
        }
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            connection.result = connection.stat.executeQuery("SELECT * FROM vwItem");
            while (connection.result.next()) {
                Object[] obj = new Object[7];
                obj[0] = connection.result.getString("itm_id");
                obj[1] = connection.result.getString("itm_nama");
                obj[2] = connection.result.getString("jn_nama");
                obj[3] = connection.result.getString("sp_nama");
                obj[4] = connection.result.getString("itm_harga");
                obj[5] = connection.result.getString("itm_stock");
                obj[6] = connection.result.getString("itm_status");
                model.addRow(obj);
            }
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data item: " + e);
        }
    }

    public int getJnId(String jenisNama) {
        try {
            connection.pstat = connection.conn.prepareStatement(
                    "SELECT jn_id FROM tblJenisItem WHERE jn_nama=?"
            );
            connection.pstat.setString(1, jenisNama);
            connection.result = connection.pstat.executeQuery();
            if (connection.result.next()) {
                int id = connection.result.getInt("jn_id");
                connection.result.close();
                return id;
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat get jn_id: " + e);
        }
        return -1;
    }

    public String getSpId(String spNama) {
        try {
            connection.pstat = connection.conn.prepareStatement(
                    "SELECT sp_id FROM tblSupplier WHERE sp_nama=?"
            );
            connection.pstat.setString(1, spNama);
            connection.result = connection.pstat.executeQuery();
            if (connection.result.next()) {
                String id = connection.result.getString("sp_id");
                connection.result.close();
                return id;
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat get sp_id: " + e);
        }
        return "";
    }

    public void simpan() {
        try {
            Format formatter = new SimpleDateFormat("yyyy-MM-dd");
            String tgl = formatter.format(datechos.getDate());
            int jnId = getJnId(cmbJenis.getSelectedItem().toString());
            String spId = getSpId(cmbSupplier.getSelectedItem().toString());
            int status = cmbStatus.getSelectedIndex() == 0 ? 1 : 0;

            if (!isEdit) {
                CallableStatement cs = connection.conn.prepareCall(
                        "{call spInsertItem(?,?,?,?,?,?,?,?)}"
                );
                cs.setString(1, txtId.getText());
                cs.setInt(2, jnId);
                cs.setString(3, spId);
                cs.setString(4, txtNama.getText());
                cs.setString(5, tgl);
                cs.setInt(6, Integer.parseInt(txtStock.getText()));
                cs.setDouble(7, Double.parseDouble(txtHarga.getText()));
                cs.setInt(8, status);
                cs.executeUpdate();
                cs.close();
                JOptionPane.showMessageDialog(null, "Data item berhasil disimpan!");
            } else {
                CallableStatement cs = connection.conn.prepareCall(
                        "{call spUpdateItem(?,?,?,?,?,?,?,?)}"
                );
                cs.setString(1, txtId.getText());
                cs.setInt(2, jnId);
                cs.setString(3, spId);
                cs.setString(4, txtNama.getText());
                cs.setString(5, tgl);
                cs.setInt(6, Integer.parseInt(txtStock.getText()));
                cs.setDouble(7, Double.parseDouble(txtHarga.getText()));
                cs.setInt(8, status);
                cs.executeUpdate();
                cs.close();
                JOptionPane.showMessageDialog(null, "Data item berhasil diupdate!");
            }
            loadData();
            batal();
        } catch (Exception e) {
            System.out.println("Terjadi error saat simpan item: " + e);
        }
    }

    public void hapus() {
        if (!isEdit) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu!");
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus?");
        if (konfirmasi != JOptionPane.YES_OPTION) return;
        try {
            CallableStatement cs = connection.conn.prepareCall("{call spDeleteItem(?)}");
            cs.setString(1, txtId.getText());
            cs.executeUpdate();
            cs.close();
            JOptionPane.showMessageDialog(null, "Data item berhasil dihapus!");
            loadData();
            batal();
        } catch (Exception e) {
            System.out.println("Terjadi error saat hapus item: " + e);
        }
    }

    public void batal() {
        txtId.setText(generateKodeItem());
        txtNama.setText("");
        txtStock.setText("");
        txtHarga.setText("");
        cmbJenis.setSelectedIndex(0);
        cmbSupplier.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        datechos.setDate(null);
        isEdit = false;
    }

    public static void main(String[] args) {
        new inputItem().setVisible(true);
    }
}
