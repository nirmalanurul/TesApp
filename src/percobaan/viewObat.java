package percobaan;

import connection.DBConnect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class viewObat extends JFrame {
    private JTable tbObat;
    private JPanel viewObat;
    DefaultTableModel model;
    DBConnect connection = new DBConnect();

    public viewObat() {
        setContentPane(viewObat);
        setTitle("View Data Obat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(700, 400);

        model = new DefaultTableModel();
        tbObat.setModel(model);
        addColumn();
        loadData();
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
            String query = "SELECT * FROM vwObat";
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

    public static void main(String[] args) {
        new viewObat().setVisible(true);
    }
}