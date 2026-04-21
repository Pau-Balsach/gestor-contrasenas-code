package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.model.Cuenta;
import com.mycompany.gestorcontrasenyas.service.CategoriaService;
import com.mycompany.gestorcontrasenyas.service.CuentaService;
import com.mycompany.gestorcontrasenyas.service.RiotService;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class ConsultarUsuario extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ConsultarUsuario.class.getName());
    private static final int CLIPBOARD_CLEAR_SECONDS = 30;
    private ScheduledExecutorService clipboardCleaner;

    private static final Color C_BG        = new Color(0xF7F8FA);
    private static final Color C_PANEL     = Color.WHITE;
    private static final Color C_NAVY      = new Color(0x1A2B4A);
    private static final Color C_GOLD      = new Color(0xC8A951);
    private static final Color C_BORDER    = new Color(0xDDE1E9);
    private static final Color C_TEXT      = new Color(0x1A2B4A);
    private static final Color C_MUTED     = new Color(0x7A869A);
    private static final Color C_RED       = new Color(0xDC2626);
    private static final Color C_BLUE      = new Color(0x1D4ED8);
    private static final Color C_GREEN     = new Color(0x059669);
    private static final Color C_ROW_ALT   = new Color(0xF8FAFC);
    private static final Color C_ROW_SEL   = new Color(0xEEF2FF);

    public ConsultarUsuario() {
        initComponents();
        setTitle("GestorContrasenyas - Consultar cuentas");
    }

    private void cargarCategoriasFiltro() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Todas las categorías");
        List<String> nombres = CategoriaService.obtenerNombres();
        for (String nombre : nombres) model.addElement(nombre);
        cmbCategoria.setModel(model);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1             = new JPanel();
        headerPanel         = new JPanel();
        jLabel1             = new JLabel();
        toolbarPanel        = new JPanel();
        jLabel2             = new JLabel();
        cmbCategoria        = new JComboBox<>();
        lblCargando         = new JLabel();
        jScrollPane1        = new JScrollPane();
        tblUsuariosGuardados = new JTable();
        actionPanel         = new JPanel();
        btnCopiar           = new JButton();
        btnEditar           = new JButton();
        btnEliminar         = new JButton();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowOpened(java.awt.event.WindowEvent evt) {
                cargarCategoriasFiltro();
                cargarTabla();
            }
        });

        // ── Panel principal ───────────────────────────────────────────
        jPanel1.setBackground(C_BG);
        jPanel1.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────
        headerPanel.setBackground(C_NAVY);
        headerPanel.setPreferredSize(new Dimension(780, 80));
        headerPanel.setLayout(new GridBagLayout());

        JPanel hInner = new JPanel();
        hInner.setOpaque(false);
        hInner.setLayout(new BoxLayout(hInner, BoxLayout.Y_AXIS));

        jLabel1.setFont(new Font("Georgia", Font.PLAIN, 22));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setText("Cuentas guardadas");
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
        hInner.add(Box.createVerticalStrut(5));
        hInner.add(sep);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 32, 0, 32);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        headerPanel.add(hInner, gbc);
        jPanel1.add(headerPanel, BorderLayout.NORTH);

        // ── Toolbar de filtro ─────────────────────────────────────────
        toolbarPanel.setBackground(C_PANEL);
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));
        toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));

        jLabel2.setText("Filtrar por categoría:");
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel2.setForeground(C_TEXT);

        cmbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbCategoria.setBackground(Color.WHITE);
        cmbCategoria.setBorder(new login.RoundedBorder(6, C_BORDER));
        cmbCategoria.setPreferredSize(new Dimension(220, 34));
        cmbCategoria.addActionListener(this::cmbCategoriaActionPerformed);

        lblCargando.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCargando.setForeground(C_MUTED);
        lblCargando.setText("");

        toolbarPanel.add(jLabel2);
        toolbarPanel.add(cmbCategoria);
        toolbarPanel.add(lblCargando);
        jPanel1.add(toolbarPanel, BorderLayout.NORTH);

        // Necesitamos un wrapper para meter header + toolbar arriba
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(headerPanel, BorderLayout.NORTH);
        topWrapper.add(toolbarPanel, BorderLayout.SOUTH);
        jPanel1.add(topWrapper, BorderLayout.NORTH);

        // ── Tabla ─────────────────────────────────────────────────────
        styleTable(tblUsuariosGuardados);
        tblUsuariosGuardados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosGuardadosMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(tblUsuariosGuardados);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_PANEL);
        jScrollPane1.setBackground(C_PANEL);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(C_BG);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));
        tableWrapper.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.setBorder(BorderFactory.createCompoundBorder(
            new login.RoundedBorder(8, C_BORDER),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        jPanel1.add(tableWrapper, BorderLayout.CENTER);

        // ── Panel de acciones ─────────────────────────────────────────
        actionPanel.setBackground(C_BG);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(14, 24, 20, 24));
        actionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        styleActionButton(btnCopiar, "📋  Copiar...", C_GREEN);
        btnCopiar.addActionListener(this::btnCopiarActionPerformed);

        styleActionButton(btnEditar, "✏  Editar", C_BLUE);
        btnEditar.addActionListener(this::btnEditarActionPerformed);

        styleActionButton(btnEliminar, "🗑  Eliminar", C_RED);
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        actionPanel.add(btnCopiar);
        actionPanel.add(btnEditar);
        actionPanel.add(btnEliminar);
        jPanel1.add(actionPanel, BorderLayout.SOUTH);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, 780, 780, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, 580, 580, Short.MAX_VALUE));

        pack();
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(C_TEXT);
        table.setBackground(C_PANEL);
        table.setRowHeight(38);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(C_BORDER);
        table.setSelectionBackground(C_ROW_SEL);
        table.setSelectionForeground(C_NAVY);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(C_MUTED);
        header.setBackground(new Color(0xF1F5F9));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        header.setPreferredSize(new Dimension(0, 38));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Renderer alternado
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!sel) c.setBackground(row % 2 == 0 ? C_PANEL : C_ROW_ALT);
                return c;
            }
        });
    }

    private void styleActionButton(JButton btn, String text, Color color) {
        btn.setText(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new login.RoundedBorder(7, color),
            BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        Color hoverBg = new Color(
            Math.min(255, color.getRed() + 230),
            Math.min(255, color.getGreen() + 230),
            Math.min(255, color.getBlue() + 230)
        );
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hoverBg); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(Color.WHITE); }
        });
    }

    // ── Lógica sin modificar ──────────────────────────────────────────
    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {
        cargarTabla();
    }

    private void cargarTabla() {
        String categoriaSeleccionada = (String) cmbCategoria.getSelectedItem();
        // Normalizar el valor "Todas las categorías" al "-" interno
        String filtro = "Todas las categorías".equals(categoriaSeleccionada) ? "-" : categoriaSeleccionada;

        boolean filtroEsRiot = CategoriaService.obtenerCategorias().stream()
                .anyMatch(c -> c.getNombre().equals(filtro) && c.isEsRiot());

        DefaultTableModel tabla = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        if (filtroEsRiot) {
            tabla.setColumnIdentifiers(new String[]{"Categoría", "Usuario", "Contraseña", "Riot ID", "Rango Valorant", "Rango LoL"});
        } else {
            tabla.setColumnIdentifiers(new String[]{"Categoría", "Usuario", "Contraseña"});
        }
        tblUsuariosGuardados.setModel(tabla);
        styleTable(tblUsuariosGuardados);

        cmbCategoria.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnEditar.setEnabled(false);
        lblCargando.setText("⏳ Cargando...");

        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                List<Cuenta> listaCuentas = CuentaService.obtenerCuentas();
                for (Cuenta cuenta : listaCuentas) {
                    if (!filtro.equals("-") && !cuenta.getCategoria().equals(filtro)) continue;
                    if (filtroEsRiot) {
                        String riotId        = cuenta.getRiotId();
                        String rangoValorant = "Sin rango";
                        String rangoLol      = "Sin rango";
                        if (riotId != null && !riotId.isEmpty()) {
                            String puuid = RiotService.obtenerPuuid(riotId);
                            if (puuid != null) {
                                rangoValorant = RiotService.obtenerRangoValorant(riotId);
                                rangoLol      = RiotService.obtenerRangoLol(puuid);
                            }
                        }
                        publish(new Object[]{cuenta.getCategoria(), cuenta.getUsuario(), cuenta.getPassword(), riotId, rangoValorant, rangoLol});
                    } else {
                        publish(new Object[]{cuenta.getCategoria(), cuenta.getUsuario(), cuenta.getPassword()});
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> filas) {
                DefaultTableModel modelo = (DefaultTableModel) tblUsuariosGuardados.getModel();
                for (Object[] fila : filas) modelo.addRow(fila);
            }

            @Override
            protected void done() {
                cmbCategoria.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnEditar.setEnabled(true);
                lblCargando.setText("");
            }
        };
        worker.execute();
    }

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = tblUsuariosGuardados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una fila para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de que deseas eliminar esta cuenta? Esta acción no se puede deshacer.",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) return;
        try {
            CuentaService.eliminarCuenta(CuentaService.getCuentaid(
                String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 1))));
            cargarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error de Servidor", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = tblUsuariosGuardados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una fila de la tabla.");
            return;
        }
        String nombreActual  = String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 1));
        String categoriaFila = String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 0));
        boolean esRiot       = CategoriaService.obtenerCategorias().stream()
                .anyMatch(c -> c.getNombre().equals(categoriaFila) && c.isEsRiot());

        JTextField txtNuevoUsuario   = new JTextField(nombreActual);
        JPasswordField txtNuevoPassword = new JPasswordField();
        JTextField txtNuevoRiotId    = new JTextField();
        if (esRiot && tblUsuariosGuardados.getColumnCount() > 3) {
            Object riotIdActual = tblUsuariosGuardados.getValueAt(filaSeleccionada, 3);
            txtNuevoRiotId.setText(riotIdActual != null ? riotIdActual.toString() : "");
        }

        Object[] message = esRiot
            ? new Object[]{"Nuevo Nombre de Usuario:", txtNuevoUsuario, "Nueva Contraseña:", txtNuevoPassword, "Nuevo Riot ID:", txtNuevoRiotId}
            : new Object[]{"Nuevo Nombre de Usuario:", txtNuevoUsuario, "Nueva Contraseña:", txtNuevoPassword};

        int option = JOptionPane.showConfirmDialog(this, message, "Editar Cuenta", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;

        String nuevoUser   = txtNuevoUsuario.getText().trim();
        char[] nuevoPass   = txtNuevoPassword.getPassword();
        String nuevoRiotId = esRiot ? txtNuevoRiotId.getText().trim() : null;

        try {
            if (nuevoUser.isEmpty() || nuevoPass.length == 0) {
                JOptionPane.showMessageDialog(this, "Error: Los campos no pueden estar vacíos.", "Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (esRiot && (nuevoRiotId == null || nuevoRiotId.isEmpty())) {
                JOptionPane.showMessageDialog(this, "Error: El Riot ID no puede estar vacío.", "Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String id = CuentaService.getCuentaid(nombreActual);
            if (id == null || id.isBlank()) {
                JOptionPane.showMessageDialog(this, "No se pudo localizar la cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            CuentaService.actualizarCuenta(id, nuevoUser, nuevoPass, nuevoRiotId);
            JOptionPane.showMessageDialog(this, "Cuenta actualizada correctamente.");
            cargarTabla();
        } finally {
            java.util.Arrays.fill(nuevoPass, '\0');
        }
    }

    private void tblUsuariosGuardadosMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() != 2) return;
        mostrarDialogoCopiar();
    }

    private void btnCopiarActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarDialogoCopiar();
    }

    private void mostrarDialogoCopiar() {
        int filaSeleccionada = tblUsuariosGuardados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String usuario  = String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 1));
        String cuentaId = CuentaService.getCuentaid(usuario);
        if (cuentaId == null || cuentaId.isBlank()) {
            JOptionPane.showMessageDialog(this, "No se pudo localizar la cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialogo = new JDialog(this, "Copiar credenciales", true);
        dialogo.setLayout(new BorderLayout(12, 12));
        dialogo.setResizable(false);
        dialogo.getRootPane().setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel lblInfo = new JLabel("<html><b style='font-size:14px'>" + usuario + "</b><br>"
            + "<span style='color:#7A869A;font-size:11px'>Elige qué quieres copiar. Se borrará en "
            + CLIPBOARD_CLEAR_SECONDS + " s.</span></html>");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(14, 18, 8, 18));
        dialogo.add(lblInfo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelBotones.setBackground(Color.WHITE);

        JButton btnCopiarUsuario = new JButton("📋  Usuario");
        styleActionButton(btnCopiarUsuario, "📋  Usuario", C_BLUE);
        btnCopiarUsuario.addActionListener(e -> {
            copiarAlPortapapeles(usuario);
            dialogo.dispose();
            mostrarFeedbackCopia("Usuario copiado · se borrará en " + CLIPBOARD_CLEAR_SECONDS + "s");
        });

        JButton btnCopiarPassword = new JButton("🔑  Contraseña");
        styleActionButton(btnCopiarPassword, "🔑  Contraseña", C_GREEN);
        btnCopiarPassword.addActionListener(e -> {
            String password = CuentaService.obtenerPasswordDescifradaPorId(cuentaId);
            if (password == null) {
                JOptionPane.showMessageDialog(dialogo, "No se pudo recuperar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            copiarAlPortapapeles(password);
            dialogo.dispose();
            mostrarFeedbackCopia("Contraseña copiada · se borrará en " + CLIPBOARD_CLEAR_SECONDS + "s");
        });

        JButton btnCerrar = new JButton("Cancelar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCerrar.setForeground(C_MUTED);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dialogo.dispose());

        panelBotones.add(btnCopiarUsuario);
        panelBotones.add(btnCopiarPassword);
        panelBotones.add(btnCerrar);
        dialogo.add(panelBotones, BorderLayout.CENTER);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void copiarAlPortapapeles(String texto) {
        if (clipboardCleaner != null && !clipboardCleaner.isShutdown()) clipboardCleaner.shutdownNow();
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
            .setContents(new StringSelection(texto), null);
        clipboardCleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "clipboard-cleaner");
            t.setDaemon(true);
            return t;
        });
        clipboardCleaner.schedule(() ->
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(""), null),
            CLIPBOARD_CLEAR_SECONDS, TimeUnit.SECONDS);
    }

    private void mostrarFeedbackCopia(String mensaje) {
        lblCargando.setForeground(C_GREEN);
        lblCargando.setText("✓ " + mensaje);
        javax.swing.Timer timer = new javax.swing.Timer(3000, e -> {
            lblCargando.setText("");
            lblCargando.setForeground(C_MUTED);
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Variables declaration
    private JButton btnEliminar, btnEditar, btnCopiar;
    private JComboBox<String> cmbCategoria;
    private JLabel jLabel1, jLabel2, lblCargando;
    private JPanel jPanel1, headerPanel, toolbarPanel, actionPanel;
    private JScrollPane jScrollPane1;
    private JTable tblUsuariosGuardados;
}