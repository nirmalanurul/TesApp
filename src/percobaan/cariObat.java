package percobaan;

import connection.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class cariObat extends JFrame {
    private JPanel mainPanel;
    private JPanel kriteriaPencarian;
    private JPanel dataObat;
    private JRadioButton rbBaseSemua;
    private JRadioButton rbBaseNama;
    private JRadioButton rbBaseMerk;
    private JTable tbDataObat;
    private JTextField txtNama;
    private JTextField txtMerk;
    DefaultTableModel model;

    public cariObat() {
        setContentPane(mainPanel);
        setTitle("Cari Data Obat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(700, 450);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbBaseSemua);
        bg.add(rbBaseNama);
        bg.add(rbBaseMerk);

        rbBaseNama.setEnabled(false);
        rbBaseMerk.setEnabled(false);

        model = new DefaultTableModel();
        tbDataObat.setModel(model);
        addColumn();

        rbBaseSemua.addActionListener(e -> loadData());

        rbBaseNama.addActionListener(e -> showByNama());

        rbBaseMerk.addActionListener(e -> showByMerk());

        txtNama.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                rbBaseNama.setEnabled(true);
                rbBaseMerk.setEnabled(false);
            }
        });

        txtMerk.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                rbBaseMerk.setEnabled(true);
                rbBaseNama.setEnabled(false);
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

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbObat";
            connection.result = connection.stat.executeQuery(query);
            while (connection.result.next()) {
                Object[] obj = new Object[9];
                obj[0] = connection.result.getString("idObat");
                obj[1] = connection.result.getString("namaObat");
                obj[2] = connection.result.getString("merkObat");
                obj[3] = connection.result.getString("kemasanObat");
                obj[4] = connection.result.getString("efekObat");
                obj[5] = connection.result.getInt("hargaBeliObat");
                obj[6] = connection.result.getInt("hargaJualObat");
                obj[7] = connection.result.getString("tglKadaluarsaObat");
                obj[8] = connection.result.getInt("stock");
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data obat: " + e);
        }
    }

    public void showByNama() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            DBConnect connection = new DBConnect();
            String query = "SELECT * FROM dbo.fnCariObatByNama(?)";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.pstat.setString(1, txtNama.getText());
            connection.result = connection.pstat.executeQuery();
            while (connection.result.next()) {
                Object[] obj = new Object[9];
                obj[0] = connection.result.getString("idObat");
                obj[1] = connection.result.getString("namaObat");
                obj[2] = connection.result.getString("merkObat");
                obj[3] = connection.result.getString("kemasanObat");
                obj[4] = connection.result.getString("efekObat");
                obj[5] = connection.result.getInt("hargaBeliObat");
                obj[6] = connection.result.getInt("hargaJualObat");
                obj[7] = connection.result.getString("tglKadaluarsaObat");
                obj[8] = connection.result.getInt("stock");
                model.addRow(obj);
            }
            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(null, "Data Obat tidak ditemukan");
            connection.pstat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data Obat: " + e);
        }
    }

    public void showByMerk() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            DBConnect connection = new DBConnect();
            String query = "SELECT * FROM dbo.fnCariObatByMerk(?)";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.pstat.setString(1, txtMerk.getText());
            connection.result = connection.pstat.executeQuery();
            while (connection.result.next()) {
                Object[] obj = new Object[9];
                obj[0] = connection.result.getString("idObat");
                obj[1] = connection.result.getString("namaObat");
                obj[2] = connection.result.getString("merkObat");
                obj[3] = connection.result.getString("kemasanObat");
                obj[4] = connection.result.getString("efekObat");
                obj[5] = connection.result.getInt("hargaBeliObat");
                obj[6] = connection.result.getInt("hargaJualObat");
                obj[7] = connection.result.getString("tglKadaluarsaObat");
                obj[8] = connection.result.getInt("stock");
                model.addRow(obj);
            }
            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(null, "Data Obat tidak ditemukan");
            connection.pstat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data Obat: " + e);
        }
    }

    public static void main(String[] args) {
        new cariObat().setVisible(true);
    }
}