
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author root
 */
public class InterfazFusion extends javax.swing.JFrame {

    /**
     * Creates new form InterfazFusion
     */
    Connection connection = null;
    PreparedStatement ps = null;
    ButtonGroup framework = new ButtonGroup();
    
    public static String twiceFrameworks = "false";
    public String path = "";
    public String url = "";
    public String shPath = "";
    public String srcPower = null;
    public String srcJrapl = null;

    public InterfazFusion() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Prototipo Fusion");
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnLoading.setVisible(false);
    }

    public boolean nuevoRegistro(String app, String date, String rutaPower, String rutaJRAPL) throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbRegistro", "root", "12345");
            String sentencia = "INSERT INTO history (NombreApp, Fecha, RutaPowerAPI, RutaJrapl) "
                    + "VALUES (?,?,?,?)";
            ps = connection.prepareStatement(sentencia);
            ps.setString(1, app);
            ps.setString(2, date);
            if (rutaPower == null) {
                ps.setString(3, null);
            }
            ps.setString(3, rutaPower);
            if (rutaJRAPL == null) {
                ps.setString(4, null);
            }
            ps.setString(4, rutaJRAPL);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ha existido un error al guardar el registro de la ejecución");
            return false;
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(InterfazFusion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtPath = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        rbtnJRAPL = new javax.swing.JRadioButton();
        rbtnPower = new javax.swing.JRadioButton();
        btnStart = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtStatus = new javax.swing.JTextArea();
        btnResults = new javax.swing.JButton();
        btnLoading = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtPath.setText("/home/roberth/Desktop/Hilo.jar");

        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setText("Prototipo Fusion");

        jLabel2.setText("Seleccione la ruta de la aplicacion a medir");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Seleccione un framework"));

        rbtnJRAPL.setText("jRAPL");

        rbtnPower.setText("PowerAPI");

        btnStart.setText("Empezar medición");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnStop.setText("Detener medición");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        btnPause.setText("Pausar medición");
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtnPower)
                    .addComponent(rbtnJRAPL))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnPause, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStart, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addComponent(btnStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(rbtnJRAPL)
                .addGap(18, 18, 18)
                .addComponent(rbtnPower)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(btnStart)
                .addGap(12, 12, 12)
                .addComponent(btnPause)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnStop)
                .addContainerGap())
        );

        txtStatus.setColumns(20);
        txtStatus.setRows(5);
        txtStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setViewportView(txtStatus);

        btnResults.setText("Ver Resultados");
        btnResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResultsActionPerformed(evt);
            }
        });

        btnLoading.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ajax-loader.gif"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnResults)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jScrollPane1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(24, 24, 24)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jButton1)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtPath, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(32, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(207, 207, 207))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(txtPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnResults)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChoser = new JFileChooser();
        fileChoser.setCurrentDirectory(new File("/home/roberth/Desktop"));
        fileChoser.setFileFilter(new FileNameExtensionFilter("Jar Files", "jar"));
        int pathChoosed = fileChoser.showDialog(null, "Select file");
        if (pathChoosed != 1) {
            txtPath.setText(fileChoser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        String strFileTested = txtPath.getText();
        EnergyCheckUtils.pathApp = strFileTested;
        EnergyCheckUtils.jraplRepeat = true;
        EnergyCheckUtils energy = new EnergyCheckUtils();
        File fileTested = new File(strFileTested);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        if (paused == true) {
            EnergyCheckUtils.jraplRepeat = true;
        }
        if (paused == false) {
            if (fileTested.exists()) {
                if (rbtnJRAPL.isSelected() && rbtnPower.isSelected()) {
                    try {
                        twiceFrameworks = "true";
                        JOptionPane.showMessageDialog(null, "Ambos");
                        strFileTested = txtPath.getText();
                        whenStarted();
                        twiceFrameworks = "true";
                        energy.framework(strFileTested);
                        while (EnergyCheckUtils.lock == true) {
                            System.out.println("1");
                        }
                        EnergyCheckUtils.jraplRepeat = true;
                        whenFinished();
                        exec.submit(powerAPIWorker);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(InterfazFusion.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(InterfazFusion.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    System.out.println("exec if======================================================");
//                    energy.powerAPI(txtPath.getText());
                    load();
                    
                }
                if (rbtnJRAPL.isSelected() && !rbtnPower.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Solo JRAPL");
                    btnLoading.setVisible(true);
                    txtStatus.setText("La medición ha empezado...\nObteniendo datos...");
//                    exec.submit(worker);
                }
                if (rbtnPower.isSelected() && !rbtnJRAPL.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Solo Power");
                    btnLoading.setVisible(true);
                    exec.submit(powerAPIWorker);
                }
            }

            if (!fileTested.exists()) {
                JOptionPane.showMessageDialog(this, "La ruta especificada del archivo a medir no es válido", "Aviso!", JOptionPane.ERROR_MESSAGE);
            }

        }
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        paused = true;
        btnStart.setEnabled(true);
        txtStatus.setText("La medición se ha pausado");
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        txtStatus.setText("La medición se ha detenido");
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResultsActionPerformed
        try {
            Process nav = null;
            if (rbtnJRAPL.isSelected()) {
                nav = Runtime.getRuntime().exec("sh /home/roberth/browser.sh");
            }
            if (rbtnPower.isSelected()) {
                nav = Runtime.getRuntime().exec("sh /home/roberth/browser2.sh");
            }

            nav.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(InterfazFusion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(InterfazFusion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnResultsActionPerformed
    boolean paused = false;

    Runnable powerAPIWorker = new Runnable() {
        EnergyCheckUtils energy = new EnergyCheckUtils();
        @Override
        public void run() {
            System.out.println("ACTIVATEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
            energy.powerAPI(txtPath.getText());
            load();
            twiceFrameworks = "false";
        }

    };

    public static void data() {
        txtStatus.setText("La medición ha empezado...\nObteniendo datos..."
                + "\nHa finalizado el monitoreo de la aplicación...\nGenerando archivos...\n");

    }

    public void load() {
        System.out.println("Ocultando gif");
        btnLoading.setVisible(false);
        btnResults.setVisible(true);
    }

//    Runnable worker = new Runnable() {
//        EnergyCheckUtils energy = new EnergyCheckUtils();
//        @Override
//        public void run() {
//            try {
//                String strFileTested = txtPath.getText();
//                whenStarted();
//                twiceFrameworks = "true";
//                energy.framework(strFileTested);
//                while (EnergyCheckUtils.lock == true) {
//                    System.out.println("1");
//                }
//                EnergyCheckUtils.flag = true;
//                whenFinished();
////                twiceFrameworks = "false";
////                if (!fileTested.exists()) {
////                    JOptionPane.showMessageDialog(null, "La ruta especificada del archivo a medir no es válido", "Aviso!", JOptionPane.ERROR_MESSAGE);
////                }
//            } catch (InterruptedException ex) {
//                Logger.getLogger(InterfazFusion.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(InterfazFusion.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    };

    public void whenStarted() {
        btnPause.setEnabled(true);
        btnStop.setEnabled(true);
        btnStart.setEnabled(false);
    }

    public void whenFinished() {
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnStart.setEnabled(true);
        btnLoading.setVisible(false);
    }

    public void changeValues() {
        if (EnergyCheckUtils.jraplRepeat == true) {
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnPause.setEnabled(true);
            txtStatus.setText("La medición ha finalizado con exito");
        }
        if (EnergyCheckUtils.jraplRepeat == false) {
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnPause.setEnabled(false);
            txtStatus.setText("");
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
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Metal".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InterfazFusion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazFusion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazFusion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazFusion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InterfazFusion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnLoading;
    private javax.swing.JButton btnPause;
    public javax.swing.JButton btnResults;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnStop;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JRadioButton rbtnJRAPL;
    public javax.swing.JRadioButton rbtnPower;
    private javax.swing.JTextField txtPath;
    public static javax.swing.JTextArea txtStatus;
    // End of variables declaration//GEN-END:variables
}
