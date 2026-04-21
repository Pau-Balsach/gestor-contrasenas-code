package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.model.Categoria;
import com.mycompany.gestorcontrasenyas.service.CategoriaService;
import com.mycompany.gestorcontrasenyas.service.CuentaService;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class NuevoUsuario extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(NuevoUsuario.class.getName());

    private Categoria categoriaSeleccionada = null;

    private static final Color C_BG      = new Color(0xF7F8FA);
    private static final Color C_PANEL   = Color.WHITE;
    private static final Color C_NAVY    = new Color(0x1A2B4A);
    private static final Color C_GOLD    = new Color(0xC8A951);
    private static final Color C_BORDER  = new Color(0xDDE1E9);
    private static final Color C_TEXT    = new Color(0x1A2B4A);
    private static final Color C_MUTED   = new Color(0x7A869A);
    private static final Color C_LINK    = new Color(0x2563EB);

    public NuevoUsuario() {
        initComponents();
        setTitle("GestorContrasenyas - Añadir cuenta");
        cargarCategorias();
    }

    private void cargarCategorias() {
        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
        model.addElement("— Selecciona categoría —");
        List<Categoria> lista = CategoriaService.obtenerCategorias();
        for (Categoria c : lista) model.addElement(c);
        cmbCategoria.setModel(model);
        categoriaSeleccionada = null;
        actualizarVisibilidadRiotId();
    }

    private void actualizarVisibilidadRiotId() {
        boolean esRiot = categoriaSeleccionada != null && categoriaSeleccionada.isEsRiot();
        jLabel5.setVisible(esRiot);
        txtRiotId.setVisible(esRiot);
        riotHintLabel.setVisible(esRiot);
        pack();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        UIManager.put("OptionPane.errorSound",       null);
        UIManager.put("OptionPane.informationSound", null);
        UIManager.put("OptionPane.questionSound",    null);
        UIManager.put("OptionPane.warningSound",     null);

        jPanel1        = new JPanel();
        headerPanel    = new JPanel();
        jLabel1        = new JLabel();
        jPanel2        = new JPanel();
        btnGuardar     = new JButton();
        btnAtras       = new JButton();
        btnNuevaCat    = new JButton();
        jLabel2        = new JLabel();
        jLabel3        = new JLabel();
        jLabel4        = new JLabel();
        jLabel5        = new JLabel();
        riotHintLabel  = new JLabel();
        txtUsuario     = new JTextField();
        txtPassword    = new JPasswordField();
        txtRiotId      = new JTextField();
        cmbCategoria   = new JComboBox<>();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // ── Layout principal ──────────────────────────────────────────
        jPanel1.setBackground(C_BG);
        jPanel1.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────
        headerPanel.setBackground(C_NAVY);
        headerPanel.setPreferredSize(new Dimension(480, 80));
        headerPanel.setLayout(new GridBagLayout());

        JPanel hInner = new JPanel();
        hInner.setOpaque(false);
        hInner.setLayout(new BoxLayout(hInner, BoxLayout.Y_AXIS));

        jLabel1.setFont(new Font("Georgia", Font.PLAIN, 22));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setText("Añadir nueva cuenta");
        jLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_GOLD); g.fillRect(0, 0, 40, 2);
            }
        };
        sep.setPreferredSize(new Dimension(40, 2));
        sep.setMaximumSize(new Dimension(40, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        hInner.add(jLabel1);
        hInner.add(Box.createVerticalStrut(6));
        hInner.add(sep);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 32, 0, 32);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        headerPanel.add(hInner, gbc);
        jPanel1.add(headerPanel, BorderLayout.NORTH);

        // ── Formulario ────────────────────────────────────────────────
        jPanel2.setBackground(C_PANEL);
        jPanel2.setBorder(BorderFactory.createEmptyBorder(28, 40, 24, 40));
        jPanel2.setLayout(new GridBagLayout());

        // Etiquetas
        jLabel2.setText("Usuario");
        jLabel3.setText("Contraseña");
        jLabel4.setText("Categoría");
        jLabel5.setText("Riot ID");
        styleLabel(jLabel2); styleLabel(jLabel3); styleLabel(jLabel4); styleLabel(jLabel5);

        riotHintLabel.setText("Formato: NombreJugador#TAG");
        riotHintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        riotHintLabel.setForeground(C_MUTED);
        riotHintLabel.setVisible(false);

        // Campos
        styleTextField(txtUsuario);
        styleTextField(txtPassword);
        txtPassword.setEchoChar('●');
        styleTextField(txtRiotId);

        // Combo
        cmbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbCategoria.setBackground(Color.WHITE);
        cmbCategoria.setForeground(C_TEXT);
        cmbCategoria.setBorder(new login.RoundedBorder(6, C_BORDER));
        cmbCategoria.setPreferredSize(new Dimension(180, 36));
        cmbCategoria.addActionListener(this::cmbCategoriaActionPerformed);

        // Botón nueva categoría
        btnNuevaCat.setText("+ Nueva categoría");
        btnNuevaCat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnNuevaCat.setForeground(C_LINK);
        btnNuevaCat.setBorderPainted(false);
        btnNuevaCat.setContentAreaFilled(false);
        btnNuevaCat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevaCat.setFocusPainted(false);
        btnNuevaCat.addActionListener(this::btnNuevaCatActionPerformed);

        // Divider
        JSeparator divider = new JSeparator();
        divider.setForeground(C_BORDER);

        // Botón guardar
        styleButtonPrimary(btnGuardar, "Guardar cuenta");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        // Botón atrás
        btnAtras.setText("← Volver");
        btnAtras.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAtras.setForeground(C_MUTED);
        btnAtras.setBorderPainted(false);
        btnAtras.setContentAreaFilled(false);
        btnAtras.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAtras.setFocusPainted(false);
        btnAtras.addActionListener(this::btnAtrasActionPerformed);

        // Fila combo + nueva categoría
        JPanel catRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        catRow.setOpaque(false);
        catRow.add(cmbCategoria);
        catRow.add(btnNuevaCat);

        // Grid layout
        GridBagConstraints f = new GridBagConstraints();
        f.fill = GridBagConstraints.HORIZONTAL;
        f.weightx = 1.0;
        f.gridx = 0;

        f.gridy = 0; f.insets = new Insets(0, 0, 5, 0); jPanel2.add(jLabel2, f);
        f.gridy = 1; f.insets = new Insets(0, 0, 20, 0); jPanel2.add(txtUsuario, f);
        f.gridy = 2; f.insets = new Insets(0, 0, 5, 0); jPanel2.add(jLabel3, f);
        f.gridy = 3; f.insets = new Insets(0, 0, 20, 0); jPanel2.add(txtPassword, f);
        f.gridy = 4; f.insets = new Insets(0, 0, 5, 0); jPanel2.add(jLabel4, f);
        f.gridy = 5; f.insets = new Insets(0, 0, 20, 0); jPanel2.add(catRow, f);
        f.gridy = 6; f.insets = new Insets(0, 0, 5, 0); jPanel2.add(jLabel5, f);
        f.gridy = 7; f.insets = new Insets(0, 0, 4, 0); jPanel2.add(txtRiotId, f);
        f.gridy = 8; f.insets = new Insets(0, 0, 16, 0); jPanel2.add(riotHintLabel, f);
        f.gridy = 9; f.insets = new Insets(4, 0, 0, 0); jPanel2.add(divider, f);
        f.gridy = 10; f.insets = new Insets(16, 0, 0, 0); jPanel2.add(btnGuardar, f);

        jPanel1.add(jPanel2, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(C_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 32, 12, 0));
        footer.add(btnAtras);
        jPanel1.add(footer, BorderLayout.SOUTH);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, 460, 460, 460));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }

    private void styleLabel(JLabel lbl) {
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(C_TEXT);
    }

    private void styleTextField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(C_TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new login.RoundedBorder(8, C_BORDER),
            BorderFactory.createEmptyBorder(8, 11, 8, 11)
        ));
        field.setPreferredSize(new Dimension(360, 40));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new login.RoundedBorder(8, C_NAVY),
                    BorderFactory.createEmptyBorder(8, 11, 8, 11)
                ));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new login.RoundedBorder(8, C_BORDER),
                    BorderFactory.createEmptyBorder(8, 11, 8, 11)
                ));
            }
        });
    }

    private void styleButtonPrimary(JButton btn, String text) {
        btn.setText(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(C_NAVY);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(360, 42));
        btn.setBorder(new login.RoundedBorder(8, C_NAVY));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(0x243D6B)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(C_NAVY); }
        });
    }

    // ── Acciones (sin modificar) ──────────────────────────────────────
    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {
        Object sel = cmbCategoria.getSelectedItem();
        categoriaSeleccionada = (sel instanceof Categoria) ? (Categoria) sel : null;
        actualizarVisibilidadRiotId();
    }

    private void btnNuevaCatActionPerformed(java.awt.event.ActionEvent evt) {
        JTextField txtNombre = new JTextField(16);
        JCheckBox chkRiot = new JCheckBox("Es cuenta de Riot Games (activa campo Riot ID)");
        JLabel lblAviso = new JLabel("<html><small>Las categorías son personales y solo las ves tú.</small></html>");
        JPanel panel = new JPanel(new java.awt.GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Nombre de la nueva categoría:"));
        panel.add(txtNombre);
        panel.add(chkRiot);
        panel.add(lblAviso);
        int opcion = JOptionPane.showConfirmDialog(this, panel, "Nueva categoría",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;
        String nombre = txtNombre.getText().trim();
        boolean esRiot = chkRiot.isSelected();
        String error = CategoriaService.crearCategoria(nombre, esRiot);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cargarCategorias();
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            Object item = cmbCategoria.getItemAt(i);
            if (item instanceof Categoria && ((Categoria) item).getNombre().equals(nombre)) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        String usuario  = txtUsuario.getText().trim();
        char[] pwdChars = txtPassword.getPassword();
        String riotId   = txtRiotId.getText().trim();
        try {
            if (categoriaSeleccionada == null || usuario.isEmpty() || pwdChars.length == 0) {
                JOptionPane.showMessageDialog(this, "Rellena todos los campos y elige una categoría.");
                return;
            }
            if (categoriaSeleccionada.isEsRiot() && riotId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduce el Riot ID.");
                return;
            }
            String nombreCategoria = categoriaSeleccionada.getNombre();
            String error = CuentaService.guardarCuenta(usuario, pwdChars, nombreCategoria,
                categoriaSeleccionada.isEsRiot() ? riotId : null);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error);
            } else {
                JOptionPane.showMessageDialog(this, "Cuenta guardada correctamente.");
                txtUsuario.setText("");
                txtPassword.setText("");
                txtRiotId.setText("");
                cmbCategoria.setSelectedIndex(0);
                categoriaSeleccionada = null;
                actualizarVisibilidadRiotId();
            }
        } finally {
            Arrays.fill(pwdChars, '\0');
        }
    }

    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    // Variables declaration
    private JButton btnAtras, btnGuardar, btnNuevaCat;
    private JComboBox<Object> cmbCategoria;
    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, riotHintLabel;
    private JPanel jPanel1, jPanel2, headerPanel;
    private JPasswordField txtPassword;
    private JTextField txtRiotId, txtUsuario;
}