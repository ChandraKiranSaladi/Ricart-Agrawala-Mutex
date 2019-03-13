import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class InvokeMain {
	public static void main(String[] args) {
		try {

			// build a node for each terminal

			// logic for assigning nodes - temporary
//			Scanner scanner = new Scanner(System.in);
			
//			int hostNumIndex = scanner.nextInt();
			// Integer.parseInt(args[0]);
			//int hostNumIndex = 0;

			String clientHostName = "";
			try {
					clientHostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}

			Node dsNode = BuildNode(clientHostName);


			System.out.println("Initializing Server with UID: " + dsNode.UID);

			
			// Start server thread

			Runnable serverRunnable = new Runnable() {
				public void run() {
					TCPServer server = new TCPServer(dsNode);
					// start listening for client requests
					server.listenSocket();
				}
			};
			Thread serverthread = new Thread(serverRunnable);
			serverthread.start();

			System.out.println("Server started and listening to client requests.........");

			// Start client threads at this node
			System.out.println("Press any key to start clients.........");

			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
//			String input = bufferRead.readLine();

			Thread.sleep(6000);
//			if(input != null){ 	
//				for (Node node : ParseConfigFile.nodeList) {
//					System.out.println("node.UIDNeighbors.size:"+node.uIDofNeighbors.size());
					dsNode.uIDofNeighbors.entrySet().forEach((neighbour) -> {
						// TCPClient client = new TCPClient(neighbour.getKey(), dsNode.port,
						// dsNode.HostName,neighbour.getValue().PortNumber,
						// neighbour.getValue().HostName);

						// hard coded to local host for now
//						if (neighbour.getKey() == dsNode.UID) {
							System.out.println("neighbour.getKey(): "+neighbour.getKey()+"dsNode.UID:"+dsNode.UID );
							Runnable clientRunnable = new Runnable() {
								public void run() {
									TCPClient client = new TCPClient(dsNode.UID,
											neighbour.getValue().PortNumber, neighbour.getValue().HostName, dsNode.getNodeHostName(), neighbour.getKey(),
											dsNode);
									System.out.println("dsNode.UID: "+dsNode.UID+"neighbour.getValue().PortNumber:"+neighbour.getValue().PortNumber
											+"neighbour.getKey() "+neighbour.getKey());
									client.listenSocket();
									client.sendHandShakeMessage();
									dsNode.addClient(client);
									client.listenToMessages();
								}
							};
							Thread clientthread = new Thread(clientRunnable);
							clientthread.start();
//						}
					});
//				}
//			}
			Thread.sleep(5000);
			RicartAgrawala algo = new RicartAgrawala(dsNode);
			algo.enquire();
			Thread.sleep(10000);
			algo.InitiateAlgorithm();

		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public static Node BuildNode(String clientHostName) {
		Node dsNode = new Node();
		try {
			dsNode = ParseConfigFile.read(
					"/home/010/c/cx/cxs172130/AOS/src/readFile.txt",
							InetAddress.getLocalHost().getHostName());
		} catch (Exception e) {
			throw new RuntimeException("Unable to get nodeList", e);
		}
		return dsNode;
	}
}
