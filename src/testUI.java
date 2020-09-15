
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author root
 */
public class testUI extends javax.swing.JFrame {

    /**
     * Creates new form testUI
     */
    public testUI() {
        initComponents();
        jButton2.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        txtPath = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtPath.setText("/home/roberth/Desktop/Hilo.jar");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ajax-loader.gif"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(117, 117, 117))
            .addGroup(layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPath))
                .addGap(76, 76, 76))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(txtPath)
                .addGap(65, 65, 65)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ArrayList<Data> lista = new ArrayList<>();
        Random ran = new Random();
        Data data;
        String fe = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(testUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int j = 0; j < 10; j++) {

                fe = dtf.format(now);
                data = new Data("\"" + fe + "\"", ran.nextInt(100));
                lista.add(data);
            }
            generaCSV(lista);
            lista.clear();
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    public static void generaCSV(ArrayList<Data> lista) {
        String appRealPID = "";
        FileWriter csvWriter = null;
        InterfazFusion interfaz = new InterfazFusion();
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        interfaz.txtStatus.setText("La medición ha empezado...\nObteniendo datos..."
                + "\nGenerando archivo Resultados_.csv");
        try {
            Iterator itr = lista.iterator();
            File f = new File("/home/roberth/Desktop/test/Resultados_.json");
            csvWriter = new FileWriter(f, false);
            int header = 1;
            if (header == 1) {
                csvWriter.append("[");
                csvWriter.append("\n");
                header++;
            }

            while (itr.hasNext()) {
                Data next = (Data) itr.next();
                if (!itr.hasNext()) {
                    csvWriter.append("[" + next.data0 + "," + next.data1 + "]");
                }
                if (itr.hasNext()) {
                    csvWriter.append("[" + next.data0 + "," + next.data1 + "]");
                    csvWriter.append(",");
                }

                csvWriter.append("\n");
            }

            csvWriter.append("]");
//            csvWriter.append(",");
//            csvWriter.append("Value");
//                csvWriter.append(";");
//                csvWriter.append("Energy Package (J)");
//                csvWriter.append(";");
//                csvWriter.append("Hora");
//                csvWriter.append(";");
//                csvWriter.append("Increase Energy DRAM (J)");
//                csvWriter.append(";");
//                csvWriter.append("Increase Energy CPU (J)");
//                csvWriter.append(";");
//                csvWriter.append("Increase Energy Package (J)");
//            csvWriter.append("\n");

//            while (itr.hasNext()) {
//                Integer get = lista.get(i);
//                
////                valueDRAM = Double.parseDouble(st.data0);
////                auxDRAM = auxDRAM + valueDRAM;
////                valueCPU = Double.parseDouble(st.data1);
////                auxCPU = auxCPU + valueCPU;
////                valuePKG = Double.parseDouble(st.data2);
////                auxPKG = auxPKG + valuePKG;
//                csvWriter.append(lista.get(i));
////                csvWriter.append(",");
////                csvWriter.append(st.data1);
////                csvWriter.append(";");
////                csvWriter.append(st.data2);
//                csvWriter.append(";");
//                csvWriter.append(st.date);
//                csvWriter.append(";");
//                csvWriter.append(String.valueOf(auxDRAM));
//                csvWriter.append(";");
//                csvWriter.append(String.valueOf(auxCPU));
//                csvWriter.append(";");
//                csvWriter.append(String.valueOf(auxPKG));
//            }
        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                csvWriter.close();
//                interfaz.btnLoading.setVisible(false);
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(testUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(testUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(testUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(testUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new testUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel txtPath;
    // End of variables declaration//GEN-END:variables
}

class Data {

    public String data0;
    public int data1;

    Data(String data0, int data1) {
        this.data0 = data0;
        this.data1 = data1;
    }
}
