// Bindhu Shree Hadya Ravi - 1001699836

package dssrc;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class Client {

	private static PrintWriter out;
	private static BufferedReader in;
	private static JFrame frame;
	private static JTextArea textArea;
	private static final String SERVER_IP = "127.0.0.1";
	private static final int SERVER_PORT = 9103;
	private static String name;
	private static StringBuffer textForClientUi = new StringBuffer();

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		try {
			clientApplication();
		} catch (Exception e) {
//			e.printStackTrace();
			addTextToClient("Server Unavailable!");
		}
	}

	public Client() {
		initialize();
	}

	// Initialize Client UI
	private static void initialize() {
		frame = new JFrame();
		frame.setBounds(50, 50, 400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Adding the text area
		textArea = new JTextArea();
		JScrollPane scrollingText = new JScrollPane(textArea);
		// Adding scrolling bar
		scrollingText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scrollingText);

		// Creating the button to kill the process without using window close
		JButton btnStop = new JButton("Kill Process");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Exiting the process
				System.exit(0);
			}
		});
		frame.getContentPane().add(btnStop, BorderLayout.NORTH);

		// Creating button to input the directory commands
		JButton dirCommand = new JButton("Command");
		dirCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Creating a input dialog to take command input from user when this button is pressed
				String input = JOptionPane.showInputDialog("Please enter the command to execute");
				out.println(input);
			}
		});
		frame.getContentPane().add(dirCommand, BorderLayout.EAST);
		
		// Creating button to input the directory commands
		JButton pollcommand = new JButton("Poll");
		pollcommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Creating a input dialog to take command input from user when this button is pressed
				String input = JOptionPane.showInputDialog("Please enter the poll command to execute");
				out.println(input);
			}
		});
		frame.getContentPane().add(pollcommand, BorderLayout.WEST);
	}

	// Manages the Client thread
	public static void clientApplication() throws UnknownHostException, IOException, InterruptedException {
		try {
			Socket s = new Socket(SERVER_IP, SERVER_PORT);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(), true);
			
			while (true) {
				// Creating an input dialog to take client name as input from user
					name = JOptionPane.showInputDialog("Please enter the client name ");
					out.println(name);
					String serverText = in.readLine();
					// Analyzing the different messages sent from the server
					if (serverText.equals("Duplicate User")) {
						addTextToClient("\n Re enter name");
					} else if (serverText.equals("Connected to the server")) {
						addTextToClient("Connected to the server");
					} else if (serverText.equals("Maximum number of clients reached")) {
						addTextToClient("Maximum server capacity reached");
						addTextToClient("*** Server Unavailable!! ***");
					} else if (serverText.equals("userName should start with Alphabets or Numbers or _")) {
						addTextToClient("userName should start with Alphabets or Numbers or _");
					} else {
						System.out.println(serverText);
						break;
					}
			}
			// Client waiting for messages
			while (true) {
				try {
					String serverTxt = in.readLine();
					// Validating for NULL, if its NULL then server is not available at the moment
					if (serverTxt != null) {
						addTextToClient(serverTxt.replace("  ,  ", "\n"));
						Thread.sleep(300);
					} else {
						// Handling server unavailability
						addTextToClient("Server Unavailable!");
						break;
					}
				} catch (Exception e) {
					// Handling server crash exceptions
					addTextToClient("Server Unavailable!");
					break;
				}
			}
		} catch (Exception e) {
			// Handling server crash exceptions
			addTextToClient("Server Unavailable!");
		}
	}

	// Function to display on Client UI
	private static void addTextToClient(String s) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// Formatting to display on Client UI
				textForClientUi.append("\n" + s + "\n");
				textArea.setText(textForClientUi.toString());
			}
		});
	}
}
