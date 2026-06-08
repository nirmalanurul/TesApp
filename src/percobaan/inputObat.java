package percobaan;

import com.toedter.calendar.JDateChooser;
import connection.DBConnect;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;

public class inputObat extends JFrame{
    private JTextField txtkode;
    private JTextField txtnama;
    private JTextField txtmerk;
    private JTextField txtkemasan;
    private JTextField txtefek;
    private JTextField txthbeli;
    private JTextField txthjual;
    private JTextField txtstock;
    private JButton batalButton;
    private JButton simpanButton;
    private JPanel jptanggal;
    private JPanel inputObat;

    DBConnect connection = new DBConnect();
    JDateChooser datechos = new JDateChooser();
    String kodeObat, nama, merk, kemasan, efek, tanggal;
    int hrgbeli, hrgjual, stok;

    public inputObat(){

        setContentPane(inputObat);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400, 400);

        jptanggal.add(datechos);


        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kodeObat = txtkode.getText();
                nama = txtnama.getText();
                merk = txtmerk.getText();
                kemasan = txtkemasan.getText();
                efek = txtefek.getText();
                hrgbeli = Integer.parseInt(txthbeli.getText());
                hrgjual = Integer.parseInt(txthjual.getText());
                stok = Integer.parseInt(txtstock.getText());

                Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                tanggal = formatter.format(datechos.getDate());

//                try{
//                    String query="INSERT INTO tbObat VALUES (?,?,?,?,?,?,?,?,?)";
//                    connection.pstat = connection.conn.prepareStatement(query);
//                    connection.pstat.setString(1, kodeObat);
//                    connection.pstat.setString(2, nama);
//                    connection.pstat.setString(3, merk);
//                    connection.pstat.setString(4, kemasan);
//                    connection.pstat.setString(5, efek);
//                    connection.pstat.setInt(6, hrgbeli);
//                    connection.pstat.setInt(7, hrgjual);
//                    connection.pstat.setString(8, tanggal);
//                    connection.pstat.setInt(9, stok);
//
//                    connection.pstat.executeUpdate();
//                    connection.pstat.close();
//
//                    JOptionPane.showMessageDialog(null, "Data obat inserted successfully");
//                }catch (SQLException ex){
//                    System.out.println("Terjadi error saat insert data obat:" +ex);
//                }
//            }
//        });

                try{
                    CallableStatement cs = connection.conn.prepareCall("{call spInsertObat(?,?,?,?,?,?,?,?,?)}");
                    cs.setString(1, kodeObat);
                    cs.setString(2, nama);
                    cs.setString(3, merk);
                    cs.setString(4, kemasan);
                    cs.setString(5, efek);
                    cs.setInt(6, hrgbeli);
                    cs.setInt(7, hrgjual);
                    cs.setString(8, tanggal);
                    cs.setInt(9, stok);

                    cs.executeUpdate();
                    cs.close();

                    JOptionPane.showMessageDialog(null, "Data obat inserted successfully");

                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
        });


        batalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


            }
        });
    }

    public static void main(String[] args){
        new inputObat().setVisible(true);
    }
}
