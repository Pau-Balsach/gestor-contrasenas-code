package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.service.AuthService;
import java.util.Arrays;

public class MasterPassword extends javax.swing.JFrame {

    private final boolean esPrimeraVez;

    // Protección anti brute-force en cliente
    private static int intentosFallidos = 0;
    private static long bloqueoHastaMs = 0;
    private static final int MAX_INTENTOS_SIN_BLOQUEO = 5;
    private static final long BLOQUEO_BASE_MS = 5_000L;
    private static final long BLOQUEO_MAX_MS = 300_000L;

    public MasterPassword(boolean esPrimeraVez) {
        this.esPrimeraVez = esPrimeraVez;
        initComponents();
        configurarVista();
    }

    private void configurarVista() {
        if (esPrimeraVez) {
            jLabel1.setText("Crear contraseña maestra");
            jLabel4.setVisible(true);
            txtConfirmar.setVisible(true);
            jLabel5.setText("Esta contraseña protege tus datos. No la olvides, no se puede recuperar.");
        } else {
            jLabel1.setText("Introduce tu contraseña maestra");
            jLabel4.setVisible(false);
            txtConfirmar.setVisible(false);
            jLabel5.setText("Necesitas tu contraseña maestra para acceder a tus datos.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtMaster = new javax.swing.JPasswordField();
        txtConfirmar = new javax.swing.JPasswordField();
        btnAceptar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 32));
        jLabel1.setText("Contraseña maestra");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel2.setText("Contraseña maestra:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel3.setText("Confirmar:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel4.setText("Confirmar:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel5.setForeground(new java.awt.Color(150, 150, 150));
        jLabel5.setText("");

        btnAceptar.setFont(new java.awt.Font("Segoe UI", 0, 18));
        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(this::btnAceptarActionPerformed);

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(btnCancelar)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel2))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(txtMaster, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                                        .addComponent(txtConfirmar))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(txtMaster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(25, 25, 25)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(txtConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                                .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(btnCancelar)
                                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel1)
                                .addGap(20, 20, 20)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    private static boolean isBlank(char[] chars) {
        for (char c : chars) {
            if (!Character.isWhitespace(c)) return false;
        }
        return true;
    }

    private static boolean equalsChars(char[] a, char[] b) {
        return Arrays.equals(a, b);
    }

    private static long calcularBackoffMs(int intentos) {
        int exceso = Math.max(0, intentos - MAX_INTENTOS_SIN_BLOQUEO);
        if (exceso == 0) return 0;
        long backoff = BLOQUEO_BASE_MS * (1L << Math.min(exceso - 1, 10));
        return Math.min(backoff, BLOQUEO_MAX_MS);
    }

    private static String tiempoRestante(long restanteMs) {
        long segundos = Math.max(1, (restanteMs + 999) / 1000);
        return segundos + " segundos";
    }

    private void registrarFallo() {
        intentosFallidos++;
        long backoff = calcularBackoffMs(intentosFallidos);
        if (backoff > 0) {
            bloqueoHastaMs = System.currentTimeMillis() + backoff;
        }
    }

    private void reiniciarProteccionBruteforce() {
        intentosFallidos = 0;
        bloqueoHastaMs = 0;
    }

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {
        char[] masterChars = txtMaster.getPassword();
        char[] confirmarChars = txtConfirmar.getPassword();

        try {
            long ahora = System.currentTimeMillis();
            if (bloqueoHastaMs > ahora) {
                long restante = bloqueoHastaMs - ahora;
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Demasiados intentos. Espera " + tiempoRestante(restante) + " antes de reintentar.");
                return;
            }

            if (masterChars.length == 0 || isBlank(masterChars)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Introduce la contraseña maestra.");
                return;
            }

            if (esPrimeraVez) {
                if (!equalsChars(masterChars, confirmarChars)) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
                    return;
                }
                if (masterChars.length < 8) {
                    javax.swing.JOptionPane.showMessageDialog(this, "La contraseña maestra debe tener al menos 8 caracteres.");
                    return;
                }
            }

            String error = AuthService.configurarMasterKey(masterChars);
            if (error != null) {
                registrarFallo();
                javax.swing.JOptionPane.showMessageDialog(this, error);
                return;
            }

            reiniciarProteccionBruteforce();
            MenuPrincipal principal = new MenuPrincipal();
            principal.setVisible(true);
            principal.setLocationRelativeTo(null);
            dispose();

        } finally {
            Arrays.fill(masterChars, '\0');
            Arrays.fill(confirmarChars, '\0');
            txtMaster.setText("");
            txtConfirmar.setText("");
        }
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
        login login = new login();
        login.setVisible(true);
        login.setLocationRelativeTo(null);
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField txtConfirmar;
    private javax.swing.JPasswordField txtMaster;
    // End of variables declaration
}