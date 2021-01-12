// Bindhu Shree Hadya Ravi - 1001699836
// REFERENCE : https://stackoverflow.com/questions/10177183/java-add-scroll-into-text-area

package dssrc;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class Server extends javax.swing.JWindow {

	private JFrame frame;
	private static JTextArea txtArea;
	private int m_port;
	private Set<String> m_userNames = new HashSet<>();
	private static Set<String> s_userNames = new HashSet<>();
	private static ArrayList<ServerCore> serverThreads = new ArrayList<>();
	public static StringBuffer textForServerUi = new StringBuffer();

	// Constructor to initialize the port number
	Server(int port) {
		m_port = port;
	}
	
	// Function which starts the Server and Listens to the clients connection request
	public void serverApplication() throws IOException {
		// Creating server socket and listening to the client connection
		ServerSocket serverSoc = new ServerSocket(m_port);
		
		System.out.println("Server is listening..");
		addTextToServer("Server is listening..");
		while (true) {
			// Maximum of 3 clients can be handled by our server
			if (m_userNames.size() < 3) {
				// Accepting the socket connection
				Socket s = serverSoc.accept();
				ServerCore serverThread = new ServerCore(s, this);
				// Adding the new thread and starting it
				serverThreads.add(serverThread);
				serverThread.start();
			}
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Creating a server window
					Server window = new Server();
					window.frame.setVisible(true);
				} catch (Exception e) {
					// Handling if any there was any problem opening the server window
					e.printStackTrace();
				}
			}
		});

		// Making 9103 port number
		Server server = new Server(9103);

		// Creating new thread
		Thread t2 = new Thread(new Runnable() {
			public void run() {

				try {
					server.serverApplication();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		// Starting the thread t2
		t2.start();
	}

	// Function which return the connected clients list
	Set<String> getClientNames() {
		return m_userNames;
	}
	
	// Returns number of clients connected at that moment
	int getNumOfClients() {
		return m_userNames.size();
	}

	// Stores the username to the connected clients list if it is not null
	void addClientName(String userName) {
		if (userName!=null && userName!="null" 
				&& !userName.equalsIgnoreCase("null")
				&& userName.matches("[A-Za-z0-9_]+")) {
			m_userNames.add(userName);
			s_userNames.add(userName);
		}
	}

	// Removes the username from the connected clients list
	void removeClientName(String userName) {
		m_userNames.remove(userName);
		s_userNames.remove(userName);
    }

	// Calling the server UI initialization function
	public Server() {
		initialize();
	}

	// Function to initialize the UI components
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(700, 50, 640, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		// Creating the text area
		txtArea = new JTextArea();
		// REFERENCE : https://stackoverflow.com/questions/10177183/java-add-scroll-into-text-area
		// Scrollable Text window was taken from stackoverflow reference
		JScrollPane scrollingText = new JScrollPane(txtArea);
		// Making the text area scrollable
		scrollingText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scrollingText);

		// Creating the button to kill the process without using window close
		JButton btnNewButton = new JButton("Terminate/Kill Server");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Exiting the process
				System.exit(0);
			}
		});
		frame.getContentPane().add(btnNewButton, BorderLayout.WEST);
		
		// Creating the button to display the number of clients
		JButton numOfClientsButton = new JButton("Connected Clients");
		frame.getContentPane().add(numOfClientsButton, BorderLayout.EAST);
		numOfClientsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Server.addTextToServer("Number of Connected Clients: "+s_userNames.size());
				Server.addTextToServer("Connected Clients: ");
				for (String user : s_userNames) {
					Server.addTextToServer("** "+user);
				}
			}
		});
	}

	// Function to display text onto the Server UI
	public static void addTextToServer(String s) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// Formatting to display text onto the Server UI
				textForServerUi.append("\n" + s + "\n");
				txtArea.setText(textForServerUi.toString());
			}
		});
	}
}
