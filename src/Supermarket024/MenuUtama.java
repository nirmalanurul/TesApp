package Supermarket024;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuUtama extends JFrame {
    private JPanel mainPanel;
    private JButton btnJenisItem;
    private JButton btnSupplier;
    private JButton btnItem;
    private JButton btnTransaksi;
    private JButton btnCariItem;

    public MenuUtama() {
        setContentPane(mainPanel);
        setTitle("Supermarket024 - Menu Second");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        btnJenisItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new inputJenisItem().setVisible(true);
            }
        });

        btnSupplier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new inputSupplier().setVisible(true);
            }
        });

        btnItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new inputItem().setVisible(true);
            }
        });

        btnTransaksi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new transaksiPenjualan().setVisible(true);
            }
        });

        btnCariItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new cariItem().setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuUtama().setVisible(true));
    }
}
