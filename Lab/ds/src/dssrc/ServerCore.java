// Bindhu Shree Hadya Ravi - 1001699836
// REFERENCE : https://www.codejava.net/java-se/networking/how-to-create-a-chat-console-application-in-java-using-socket

package dssrc;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.Socket;


public class ServerCore extends Thread {
	private PrintWriter m_writer;
	private BufferedReader m_reader;
	private Socket m_socket;
	private Server m_server;
	private String userName;
	private String buff;
	private int m_no;

	public synchronized void setM_no(int m_no) {
		this.m_no = m_no;
	}
	
	// Constructor to initialize the socket
	public ServerCore(Socket socket, Server server) {
		m_socket = socket;
		m_server = server;
		setM_no(-1);
	}
	
	// Function to display the directories and files (ls command)
	public static void displayDirectoryFiles (File[] dir,PrintWriter m_writer) {
		// Traversing through all the files in the given directory
	    for (File item : dir) {
	    	// Checking whether item is a file or a directory
	        if (item.isDirectory()) {
	            m_writer.println("Directory: "+item.getName());
	            // Calling recursively to list the sub directory
	            displayDirectoryFiles (item.listFiles(),m_writer);
	        } else {
	        	// Listing a file
	            m_writer.println("File: "+item.getName());
	        }
	    }
	}
	
	// Function to rename the given directory
	public static void renameDirectoryFiles (File srcName, File destName, PrintWriter m_writer) {
		// Checking whether source exists or not
		if (srcName.exists()) {
			// Using renameTo API to change the name from srcName to destName
			if (srcName.renameTo(destName)) {
				m_writer.println("Directory renamed successfully");
			} else {
				// If any error occurs, we are sending error message to the client
				m_writer.println("ERROR : Unable to rename the directory");
				m_writer.println("(Directory doesn't exists or something went wrong!)");
			}
		} else {
			// Source directory does not exist so sending an error message
			m_writer.println("ERROR : Unable to rename the directory");
			m_writer.println("(Directory doesn't exists!)");
		}
	}
	
	// Function to rename the given directory
	public static void renameDirectoryFiles2 (File srcName, File destName) {
		// Checking whether source exists or not
		if (srcName.exists()) {
			// Using renameTo API to change the name from srcName to destName
			srcName.renameTo(destName);
		}
	}
	
	// Function to delete directory and all its files within that directory (rm command)
	public static void deleteDirectoryFiles (File dir,  PrintWriter m_writer) {
		
		// Listing all the files within the directory
	    File[] items = dir.listFiles();
	    if (items != null) {
	    	// Iterating through all the files within directory
	        for (File subDir : items) {
	            // Deleting the sub directories too
	            deleteDirectoryFiles (subDir, m_writer);
	        }
	    }
	    // delete() function tries to delete the directory
	    if (dir.delete()) {
	    	// Directory is deleted
	    	System.out.printf("Delete : %s%n", dir);
	        m_writer.printf("Deleted : %s%n", dir);
	    } else {
	    	// Directory doesn't exists or something went wrong, Directory wouldn't be deleted
	        m_writer.printf("ERROR : Unable to delete file or directory : %s%n", dir);
	        m_writer.println("(Directory doesn't exists or something went wrong!)");
	    }
	}
	
	// Function to delete directory and all its files within that directory (rm command)
		public static void deleteDirectoryFiles2 (File dir) {
			
			System.out.printf("dir :"+ dir);
			// Listing all the files within the directory
		    File[] items = dir.listFiles();
		    if (items != null) {
		    	// Iterating through all the files within directory
		        for (File subDir : items) {
		            // Deleting the sub directories too
		            deleteDirectoryFiles2 (subDir);
		        }
		    }
		    // delete() function tries to delete the directory
		    dir.delete();
		}
	
