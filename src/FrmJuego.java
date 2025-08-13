/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import dao.EstadisticasDAO;
import modelo.Estadisticas;

public class FrmJuego extends javax.swing.JFrame {
  int numFilas = 10;
    int numColumnas = 10;
    int numMinas = 20; // 2*L donde L=10, según el proyecto
    
    JButton[][] botonesTablero;
    TableroBuscaminas tableroBuscaminas;
    Estadisticas estadisticas;
    EstadisticasDAO estadisticasDAO;
    private javax.swing.JMenuItem menuEstadisticas;
    private javax.swing.JMenuItem menuSalir;
    
    private boolean juegoTerminado = false;

    public FrmJuego() {
    initComponents();
    estadisticasDAO = new EstadisticasDAO();  // PRIMERO crear el DAO
    estadisticas = estadisticasDAO.cargarEstadisticasTotales();  // DESPUÉS usarlo
    
    // Agregar menús dinámicamente
    agregarMenusDinamicos();
    
    mostrarEstadisticasIniciales();
    juegoNuevo();
    }
    
    /**
     * Agrega los menús de Estadísticas y Salir dinámicamente
     */
    private void agregarMenusDinamicos() {
        // Agregar menú Estadísticas
        menuEstadisticas = new javax.swing.JMenuItem();
        menuEstadisticas.setText("Estadísticas");
        menuEstadisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEstadisticasActionPerformed(evt);
            }
        });
        jMenu1.add(menuEstadisticas);

        // Agregar menú Salir
        menuSalir = new javax.swing.JMenuItem();
        menuSalir.setText("Salir");
        menuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSalirActionPerformed(evt);
            }
        });
        jMenu1.add(menuSalir);
    }
    
    /**
     * Muestra las estadísticas al inicio del juego
     */
    private void mostrarEstadisticasIniciales() {
        estadisticas.mostrarEstadisticas();
    }
    
    /**
     * Descarga los controles del tablero anterior
     */
    void descargarControles() {
        if (botonesTablero != null) {
            for (int i = 0; i < botonesTablero.length; i++) {
                for (int j = 0; j < botonesTablero[i].length; j++) {
                    if (botonesTablero[i][j] != null) {
                        getContentPane().remove(botonesTablero[i][j]);
                    }
                }
            }
        }
    }
    
    /**
     * Inicia un nuevo juego
     */
    private void juegoNuevo() {
        juegoTerminado = false;
        descargarControles();
        cargarControles();
        crearTableroBuscaminas();
        repaint();
    }
    
    /**
     * Crea el tablero de buscaminas y configura los eventos
     */
    private void crearTableroBuscaminas() {
        tableroBuscaminas = new TableroBuscaminas(numFilas, numColumnas, numMinas);
        
        // Evento cuando se pierde la partida
        tableroBuscaminas.setEventoPartidaPerdida(new Consumer<List<Casilla>>() {
            @Override
            public void accept(List<Casilla> casillasConMinas) {
                juegoTerminado = true;
                for (Casilla casillaConMina : casillasConMinas) {
                    botonesTablero[casillaConMina.getPosFila()][casillaConMina.getPosColumna()].setText("💣");
                }
                // Registrar derrota
                estadisticas.registrarDerrota();
                estadisticasDAO.guardarEstadistica(estadisticas.getJuegosJugados(), 
                                                 estadisticas.getJuegosGanados(), 
                                                 estadisticas.getJuegosPerdidos());
                
                // Mostrar mensaje de derrota
                JOptionPane.showMessageDialog(null, "¡Has perdido! 💥", "Juego Terminado", JOptionPane.ERROR_MESSAGE);
                preguntarNuevoJuego();
            }
        });
        
        // Evento cuando se gana la partida
        tableroBuscaminas.setEventoPartidaGanada(new Consumer<List<Casilla>>() {
            @Override
            public void accept(List<Casilla> casillasConMinas) {
                juegoTerminado = true;
                for (Casilla casillaConMina : casillasConMinas) {
                    botonesTablero[casillaConMina.getPosFila()][casillaConMina.getPosColumna()].setText("🚩");
                }
                // Registrar victoria
                estadisticas.registrarVictoria();
                estadisticasDAO.guardarEstadistica(estadisticas.getJuegosJugados(), 
                                                 estadisticas.getJuegosGanados(), 
                                                 estadisticas.getJuegosPerdidos());
                
                // Mostrar mensaje de victoria
                JOptionPane.showMessageDialog(null, "¡Felicidades! Has ganado! 🎉", "¡Victoria!", JOptionPane.INFORMATION_MESSAGE);
                preguntarNuevoJuego();
            }
        });
        
        // Evento cuando se abre una casilla
        tableroBuscaminas.setEventoCasillaAbierta(new Consumer<Casilla>() {
            @Override
            public void accept(Casilla casilla) {
                JButton boton = botonesTablero[casilla.getPosFila()][casilla.getPosColumna()];
                boton.setEnabled(false);
                String texto = casilla.getNumMinasAlrededor() == 0 ? "" : casilla.getNumMinasAlrededor() + "";
                boton.setText(texto);
            }
        });
    }
    
    /**
     * Pregunta al usuario si desea jugar de nuevo
     */
    private void preguntarNuevoJuego() {
        estadisticas.mostrarEstadisticas();
        int opcion = JOptionPane.showConfirmDialog(null, 
                                                  "¿Deseas jugar de nuevo?", 
                                                  "Nuevo Juego", 
                                                  JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            juegoNuevo();
        } else {
            System.exit(0);
        }
    }
    
    /**
     * Carga los controles (botones) del tablero
     */
    private void cargarControles() {
        int posXReferencia = 25;
        int posYReferencia = 25;
        int anchoControl = 30;
        int altoControl = 30;
        
        botonesTablero = new JButton[numFilas][numColumnas];
        for (int i = 0; i < botonesTablero.length; i++) {
            for (int j = 0; j < botonesTablero[i].length; j++) {
                botonesTablero[i][j] = new JButton();
                botonesTablero[i][j].setName(i + "," + j);
                botonesTablero[i][j].setBorder(null);
                
                // Posicionamiento de botones
                if (i == 0 && j == 0) {
                    botonesTablero[i][j].setBounds(posXReferencia, posYReferencia, anchoControl, altoControl);
                } else if (i == 0 && j != 0) {
                    botonesTablero[i][j].setBounds(
                            botonesTablero[i][j - 1].getX() + botonesTablero[i][j - 1].getWidth(),
                            posYReferencia, anchoControl, altoControl);
                } else {
                    botonesTablero[i][j].setBounds(
                            botonesTablero[i - 1][j].getX(),
                            botonesTablero[i - 1][j].getY() + botonesTablero[i - 1][j].getHeight(),
                            anchoControl, altoControl);
                }
                
                // Agregar listeners para clic izquierdo y derecho
                botonesTablero[i][j].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!juegoTerminado) {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                // Clic izquierdo - abrir casilla
                                btnClickIzquierdo(e);
                            } else if (SwingUtilities.isRightMouseButton(e)) {
                                // Clic derecho - marcar/desmarcar casilla
                                btnClickDerecho(e);
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}

                    @Override
                    public void mouseReleased(MouseEvent e) {}

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                
                getContentPane().add(botonesTablero[i][j]);
            }
        }
        
        // Redimensionar ventana
        this.setSize(botonesTablero[numFilas - 1][numColumnas - 1].getX() +
                    botonesTablero[numFilas - 1][numColumnas - 1].getWidth() + 50,
                    botonesTablero[numFilas - 1][numColumnas - 1].getY() +
                    botonesTablero[numFilas - 1][numColumnas - 1].getHeight() + 100);
    }
    
    /**
     * Maneja el clic izquierdo (abrir casilla)
     */
    private void btnClickIzquierdo(MouseEvent e) {
        JButton btn = (JButton) e.getSource();
        String[] coordenada = btn.getName().split(",");
        int posFila = Integer.parseInt(coordenada[0]);
        int posColumna = Integer.parseInt(coordenada[1]);
        
        tableroBuscaminas.seleccionarCasilla(posFila, posColumna);
    }
    
    /**
     * Maneja el clic derecho (marcar/desmarcar casilla)
     */
    private void btnClickDerecho(MouseEvent e) {
        JButton btn = (JButton) e.getSource();
        String[] coordenada = btn.getName().split(",");
        int posFila = Integer.parseInt(coordenada[0]);
        int posColumna = Integer.parseInt(coordenada[1]);
        
        tableroBuscaminas.alternarMarcado(posFila, posColumna);
        
        // Actualizar visualmente el botón
        Casilla casilla = tableroBuscaminas.getCasilla(posFila, posColumna);
        if (casilla.isMarcada()) {
            btn.setText("🚩"); // Marca con bandera
        } else {
            btn.setText(""); // Desmarca (quita la bandera)
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuNuevoJuego = new javax.swing.JMenuItem();
        menuTamano = new javax.swing.JMenuItem();
        menuNumMinas = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu1.setText("Juego");

        menuNuevoJuego.setText("Nuevo");
        menuNuevoJuego.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNuevoJuegoActionPerformed(evt);
            }
        });
        jMenu1.add(menuNuevoJuego);

        menuTamano.setText("Tamaño");
        menuTamano.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuTamanoActionPerformed(evt);
            }
        });
        jMenu1.add(menuTamano);

        menuNumMinas.setText("Numero Minas");
        menuNumMinas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNumMinasActionPerformed(evt);
            }
        });
        jMenu1.add(menuNumMinas);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 277, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuNuevoJuegoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNuevoJuegoActionPerformed
        juegoNuevo();
    }//GEN-LAST:event_menuNuevoJuegoActionPerformed

    private void menuTamanoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuTamanoActionPerformed
        try {
            String input = JOptionPane.showInputDialog("Digite tamaño de la matriz (n×n).\nDebe ser mayor a 2:");
            if (input != null && !input.trim().isEmpty()) {
                int num = Integer.parseInt(input.trim());
                if (num <= 2) {
                    JOptionPane.showMessageDialog(null, "El tamaño debe ser mayor a 2", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                this.numFilas = num;
                this.numColumnas = num;
                // Calcular número de minas según la fórmula del proyecto: 2*L
                this.numMinas = 2 * num;
                juegoNuevo();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_menuTamanoActionPerformed

    private void menuNumMinasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNumMinasActionPerformed
        try {
            int maxMinas = (numFilas * numColumnas) - 1;
            int minasRecomendadas = 2 * numFilas; // Según la fórmula del proyecto
            String input = JOptionPane.showInputDialog("Digite número de Minas.\n" +
                "Recomendado según proyecto (2*L): " + minasRecomendadas + "\n" +
                "Máximo permitido: " + maxMinas);
            if (input != null && !input.trim().isEmpty()) {
                int num = Integer.parseInt(input.trim());
                if (num <= 0 || num >= maxMinas) {
                    JOptionPane.showMessageDialog(null,
                        "El número de minas debe estar entre 1 y " + maxMinas,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                this.numMinas = num;
                juegoNuevo();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_menuNumMinasActionPerformed
    private void menuEstadisticasActionPerformed(java.awt.event.ActionEvent evt) {
       if (estadisticas != null) {
            estadisticas.mostrarEstadisticas();
        } else {
            JOptionPane.showMessageDialog(null, "No hay estadísticas disponibles todavía.");
        }
    }

    private void menuSalirActionPerformed(java.awt.event.ActionEvent evt) {
        int opcion = JOptionPane.showConfirmDialog(null, 
                                                  "¿Está seguro que desea salir?", 
                                                  "Confirmar Salida", 
                                                  JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
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
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
  try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmJuego().setVisible(true);
            }
        });
    }   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem menuNuevoJuego;
    private javax.swing.JMenuItem menuNumMinas;
    private javax.swing.JMenuItem menuTamano;
    // End of variables declaration//GEN-END:variables
}
