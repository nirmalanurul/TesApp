package Supermarket024;

import connection024.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.CallableStatement;

public class inputSupplier extends JFrame {
    private JPanel mainPanel;
    private JTextField txtId;
    private JTextField txtNama;
    private JTextField txtAlamat;
    private JTextField txtNo;
    private JTextField txtEmail;
    private JButton simpanButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JTable tbSupplier;

    DefaultTableModel model;
    DBConnect connection = new DBConnect();
    boolean isEdit = false;

    public inputSupplier() {
        setContentPane(mainPanel);
        setTitle("Master Supplier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 480);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();
        tbSupplier.setModel(model);
        model.addColumn("Kode");
        model.addColumn("Nama");
        model.addColumn("Alamat");
        model.addColumn("No HP");
        model.addColumn("Email");

        loadData();

        tbSupplier.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = tbSupplier.getSelectedRow();
                if (i == -1) return;
                isEdit = true;
                txtId.setText(model.getValueAt(i, 0).toString());
                txtId.setEnabled(false);
                txtNama.setText(model.getValueAt(i, 1).toString());
                txtAlamat.setText(model.getValueAt(i, 2).toString());
                txtNo.setText(model.getValueAt(i, 3).toString());
                txtEmail.setText(model.getValueAt(i, 4).toString());
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
            connection.result = connection.stat.executeQuery("SELECT * FROM tblSupplier");
            while (connection.result.next()) {
                Object[] obj = new Object[5];
                obj[0] = connection.result.getString("sp_id");
                obj[1] = connection.result.getString("sp_nama");
                obj[2] = connection.result.getString("sp_alamat");
                obj[3] = connection.result.getString("sp_no");
                obj[4] = connection.result.getString("sp_email");
                model.addRow(obj);
            }
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load supplier: " + e);
        }
    }

    public void simpan() {
        try {
            if (!isEdit) {
                CallableStatement cs = connection.conn.prepareCall("{call spInsertSupplier(?,?,?,?,?)}");
                cs.setString(1, txtId.getText());
                cs.setString(2, txtNama.getText());
                cs.setString(3, txtAlamat.getText());
                cs.setString(4, txtNo.getText());
                cs.setString(5, txtEmail.getText());
                cs.executeUpdate();
                cs.close();
                JOptionPane.showMessageDialog(null, "Data supplier berhasil disimpan!");
            } else {
                CallableStatement cs = connection.conn.prepareCall("{call spUpdateSupplier(?,?,?,?,?)}");
                cs.setString(1, txtId.getText());
                cs.setString(2, txtNama.getText());
                cs.setString(3, txtAlamat.getText());
                cs.setString(4, txtNo.getText());
                cs.setString(5, txtEmail.getText());
                cs.executeUpdate();
                cs.close();
                JOptionPane.showMessageDialog(null, "Data supplier berhasil diupdate!");
            }
            loadData();
            batal();
        } catch (Exception e) {
            System.out.println("Terjadi error saat simpan supplier: " + e);
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
            CallableStatement cs = connection.conn.prepareCall("{call spDeleteSupplier(?)}");
            cs.setString(1, txtId.getText());
            cs.executeUpdate();
            cs.close();
            JOptionPane.showMessageDialog(null, "Data supplier berhasil dihapus!");
            loadData();
            batal();
        } catch (Exception e) {
            System.out.println("Terjadi error saat hapus supplier: " + e);
        }
    }

    public void batal() {
        txtId.setText("");
        txtId.setEnabled(true);
        txtNama.setText("");
        txtAlamat.setText("");
        txtNo.setText("");
        txtEmail.setText("");
        isEdit = false;
    }

    public static void main(String[] args) {
        new inputSupplier().setVisible(true);
    }
}