	// This thread listens to the input from client
	public void serverFunctionality(PrintWriter m_writer) {

		while (true) {
			try {
				String inputFromClient = m_reader.readLine();
				// NULL check for input from Client
				if (inputFromClient != null)
				{
					String[] splited = inputFromClient.split(" ");
					
					if (splited.length == 1 && splited[0].equalsIgnoreCase("list")) {
						
						// Listing Directories
						Server.addTextToServer("Executing **list directory** for client "+userName);
						// Forming the filepath
						String current = System.getProperty("user.dir");
						current = current.concat("/server/");
						current = current.concat(userName);
						System.out.println("cur:"+current);
						// listFiles() API is used to list all the files and directories in a directory
						File[] dir = new File(current).listFiles();
						m_writer.println(userName+" Client Directory listing: ");
						if (dir.length == 0) {
							System.out.println("Directory is empty!");
							m_writer.println("Directory is empty!");
							m_writer.flush();
						} else {
							// Function to list all the directories
							displayDirectoryFiles (dir,m_writer);
							m_writer.flush();
						}
					} else if (splited.length == 2 && splited[0].equalsIgnoreCase("sync")) {
						
						// Syncing Directories
						m_writer.println("Executing **sync server home directory** for client "+userName);
						// Forming the filepath
						System.out.println(splited[1]);
						// Forming the filepath
						String current = System.getProperty("user.dir");
						current = current.concat("/server/");
						System.out.println(current);
						current = current.concat(splited[1]);
						System.out.println(current);
						File file = new File(current);
						String current2 = System.getProperty("user.dir");
						current2 = current2.concat("/localsystem/");
						// Buffer
//						if (userName=="cli1" || userName.equals("cli1") || userName.equals("homedir1")) {
//							current2 = current2.concat("A");
//						} else if (userName=="cli2" || userName.equals("cli2") || userName.equals("homedir2")) {
//							current2 = current2.concat("B");
//						} else {
//							current2 = current2.concat("C");
//						}
						current2 = current2.concat(buff);
						current2 = current2.concat("/");
						current2 = current2.concat(splited[1]);
						
						System.out.println("cur:"+current2);
						File file2 = new File(current2);
						// mkdirs() is used to create a directory
						if (file.exists()) {
							// Copying it onto buffer
							for (File srcFile: file.listFiles()) {
							    if (srcFile.isDirectory()) {
							        FileUtils.copyDirectoryToDirectory(srcFile, file2);
							    } else {
							        FileUtils.copyFileToDirectory(srcFile, file2);
							    }
							}
						} else {
							System.out.println("ERROR : Couldn't find the server directory");
						    m_writer.println("ERROR : Couldn't find the server directory");
						}
					} else if (splited.length == 2 && splited[0].equalsIgnoreCase("create")) {
						
						// Create Directories
						Server.addTextToServer("Executing **create directory** for client "+userName);
						System.out.println(splited[1]);
						String[] pathSplit = splited[1].split("/");
						if (true) {
//							if (pathSplit[1].equalsIgnoreCase(userName)) {
							// Forming the filepath
							String current = System.getProperty("user.dir");
							current = current.concat("/server");
							String current2 = System.getProperty("user.dir");
							current2 = current2.concat("/localsystem/");
							current2 = current2.concat("A");
							String[] pathSplit1 = splited[1].split("/");
							System.out.println(current);
							System.out.println(current2);
							current = current.concat(splited[1]);
							
							String temp=current2;
							temp = temp.concat(pathSplit1[0]);
							File tmp = new File(temp);
							if (tmp.exists()) {
								current2 = current2.concat(splited[1]);
								File file2 = new File(current2);
								file2.mkdirs();
							}
							
							current2 = System.getProperty("user.dir");
							current2 = current2.concat("/localsystem/");
							current2 = current2.concat("B");
							pathSplit1 = splited[1].split("/");
							
							temp=current2;
							temp = temp.concat(pathSplit1[0]);
							tmp = new File(temp);
							if (tmp.exists()) {
								current2 = current2.concat(splited[1]);
								File file2 = new File(current2);
								file2.mkdirs();
							}
							
							current2 = System.getProperty("user.dir");
							current2 = current2.concat("/localsystem/");
							current2 = current2.concat("C");
							pathSplit1 = splited[1].split("/");
							
							temp=current2;
							temp = temp.concat(pathSplit1[0]);
							tmp = new File(temp);
							if (tmp.exists()) {
								current2 = current2.concat(splited[1]);
								File file2 = new File(current2);
								file2.mkdirs();
							}
							
							System.out.println(current);
							System.out.println(current2);
						    File file = new File(current);
						    
						    // mkdirs() is used to create a directory
							boolean ret = file.mkdirs();
							if(ret) {
								System.out.println("Created Directory successfully");
								m_writer.println("Created Directory successfully");
							} else {
						        System.out.println("ERROR : Couldn't create the directory");
						        m_writer.println("ERROR : Couldn't create the directory");
						        m_writer.println("(Directory already exists or something went wrong!)");
						    }
						} 
//						else {
//							// Client accessing unauthorized files/directories
//							System.out.println("ERROR : Permission Denied!");
//							Server.addTextToServer("Unauthorized Access by Client "+userName);
//							m_writer.println("ERROR : Permission Denied!");
//						}
						m_writer.flush();
					} else if (splited.length == 2 && splited[0].equalsIgnoreCase("delete")) {
						
						// Delete Directories
						Server.addTextToServer("Executing **delete directory** for client "+userName);
						System.out.println(splited[1]);
						String[] pathSplit = splited[1].split("/");
						if (true) {
//						if (pathSplit[1].equalsIgnoreCase(userName)) {
							// Forming the filepath
							String current = System.getProperty("user.dir");
							current = current.concat("/server");
							current = current.concat(splited[1]);
							String current2 = System.getProperty("user.dir");
							current2 = current2.concat("/localsystem/");
//							if (userName=="cli1" || userName.equals("cli1") || userName.equals("homedir1")) {
//								current2 = current2.concat("A");
//							} else if (userName=="cli2" || userName.equals("cli2") || userName.equals("homedir2")) {
//								current2 = current2.concat("B");
//							} else {
//								current2 = current2.concat("C");
//							}
							current2 = current2.concat("A");
							current2 = current2.concat(splited[1]);
							String current3 = System.getProperty("user.dir");
							current3 = current3.concat("/localsystem/");
							current3 = current3.concat("B");
							current3 = current3.concat(splited[1]);
							String current4 = System.getProperty("user.dir");
							current4 = current4.concat("/localsystem/");
							current4 = current4.concat("C");
							current4 = current4.concat(splited[1]);
							System.out.println(current);
							System.out.println(current2);
							File path = new File(current);
							File path2 = new File(current2);
							File path3 = new File(current3);
							File path4 = new File(current4);
							// Funtion to delete the directory
							System.out.println(path);
							deleteDirectoryFiles(path,m_writer);
							System.out.println(" ");
							deleteDirectoryFiles2(path2);
							System.out.println(" ");
							deleteDirectoryFiles2(path3);
							System.out.println(" ");
							deleteDirectoryFiles2(path4);
						} 
//						else {
//							// Client accessing unauthorized files/directories
//							System.out.println("ERROR : Permission Denied!");
//							Server.addTextToServer("Unauthorized Access by Client "+userName);
//							m_writer.println("ERROR : Permission Denied!");
//						}
						m_writer.flush();
					} else if (splited.length == 3 && splited[0].equalsIgnoreCase("rename")) {
						
						// Rename Directories
						Server.addTextToServer("Executing **rename directory** for client "+userName);
						System.out.println(splited[1]);
						String[] pathSplit1 = splited[1].split("/");
						System.out.println(splited[2]);
						String[] pathSplit2 = splited[2].split("/");
						if (true) {
//							if (pathSplit1[1].equalsIgnoreCase(userName) && pathSplit2[1].equalsIgnoreCase(userName)) {
							// Forming the filepath
							String current1 = System.getProperty("user.dir");
							current1 = current1.concat("/server");
							current1 = current1.concat(splited[1]);
							System.out.println(current1);
							File path1 = new File(current1);
							// Forming the filepath
							String current2 = System.getProperty("user.dir");
							current2 = current2.concat("/server");
							current2 = current2.concat(splited[2]);
							System.out.println(current2);
							File path2 = new File(current2);
							// Funtion which renames the directory files
							renameDirectoryFiles(path1,path2,m_writer);
							
							// Forming the filepath
							String current3 = System.getProperty("user.dir");
							current3 = current3.concat("/localsystem/");
							current3 = current3.concat("A");
							current3 = current3.concat(splited[1]);
							File path3 = new File(current3);
							// Forming the filepath
							String current4 = System.getProperty("user.dir");
							current4 = current4.concat("/localsystem/");
							current4 = current4.concat("A");
							current4 = current4.concat(splited[2]);
							File path4 = new File(current4);
							// Funtion which renames the directory files
							renameDirectoryFiles2(path3,path4);
							
							current3 = System.getProperty("user.dir");
							current3 = current3.concat("/localsystem/");
							current3 = current3.concat("B");
							current3 = current3.concat(splited[1]);
							path3 = new File(current3);
							// Forming the filepath
							current4 = System.getProperty("user.dir");
							current4 = current4.concat("/localsystem/");
							current4 = current4.concat("B");
							current4 = current4.concat(splited[2]);
							path4 = new File(current4);
							// Funtion which renames the directory files
							renameDirectoryFiles2(path3,path4);
							
							current3 = System.getProperty("user.dir");
							current3 = current3.concat("/localsystem/");
							current3 = current3.concat("C");
							current3 = current3.concat(splited[1]);
							path3 = new File(current3);
							// Forming the filepath
							current4 = System.getProperty("user.dir");
							current4 = current4.concat("/localsystem/");
							current4 = current4.concat("C");
							current4 = current4.concat(splited[2]);
							path4 = new File(current4);
							// Funtion which renames the directory files
							renameDirectoryFiles2(path3,path4);
						} 
//						else {
//							// Client accessing unauthorized files/directories
//							System.out.println("ERROR : Permission Denied!");
//							Server.addTextToServer("Unauthorized Access by Client "+userName);
//							m_writer.println("ERROR : Permission Denied!");
//						}
						m_writer.flush();
					} else if (splited.length == 3 && splited[0].equalsIgnoreCase("move")) {
						
						// Move Directories
						Server.addTextToServer("Executing **move directory** for client "+userName);
						System.out.println(splited[1]);
						String[] pathSplit1 = splited[1].split("/");
						System.out.println(splited[2]);
						String[] pathSplit2 = splited[2].split("/");
						if (true) {
//						if (pathSplit1[1].equalsIgnoreCase(userName) && pathSplit2[1].equalsIgnoreCase(userName)) {
							String current1 = System.getProperty("user.dir");
							current1 = current1.concat("/server");
							current1 = current1.concat(splited[1]);
							System.out.println(current1);
							File path1 = new File(current1);
							String current2 = System.getProperty("user.dir");
							current2 = current2.concat("/server");
							current2 = current2.concat(splited[2]);
							System.out.println(current2);
							File path2 = new File(current2);
							
							String current3 = System.getProperty("user.dir");
							current3 = current3.concat("/localsystem/");
							current3 = current3.concat("A");
							current3 = current3.concat(splited[1]);
							File path3 = new File(current3);
							String current4 = System.getProperty("user.dir");
							current4 = current4.concat("/localsystem/");
							current4 = current4.concat("A");
							current4 = current4.concat(splited[2]);
							File path4 = new File(current4);
							
							String current5 = System.getProperty("user.dir");
							current5 = current5.concat("/localsystem/");
							current5 = current5.concat("B");
							current5 = current5.concat(splited[1]);
							File path5 = new File(current5);
							String current6 = System.getProperty("user.dir");
							current6 = current6.concat("/localsystem/");
							current6 = current6.concat("B");
							current6 = current6.concat(splited[2]);
							File path6 = new File(current6);
							
							String current7 = System.getProperty("user.dir");
							current7 = current7.concat("/localsystem/");
							current7 = current7.concat("C");
							current7 = current7.concat(splited[1]);
							File path7 = new File(current7);
							String current8 = System.getProperty("user.dir");
							current8 = current8.concat("/localsystem/");
							current8 = current8.concat("C");
							current8 = current8.concat(splited[2]);
							File path8 = new File(current8);
							
							try {
					            // Move the source directory (all its sub-directories) to the destination directory
								// Implementing mv -n mac command
					            FileUtils.moveDirectoryToDirectory(path1, path2,true);
					            FileUtils.moveDirectoryToDirectory(path3, path4,true);
					            FileUtils.moveDirectoryToDirectory(path5, path6,true);
					            FileUtils.moveDirectoryToDirectory(path7, path8,true);
					            System.out.println("Directory was moved successfully");
								m_writer.println("Directory was moveed successfully");
					        } catch (IOException e) {
//					            e.printStackTrace();
					            System.out.println("ERROR : Unable to move the directory");
								m_writer.println("ERROR : Unable to move the directory");
								m_writer.println("ERROR : Destination directory already exists and not empty");
					        }
						} 
//						else {
//							System.out.println("ERROR : Permission Denied!");
//							Server.addTextToServer("Unauthorized Access by Client "+userName);
//							m_writer.println("ERROR : Permission Denied!");
//						}
						m_writer.flush();
					} else if (splited.length == 1 && splited[0].equalsIgnoreCase("clients")) {
						
						// Display all the connected clients
						System.out.println("Num of Clients = " + m_server.getNumOfClients());
						Server.addTextToServer("Num of Clients = " + m_server.getNumOfClients());
						for (String user : m_server.getClientNames()) {
							System.out.println(user);
							Server.addTextToServer(user);
						}
					} else if (splited.length == 1 && splited[0].equalsIgnoreCase("poll")) {
						
						// Display all the connected clients
						System.out.println("Client " + userName+ " sent the poll request");
						Server.addTextToServer("Client " + userName+ " sent the poll request");
					} else {
						
						// Invalid Command handling
						m_writer.println("ERROR : Invalid Command! Command not supported");
						m_writer.flush();
					}
				} else {

					if (userName!=null && !userName.equalsIgnoreCase("null") && !userName.equals("")
							&& !userName.equals(" ")) {
						// Error when client exits abruptly
						Server.addTextToServer(userName + " client exited");
					}
					// Removing the userName as it is not valid
					m_server.removeClientName(userName);
					break;
				}
			} catch (Exception e) {
				// Error when client exits abruptly
//				e.printStackTrace();
				Server.addTextToServer(userName+" Client Exits");
				break;
			}
		}
	}

