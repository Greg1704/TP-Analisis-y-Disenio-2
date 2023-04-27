package ventana;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import controlador.Controlador;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

public class VentanaDefinitiva extends JFrame implements MouseListener,IVista,KeyListener {

	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JPanel panelBusqueda;
	private JLabel lblNombreApp;
	private JLabel lblIp;
	private JLabel lblPuerto;
	private JTextField textFieldIp;
	private JTextField textFieldPuerto;
	private JButton btnConectar;
	private JPanel panelChat;
	private JTextArea textAreaChatText;
	private JScrollPane scrollPaneChatText;
	private JLabel lblMensajeUsuario;
	private JPanel panelChatBotones;
	private JButton btnEnviarMensaje;
	private JPanel panelChatAuxiliar;
	private JButton btnCerrarSesion;
	private JPanel panelBusquedaComponentes;
	private JTextField textFieldChatMensajeUsuario;
	private Controlador c;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaDefinitiva frame = new VentanaDefinitiva();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaDefinitiva() {
		
		this.setVisible(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 878, 546);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.contentPane.add(this.tabbedPane, BorderLayout.CENTER);
		
		this.panelBusqueda = new JPanel();
		this.tabbedPane.addTab("Busqueda", null, this.panelBusqueda, null);
		this.panelBusqueda.setLayout(null);
		
		this.panelBusquedaComponentes = new JPanel();
		this.panelBusquedaComponentes.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		this.panelBusquedaComponentes.setBounds(287, 91, 290, 213);
		this.panelBusqueda.add(this.panelBusquedaComponentes);
		this.panelBusquedaComponentes.setLayout(null);
		
		this.lblNombreApp = new JLabel("ChitChat");
		this.lblNombreApp.setBounds(56, 11, 198, 45);
		this.panelBusquedaComponentes.add(this.lblNombreApp);
		this.lblNombreApp.setFont(new Font("Arial Black", Font.PLAIN, 28));
		
		this.lblIp = new JLabel("Ip:");
		this.lblIp.setBounds(22, 74, 20, 22);
		this.panelBusquedaComponentes.add(this.lblIp);
		
		this.textFieldIp = new JTextField();
		this.textFieldIp.setBounds(89, 75, 165, 20);
		this.panelBusquedaComponentes.add(this.textFieldIp);
		this.textFieldIp.setColumns(10);
		
		this.lblPuerto = new JLabel("Puerto:");
		this.lblPuerto.setBounds(22, 127, 46, 14);
		this.panelBusquedaComponentes.add(this.lblPuerto);
		
		this.textFieldPuerto = new JTextField();
		this.textFieldPuerto.setBounds(89, 121, 165, 20);
		this.panelBusquedaComponentes.add(this.textFieldPuerto);
		this.textFieldPuerto.setColumns(10);
		
		this.btnConectar = new JButton("Conectar");
		this.btnConectar.setBounds(103, 164, 89, 23);
		this.panelBusquedaComponentes.add(this.btnConectar);
		
		this.panelChat = new JPanel();
		this.tabbedPane.addTab("Chat", null, this.panelChat, null);
		this.panelChat.setLayout(null);
		
		this.scrollPaneChatText = new JScrollPane();
		this.scrollPaneChatText.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		this.scrollPaneChatText.setBounds(10, 11, 827, 289);
		this.panelChat.add(this.scrollPaneChatText);
		
		this.textAreaChatText = new JTextArea();
		this.scrollPaneChatText.setViewportView(this.textAreaChatText);
		this.textAreaChatText.setFont(new Font("Arial", Font.PLAIN, 12));
		
		this.lblMensajeUsuario = new JLabel("Mensaje:");
		this.lblMensajeUsuario.setFont(new Font("Tahoma", Font.PLAIN, 18));
		this.lblMensajeUsuario.setBounds(10, 311, 83, 36);
		this.panelChat.add(this.lblMensajeUsuario);
		
		this.panelChatBotones = new JPanel();
		this.panelChatBotones.setBounds(95, 373, 632, 62);
		this.panelChat.add(this.panelChatBotones);
		this.panelChatBotones.setLayout(new GridLayout(0, 3, 0, 0));
		
		this.btnEnviarMensaje = new JButton("Enviar");
		this.panelChatBotones.add(this.btnEnviarMensaje);
		
		this.panelChatAuxiliar = new JPanel();
		this.panelChatBotones.add(this.panelChatAuxiliar);
		
		this.btnCerrarSesion = new JButton("Cerrar Sesion");
		this.panelChatBotones.add(this.btnCerrarSesion);
		
		this.textFieldChatMensajeUsuario = new JTextField();
		this.textFieldChatMensajeUsuario.setBounds(95, 317, 723, 31);
		this.panelChat.add(this.textFieldChatMensajeUsuario);
		this.textFieldChatMensajeUsuario.setColumns(10);
	}
	

	public void setControlador(Controlador c) {
		this.c = c;
		setDefault();
	}
	
	public void setDefault() {
		
		//this.tabbedPane.setEnabledAt(1, false);
		
		this.btnEnviarMensaje.addMouseListener(this);
		this.btnEnviarMensaje.addActionListener(c);
		this.btnEnviarMensaje.setActionCommand(enviarMensaje);
		
		this.btnConectar.addMouseListener(this);
		this.btnConectar.addActionListener(c);
		this.btnConectar.setActionCommand(intentoDeConexion);
		
		this.btnCerrarSesion.addMouseListener(this);
		this.btnCerrarSesion.addActionListener(c);
		this.btnCerrarSesion.setActionCommand(cerrarSesion);
		
		this.textFieldChatMensajeUsuario.addKeyListener(this);
	}
	

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.btnConectar) {
			this.textFieldIp.setText("");
			this.textFieldPuerto.setText("");
		}else if(e.getSource() == this.btnEnviarMensaje) {
			this.textFieldChatMensajeUsuario.setText("");
			this.btnEnviarMensaje.setEnabled(false);
		}else if(e.getSource() == this.btnCerrarSesion) {
			
		}
		
	}

	public String getTextFieldChatMensajeUsuario() {
		return textFieldChatMensajeUsuario.getText();
	}
	
	public void agregarMensajeAlChat(String mensaje) {
		this.textAreaChatText.append(mensaje + "\n");
	}

	public String getTextFieldIp() {
		return textFieldIp.getText();
	}

	public String getTextFieldPuerto() {
		return textFieldPuerto.getText();
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == this.textFieldChatMensajeUsuario) {
			if (this.textFieldChatMensajeUsuario.getText().isEmpty()) {
				this.btnEnviarMensaje.setEnabled(false);
			} else {
				this.btnEnviarMensaje.setEnabled(true);
			}
		}

	}
	
	
	
}