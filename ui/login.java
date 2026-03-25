package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import java.util.Arrays;

public class login extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(login.class.getName());

    private static int loginFallidos = 0;
    private static long loginBloqueoHastaMs = 0;
    private static final int LOGIN_MAX_INTENTOS_SIN_BLOQUEO = 5;
    private static final long LOGIN_BLOQUEO_BASE_MS = 5_000L;
    private static final long LOGIN_BLOQUEO_MAX_MS = 300_000L;

    public login() {
        initComponents();
        setTitle("GestorContrasenyas - Login");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {


    // Bloqueador global de "beeps" por borrar en campos vacíos
    java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
        if (e.getID() == java.awt.event.KeyEvent.KEY_PRESSED && e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE) {
            Object source = e.getSource();
            if (source instanceof javax.swing.JTextField) {
                javax.swing.JTextField tf = (javax.swing.JTextField) source;
                if (tf.getText().isEmpty()) {
                    e.consume(); // Mata el evento para que no suene el "ding"
                    return true;
                }
            }
        }
        return false;
    });
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtUser = new javax.swing.JTextField();
        btnLogin = new javax.swing.JButton();
        btnSignin = new javax.swing.JButton();
        btnResetPasswd = new javax.swing.JButton();


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 48));
        jLabel1.setText("Iniciar sesión");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel2.setText("Usuario:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel3.setText("Contrasenya:");

        txtUser.addActionListener(this::txtUserActionPerformed);

        btnLogin.setText("Iniciar sesión");
        btnLogin.addActionListener(this::btnLoginActionPerformed);

        btnSignin.setText("Registrarse");
        btnSignin.addActionListener(this::btnSigninActionPerformed);

        btnResetPasswd.setText("¿Olvidaste tu contraseña?");
        btnResetPasswd.addActionListener(this::btnResetPasswdActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnResetPasswd)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSignin, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(174, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSignin, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnResetPasswd)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(201, 201, 201)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(108, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
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

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void btnResetPasswdActionPerformed(java.awt.event.ActionEvent evt) {
        String email = txtUser.getText().trim();

        if (email.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Introduce tu email primero.");
            return;
        }

        String error = com.mycompany.gestorcontrasenyas.service.AuthService.enviarRecuperacion(email);
        if (error == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Correo de recuperación enviado. Revisa tu bandeja de entrada.");
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, error);
        }
    }

    private static long calcularLoginBackoffMs(int intentos) {
        int exceso = Math.max(0, intentos - LOGIN_MAX_INTENTOS_SIN_BLOQUEO);
        if (exceso == 0) return 0;
        long backoff = LOGIN_BLOQUEO_BASE_MS * (1L << Math.min(exceso - 1, 10));
        return Math.min(backoff, LOGIN_BLOQUEO_MAX_MS);
    }

    private static String tiempoLoginRestante(long restanteMs) {
        long segundos = Math.max(1, (restanteMs + 999) / 1000);
        return segundos + " segundos";
    }

    private static void registrarLoginFallido() {
        loginFallidos++;
        long backoff = calcularLoginBackoffMs(loginFallidos);
        if (backoff > 0) {
            loginBloqueoHastaMs = System.currentTimeMillis() + backoff;
        }
    }

    private static void reiniciarLoginBruteforce() {
        loginFallidos = 0;
        loginBloqueoHastaMs = 0;
    }

    private static boolean cumplePoliticaPassword(char[] passwordChars) {
        if (passwordChars == null || passwordChars.length < 10) return false;
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : passwordChars) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String email    = txtUser.getText().trim();
        char[] pwdChars = txtPassword.getPassword();

        try {
            long ahora = System.currentTimeMillis();
            if (loginBloqueoHastaMs > ahora) {
                long restante = loginBloqueoHastaMs - ahora;
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Demasiados intentos. Espera " + tiempoLoginRestante(restante) + " antes de reintentar.");
                return;
            }

            if (email.isEmpty() || pwdChars.length == 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Rellena todos los campos.");
                return;
            }

            String error = com.mycompany.gestorcontrasenyas.service.AuthService.login(email, pwdChars);

            if (error != null) {
                registrarLoginFallido();
                javax.swing.JOptionPane.showMessageDialog(this, error);
                return;
            }

            reiniciarLoginBruteforce();
            String userId       = SupabaseAuth.getUserId();
            boolean tieneMaster = com.mycompany.gestorcontrasenyas.service.AuthService.tieneMasterKey(userId);

            MasterPassword master = new MasterPassword(!tieneMaster);
            master.setVisible(true);
            master.setLocationRelativeTo(null);
            dispose();

        } finally {
            Arrays.fill(pwdChars, '\0');
            txtPassword.setText("");
        }
    }

    private void btnSigninActionPerformed(java.awt.event.ActionEvent evt) {
        String email    = txtUser.getText().trim();
        char[] pwdChars = txtPassword.getPassword();

        try {
            if (email.isEmpty() || pwdChars.length == 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Rellena todos los campos.");
                return;
            }

            if (!cumplePoliticaPassword(pwdChars)) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "La contraseña debe tener 10+ caracteres e incluir mayúscula, minúscula, número y símbolo.");
                return;
            }

            String error = com.mycompany.gestorcontrasenyas.service.AuthService.signup(email, pwdChars);

            if (error != null) {
                javax.swing.JOptionPane.showMessageDialog(this, error);
                return;
            }

            javax.swing.JOptionPane.showMessageDialog(this, "Registro correcto! Revisa tu email para verificar la cuenta.");

        } finally {
            Arrays.fill(pwdChars, '\0');
            txtPassword.setText("");
        }
    }
    

    // Variables declaration - do not modify
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnResetPasswd;
    private javax.swing.JButton btnSignin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUser;
    // End of variables declaration
}