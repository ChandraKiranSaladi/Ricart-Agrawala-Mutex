//import java.io.*;
//import java.net.*;
//
//class ClientRequestHandler implements Runnable {
//	private Socket client;
//	ObjectInputStream in;
//	ObjectOutputStream out;
//	private String clientUID;
//
//	ClientRequestHandler(Socket client) {
//		this.client = client;
//	}
//
//	public Socket getClientSocket() {
//		return this.client;
//	}
//
//	public int getClientUID() {
//		return Integer.parseInt(this.clientUID);
//	}
//
//	public ObjectInputStream getInputReader() {
//		return this.in;
//
//	}
//
//	public ObjectOutputStream getOutputWriter() {
//		return this.out;
//	}
//
//	public void run() {
//
//		try {
//			in = new ObjectInputStream(client.getInputStream());
//			out = new ObjectOutputStream(client.getOutputStream());
//		} catch (IOException e) {
//			System.out.println("in or out failed");
//			System.exit(-1);
//		}
//
//		while (true) {
//			try {
//				// Read data from client
//				clientUID = in.readObject().toString();
//				System.out.println("Text received from client: " + clientUID);
//				
//				// Send data back to client
//				out.writeObject(clientUID);
//				
//			} catch (IOException | ClassNotFoundException e) {
//				System.out.println("Read failed");
//				
//				System.exit(-1);
//			}
//		}
//	}
//}