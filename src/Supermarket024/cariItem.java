package Supermarket024;

import connection024.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

public class cariItem extends JFrame {
    private JPanel mainPanel;
    private JRadioButton rbBaseSemua;
    private JRadioButton rbBaseNama;
    private JRadioButton rbBaseJenis;
    private JTextField txtNama;
    private JComboBox<String> cmbJenis;
    private JTable tbDataItem;

    DefaultTableModel model;

    public cariItem() {
        setContentPane(mainPanel);
        setTitle("Cari Data Item");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 480);
        setLocationRelativeTo(null);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbBaseSemua);
        bg.add(rbBaseNama);
        bg.add(rbBaseJenis);

        rbBaseNama.setEnabled(false);
        rbBaseJenis.setEnabled(false);

        model = new DefaultTableModel();
        tbDataItem.setModel(model);
        addColumn();

        loadKomboJenis();

        rbBaseSemua.addActionListener(e -> loadData());
        rbBaseNama.addActionListener(e -> showByNama());
        rbBaseJenis.addActionListener(e -> showByJenis());

        txtNama.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                rbBaseNama.setEnabled(true);
                rbBaseJenis.setEnabled(false);
            }
        });

        cmbJenis.addItemListener(e -> {
            rbBaseJenis.setEnabled(true);
            rbBaseNama.setEnabled(false);
        });
    }

    public void addColumn() {
        model.addColumn("Kode Item");
        model.addColumn("Nama Item");
        model.addColumn("Jenis");
        model.addColumn("Supplier");
        model.addColumn("Harga");
        model.addColumn("Stock");
        model.addColumn("Status");
    }

    public void loadKomboJenis() {
        try {
            DBConnect connection = new DBConnect();
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

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            DBConnect connection = new DBConnect();
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

    public void showByNama() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            DBConnect connection = new DBConnect();
            String query = "SELECT * FROM dbo.fnCariItemByNama(?)";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.pstat.setString(1, txtNama.getText());
            connection.result = connection.pstat.executeQuery();
            isiTabel(connection);
            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(null, "Data Item tidak ditemukan");
            connection.pstat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data item: " + e);
        }
    }

    public void showByJenis() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            DBConnect connection = new DBConnect();
            String jenisNama = cmbJenis.getSelectedItem().toString();
            connection.pstat = connection.conn.prepareStatement(
                    "SELECT jn_id FROM tblJenisItem WHERE jn_nama=?"
            );
            connection.pstat.setString(1, jenisNama);
            connection.result = connection.pstat.executeQuery();
            int jnId = -1;
            if (connection.result.next()) jnId = connection.result.getInt("jn_id");
            connection.result.close();

            String query = "SELECT * FROM dbo.fnCariItemByJenis(?)";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.pstat.setInt(1, jnId);
            connection.result = connection.pstat.executeQuery();
            while (connection.result.next()) {
                Object[] obj = new Object[7];
                obj[0] = connection.result.getString("itm_id");
                obj[1] = connection.result.getString("itm_nama");
                obj[2] = jenisNama;
                obj[3] = "-";
                obj[4] = connection.result.getDouble("itm_harga");
                obj[5] = connection.result.getInt("itm_stock");
                obj[6] = connection.result.getInt("itm_status");
                model.addRow(obj);
            }
            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(null, "Data Item tidak ditemukan");
            connection.pstat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data item: " + e);
        }
    }

    private void isiTabel(DBConnect connection) throws Exception {
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
    }

    public static void main(String[] args) {
        new cariItem().setVisible(true);
    }
}
