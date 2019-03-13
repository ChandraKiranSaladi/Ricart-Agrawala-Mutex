import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.io.IOException;
import java.net.*;

public class Node {
	int UID, port;
	String HostName;
	HashMap<Integer, NeighbourNode> uIDofNeighbors;
	ServerSocket serverSocket;
	List<TCPClient> connectedClients = Collections.synchronizedList(new ArrayList<TCPClient>());
	BlockingQueue<Message> msgQueue;
	boolean reply_deferred[][];
	boolean authorize[][];
	boolean using[];
	private int timeStamp = 1;
	int temp;
	int fileNumber;
	private int serverFilesCount;
	private boolean waiting[];

	public Node(int UID, int port, String hostName, HashMap<Integer, NeighbourNode> uIDofNeighbors) {
		this.UID = UID;
		this.port = port;
		this.HostName = hostName;
		this.uIDofNeighbors = uIDofNeighbors;
		this.msgQueue = new PriorityBlockingQueue<Message>();
	}

	public Node() {
	}

	public int getFileNumber() {
		return this.fileNumber;
	}
	
	public void setFileNumber(int FileNumber) {
		this.fileNumber = FileNumber;
	}
	
	public int getServerFileCount() {
		return this.serverFilesCount;
	}
	public Message getHeadMessageFromQueue() {
		if (this.msgQueue.peek() != null) {
			Message msg = this.msgQueue.peek();
			this.msgQueue.remove();
			return msg;
		}
		return null;
	}

	public Message getMessageFromQueue() {
		Message msg = null;
		try {
			msg = this.msgQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return msg;
	}

	// Adds all messages to queue except Requests
	synchronized public void addMessageToQueue(Message msg) {
		//			setMyTimeStamp(Math.max(msg.timeStamp,getMyTimeStamp()));
		if(msg.getMsgType() == MessageType.Request) {
			
			Treat_Request_Message(msg);
		}
		else
			msgQueue.add(msg);
	}

	// Implemented from the Paper. 
	synchronized private void Treat_Request_Message(Message msg) {
		//	Calculates the priority of the current node request with the incoming node request	
		Boolean priority = (msg.getTimeStamp() > temp) || ((msg.getTimeStamp() == temp)
				&& (msg.getsenderUID()>this.getNodeUID()));
		int fileNo = msg.getFileNumber();
		
		// if you are using the CS or waiting for a request and have a priority ==> defer request
		if(this.using[fileNo] || (this.waiting[fileNo] && priority)) {
			System.out.println("Reply Deferred for file: "+fileNo+" UID:"+ msg.getsenderUID()+ " tmp:"+msg.getTimeStamp()+" at:"+getMyTimeStamp());
			this.reply_deferred[fileNo][msg.getsenderUID()] = true;
			this.authorize[fileNo][msg.getsenderUID()] = false;
		}

		// if neither not using the CS nor waiting for a request or if you don't have a priority ==> send reply
		else if( !(this.using[fileNo] || this.waiting[fileNo]) || (this.waiting[fileNo] && !authorize[fileNo][msg.getsenderUID()])&& !priority) {
			this.authorize[fileNo][msg.getsenderUID()] = false;
			sendReply(fileNo,msg.getsenderUID());
		}
		// Send the request to the node as you haven't send it because you had its authorization.  
		else if(this.waiting[fileNo] && this.authorize[fileNo][msg.getsenderUID()] && !priority) {
			System.out.println("Setting auth for file: "+fileNo+" UID: "+msg.getsenderUID()+" to:false");
			this.authorize[fileNo][msg.getsenderUID()] = false;
			sendReply(fileNo,msg.getsenderUID());
			sendRequest(fileNo,msg.getsenderUID());
		}
	}
	// Returns true when a request is received from the Node. Useful for multiple CS Entry
	synchronized public boolean ifRequested() {
		
		for(int i = 0; i < authorize[0].length;i++) {
			if(i == UID)
				continue;
			if(authorize[fileNumber][i] == false)
				return true;
		}
		return false;
	}
	// Returns the count of requested nodes
	synchronized public int getCountNotAuthorized() {
		int count =0;
		for(int i = 0; i < authorize[0].length;i++) {
			if(i == UID)
				continue;
			if(authorize[fileNumber][i] == false)
				count++;
		}
		return count;
	}
	// Sends Request to the specified to Node with the UID. 
	public void sendRequest(int fileNumber,int getsenderUID) {
		for(TCPClient client: connectedClients) {
			if(getsenderUID == client.getClientUID())
				try {
					IncrementMyTimeStamp();
					System.out.println("Sending Request for file: "+fileNumber+" to :"+getsenderUID+" at: "+getMyTimeStamp());
					client.getOutputWriter().writeObject(new Message(getMyTimeStamp(),this.UID,MessageType.Request,fileNumber));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
// Sends Replies to the UID specified along with the fileNumber. 
	public void sendReply(int fileNumber, int getsenderUID) {
		for(TCPClient client: connectedClients) {
			if(getsenderUID == client.getClientUID())
				try {
					IncrementMyTimeStamp();
					System.out.println("Sending Reply to:"+getsenderUID+" at:"+getMyTimeStamp()+" to file"+fileNumber+".txt");
					client.getOutputWriter().writeObject(new Message(getMyTimeStamp(),this.UID,MessageType.Reply, fileNumber));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public void attachServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public int getNodeUID() {
		return this.UID;
	}

	public int getNodePort() {
		return this.port;
	}

	public String getNodeHostName() {
		return this.HostName;
	}

	public HashMap<Integer, NeighbourNode> getNeighbors() {
		return this.uIDofNeighbors;
	}

	public void addClient(TCPClient client) {
		synchronized (connectedClients) {
			connectedClients.add(client);
		}
	}

	public List<TCPClient> getAllConnectedClients() {
		return this.connectedClients;
	}
	// Lamports Logical TimeStamp
	synchronized public void IncrementMyTimeStamp() {
		timeStamp++;
	}
	
	public int getMyTimeStamp() {
		return timeStamp;
	}
	
	// Sets the size of each Arrays. Depending on the files hosted.
	public void setAuthorizeReplySize(int fileNumbers){
		this.using = new boolean[fileNumbers];;
		this.waiting = new boolean[fileNumbers];;
		this.authorize = new boolean[fileNumbers][5];
		this.reply_deferred = new boolean[fileNumbers][5];
		this.serverFilesCount = fileNumbers;
	}
	
	synchronized public void setMyTimeStamp(int val) {
		this.timeStamp = val;
	}
	
	synchronized public void setWaiting(Boolean val) {
		this.waiting[fileNumber] = val;
	}
	
	synchronized public Boolean getWaiting() {
		return this.waiting[fileNumber];
	}
	
	synchronized public void setUsing(Boolean val) {
		this.using[fileNumber] = val;
	}
	
	synchronized public Boolean getUsing() {
		return this.using[fileNumber];
	}

	public void waitforReplies() {
		// For optimization Requests shouldn't be sent again to Received replies unless those nodes send request back
		// Count not authorized gives the info abnout those newly received requests from nodes
		int requiredCount = getCountNotAuthorized(); long count = 0;
		System.out.println("Count not authorized: "+requiredCount);
		while(count != requiredCount) {
			if(!this.msgQueue.isEmpty()) {
				Message msg = getMessageFromQueue();
				// If received a reply update the Authorize array for that file and Node.
				authorize[fileNumber][msg.getsenderUID()] = true;
				System.out.println("count = "+(++count));
				if(count > requiredCount) {
					System.out.println("Count exceeded");
					System.exit(1);
				}
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
