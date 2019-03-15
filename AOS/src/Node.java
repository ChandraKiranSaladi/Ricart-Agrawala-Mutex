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

//	public int getLeaderUID() {
//		return this.leaderUID = this.highestUID;
//	}
//
//	public void setNodeAsLeader() {
//		this.isLeader = true;
//	}

//	public int getHighestUIDSoFar(int phase) {
//		int[] lowestTimeStamp = new int[1];
//		msgQueue.forEach((msg) -> {
//			if (msg.getMsgType() == MessageType.SEND && msg.getSenderUID() > highestUID[0]
//					&& msg.getMessagePhase() == phase) {
//				highestUID[0] = msg.getSenderUID();
//			}
//		});
//		this.lowestTimeStamp = highestUID[0];
//		return this.highestUID;
//	}
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

	synchronized public void addMessageToQueue(Message msg) {
		//			setMyTimeStamp(Math.max(msg.timeStamp,getMyTimeStamp()));
		if(msg.getMsgType() == MessageType.Request) {
			
			Treat_Request_Message(msg);
		}
		else
			msgQueue.add(msg);
	}

	synchronized private void Treat_Request_Message(Message msg) {
//		int our_timeStamp = getMyTimeStamp();
		Boolean priority = (msg.getTimeStamp() > temp) || ((msg.getTimeStamp() == temp)
				&& (msg.getsenderUID()>this.getNodeUID()));
		int fileNo = msg.getFileNumber();
		
		if(this.using[fileNo] || (this.waiting[fileNo] && priority)) {
			System.out.println("Reply Deferred for file: "+fileNo+" UID:"+ msg.getsenderUID()+ " tmp:"+msg.getTimeStamp()+" at:"+getMyTimeStamp());
			this.reply_deferred[fileNo][msg.getsenderUID()] = true;
			this.authorize[fileNo][msg.getsenderUID()] = false;
		}
		else if( !(this.using[fileNo] || this.waiting[fileNo]) || (this.waiting[fileNo] && !authorize[fileNo][msg.getsenderUID()])&& !priority) {
			this.authorize[fileNo][msg.getsenderUID()] = false;
			sendReply(fileNo,msg.getsenderUID());
		}
		else if(this.waiting[fileNo] && this.authorize[fileNo][msg.getsenderUID()] && !priority) {
			System.out.println("Setting auth for file: "+fileNo+" UID: "+msg.getsenderUID()+" to:false");
			this.authorize[fileNo][msg.getsenderUID()] = false;
			sendReply(fileNo,msg.getsenderUID());
			sendRequest(fileNo,msg.getsenderUID());
		}
	}

	synchronized public boolean ifRequested() {
		
		for(int i = 0; i < authorize[0].length;i++) {
			if(i == UID)
				continue;
			if(authorize[fileNumber][i] == false)
				return true;
		}
		return false;
	}
	
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
	
	public void sendRequest(int fileNumber,int getsenderUID) {
		for(TCPClient client: connectedClients) {
			if(getsenderUID == client.getClientUID())
				try {
					IncrementMyTimeStamp();
					System.out.println("Sending Request for file: "+fileNumber+" to :"+getsenderUID+" at: "+getMyTimeStamp());
					client.getOutputWriter().writeObject(new Message(temp,this.UID,MessageType.Request,fileNumber));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

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
	
	synchronized public void IncrementMyTimeStamp() {
		timeStamp++;
	}
	
	public int getMyTimeStamp() {
		return timeStamp;
	}
	
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
		
		// TODO What if reply occurs after processing of the queue and we are clearing the queue
		int requiredCount = getCountNotAuthorized(); long count = 0;
		System.out.println("Count not authorized: "+requiredCount);
		while(count != requiredCount) {
			if(!this.msgQueue.isEmpty()) {
				Message msg = getMessageFromQueue();
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
//		this.msgQueue.removeIf(t -> t.getMsgType() == MessageType.Reply);
	}
}
