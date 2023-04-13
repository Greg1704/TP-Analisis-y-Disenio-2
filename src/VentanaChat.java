import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.GridLayout;

public class VentanaChat extends JFrame {

	private JPanel contentPane;
	private JPanel panelChat;
	private JTextPane textPaneChatText;
	private JScrollPane scrollPaneChatText;
	private JLabel lblMensajeUsuario;
	private JTextField textFieldMensajeUsuario;
	private JScrollPane scrollPaneMensajeUsuario;
	private JPanel panelChatBotones;
	private JButton btnEnviarMensaje;
	private JPanel panelChatAuxiliar;
	private JButton btnCerrarSesion;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaChat frame = new VentanaChat();
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
	public VentanaChat() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 509);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.panelChat = new JPanel();
		this.contentPane.add(this.panelChat, BorderLayout.CENTER);
		this.panelChat.setLayout(null);
		
		this.scrollPaneChatText = new JScrollPane();
		this.scrollPaneChatText.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		this.scrollPaneChatText.setBounds(10, 11, 654, 289);
		this.panelChat.add(this.scrollPaneChatText);
		
		this.textPaneChatText = new JTextPane();
		this.scrollPaneChatText.setViewportView(this.textPaneChatText);
		this.textPaneChatText.setFont(new Font("Arial", Font.PLAIN, 12));
		
		this.lblMensajeUsuario = new JLabel("Mensaje:");
		this.lblMensajeUsuario.setFont(new Font("Tahoma", Font.PLAIN, 18));
		this.lblMensajeUsuario.setBounds(10, 311, 83, 36);
		this.panelChat.add(this.lblMensajeUsuario);
		
		this.scrollPaneMensajeUsuario = new JScrollPane();
		this.scrollPaneMensajeUsuario.setBounds(88, 317, 562, 31);
		this.panelChat.add(this.scrollPaneMensajeUsuario);
		
		this.textFieldMensajeUsuario = new JTextField();
		this.scrollPaneMensajeUsuario.setViewportView(this.textFieldMensajeUsuario);
		this.textFieldMensajeUsuario.setColumns(10);
		
		this.panelChatBotones = new JPanel();
		this.panelChatBotones.setBounds(95, 373, 486, 62);
		this.panelChat.add(this.panelChatBotones);
		this.panelChatBotones.setLayout(new GridLayout(0, 3, 0, 0));
		
		this.btnEnviarMensaje = new JButton("Enviar");
		this.panelChatBotones.add(this.btnEnviarMensaje);
		
		this.panelChatAuxiliar = new JPanel();
		this.panelChatBotones.add(this.panelChatAuxiliar);
		
		this.btnCerrarSesion = new JButton("Cerrar Sesion");
		this.panelChatBotones.add(this.btnCerrarSesion);
	}
}
