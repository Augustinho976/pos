package pos.com;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sale {
    private JPanel myPanel;
    private JTextField txtpId;
    private JTextField txtpNmae;
    private JSpinner spinner1;
    private JTextField txtPrice;
    private JTextField txtAmnt;
    private JTextField txtTotal;
    private JTextField txtPay;
    private JTextField txtBalance;
    private JButton addButton;
    private JPanel myPanel2;
    private JPanel myPanel3;
    private JTable table1;
    private JTextArea txtArea;
    private JButton printBillButton;

    public Sale() {

        txtpId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                super.keyPressed(keyEvent);



                Connection con = null;
                PreparedStatement pst;
                ResultSet rs = null;
                String host = "localhost";
                String port = "5432";
                String db_name = "pos";
                String username = "postgres";
                String password = "";
                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    String pCode = txtpId.getText();
                    try {
                        Class.forName("org.postgresql.Driver");
                        con = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + db_name + "", "" + username + "", "" + password + "");
                        pst = con.prepareStatement("select * from products where id = ?");
                        pst.setString(1, pCode);
                        rs = pst.executeQuery();

                        if(rs.next() == false)
                        {
                            JOptionPane.showMessageDialog(null, "Product Code Not Found");
                        }
                        else
                        {
                            String pName = rs.getString("product_name");
                            String price = rs.getString("price");
                            txtpNmae.setText(pName.trim());
                            txtPrice.setText(price.trim());
                        }
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Sale.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(Sale.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        spinner1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int qtty = Integer.parseInt(spinner1.getValue().toString());
                int price = Integer.parseInt(txtPrice.getText());
                int total = qtty * price;

                txtAmnt.setText(String.valueOf(total));
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultTableModel model = new DefaultTableModel();
                model = (DefaultTableModel)table1.getModel();
                model.addRow(new Object[]

                        {
                                txtpId.getText(),
                                txtpNmae.getText(),
                                spinner1.getValue().toString(),
                                txtPrice.getText(),
                                txtAmnt.getText(),
                        });

                int sum = 0;

                for(int i = 0; i<table1.getRowCount(); i++)
                {
                    sum = sum + Integer.parseInt(table1.getValueAt(i, 3).toString());
                }

                txtTotal.setText(Integer.toString(sum));

                txtpId.setText("");
                txtpNmae.setText("");
                txtPrice.setText("");
                txtAmnt.setText("");
                txtpId.requestFocus();
            }
        });
        printBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Change();
                Receipt();

            }
        });
    }
    public void Change(){
        int total= Integer.parseInt(txtTotal.getText());
        int paid = Integer.parseInt(txtPay.getText());

        int change= paid-total;
        txtBalance.setText(String.valueOf(change));
    }
    public void Receipt(){
        String total = txtTotal.getText();
        String paid = txtPay.getText();
        String change = txtBalance.getText();

        DefaultTableModel model = new DefaultTableModel();

        model = (DefaultTableModel)table1.getModel();

        txtArea.setText(txtArea.getText() + "******************************************************\n");
        txtArea.setText(txtArea.getText() + "           POS_RECEIPT                                   \n");
        txtArea.setText(txtArea.getText() + "*******************************************************\n");

        //Heading
        txtArea.setText(txtArea.getText() + "Product" + "\t" + "Price" + "\t" + "Amount" + "\n"  );


        for(int i = 0; i < model.getRowCount(); i++)
        {

            String pname = (String)model.getValueAt(i, 1);
            String price = (String)model.getValueAt(i, 3);
            String amount = (String)model.getValueAt(i, 4);

            txtArea.setText(txtArea.getText() + pname  + "\t" + price + "\t" + amount  + "\n"  );

        }

        txtArea.setText(txtArea.getText() + "\n");

        txtArea.setText(txtArea.getText() + "\t" + "\t" + "Subtotal :" + total + "\n");
        txtArea.setText(txtArea.getText() + "\t" + "\t" + "Pay :" + paid + "\n");
        txtArea.setText(txtArea.getText() + "\t" + "\t" + "Balance :" + change + "\n");
        txtArea.setText(txtArea.getText() + "\n");
        txtArea.setText(txtArea.getText() + "+++++++++++++++++++++++++++++++++++++++++++++++++\n");
        txtArea.setText(txtArea.getText() + "          THANK YOU, ALWAYS COME BACK            \n");

    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Sale");
        frame.setContentPane(new Sale().myPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