	@Override
	public void run() {
		InputStream input;

		try {
			input = m_socket.getInputStream();
			m_reader = new BufferedReader(new InputStreamReader(input));
			OutputStream output = m_socket.getOutputStream();
			m_writer = new PrintWriter(output, true);

			processClientInteraction(m_writer);
			serverFunctionality(m_writer);
			
		} catch (Exception ex) {
			// Error when client exits abruptly or when thread is a error in client thread
			System.out.println("Error in Client Thread: " + ex.getMessage());
//			ex.printStackTrace();
			Server.addTextToServer(userName+" Client Exits");
		}
	}

	// Function to validates and process Client requests and responses
	public void processClientInteraction (PrintWriter m_writer) throws IOException {
		while (true) {
			boolean isUnique;
			String[] splited = m_reader.readLine().split(" ");
			userName=splited[0];
			buff=splited[1];
			System.out.println("buff "+buff);
			isUnique = checkIfUnique(userName);

			// Printing the Connected Clients everytime we visit this function
//			Server.addTextToServer("Connected clients");
//			for (String user : m_server.getClientNames()) {
//				Server.addTextToServer(user);
//			}
			// Accepting the connection only if there are less than 3 clients and it has given a valid username
			if (isUnique && (m_server.getNumOfClients() < 3)) {
				m_server.addClientName(userName);
				Server.addTextToServer("*******************************************");
				Server.addTextToServer(userName + " is connected and currently active");
				Server.addTextToServer("*******************************************");
				m_writer.println("Connected to the server");
				m_writer.flush();
				// Server reached it's maximum capacity and will not accept any more connection further
				if (m_server.getNumOfClients() == 3) {
					Server.addTextToServer("Maximum capacity reached! Can not accept anymore clients");
				}
//				File dir = new File(userName);
				String current = System.getProperty("user.dir");
//				System.out.println(current);
				current = current.concat("/server/");
//				System.out.println(current);
				current = current.concat(userName);
//				System.out.println(current);
				// Creating the directory with respect to every client if it doesn't exists
				// Client directory already exists
				File dir = new File(current);
				System.out.println("IMPPP!!!"+dir);
			    if (dir.exists()) {
			        System.out.println("Client directory already exists");
			        Server.addTextToServer(userName+" Client directory already exists ");
			    }
			    // mkdirs() tries to create a directory if its not present
			    if (dir.mkdirs()) {
			    	System.out.println("Created the newly joined client directory");
			    	Server.addTextToServer("Created the newly joined client directory for "+userName);
			    }
			    System.out.println("Num of Clients = " + m_server.getNumOfClients());
				Server.addTextToServer("Num of Clients = " + m_server.getNumOfClients());
				
				// Listing Directories
//				Server.addTextToServer("Executing **list directory** for A B C ");
				// Forming the filepath
				m_writer.println("Server Directory Listing:");
				String current1 = System.getProperty("user.dir");
				System.out.println(current1);
				current1 = current1.concat("/server/");
				System.out.println(current1);
				// listFiles() API is used to list all the files and directories in a directory
				File[] dir2 = new File(current1).listFiles();
				if (dir2.length == 0) {
					System.out.println("Directory is empty!");
					m_writer.println("Directory is empty!");
					m_writer.flush();
				} else {
					// Function to list all the directories
					displayDirectoryFiles (dir2,m_writer);
					m_writer.flush();
				}
				
//				for (String user : m_server.getClientNames()) {
//					System.out.println(user);
//					Server.addTextToServer(user);
//				}
				break;

			}  else if (userName == null || userName == "null" ||
					userName.equalsIgnoreCase("null")) {

				// Invalid UserName
				System.out.println("null userName can't be accepted");
				m_writer.println("null userName can't be accepted");
				m_writer.flush();
				break;
			} else if (userName.length()<1 || userName.length()>30) {
				// Invalid UserName
				System.out.println("userName length should be > 0 and < 30");
				m_writer.println("userName length should be > 0 and < 30");
				m_writer.flush();
//				break;
			} else if (!userName.matches("[A-Za-z0-9_]+")) {
				// Invalid UserName
				System.out.println("userName should start with Alphabets or Numbers or _");
				m_writer.println("userName should start with Alphabets or Numbers or _");
				m_writer.flush();
//				break;
				
			} else if (m_server.getNumOfClients() >= 3) {

				System.out.println("Maximum number of clients reached");
				m_writer.println("Maximum number of clients reached");
				m_writer.flush();
//				break;
			} else {
				// There is already one client with the same name, so client will be prompted to input different name
				m_writer.println("Duplicate User");
				m_writer.flush();
			}
		}
	}

	// Client UserName Validation
	public boolean checkIfUnique(String userName) {

		if (userName == null || userName.length()<1 ||
				userName.length()>30 || userName == "null" ||
				userName.equalsIgnoreCase("null") ||
				!userName.matches("[A-Za-z0-9_]+")) {
			// Invalid UserName
			return false;
		}
		// Taking every connected client and checking whether the current username matches
		for (String user : m_server.getClientNames()) {
			if (user.equals(userName)) {
				// There is already one user with that name so rejecting it
				return false;
			}
		}
		// Valid username,
		return true;
	}
}
