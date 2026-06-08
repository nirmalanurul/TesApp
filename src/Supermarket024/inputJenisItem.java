package Supermarket024;

import connection024.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class inputJenisItem extends JFrame {
    private JPanel mainPanel;
    private JTextField txtNama;
    private JComboBox<String> cmbStatus;
    private JButton simpanButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JTable tbJenis;

    DefaultTableModel model;
    DBConnect connection = new DBConnect();
    int selectedId = -1;

    public inputJenisItem() {
        setContentPane(mainPanel);
        setTitle("Master Jenis Item");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 420);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();
        tbJenis.setModel(model);
        model.addColumn("ID");
        model.addColumn("Nama Jenis");
        model.addColumn("Status");

        loadData();

        tbJenis.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = tbJenis.getSelectedRow();
                if (i == -1) return;
                selectedId = Integer.parseInt(model.getValueAt(i, 0).toString());
                txtNama.setText(model.getValueAt(i, 1).toString());
                cmbStatus.setSelectedItem(model.getValueAt(i, 2).toString());
            }
        });

        simpanButton.addActionListener(e -> simpan());
        hapusButton.addActionListener(e -> hapus());
        batalButton.addActionListener(e -> batal());
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            String query = "SELECT * FROM tblJenisItem";
            connection.result = connection.stat.executeQuery(query);
            while (connection.result.next()) {
                Object[] obj = new Object[3];
                obj[0] = connection.result.getInt("jn_id");
                obj[1] = connection.result.getString("jn_nama");
                obj[2] = connection.result.getInt("jn_status") == 1 ? "Aktif" : "Tidak Aktif";
                model.addRow(obj);
            }
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load jenis item: " + e);
        }
    }

    public void simpan() {
        String nama = txtNama.getText();
        int status = cmbStatus.getSelectedIndex() == 0 ? 1 : 0;
        try {
            if (selectedId == -1) {
                String query = "INSERT INTO tblJenisItem (jn_nama, jn_status) VALUES (?, ?)";
                connection.pstat = connection.conn.prepareStatement(query);
                connection.pstat.setString(1, nama);
                connection.pstat.setInt(2, status);
                connection.pstat.executeUpdate();
                connection.pstat.close();
                JOptionPane.showMessageDialog(null, "Data jenis item berhasil disimpan!");
            } else {
                String query = "UPDATE tblJenisItem SET jn_nama=?, jn_status=? WHERE jn_id=?";
                connection.pstat = connection.conn.prepareStatement(query);
                connection.pstat.setString(1, nama);
                connection.pstat.setInt(2, status);
                connection.pstat.setInt(3, selectedId);
                connection.pstat.executeUpdate();
                connection.pstat.close();
                JOptionPane.showMessageDialog(null, "Data jenis item berhasil diupdate!");
            }
            loadData();
            batal();
        } catch (Exception e) {
            System.out.println("Terjadi error saat simpan jenis item: " + e);
        }
    }

    public void hapus() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu!");
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus?");
        if (konfirmasi != JOptionPane.YES_OPTION) return;
        try {
            String query = "DELETE FROM tblJenisItem WHERE jn_id=?";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.pstat.setInt(1, selectedId);
            connection.pstat.executeUpdate();
            connection.pstat.close();
            JOptionPane.showMessageDialog(null, "Data jenis item berhasil dihapus!");
            loadData();
            batal();
        } catch (Exception e) {
            System.out.println("Terjadi error saat hapus jenis item: " + e);
        }
    }

    public void batal() {
        txtNama.setText("");
        cmbStatus.setSelectedIndex(0);
        selectedId = -1;
    }

    public static void main(String[] args) {
        new inputJenisItem().setVisible(true);
    }
}
