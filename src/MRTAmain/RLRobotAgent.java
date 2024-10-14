package MRTAmain;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;

import java.awt.geom.Ellipse2D;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.core.behaviours.*;

import java.util.*;

import jade.lang.acl.ACLMessage;
import jade.core.AID;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.Object;

import org.sazonov.fuzzy.engine.EvaluationException;

import DStarLiteJava.State;
import Djkstra.Vertex;
import Enums.eBidValuation;
import Enums.eExecutionMode;
import Enums.eExecutionPhase;
import Enums.eLocalScheduling;
import Enums.ePathStyle;
import Enums.eRobotStatus;
import Enums.eServiceArea;
import Enums.eTSPLoopMode;
import Enums.eTaskState;
import Enums.eTradeStatus;
import IndoorStructure.IndoorStructure;
import IndoorStructure.TransitionPoint;
import IndoorStructure.Wall;
import IndoorStructure.WallNode;

import java.lang.reflect.*;

public  class RLRobotAgent extends RobotAgent {


	private static final long serialVersionUID = 1265113151786164658L;
	public int MAX_TASKS = 100;
	public int MAX_CHARGERS = 100;
	public int MAX_ROBOTS = 100;

	
	private  eExecutionMode EXECUTION_MODE = eExecutionMode.CENTRALIZED;
	private eBidValuation TRADE_APPROACH = eBidValuation.DSTARLITE;
	private eBidValuation ITRADE_APPROACH = eBidValuation.DSTARLITE;
	boolean tradingPhase = false;
	boolean tradeCompleted = false;

	/*
	 * EXECUTION_MODE 0 : DYNAMIC OPTIMAL 1 : OPTIMAL + TRADE
	 */
	private int inactionTicker = 0;
	
	private String taskSold = "";
	private String taskAnnounced = "";
	private int virtualTaskCount = 0;

	private Robotum thisRobot ;
	private RLRobotAgent thisa = this;
	private String auctioneer = "OM";
	private String terrain = "OARENA";
	private String simSetupSelection=""; 

	private double heading = 90; 
	
	// BEHAVIOURS
	private InformLocation inform = new InformLocation(this, 10);
	// private MoveRobot move = new MoveRobot (this, 100);
	private ListenNReply listennreply = new ListenNReply(this, 10);
	
	// private MyWaker mw = new MyWaker (this, 1000);

	private InformAuctioneerTicker informTicker = new InformAuctioneerTicker(
			thisa, 1000);
	// private MovementListener inactionListener = new MovementListener (thisa,
	// 300000);

	private TaskBundle taskBundles[] = new TaskBundle[MAX_TASKS]; // Robota
																	// duyurusu
																	// yapan
																	// bütün
																	// görevler
	private TaskBundle taskQueue[] = new TaskBundle[MAX_TASKS]; // Robotun
																// mevcut görev
																// listesi
	private TaskBundle temptaskQueue[] = new TaskBundle[MAX_TASKS]; // Robotun
																	// mevcut
																	// görev
																	// listesi
	private TaskBundle sortedtaskQueue[] = new TaskBundle[MAX_TASKS]; // Robotun
																		// mevcut
																		// görev
																		// listesi
	private TaskBundle otherTasks[] = new TaskBundle[MAX_TASKS]; // Robotun
																	// teklif
																	// verdiði
																	// ve henüz
																	// sahip
																	// olmadýðý
																	// görevler

	private Robotum robots[] = new Robotum[MAX_ROBOTS]; // Duyuru yapan bütün
														// robotlarýn listesi
	private int robotCount = 0; // Duyuru yapan robot sayýsý

	private Random randomGenerator = new Random();
	private float robotStep = (float) 8.0;
	private int motionInterval; // ms.
	private int simSpeed = 40;
	public int myWay[][] = new int[10000][2];
	public int myStep = 0;

	private int others = 0;
	private TaskBundle ExecutingNow = null;
	private Task Executing = null;
	private int myTaskBundles = 0;
	private int mytempTaskBundles = 0;

	private int proposalstoMyTasks[][] = new int[MAX_ROBOTS][MAX_TASKS];
	private int proposalstoOtherTasks[][] = new int[MAX_ROBOTS][MAX_TASKS];
	private int citiesToVisit;
	public int tradedTask = -1;
	public int proposalstoTradedTask[] = new int[MAX_TASKS];

	public int ENERGY_THRESHOLD = 300;

	private int taskBundleCount = 0;

	// ChargeUnit[] chargers;
	private int TERRAIN_WIDTH = 700;
	private int TERRAIN_HEIGHT = 700;

	private FileOutputStream out;
	private PrintStream p;

	private boolean proposalEvaluationProcess = false;

	public IndoorStructure is_main;
	private eTSPLoopMode TSP_MODE = eTSPLoopMode.TSP_PATH; // 0 : TSP TOUR - 1: TSP PATH - 2: TSP PATH TO
								// STATION
	// private int WALLOVER = 0;
	public int visitedCityCount = 0;

	public Task executionOrder[] = new Task[MAX_TASKS * 100];
	public Task executionOrder_final[] = new Task[MAX_TASKS * 100];
	public String templateSelect;
	public String taskBuyer;
	private int RRTdelta = 20;
	private int RRTmaxIter = 9999999;

	public int propCount = 0;
	private int currentPrice = 0;

	private LinkedList<Integer> mpl = null;
	private int exeTasks = 0;
	private int executingTask = 0;
	private eExecutionPhase EXECUTION_PHASE = eExecutionPhase.NOT_STARTED;
	private TaskBundle worstTaskForMe = null;
	private String lastBought = "";
	private int stationCount = 1;
	private SazanovFuzzy sf_main = new SazanovFuzzy();
	private BuildDStarLite dsl;
	private eServiceArea expMode = eServiceArea.ALL_TERRAIN;

	// private long iTime = 0;
	public int estimatedPickUpDuration = 120;
	//private int bidRange = 2700; // in pixels
	int accessibleRange = 500; // in pixels
	private IOLogger iol;
	private MRTATime mrt = new MRTATime();
	

	private eLocalScheduling localScheduleSelection;
	private ePathStyle pathStyleSelection; 
	private BasicPoint[] bps;
	String rc1[] ; 
	
	//HashMap <String,Class<ParseClass>> m = new HashMap <String, Class<ParseClass>> (); 
	Map<String, Method> methodMap = new HashMap<String, Method>();
	Map<String, Object> handlerMap = new HashMap<String, Object>();
	boolean messageMapFlag = false; 
	TSPMinPath path[] ;
	int rw = 5; 
	private boolean I_CAN_TRADE = true; 
	private int UPPERLIMITFORBID = 2000; 
	public int globalMovementCounter = 0;
	double pre5x , pre5y ; 
	
	eBidValuation CITYTOCITYDISTANCECALCMETHOD =eBidValuation.TMPTSP ; 
	
	Lidar ldr ; 
	
	

public void getCallerMethod ()  
{
	
	  final StackTraceElement[] ste = Thread.currentThread().getStackTrace(); 
	  
	  System.out.println("");
	  System.out.println("----------------RobotAgent->"+thisRobot.robotName+" "+ste[2].getMethodName()+"--"+ste[3].getMethodName()+"--"+ste[4].getMethodName()+"--"+ste[5].getMethodName()+"--");
	  System.out.println("");
	   
}

	
	/*
	 * EXECUTION_PHASE 0 : NOT STARTED 1 : INITIAL ASSIGNMENTS 2 : TRADING
	 */
	public HRectangle OuterRect(HRectangle rect) {
		HRectangle outer = null;
		int xdist = rect.x2 - rect.x1;
		int ydist = rect.y2 - rect.y1;
		xdist = xdist < 500 ? 500 : xdist;
		ydist = ydist < 500 ? 500 : ydist;
		if (xdist > 0 && ydist > 0) {

			int xx1 = rect.x1 - xdist / 2 > 0 ? rect.x1 - xdist / 2 : 0;
			int xx2 = rect.x2 + xdist / 2 < TERRAIN_WIDTH ? rect.x2 + xdist / 2
					: TERRAIN_WIDTH - 1;
			int yy1 = rect.y1 - ydist / 2 > 0 ? rect.y1 - ydist / 2 : 0;
			int yy2 = rect.y2 + ydist / 2 < TERRAIN_HEIGHT ? rect.y2 + ydist
					/ 2 : TERRAIN_HEIGHT - 1;
			outer = new HRectangle(xx1, yy1, xx2, yy2);
		} else
			outer = rect;

		return outer;

	}

	public void Yaz(String st) {

		iol.Yaz(st);

	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}

	protected void setup() {
	
		thisRobot = new Robotum();
		
		//getCallerMethod();
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ROBOT");
		sd.setName(getLocalName());
		register(sd);

		// System.exit(0);

		Object[] args = getArguments();

		int tempx = 0, tempy = 0;

		if (args != null) {
			/*
			 * for (int i = 0; i < args.length; ++i) { Yaz("- "+args[i]); }
			 */
	      	iol = new IOLogger(thisRobot.robotName, Integer.parseInt(args[24].toString()));
			thisRobot.robotName = args[0].toString();
			auctioneer = args[3].toString();
			// Yaz ("auctioneeragentname :  "+auctioneer);
			thisRobot.colorIndex = Integer.parseInt(args[4].toString());
			TRADE_APPROACH = eBidValuation.values() [Integer.parseInt(args[5].toString())];
			ITRADE_APPROACH = eBidValuation.values() [Integer.parseInt(args[10].toString())]; 
			TERRAIN_WIDTH = Integer.parseInt(args[6].toString());
			TERRAIN_HEIGHT = Integer.parseInt(args[7].toString());
			templateSelect = args[8].toString();
			thisRobot.speed = Integer.parseInt(args[11].toString());

			stationCount = Integer.parseInt(args[12].toString());
			thisRobot.capacityNormal = Integer.parseInt(args[13].toString());
			thisRobot.capacityExtra = Integer.parseInt(args[14].toString());

			TSP_MODE = eTSPLoopMode.values() [Integer.parseInt(args[9].toString())];

			terrain = args[15].toString();
			// Yaz ("terrainagentname :  "+terrain);
			thisRobot.rect.x1 = Integer.parseInt(args[16].toString());
			thisRobot.rect.y1 = Integer.parseInt(args[17].toString());
			thisRobot.rect.x2 = Integer.parseInt(args[18].toString());
			thisRobot.rect.y2 = Integer.parseInt(args[19].toString());
			expMode = eServiceArea.values()[Integer.parseInt(args[20].toString())];
System.out.println("expMode: "+expMode);
			estimatedPickUpDuration = Integer.parseInt(args[21].toString());
			localScheduleSelection = eLocalScheduling.values() [Integer.parseInt(args[22].toString())];
			pathStyleSelection =ePathStyle.values()[Integer.parseInt(args[23].toString())];
			CITYTOCITYDISTANCECALCMETHOD = TransformDistanceCalculationStrategy();
			simSetupSelection = args[25].toString();
			

			thisRobot.outerRect = OuterRect(thisRobot.rect);

			is_main = new IndoorStructure(templateSelect);

			if (args.length == 26) {

				if (!OutOfTerrain(Integer.parseInt(args[1].toString()), Integer.parseInt(args[2].toString()))) {

					tempx = Integer.parseInt(args[1].toString());
					tempy = Integer.parseInt(args[2].toString());
				}

			} else {
				tempx = randomGenerator.nextInt(TERRAIN_WIDTH);
				tempy = randomGenerator.nextInt(TERRAIN_HEIGHT);
				Yaz(thisRobot.robotName	+ "> Baslangic koordinatlari girisinde hata! Rasgele konumlama yapilacak...");

			}
			
			ldr=  new Lidar (is_main, thisRobot); 

		}
		
	

		thisRobot.ResetLocation(tempx, tempy);
		Yaz(thisRobot.robotName + "> ***");
		dsl = null;//new BuildDStarLite(is_main);
		// trade yaklaþýmý d* ise ya da d*-tpm ise
		if (TRADE_APPROACH == eBidValuation.DSTARLITE || TRADE_APPROACH == eBidValuation.TMPTSP  || TRADE_APPROACH == eBidValuation.DSTARLITETSP ) {
			// if (expMode < 4 )
			// dsl = new BuildDStarLite(is_main);
			/*
			 * else dsl = new BuildDStarLite(is_main, thisRobot.rect);
			 */

			// dsl çözümü diðer robotlarda bitmemiþ olabilir. Bu nedenle birkaç
			// saniye bekletmekte fayda var.
			Sleepy (3000); 
		}

		addBehaviour(inform);
		addBehaviour(informTicker);
		addBehaviour(new InformLocationOnce());
        
		// addBehaviour(move);
		addBehaviour(listennreply);
		Yaz(thisRobot.robotName + "> *** ");
		//PrepareFuzzy();
		// addBehaviour(inactionListener);

		/*
		 * SazanovFuzzy sf = new SazanovFuzzy(); try { selection = (int)
		 * sf.SazanFuzzy(is_main.walls.size(), is_main.edges.size()); } catch
		 * (EvaluationException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	
		/*
		 * OutputStream output = null; try { output = new
		 * FileOutputStream(thisRobot.robotName+".txt"); } catch
		 * (FileNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } PrintStream printOut = new
		 * PrintStream(output);
		 * 
		 * System.setOut(printOut);
		 */
	}
	public void PrepareFuzzy () 
	{
		//getCallerMethod();
		try {
			sf_main.InitializeFuzzyMemberships();
			sf_main.LoadPricingRules(); 
			sf_main.LoadValuationRules(); 
			
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void register(ServiceDescription sd) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	AID getService(String service) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(service);
		dfd.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, dfd);
			if (result.length > 0)
				return result[0].getName();
		} catch (Exception fe) {
		}
		return null;
	}

	public AID[] searchDF(String service)
	// ---------------------------------
	{
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(service);
		dfd.addServices(sd);

		SearchConstraints ALL = new SearchConstraints();
		ALL.setMaxResults(new Long(-1));

		try {
			DFAgentDescription[] result = DFService.search(this, dfd, ALL);
			AID[] agents = new AID[result.length];
			for (int i = 0; i < result.length; i++)
				agents[i] = result[i].getName();
			return agents;

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		return null;
	}

	public void SearchForRobots() {
		//getCallerMethod();
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			DFService.search(this, dfd);

			AID[] robots = searchDF("ROBOT");
			// System.out.print("\nROBOTS: ");
			if (robots!=null)
			{
				for (int i = 0; i < robots.length; i++) {
					AddUpdateRobot(robots[i].getLocalName());
				}
			}
			// System.out.print( robots[i].getLocalName() + ",  ");
			// Yaz();

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}
	
	 public void CreateMap() throws Exception {
			
		 Yaz("CreateMap") ;    
		 
		 Class[] cArg = new Class[11];
	        cArg[0] =  ACLMessage.class;  
	        cArg[1] =  Class.forName("java.lang.String"); 
	        cArg[2] =  Class.forName("java.lang.String"); 
	        cArg[3] =  Class.forName("java.lang.String"); 
	        cArg[4] =  Class.forName("java.lang.String"); 
	        cArg[5] =  Class.forName("java.lang.String"); 
	        cArg[6] =  Class.forName("java.lang.String"); 
	        cArg[7] =  Class.forName("java.lang.String"); 
	        cArg[8] =  Class.forName("java.lang.String"); 
	        cArg[9] =  Class.forName("java.lang.String"); 
	        cArg[10] =  Class.forName("java.lang.String"); 
	        
		    
		    
		 
		// methodMap.put("0000", RobotAgent.ListenNReply.class.getMethod("RandomTaskCheck", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));
		 methodMap.put("00", RLRobotAgent.ListenNReply.class.getMethod("Proposal", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));
		 methodMap.put("09", RLRobotAgent.ListenNReply.class.getMethod("EndOfTour", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));
		 methodMap.put("04", RLRobotAgent.ListenNReply.class.getMethod("BuyTaskFromAuctioneer", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("04RAR", RLRobotAgent.ListenNReply.class.getMethod("CompleteSellingProcess", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("04RNR", RLRobotAgent.ListenNReply.class.getMethod("TradeCompleted", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("066", RLRobotAgent.ListenNReply.class.getMethod("TaskCompletionAcknowledge",ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
			
		 methodMap.put("10", RLRobotAgent.ListenNReply.class.getMethod("EndOfTrade", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("61", RLRobotAgent.ListenNReply.class.getMethod("SimulationStarted", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("91", RLRobotAgent.ListenNReply.class.getMethod("LogToFile", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("103", RLRobotAgent.ListenNReply.class.getMethod("TaskAccepted", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("104", RLRobotAgent.ListenNReply.class.getMethod("TaskRejected",ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("112", RLRobotAgent.ListenNReply.class.getMethod("IBecomeSELLER", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("115", RLRobotAgent.ListenNReply.class.getMethod("TradeIsOver", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("116", RLRobotAgent.ListenNReply.class.getMethod("ExecutionStart", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("121", RLRobotAgent.ListenNReply.class.getMethod("MakeABid", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("122", RLRobotAgent.ListenNReply.class.getMethod("IncomingBid", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("123", RLRobotAgent.ListenNReply.class.getMethod("BuyTaskFromRobot", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 methodMap.put("191", RLRobotAgent.ListenNReply.class.getMethod("Reset", ACLMessage.class, String.class, String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class));	
		 // methodMap.put("123", RobotAgent.ListenNReply.class.getMethod("BuyTaskFromRobot", cArg));	
		 
	 }

	public void ListTasks() {

		for (int i = 0; i < taskBundleCount; i++) {
			TaskBundle tb = taskBundles[i];
			Yaz(thisRobot.robotName + "> TASK BUNDLE " + Integer.toString(i));
			for (int j = 0; j < tb.taskCount(); j++) {
				Task t = tb.GetTask(j);
				Yaz(thisRobot.robotName + ">         " + t.taskName);
			}

		}

	}

	public void SendTaskStateToAuctioneer(Task t) {

		ACLMessage fmsg = new ACLMessage(ACLMessage.INFORM);
		AID faid = new AID(auctioneer, AID.ISLOCALNAME);
		fmsg.addReceiver(faid);
		fmsg.setLanguage("English");

		fmsg.setContent("66_" + thisRobot.robotName + "_" + t.taskName + "_"
				+ Integer.toString(t.state.getValue()));
		send(fmsg);

	}

	public void SendACLMessage(String rcv[], int rCount, String content[],
			int pCount) {

		String msgContent = "";

		for (int i = 0; i < pCount; i++) {
			msgContent = msgContent + content[i];
			msgContent = msgContent + "_";
		}

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

		for (int i = 0; i < rCount; i++) {
			AID aid = new AID(rcv[i], AID.ISLOCALNAME);
			msg.addReceiver(aid);
		}

		msg.setLanguage("English");
		msg.setContent(msgContent);
		send(msg);
	}

	public boolean OutOfTerrain(int tx, int ty) {
		return tx < 0 | ty < 0 | tx > TERRAIN_WIDTH | ty > TERRAIN_HEIGHT;
	}

	public void ResetTaskList() {
		for (int i = 0; i < MAX_TASKS; i++) {
			TaskBundle tb = taskBundles[i];
			while (tb != null && tb.taskCount() > 0) {
				tb.RemoveTask(0);
			}
			taskBundles[i] = null;
		}

		taskBundleCount = 0;
	}

	public void ListMyTasks() {
		Yaz(thisRobot.robotName + "> My Task Bundles To Execute = "
				+ Integer.toString(myTaskBundles));
		for (int i = 0; i < myTaskBundles; i++) {

			TaskBundle tb = taskQueue[i];
			Yaz(thisRobot.robotName + ">  TASK BUNDLE :  "
					+ Integer.toString(i) + " - Task Count : "
					+ Integer.toString(tb.taskCount));
			for (int j = 0; j < tb.taskCount; j++) {
				Task t = tb.GetTask(j);

				System.out.print(thisRobot.robotName
						+ ">          taskBundles[" + Integer.toString(i)
						+ "].GetTask(0) = " + t.taskName);
				System.out.print(" xLoc :  " + Integer.toString(t.xLoc)
						+ " yLoc : " + Integer.toString(t.yLoc));
				System.out.print(" TRADESTATUS :  "
						+ tb.tradeStatus + " offered "
						+ tb.offered);
				Yaz(" bundleHead :  " + t.bundleHead + " price : "
						+ Integer.toString(t.price));
			}
		}
	}

	public void ResetProposals() {
		for (int i = 0; i < MAX_ROBOTS; i++)
			for (int j = 0; j < MAX_TASKS; j++)
				proposalstoMyTasks[i][j] = -1;

	}

	public void ResetProposalsToTradedTask() {
		for (int i = 0; i < MAX_ROBOTS; i++)
			proposalstoTradedTask[i] = -1;

	}

	public void ResetProposalsToOtherTasks() {
		for (int i = 0; i < MAX_ROBOTS; i++)
			for (int j = 0; j < MAX_TASKS; j++)
				proposalstoOtherTasks[i][j] = -1;
		others = 0;

	}

	public boolean ProposalsCompleted() {
		for (int i = 0; i < robotCount; i++)
			for (int j = 0; j < myTaskBundles; j++) {
				if (proposalstoMyTasks[i][j] == -1)
					return false;
			}

		return true;
	}

	public int TaskBundleIndex(String taskName) {

		for (int i = 0; i < taskBundleCount; i++) {
			TaskBundle tb = taskBundles[i];
			for (int j = 0; j < tb.taskCount(); j++) {
				Task t = tb.GetTask(j);
				String tskName = t.taskName;
				if (tskName.compareTo(taskName) == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	public int TaskBundleIndex_B(String bundleName) {

		for (int i = 0; i < taskBundleCount; i++) {
			TaskBundle tb = taskBundles[i];
			Task t = tb.GetTask(0);
			if (t != null && t.bundleHead.compareTo(bundleName) == 0)
				return i;
		}
		return -1;
	}

	public void ResetMyWay() {

		myStep = 0;

	}

	public int RobotIndex(String robotName) {

		for (int i = 0; i < robotCount; i++) {
			Robotum r = robots[i];
			String rName = r.robotName;

			if (rName.compareTo(robotName) == 0) {
				return i;
			}
		}
		return -1;
	}

	public void AddUpdateRobot(String rName) {

		if (rName.compareTo(thisRobot.robotName) == 0) {
			// do Nothing
		}

		else {

			Robotum r;
			int rIndex = RobotIndex(rName);
			// Robot bilgisi mevcutsa
			if (rIndex < 0) {
				r = new Robotum();
				rIndex = robotCount;
				robots[robotCount++] = r;
			} else {
				r = robots[rIndex];
			}

			r.robotName = rName;

			robots[rIndex] = r;

			// System.out.print(thisRobot.robotName+"> "+r.robotName+" robotu listeye eklendi.");
			PrintWallWatch();
		}
		/*
		 * BU FONKSIYONDA PARAMETRELERÝN GEÇERLÝLÝK KONTROLÜ YAPILMAMIÞTIR.
		 * GEÇERLÝ GÖNDERÝLDÝÐÝ VARSAYILMIÞTIR.
		 */

	}

	public Station ClosestStation(Task t) {
		int minn = 100000;
		Station closest = null;
		for (int i = 0; i < is_main.stationCount; i++) {
			Station st = is_main.GetStation(i);
			int dist = CalcTPDistance(t, st);

			if (dist < minn) {
				minn = dist;
				closest = st;
			}

		}
		return closest;

	}

	// Task bilgisi ekleme ve guncelleme
	public void AddUpdateTask(String tName, String xLoc, String yLoc, String bundleHead, String price, boolean mine, eTradeStatus tradeSt, eTaskState taskSt) {
		Yaz("------------------------------------------------------------");
		Yaz(thisRobot.robotName + "> *  00 * " + tName + "_" + bundleHead);
		Yaz("------------------------------------------------------------");
		TaskBundle tb;
		Task t = null;
		int tBundleIndex = TaskBundleIndex_B(bundleHead);
		int tIndex = TaskIndex(tName);

		// Task bilgisi mevcut deðilse
		if (tBundleIndex < 0) {
			tb = new TaskBundle();
			t = new Task();
			t.taskName = tName;
			t.xLoc = Integer.parseInt(xLoc);
			t.yLoc = Integer.parseInt(yLoc);
			t.closest = ClosestStation(t);
			tb.AddTask(t);
			tb.taskCount = 1;

			tIndex = 0;
			tBundleIndex = taskBundleCount;
			taskBundles[taskBundleCount++] = tb;

		} else {   // TASK BUNDLE görevlerim arasýnda ise   
			tb = taskBundles[tBundleIndex];
			tIndex = tb.TaskIndex(tName);
			if (tIndex < 0) {   // task yoksa 
				t = new Task();
				t.taskName = tName;
				t.xLoc = Integer.parseInt(xLoc);
				t.yLoc = Integer.parseInt(yLoc);
				t.closest = ClosestStation(t);
				t.tradeStatus = eTradeStatus.OWNED;
				tb.AddTask(t);

				tIndex = tb.taskCount - 1;
			} else {   // task varsa 
				t = tb.GetTask(tIndex);
			}  

		} // end of else

		t.price = Integer.parseInt(price);
		t.bundleHead = bundleHead;
		t.state = taskSt; //eTaskState.NOT_STARTED;
		t.mineTask = mine;
		t.tradeStatus = tradeSt;
		
	
		taskBundles[tBundleIndex].SetTask(tIndex, t);
		taskBundles[tBundleIndex].state = t.state; //eTaskState.NOT_STARTED;
		taskBundles[tBundleIndex].tradeStatus = t.tradeStatus;

		/*
		 * BU FONKSIYONDA PARAMETRELERÝN GEÇERLÝLÝK KONTROLÜ YAPILMAMIÞTIR.
		 * GEÇERLÝ GÖNDERÝLDÝÐÝ VARSAYILMIÞTIR.
		 */
	}

	protected void onGuiEvent(GuiEvent ev) {

	}

	
	// Inner class
	public class InformAuctioneerTicker extends TickerBehaviour {

		/**
	 * 
	 */
		private static final long serialVersionUID = -7610736996603633081L;

		public InformAuctioneerTicker(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			AID aid = new AID(auctioneer, AID.ISLOCALNAME);
			msg.addReceiver(aid);
			msg.setLanguage("English");

			String taskName = ExecutingNow != null ? ExecutingNow.GetTask(0).taskName
					: "null";

			msg.setContent("01_" + thisRobot.robotName + "_"
					+ Integer.toString(thisRobot.xLoc) + "_"
					+ Integer.toString(thisRobot.yLoc) + "_"
					+ Integer.toString(thisRobot.status.getValue()) + "_" + taskName);
			send(msg);

		}

	}
	/*
	public class InformTicker extends TickerBehaviour {

		/**
	 * 
	 
		private static final long serialVersionUID = -7610736996603633081L;

		public InformAuctioneerTicker(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			AID aid = new AID(auctioneer, AID.ISLOCALNAME);
			msg.addReceiver(aid);
			msg.setLanguage("English");

			String taskName = ExecutingNow != null ? ExecutingNow.GetTask(0).taskName
					: "null";

			msg.setContent("01_" + thisRobot.robotName + "_"
					+ Integer.toString(thisRobot.xLoc) + "_"
					+ Integer.toString(thisRobot.yLoc) + "_"
					+ Integer.toString(thisRobot.status.getValue()) + "_" + taskName);
			send(msg);

		}

	}
	*/
	

	public boolean MyStation(Station st) {
		// Eðer robotun konumu istasyonun konumu civarýndaysa
		// örneðin 10 birim
		// o zaman "benim istasyonum" dur diyoruz.
		if (Math.abs(thisRobot.xLoc - st.xLoc) < 10
				&& Math.abs(thisRobot.yLoc - st.yLoc) < 10)
			return true;
		else
			return false;

	}
	public eBidValuation TransformDistanceCalculationStrategy ()
	{
		if (pathStyleSelection == ePathStyle.BRESENHAM || pathStyleSelection == ePathStyle.DJKSTRA)
		{
			CITYTOCITYDISTANCECALCMETHOD = eBidValuation.TMPTSP; 
		}
		else if (pathStyleSelection == ePathStyle.DSTARLITE )
		{
			CITYTOCITYDISTANCECALCMETHOD = eBidValuation.DSTARLITE; 
		}
		else if (pathStyleSelection == ePathStyle.RRT)
		{
			CITYTOCITYDISTANCECALCMETHOD = eBidValuation.RRT; 
		}
		return CITYTOCITYDISTANCECALCMETHOD;
		
	}

	public class CreateSchedule_MTP extends OneShotBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = 8970062481684507292L;

		public void action() {
		Yaz(thisRobot.robotName + " > CreateSchedule_MTP");
			long begin = System.currentTimeMillis();
			CloneTaskQueue();
			Station st = null;


			int mini = -1;
			path = new TSPMinPath[is_main.stationCount];
			TSPMinPath tmpPath = null;
			
			System.out.println("TSP_MODE: "+TSP_MODE);
			System.out.println("stationCount: "+stationCount);
			System.out.println("TRADE_APPROACH: "+TRADE_APPROACH);
			System.out.println("Distance Calculation Method For Scheduling: "+CITYTOCITYDISTANCECALCMETHOD);
			
			if ((TSP_MODE == eTSPLoopMode.TSP_PATH || TSP_MODE==eTSPLoopMode.TSP_TOUR) || stationCount == 0) {
				mini = 0;
				st = null;

				tmpPath = TSPCreate(st);
				System.out.println("tmpPath point count"+ tmpPath.PointCount());
				
				tmpPath.TSPPathLength();
				System.out.println("tmpPath point count"+ tmpPath.PointCount());
				 PrintPath(tmpPath);
			}  
			else {
				tmpPath  = BestStation(); 
				if (tmpPath != null) 
					{
					
					// tmpPath = path[mini];
				     //st = is_main.GetStation(mini);
				     
				     Yaz(thisRobot.robotName + " > CreateSchedule_MTP() -> mini: "+mini);
				    
				     PrintPath(tmpPath);
				     
				     }
			  else {
				Yaz(thisRobot.robotName + " > CreateSchedule_MTP() -> Final istasyona çizelgeleme YAPILAMADI !!!! - Uygun istasyon bulunamadý ");
			}
			} // else 
			if (tmpPath!=null /*&& st!=null*/) 
			{
				CreateScheduleMTP (tmpPath, st); 
				Yaz(thisRobot.robotName + " > CreateSchedule_MTP() -> Çizelge tamamlandý : ");
				ScheduleTime(begin);
			}
			else 
			{
				Yaz(thisRobot.robotName + " > !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  ");
				Yaz(thisRobot.robotName + " > CreateSchedule_MTP() -> Çizelgeleme eylemi baþarýsýz !!!!  ");
				Yaz(thisRobot.robotName + " > !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  ");
			}

		} // action
		
		public void PrintPath(TSPMinPath tp) {
			
			Yaz ("");
			Yaz ("----------------------------------");
			System.out.println("Calculated Path of " + thisRobot.robotName); 
			for (int i=0; i<tp.tourSize;i++) 
			{
				System.out.println(thisRobot.robotName +"> i: " + i + " tour " + tp.tour.get(i) + " name: "+executionOrder[tp.tour.get(i)].taskName);
			}
			
			Yaz ("----------------------------------");
			Yaz ("");
			
		}
		public TSPMinPath BestStation () 
		{
			double minLen = 10000000;
			Station st = null;
			Station minSt = null; 
			int mini = -1; 
			TSPMinPath tspm=null, tmpp = null; 
			Yaz(thisRobot.robotName +"  BEST STATION() ");
			//Yaz(thisRobot.robotName +"   station count " +stationCount);
			for (int i = 0; i < stationCount; i++) {
				st = is_main.GetStation(i);

				Yaz(thisRobot.robotName +"   Best station i: " + i + " / station count " +stationCount + " minlen: " + minLen);
				path[i] = null; 

				if (IsInExtendedBoundary(st.xLoc, st.yLoc) || MyStation(st)) {
				
					tmpp= TSPCreate(st);
				    //tspm = TSPCreate(st);
					double stLen = tmpp.TSPPathLength();
				    
					Yaz(thisRobot.robotName +"   " +i+ " stlen: "+stLen + " minlen: " + minLen);
					
					if (stLen < minLen) {
						minLen = stLen;
						mini = i;
						// tmpPath = path[i];
						tspm = null; 
						tspm = new TSPMinPath(tmpp);
						minSt = st; 
						Yaz(thisRobot.robotName + "> Þimdilik en kýsa path  sýra : " + mini + " uzunluk : " + minLen + " st " + st.taskName + "(" + st.xLoc + "-"+ st.yLoc + ")");
						//PrintPath (path[i]); 
						PrintPath (tmpp); 
						//PrintPath (tspm); 
						
					} // is stlen

				} // if
				else 
				{
					path[i] = null; 
					tmpp = null; 
				}
				Yaz(thisRobot.robotName + "> end of i: "+i);
				//PrintPath (tspm); 

			} // for 
			if (mini >-1) 
			{
						Yaz(thisRobot.robotName + "> Seçilen istasyon : " + mini + " uzunluk : " + minLen + " st " + minSt.taskName + "(" + minSt.xLoc + "-"+ minSt.yLoc + ")");
	      //  PrintPath(tspm);
						tspm= TSPCreate(is_main.GetStation(mini));
					    //tspm = TSPCreate(st);
						double stLen = tspm.TSPPathLength();
			}


			
			
			return tspm; 
		}
		public void CreateScheduleMTP (TSPMinPath tmpPath, Station st) 
		{
			getCallerMethod();	
			mpl = new LinkedList<Integer>();
			List<Integer> tour=tmpPath.tour;
			
			
			for (int i=0; i<tmpPath.tourSize;i++) 
			{
			    				
				// mpl.add(tmpPath.points[tour.get(i)].index);
				// mpl.addAll(tmpPath.points);
			// 	mpl.addAll(tour);
				mpl.add(tour.get(i));
			
				
			}
/*
			Yaz(thisRobot.robotName + " robotunun çizelgesi"); 
				for (int ii = 0; ii < mpl.size(); ii++) {
					Yaz(thisRobot.robotName + " * " + ii + " -> " + mpl.get(ii));
					Yaz(thisRobot.robotName + " * " + executionOrder[ii].taskName);
				}
*/
				CreateFinalSchedule_Optimal(st);
				// CreateFinalSchedule_Fast(st);
		    	SendScheduleCompletedMessage();

	}
		
		public void ScheduleTime (long begin) 
		{
			
			long end = System.currentTimeMillis();
			long duration = end - begin;
			Yaz(thisRobot.robotName + ">CreateSchedule_MTP() time :" 	+ duration);

			thisRobot.schTime += (duration / 1000) + 1;
			
			/*
			if (expMode == eServiceArea.ALL_TERRAIN) {
				thisRobot.schTime += 10;

			}

			else {
				thisRobot.schTime += (duration / 1000) + 1;
			}*/
			addBehaviour(new InformLocationOnce());
		}


	}//  class CreateSchedule_MTP
	
	public class CreateSchedule_MST extends OneShotBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = 6115228462502275959L;

		public void action() {

			Yaz(thisRobot.robotName + " > CreateSchedule_MST");
			CloneTaskQueue();
			Station st = null;

			int minLen = 10000000;
			int mini = -1;

			if (TSP_MODE.getValue() < eTSPLoopMode.TSP_PATHTOSTATION.getValue() || stationCount == 0) {
				// do nothing

			} 
			else {
				for (int i = 0; i < stationCount; i++) {
					st = is_main.GetStation(i);
					// KruskalEdges vv = MSTFinalEdges(st, 6);
					int stLen = MSTLength(st, CITYTOCITYDISTANCECALCMETHOD);

					if (stLen < minLen) {
						minLen = stLen;
						mini = i;

					}
				}
			}

			if (mini > -1) {
				st = is_main.GetStation(mini);
			}

			/*
			 * KruskalEdges vv = MSTFinalEdges(st, TRADE_APPROACH); int total =
			 * 0; for (EdgeMST edge : vv.getEdges()) { Yaz(edge);
			 * 
			 * total += edge.getWeight(); }
			 */

		}

	}

	public int GetNearest(Task t) {
		int minDist = 10000;
		int mini = -1;
		for (int i = 0; i < mytempTaskBundles; i++) {
			Task ii = temptaskQueue[i].GetTask(0);
			if (ii.nearestFlag == false) {
				int tpDist = CalcTPDistance(ii, t);
				if (tpDist < minDist && tpDist > 0) {
					minDist = tpDist;
					mini = i;
				}
			}
		}
		return mini;
	}

	public class CreateSchedule_OBO_Nearest extends OneShotBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = -2835700951580161128L;

		public void action() {

			Yaz(thisRobot.robotName + " > CreateSchedule_OneByOne Nearest");
			CloneTaskQueue();
			Station st = null;

			int minLen = 10000000;
			int mini = -1;
			citiesToVisit = 0;
			mpl = new LinkedList<Integer>();
			executionOrder[0] = new Task(thisRobot);
			mpl.add(0);
			for (int i = 0; i < mytempTaskBundles; i++) {
				executionOrder[++citiesToVisit] = temptaskQueue[i].GetTask(0);
			}

			for (int i = 0; i < citiesToVisit; i++) {
				int nearest = GetNearest(executionOrder[i]);
				Yaz(thisRobot.robotName + " > i : "
						+ executionOrder[i].taskName + " nearest : "
						+ executionOrder[nearest + 1].taskName);
				if (nearest > -1) {
					executionOrder[nearest + 1].nearestFlag = true;
					mpl.add(nearest + 1);
				}

			}

			if (TSP_MODE == eTSPLoopMode.TSP_TOUR) {
				executionOrder[++citiesToVisit] = new Task(thisRobot);
				mpl.add(citiesToVisit);
			} else if (TSP_MODE == eTSPLoopMode.TSP_PATHTOSTATION) {
				for (int i = 0; i < stationCount; i++) {
					st = is_main.GetStation(i);

					int stLen = CalcTPDistance(st,
							executionOrder[citiesToVisit]);

					if (stLen < minLen) {
						minLen = stLen;
						mini = i;

					}
				}

				if (mini > -1) {
					st = is_main.GetStation(mini);
					executionOrder[++citiesToVisit] = st;
					mpl.add(citiesToVisit);
				} else
					st = null;
			}

			for (int ii = 0; ii < mpl.size(); ii++) {
				Yaz(thisRobot.robotName + " * " + ii + " -> " + mpl.get(ii));
				Yaz(thisRobot.robotName + " * " + executionOrder[ii].taskName);
			}
			citiesToVisit++;
			CreateFinalSchedule_Fast(st);

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			AID aid = new AID(auctioneer, AID.ISLOCALNAME);
			msg.addReceiver(aid);
			msg.setLanguage("English");

			msg.setContent("69_" + thisRobot.robotName);
			send(msg);

		}
	}

	

	public void StartExecution() {

		if (simSetupSelection.compareTo("RL")==0) 
		{
			
		}
		
		// MRTAMode
		else 
		{
		thisRobot.status = eRobotStatus.TRAVERSING;
		// addBehaviour(new ScheduleDelay(thisa, 5000));
		/*
		 * switch (LOCAL_SCHEDULE) {
		 * 
		 * // TSP on manually located Transition Points case 0: addBehaviour(new
		 * CreateSchedule_MTP()); break; // TSP on RRT-based located Transition
		 * Points case 1: addBehaviour(new CreateSchedule_RRT()); break; case 2:
		 * addBehaviour(new CreateSchedule_MST()); break; }
		 */
		if (WaitingTasks() > 0) {

			Yaz(thisRobot.robotName + " StartExecution ");
		//	addBehaviour(new ScheduleDelay(thisa, 10));
			addBehaviour(new ExecutionDelay(thisa, 1000));
		} else {
			Yaz(thisRobot.robotName + " StartExecution - Yapacak görev yok ");
		} 
		
		}
	}

	public boolean ProposalsCompleted_Sequential() {
		boolean repliedFlag = true;
		for (int i = 0; i < robotCount; i++) {
			if (proposalstoTradedTask[i] < 0) {
				repliedFlag = false;
				break;
			}
		}

		return repliedFlag;
	}

	public boolean EvaluateProposals_Sequential() {
		int minValue = 4000;
		int mini = -1;

		if (robotCount > 1) {
			for (int i = 0; i < robotCount; i++) {
				if (proposalstoTradedTask[i] < minValue
						&& proposalstoTradedTask[i] > 0) {
					minValue = proposalstoTradedTask[i];
					mini = i;
				}
			}
		} else if (robotCount == 1) {
			if (proposalstoTradedTask[0] < currentPrice) {
				mini = 0;
			}

		}

		if (mini > -1) {

			Yaz(thisRobot.robotName + "> "
					+ taskQueue[tradedTask].GetTask(0).taskName + " görevinin "
					+ robots[mini].robotName
					+ " robotuna satýþ iþlemi baþlatýldý.");
			SellTask_Sequential(mini, tradedTask);

			return true;

			// // Yaz("task satýldý - reset");

		} else {
			taskQueue[tradedTask].tradeStatus = eTradeStatus.OWNED;
			taskSold = "*";
			return false;

		}

	}

	public void CompleteSellingProcess(ACLMessage msg, String temp[]) {

		Yaz(thisRobot.robotName + "> " + taskSold
				+ " görevinin satýþ iþlemi tamamlandý.");
		taskSold = taskQueue[tradedTask].GetTask(0).taskName;
		ResetOffers();
		taskQueue[tradedTask].state = eTaskState.SOLD;
		for (int k = tradedTask; k < myTaskBundles - 1; k++) {
			taskQueue[k] = taskQueue[k + 1];

		}

		myTaskBundles--;
		ResetProposalsToTradedTask();
		// taskListChanged = true;

		if (WaitingTasks() == 0) {
			Yaz(thisRobot.robotName
					+ "> "
					+ taskSold
					+ " görevinin satýþ iþlemi sonucunda baþka görevim kalmadý.");
			thisRobot.status = eRobotStatus.IDLE;
			addBehaviour(new InformLocationOnce());
		} else {
			Yaz(thisRobot.robotName + "> " + taskSold
					+ " görevinin satýþ iþlemi sonucu hala görevim var.");

		}

		tradeCompleted = true;

	}

	public class TaskListener extends WakerBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = -6665240822194671321L;

		public TaskListener(Agent a, long interval) {
			super(a, interval);
		}

		protected void onWake() {
			if (ExecutingNow == null) {
				if (Math.abs(thisRobot.xLoc - thisRobot.startX) < 5
						&& Math.abs(thisRobot.yLoc - thisRobot.startY) < 5) {

				} else {
					Yaz(thisRobot.robotName + "> Baþlangýca dönüyorum.");
					PrintWallWatch();
					Task t = new Task(thisRobot.startX, thisRobot.startY,
							"initial");
					ExecutingNow = new TaskBundle();
					ExecutingNow.AddTask(t);
					thisRobot.status = eRobotStatus.TRAVERSING;

				}

			}
		}
	}

	public class MovementListener extends TickerBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = 6054606082817177085L;

		public MovementListener(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {

			if (thisRobot.xLoc == thisRobot.preX
					&& thisRobot.yLoc == thisRobot.preY) {
				inactionTicker++;
			}
			if (inactionTicker > 1000) {

				Yaz(thisRobot.robotName + "> BELÝRLÝ BÝR SÜRE HAREKET TESPÝT EDÝLEMEDÝ.......");
				ExecutingNow = null;
				thisRobot.status = eRobotStatus.IDLE;
				inactionTicker = 0;
				if (WaitingTasks() > 0)
					StartExecution();

			}

			/*
			 * if (thisRobot.xLoc == thisRobot.preX && thisRobot.yLoc ==
			 * thisRobot.preY ) { inactionTicker++; } if (inactionTicker > 5) {
			 * 
			 * ExecutingNow = null; thisRobot.status = 0; inactionTicker = 0 ; }
			 */

		}
	}

	public void GetWay (Task fr, Task to) 
	{
		
		if (localScheduleSelection == eLocalScheduling.TSP) 
		{
		      if (pathStyleSelection == ePathStyle.BRESENHAM) 
		      {
		    	  GetWay_Bresenham(fr, to);
		      }
		      
		      else if (pathStyleSelection == ePathStyle.DJKSTRA) 
		      {
		    	  GetWay_Fast(fr, to);
		    	 
		      }
		      if (pathStyleSelection == ePathStyle.DSTARLITE) 
		      {
		    	  GetWay_DStar(fr, to);
		      }
		      if (pathStyleSelection == ePathStyle.RRT) 
		      {
		    	  GetWay_RRT(fr, to);
		      }
			
			
			/*distanceCalculationMethod.getValue()) {
		case eDistanceCalculationMethod.BRESENHAM.getValue():
			 //GetWay_Optimal(fr, to);
			GetWay_Bresenham(fr, to);
			break;
		case 1:
			GetWay_RRT(fr, to);
			break;
		case 2:
			GetWay_DStar(fr, to);
			break;
		case 3:
			GetWay_Fast(fr, to);
			break;

		default:
			GetWay_Optimal(fr, to);
			break;

		}*/
				
		}
		else if (localScheduleSelection == eLocalScheduling.NEAREST) 
		{
			
		}
		else if (localScheduleSelection == eLocalScheduling.BYNAME) 
		{
			
		}
		else if (localScheduleSelection == eLocalScheduling.RANDOM) 
		{
			
		}
		
		
	}
	public void CreateFinalSchedule_Optimal(Station st) {
		getCallerMethod();
		long begin = System.currentTimeMillis();
		exeTasks = 0;
		
		Yaz ("Create_optimal -> localschedule :"+localScheduleSelection + " mpl.size:" + mpl.size());
		for (int ii = 1; ii < mpl.size(); ii++) {
        Yaz(thisRobot.robotName + " From  -> " +mpl.get(ii - 1) + "To "+ " -> " + mpl.get(ii) 	+ "cities : " + citiesToVisit);
			
			Task fr = DefineFrom(mpl.get(ii - 1));
			
            Task to = DefineTo (mpl.get(ii), st); 
                 
            GetWay(fr, to);

		}

		executingTask = 0;
		long end = System.currentTimeMillis();

		if (st != null) 
			Yaz(thisRobot.robotName + "> CreateFinalSchedule_Optimal()  tamamlandý st : " + st.taskName + " time : " + (end - begin));

	}
	
	public Task DefineTo (int toIndex, Station st)
	{
		Task to = null; 
		if ((toIndex < 0 || toIndex >= citiesToVisit-1) && st != null) {
			to = new Task(st.xLoc, st.yLoc, "STATION");
		} else

			to = executionOrder[toIndex];
		return to; 
	}
	public Task DefineFrom (int frIndex) 
	{
		Task fr = null; 
		if (frIndex == 0) {
			fr = new Task(thisRobot.xLoc, thisRobot.yLoc, "ROBOT");
		} else

			fr = executionOrder[frIndex];
		return fr; 

	}

	public void CreateFinalSchedule_Fast(Station st) {
		exeTasks = 0;
		

		for (int ii = 1; ii < mpl.size(); ii++) {
			Yaz(thisRobot.robotName + " From  -> " +mpl.get(ii - 1) + "To "+ " -> " + mpl.get(ii) 	+ "cities : " + citiesToVisit);
			
			Task fr = DefineFrom(mpl.get(ii - 1));
			
            Task to = DefineTo (mpl.get(ii), st); 
			
			GetWay_Fast(fr, to);

		}

		executingTask = 0;
	}

	public void GetWay_Optimal(Task fr, Task to) {
		long begin = System.currentTimeMillis();
		WallNode wnr = new WallNode(fr.xLoc, fr.yLoc);
		WallNode wnt = new WallNode(to.xLoc, to.yLoc);
		Wall w = new Wall(wnr, wnt);

		if (is_main.InterSection(w)) {
			LinkedList<Vertex> fr_to = is_main.ShortestofClosests(fr, to);

			if (fr_to != null && fr_to.size() > 0)
				for (Vertex vertex : fr_to) {
					executionOrder_final[exeTasks++] = new Task(vertex.tp.xLoc,
							vertex.tp.yLoc, vertex.getName());

					Yaz(thisRobot.robotName + "> Pathe eklenen TP : "
							+ vertex.getName() + "   -  "
							+ Integer.toString(exeTasks));

				}

		}
		executionOrder_final[exeTasks++] = to;
		Yaz(thisRobot.robotName + "> Pathe eklenen Task : " + to.taskName + "   -  " + Integer.toString(exeTasks));
		long end = System.currentTimeMillis();

		Yaz(thisRobot.robotName + "> GetWay_Optimal()  tamamlandý fr : "
				+ fr.taskName + " to : " + to.taskName + " time : "
				+ (end - begin));

	}

	
	public BasicPoint  BresenhamLink (int oldX, int oldY, int toX, int toY, int k) 
	{
		
		int pointCount = line(oldX, oldY, toX, toY);

		for (int i = 0; i < pointCount; i++) {
				executionOrder_final[exeTasks++] = new Task(bps[i].xx, bps[i].yy, "BL" + k++);
				oldX = bps[i].xx;
				oldY = bps[i].yy;

				Yaz(thisRobot.robotName + "> Pathe eklenen Bresenham Point  : "	+ k + "   -  " + Integer.toString(exeTasks));

		
		}
		
		return new BasicPoint(oldX, oldY, k);

	}
	
	public void GetWay_RRT(Task fr, Task to) {
		
		getCallerMethod();
		long begin = System.currentTimeMillis();
		WallNode wnr = new WallNode(fr.xLoc, fr.yLoc);
		WallNode wnt = new WallNode(to.xLoc, to.yLoc);
		Wall w = new Wall(wnr, wnt);
		BasicPoint bp = null; 
		
		IndoorStructure istemp = new IndoorStructure(templateSelect);
		RRT rrt = new RRT(RRTdelta, RRTmaxIter, istemp);
		//istemp = rrt.RRTPath(fr, to);
		istemp = rrt.BuildRRT_BiDirect(fr, to);

		GetWay_Fast_ForRRT(istemp, fr, to); 
		/*
		if (is_main.InterSection(w)) {

			IndoorStructure istemp = new IndoorStructure(templateSelect);
			RRT rrt = new RRT(20, RRTmaxIter, istemp);
			istemp = rrt.RRTPath(fr, to);

			if (istemp != null) {   // iki düðüm arasýnda engel varsa 
				TransitionPoint frc = istemp.Closest(new TransitionPoint(fr));
				TransitionPoint frt = istemp.Closest(new TransitionPoint(to));
				if (frc!=null && frt!=null) 
				{
					LinkedList<Vertex> fr_to = istemp.ShortestPath(frc, frt);

					bp = BresenhamLink(fr.xLoc, fr.yLoc ,frc.xLoc, frc.yLoc, 0); 

					if (fr_to != null && fr_to.size() > 0)    // en yakýn düðümler arasýnda en kýsa yol mevcutsa
						for (Vertex vertex : fr_to) { 					
							bp = BresenhamLink(bp.xx, bp.yy, vertex.tp.xLoc, vertex.tp.yLoc, bp.kk); 
					         Yaz(thisRobot.robotName + "> Pathe eklenen TP : " + vertex.getName() + "   -  " +  Integer.toString(exeTasks));
					    }
					bp = BresenhamLink(bp.xx, bp.yy, frt.xLoc, frt.yLoc, bp.kk);
				}
			}
			else 
			{
				Yaz(thisRobot.robotName + "> Get way RRT'de rrt path grafý oluþturulamadý: indoorstructure eksik ");
			}

		}
	    if (bp != null) 
	    	bp = BresenhamLink(bp.xx, bp.yy, to.xLoc, to.yLoc, bp.kk); 
	    else 
	    	bp = BresenhamLink(fr.xLoc, fr.yLoc, to.xLoc, to.yLoc, 0); 
 
		*/
	/*
		if (is_main.InterSection(w)) {
			// LinkedList<Vertex> fr_to = is_main.ShortestofClosests(fr, to);

			TransitionPoint tpFrom = is_main.Closest(new TransitionPoint(fr));
			Yaz ("getway fast: tp from "+tpFrom.xLoc + " "+tpFrom.yLoc );
			TransitionPoint tpTo = is_main.Closest(new TransitionPoint(to));
			Yaz ("getway fast: tp to "+tpTo.xLoc + " "+tpTo.yLoc );
			if (tpFrom!= null && tpTo!=null) 
			{
			
			LinkedList<Vertex> fr_to = is_main.ShortestPath(tpFrom, tpTo);
			if (fr_to != null && fr_to.size() > 0)
				for (Vertex vertex : fr_to) {
					executionOrder_final[exeTasks++] = new Task(vertex.tp.xLoc,
							vertex.tp.yLoc, vertex.getName());

					Yaz(thisRobot.robotName + "> Pathe eklenen TP : "
							+ vertex.getName() + "   -  "
							+ Integer.toString(exeTasks));

				}
			}
		}
*/
	//	executionOrder_final[exeTasks++] = to;
	//	Yaz(thisRobot.robotName + "> Pathe eklenen Task : " + to.taskName + "   -  " + Integer.toString(exeTasks));

		
		
	//	executionOrder_final[exeTasks++] = to;
		//Yaz(thisRobot.robotName + "> Pathe eklenen Task : " + to.taskName 	+ "   -  " + Integer.toString(exeTasks));
		long end = System.currentTimeMillis();
		Yaz(thisRobot.robotName + "> GetWay_RRT()  tamamlandý fr : " + fr.taskName + " to : " + to.taskName + " time : " + (end - begin));

	}

	public void GetWay_DStar(Task fr, Task to) {
		long begin = System.currentTimeMillis();
		// WallNode wnr = new WallNode(fr.xLoc, fr.yLoc);
		// WallNode wnt = new WallNode(to.xLoc, to.yLoc);
		// Wall w = new Wall (wnr,wnt);
		int oldx = -1, oldy = -1;
		// if (is_main.InterSection(w))
		// {

		DStarPath dpath = dsl.DStarLitePath(fr.xLoc, fr.yLoc, to.xLoc, to.yLoc);
		if (dpath != null) {
			int k = 0;
			for (State i : dpath.dsPath) {

				if (oldx < 0 && oldy < 0) {
					oldx = i.x;
					oldy = i.y;
				}
				// double dist = CalcDistanceD (oldx , oldy, i.x, i.y);
				// if (dist>robotStep)
				// {
				executionOrder_final[exeTasks++] = new Task(i.x, i.y, "DP" 	+ k++);
				oldx = i.x;
				oldy = i.y;

				//Yaz(thisRobot.robotName + "> Pathe eklenen DS  : " + k 	+ "   -  " + Integer.toString(exeTasks));
			}
		}

		/*
		 * LinkedList<Vertex> fr_to = is_main.ShortestofClosests(fr, to);
		 * 
		 * if (fr_to != null && fr_to.size()>0) for (Vertex vertex:fr_to) {
		 * executionOrder_final[exeTasks++] = new Task (vertex.tp.xLoc,
		 * vertex.tp.yLoc, vertex.getName());
		 * 
		 * Yaz(thisRobot.robotName+"> Pathe eklenen TP : " +
		 * vertex.getName()+"   -  "+ Integer.toString(exeTasks));
		 * 
		 * 
		 * }
		 */

		// }
		executionOrder_final[exeTasks++] = to;
		Yaz(thisRobot.robotName + "> Pathe eklenen Task : " + to.taskName 	+ "   -  " + Integer.toString(exeTasks));
		long end = System.currentTimeMillis();

		Yaz(thisRobot.robotName + "> GetWay_DStar()  tamamlandý fr : " + fr.taskName + " to : " + to.taskName + " time : " + (end - begin));

	}
	public boolean CrossTypeIntersection5 (IndoorStructure is, int i, int j) 
	{
		boolean retVal = false; 
		Wall w = new Wall (i-5, j-5, i+5, j+5); 
		Wall w2 = new Wall (i+5, j-5, i-5, j+5); 
		if (is.InterSection(w) || is.InterSection(w2)) 
			retVal =  true;
		return retVal;  
				 
	}
	public int line(int x, int y, int x2, int y2) {

		// http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/

		int w = x2 - x;
		int h = y2 - y;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0)
			dx1 = -1;
		else if (w > 0)
			dx1 = 1;
		if (h < 0)
			dy1 = -1;
		else if (h > 0)
			dy1 = 1;
		if (w < 0)
			dx2 = -1;
		else if (w > 0)
			dx2 = 1;
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		bps = new BasicPoint[longest + 1];
		for (int i = 0; i <= longest; i++) {
			bps[i] = new BasicPoint(x, y);
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
			
			/*int sur = Surroundings(x,y) ; 
			while (sur!=rw*11) 
			{
				//Yaz(thisRobot.robotName + ">     Task : " + Executing.taskName + "  sur "+sur );
				int xf = (sur - rw*11)/10;
				int yf = (sur - rw*11)%10; 
				x+=xf;
				y+=yf;
				//tx += (randomGenerator.nextInt(2)-1); 
				//ty += (randomGenerator.nextInt(2)-1); 
				
				sur = Surroundings(x,y);
				Yaz(thisRobot.robotName + ">      xf "+ xf+ "yf "+yf +" tx "+x + " ty "+y + " sur "+sur );
			}*/
		}

		return longest;
	}
	
	



	public void GetWay_Bresenham(Task fr, Task to) {
		long begin = System.currentTimeMillis();
		WallNode wnr = new WallNode(fr.xLoc, fr.yLoc);
		WallNode wnt = new WallNode(to.xLoc, to.yLoc);
		Wall w = new Wall(wnr, wnt);
		
		int oldx = fr.xLoc; 
		int oldy = fr.yLoc; 
		int k=0; 
		BasicPoint bp = null; 
		if (is_main.InterSection(w)) {
            System.out.println ("getway_bresenham: from-to arasýnda engel var, en kýsa yol aranacak!");
			LinkedList<Vertex> fr_to = is_main.ShortestofClosests(fr, to);

			if (fr_to != null && fr_to.size() > 0)
				for (Vertex vertex : fr_to) {

					int tx = vertex.tp.xLoc;
					
					int ty = vertex.tp.yLoc;
					
					//int sur = Surroundings(x, y)
					
					Wall e = new Wall (new WallNode(oldx,oldy), new WallNode(tx,ty)); 
					/*int sur = Surroundings(e); 
					while (sur!=rw*11) 
					{
						//Yaz(thisRobot.robotName + ">     Task : " + Executing.taskName + "  sur "+sur );
						int xf = (sur - rw*11)/10;
						int yf = (sur - rw*11)%10; 
						tx+=xf;
						ty+=yf;
						//tx += (randomGenerator.nextInt(2)-1); 
						//ty += (randomGenerator.nextInt(2)-1); 
						Wall f = new Wall (new WallNode(oldx,oldy), new WallNode(tx,ty));
						sur = Surroundings(f);
						Yaz(thisRobot.robotName + ">      xf "+ xf+ "yf "+yf +" tx "+tx + " ty "+ty + " sur "+sur );
					}
					//bp = BresenhamLink(oldx, oldy, tx, ty, k); 
					*/
					
					bp = BresenhamLink(oldx, oldy, vertex.tp.xLoc, vertex.tp.yLoc, k); 
					
					oldx = bp.xx; 
					oldy= bp.yy; 
					k= bp.kk;
					
					Yaz(thisRobot.robotName + "> Pathe eklenen TP : " + vertex.getName() + "   -  " + Integer.toString(exeTasks));

				}
		}
		
		bp = BresenhamLink(oldx, oldy, to.xLoc, to.yLoc, k); 
		executionOrder_final[exeTasks++] = to;
		Yaz(thisRobot.robotName + "> Pathe eklenen Task : " + to.taskName+ "   -  " + Integer.toString(exeTasks));
		long end = System.currentTimeMillis();

		Yaz(thisRobot.robotName + "> GetWay_Bresenham()  tamamlandý fr : "+ fr.taskName + " to : " + to.taskName + " time : "+ (end - begin));

	}
	
	public void GetWay_Fast_ForRRT(IndoorStructure isrrt,Task fr, Task to) {

		WallNode wnr = new WallNode(fr.xLoc, fr.yLoc);
		WallNode wnt = new WallNode(to.xLoc, to.yLoc);
		Wall w = new Wall(wnr, wnt);

		//if (isrrt.InterSection(w)) {
			// LinkedList<Vertex> fr_to = is_main.ShortestofClosests(fr, to);

			TransitionPoint tpFrom = isrrt.Closest(new TransitionPoint(fr));
			Yaz ("getway fast: tp from "+tpFrom.xLoc + " "+tpFrom.yLoc );
			TransitionPoint tpTo = isrrt.Closest(new TransitionPoint(to));
			Yaz ("getway fast: tp to "+tpTo.xLoc + " "+tpTo.yLoc );
			if (tpFrom!= null && tpTo!=null) 
			{
			
			LinkedList<Vertex> fr_to = isrrt.ShortestPath(tpFrom, tpTo);
			if (fr_to != null && fr_to.size() > 0)
				for (Vertex vertex : fr_to) {
					executionOrder_final[exeTasks++] = new Task(vertex.tp.xLoc,vertex.tp.yLoc, vertex.getName());

					Yaz(thisRobot.robotName + "> Pathe eklenen TP : "
							+ vertex.getName() + "   -  "
							+ Integer.toString(exeTasks));

				}
			}
	//	}

		executionOrder_final[exeTasks++] = to;
		Yaz(thisRobot.robotName + "> Pathe eklenen Task : " + to.taskName + "   -  " + Integer.toString(exeTasks));

	}

	public void GetWay_Fast(Task fr, Task to) {

		WallNode wnr = new WallNode(fr.xLoc, fr.yLoc);
		WallNode wnt = new WallNode(to.xLoc, to.yLoc);
		Wall w = new Wall(wnr, wnt);

		if (is_main.InterSection(w)) {
			// LinkedList<Vertex> fr_to = is_main.ShortestofClosests(fr, to);

			TransitionPoint tpFrom = is_main.Closest(new TransitionPoint(fr));
			Yaz ("getway fast: tp from "+tpFrom.xLoc + " "+tpFrom.yLoc );
			TransitionPoint tpTo = is_main.Closest(new TransitionPoint(to));
			Yaz ("getway fast: tp to "+tpTo.xLoc + " "+tpTo.yLoc );
			if (tpFrom!= null && tpTo!=null) 
			{
			
			LinkedList<Vertex> fr_to = is_main.ShortestPath(tpFrom, tpTo);
			if (fr_to != null && fr_to.size() > 0)
				for (Vertex vertex : fr_to) {
					executionOrder_final[exeTasks++] = new Task(vertex.tp.xLoc,
							vertex.tp.yLoc, vertex.getName());

					Yaz(thisRobot.robotName + "> Pathe eklenen TP : "
							+ vertex.getName() + "   -  "
							+ Integer.toString(exeTasks));

				}
			}
		}

		executionOrder_final[exeTasks++] = to;
		Yaz(thisRobot.robotName + "> Pathe eklenen Task : " + to.taskName + "   -  " + Integer.toString(exeTasks));

	}

	// Robota o an TSP hesabýna göre sýradaki görevini bulur ve orayý ziyaret
	// eder.
	public class ExecuteNextTaskBundle extends OneShotBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = 1706267188086177584L;

		public void action() {

			Yaz(thisRobot.robotName + "> ExecuteNextTaskBundle");

			if (Executing == null && executingTask < exeTasks) {
				if (executionOrder_final[executingTask] != null) {
					Executing = executionOrder_final[executingTask++];
					thisRobot.status = eRobotStatus.TRAVERSING;
					Executing.state = eTaskState.PROCESSING;

					//Yaz("auctioneeragentname :  " + auctioneer);
					//Yaz("terrainagentname :  " + terrain);
					//SendTaskStateToAuctioneer(Executing);
					Yaz(thisRobot.robotName + ">" + Executing.taskName + " gorevine baþladým. State :" + Executing.state);
					Yaz(thisRobot.robotName + ">" + Executing.taskName + " xloc "+Executing.xLoc + " yloc "+Executing.yLoc);
				} else {
					Yaz(thisRobot.robotName
							+ "> "
							+ executingTask
							+ ". sýradaki görev geçersiz, bir sonrakine geçiyorum.");

					executingTask++;
				}

			} else if (Executing == null) 
			{
				Yaz(thisRobot.robotName + "> " + " exeTasks : " + exeTasks + " executingTask Order " + executingTask);
			} 
			else if (Executing != null) 
			{
				Yaz(thisRobot.robotName + "> Executing = " + Executing.taskName	+ " exeTasks : " + exeTasks + " executingTask Order "
						+ executingTask);
			}

		}

	}

	// Satma kararý verildiðinde ilgili robota mesaj gönderir.
	public void SellTask(int robot, int task) {

		Yaz(thisRobot.robotName + "> SELLTASKS -> ROBOT : "
				+ Integer.toString(robot) + " TASK "
				+ taskQueue[task].GetTask(0).taskName);
		TaskBundle tb = taskQueue[task];
		for (int i = 0; i < tb.taskCount(); i++) {
			Task t = tb.GetTask(i);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			AID aid = new AID(robots[robot].robotName, AID.ISLOCALNAME);
			msg.addReceiver(aid);
			msg.setLanguage("English");
			msg.setContent("103_" + thisRobot.robotName + "_" + t.taskName
					+ "_" + Integer.toString(t.xLoc) + "_"
					+ Integer.toString(t.yLoc) + "_" + t.bundleHead + "_"
					+ Integer.toString(proposalstoMyTasks[robot][task]));
			send(msg);
		}

	}

	// Satma kararý verildiðinde ilgili robota mesaj gönderir.
	public void SellTask_Sequential(int robot, int task) {
		taskBuyer = robots[robot].robotName;
		Yaz(thisRobot.robotName + "> SELLTASKS -> ROBOT : " + taskBuyer
				+ " TASK " + taskQueue[task].GetTask(0).taskName);

		TaskBundle tb = taskQueue[task];
		lastBought = tb.GetTask(0).bundleHead;
		// taskQueue[task].state = 7;
		for (int i = 0; i < tb.taskCount(); i++) {
			Task t = tb.GetTask(i);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			AID aid = new AID(robots[robot].robotName, AID.ISLOCALNAME);
			msg.addReceiver(aid);
			msg.setLanguage("English");
			msg.setContent("123_" + thisRobot.robotName + "_" + t.taskName
					+ "_" + Integer.toString(t.xLoc) + "_"
					+ Integer.toString(t.yLoc) + "_" + t.bundleHead + "_"
					+ Integer.toString(proposalstoMyTasks[robot][task]));
			send(msg);
		}

		// ResetOffers();
	}

	// Satýn alma kararý verilmeyen teklifleri yapan robotlara sonucun olumsuz
	// olduðu mesajla bildirilir.
	public void IgnoreTasks(int robot, int task) {

		for (int i = 0; i < robotCount; i++) {
			if (i != robot) {

				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID aid = new AID(robots[i].robotName, AID.ISLOCALNAME);
				msg.addReceiver(aid);
				msg.setLanguage("English");
				msg.setContent("104_" + robots[i].robotName + "_"
						+ taskQueue[task].GetTask(0).taskName);
				send(msg);
			}
		}

	}

	

	// ARENA'ya anlýk pozisyon bilgisi periyodik olarak gönderilir.
	// Yer deðiþimi fazla deðilse mesaj yükünü önlemek açýsýndan lokasyon
	// gönderilmeyebilir.
	public class InformLocation extends TickerBehaviour {

		/**
	 * 
	 */
		private static final long serialVersionUID = -1143856401654185145L;

		public InformLocation(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {

			if (Math.abs(thisRobot.xLoc - thisRobot.preX) > 3
					|| Math.abs(thisRobot.yLoc - thisRobot.preY) > 3) {
				addBehaviour(new InformLocationOnce());

			}

		}
	}

	

	public class InformLocationOnce extends OneShotBehaviour {

		/**
	 * 
	 */
		private static final long serialVersionUID = -8497620215111926467L;

		public void action() {
			// Yaz(thisRobot.robotName+"> ** 71 * ");
			thisRobot.pickUpTime = CompletedTasks() * estimatedPickUpDuration; // pickup time burada hesaplanýyor
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(terrain, AID.ISLOCALNAME));
			msg.setLanguage("English"); 
			
			String ldrStr = Integer.toString((int)thisRobot.heading) + "_"+Integer.toString(ldr.numberofSectors)+"_"; 
			for (int i=0; i<ldr.numberofSectors; i++) 
			{
				ldrStr += Integer.toString((int) ldr.meanProximities[i]) + "_"; 
				System.out.println("Inform Location Once: " + ldr.meanProximities[i]);
			}
			
			System.out.println("ldrStr: " + ldrStr);
			msg.setContent("71_" + thisRobot.robotName + "_" // 
					+ Integer.toString(thisRobot.xLoc) + "_" 
					+ Integer.toString(thisRobot.yLoc) + "_"
					+ Integer.toString((int) thisRobot.cumulativeWay) + "_"
					+ Integer.toString(thisRobot.energyLevel) + "_"
					+ Integer.toString(thisRobot.completedTasks) + "_"
					+ Integer.toString(thisRobot.chargeCount) + "_"
					+ Integer.toString(thisRobot.colorIndex) + "_"
					+ Integer.toString(thisRobot.status.getValue()) + "_"
					+ Integer.toString((int) thisRobot.schTime) + "_"
					+ Integer.toString((int) thisRobot.pickUpTime) + "_"
					+ Integer.toString((int) thisRobot.execTime) + "_"
					+ Integer.toString(CompletedTasks()) + "_"
					+ Integer.toString(thisRobot.rect.x1) + "_"    // görevler için 
					+ Integer.toString(thisRobot.rect.y1) + "_"
					+ Integer.toString(thisRobot.rect.x2) + "_"
					+ Integer.toString(thisRobot.rect.y2) + "_"
					+ Integer.toString(thisRobot.outerRect.x1) + "_" // istasyonlar için 
					+ Integer.toString(thisRobot.outerRect.y1) + "_"
					+ Integer.toString(thisRobot.outerRect.x2) + "_"
					+ Integer.toString(thisRobot.outerRect.y2)+"_"
					+ldrStr
					
					
			);
			send(msg);
			thisRobot.preX = thisRobot.xLoc;
			thisRobot.preY = thisRobot.yLoc;
			// Yaz(thisRobot.robotName+"> ** 71 * ");

		}
	}
	
	public class SendLidarInfoToTerrain extends OneShotBehaviour {

		/**
	 * 
	 */
		private static final long serialVersionUID = -8497620215111926467L;

		public void action() {
			// Yaz(thisRobot.robotName+"> ** 71 * ");
		
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(terrain, AID.ISLOCALNAME));
			msg.setLanguage("English"); 
			String ldrStr = Integer.toString((int)thisRobot.heading) + "_"+Integer.toString(ldr.numberofSectors)+"_"; 
			for (int i=0; i<ldr.numberofSectors; i++) 
			{
				ldrStr += Integer.toString((int) ldr.meanProximities[i]) + "_"; 
			}
			
			msg.setContent("1001_" + thisRobot.robotName + "_"+ldrStr);
			send(msg);
			
			// Yaz(thisRobot.robotName+"> ** 71 * ");

		}
	}
	
	public class NewBornRobotMove extends WakerBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = -3161074789702556522L;

		public NewBornRobotMove(Agent a, long interval) {
			super(a, interval);
		}

		protected void onWake() {

			Yaz(thisRobot.robotName + " > ExecutionDelay");
		/*	for (int i = 0; i < exeTasks; i++) {
				Yaz(executionOrder_final[i].taskName);
			}
        */

			addBehaviour(new ExecuteNextTaskBundle());

		}
	}

	public class ExecutionDelay extends WakerBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = -3161074789702556522L;

		public ExecutionDelay(Agent a, long interval) {
			super(a, interval);
		}

		protected void onWake() {

			Yaz(thisRobot.robotName + " > ExecutionDelay");
		/*	for (int i = 0; i < exeTasks; i++) {
				Yaz(executionOrder_final[i].taskName);
			}
        */

			addBehaviour(new ExecuteNextTaskBundle());

		}
	}

	public class ScheduleDelay extends WakerBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = -7067523305480542455L;

		public ScheduleDelay(Agent a, long interval) {
			super(a, interval);
		}

		protected void onWake() {

			Yaz(thisRobot.robotName + " > ScheduleDelay");
			EXECUTION_PHASE = eExecutionPhase.TRADES_COMPLETED;

			// iTime = Calendar.getInstance().getTimeInMillis();

			//addBehaviour(new CreateSchedule_MTP());

			/*
			 * switch (LOCAL_SCHEDULE) {
			 * 
			 * // TSP on manually located Transition Points case 0:
			 * addBehaviour(new CreateSchedule_MTP()); break; // TSP on
			 * RRT-based located Transition Points case 1: addBehaviour(new
			 * CreateSchedule_RRT()); break; case 2: addBehaviour(new
			 * CreateSchedule_MST()); break; case 3: addBehaviour(new
			 * CreateSchedule_OBO_Nearest()); break; }
			 */
		}
	}

	public class EndOfATrade extends WakerBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = 4352338673288005612L;

		public EndOfATrade(Agent a, long interval) {
			super(a, interval);
		}

		protected void onWake() {

			while (tradeCompleted == false) {
				Sleepy (5000);
			}
			String rc[] = { auctioneer };
			String mc[] = { "113", thisRobot.robotName, taskBuyer,
					taskAnnounced, taskSold };
			SendACLMessage(rc, 1, mc, 5);
			Yaz(thisRobot.robotName
					+ "> END OF A TRADE MESAJI 113 GÖNDERÝLDÝ  !!!!  ");

		}
	}

	public class MyWaker extends WakerBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = 1913779856846420034L;

		public MyWaker(Agent a, long interval) {
			super(a, interval);
		}

		protected void onWake() {

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			AID aid = new AID(auctioneer, AID.ISLOCALNAME);
			msg.addReceiver(aid);
			ExecutingNow.state = eTaskState.NOT_STARTED;
			msg.setLanguage("English");

			msg.setContent("54_" + thisRobot.robotName + "_"
					+ ExecutingNow.GetTask(0).taskName + "_"
					+ Integer.toString(ExecutingNow.state.getValue()));

			send(msg);

			++thisRobot.chargeCount;
			addBehaviour(new InformLocationOnce());

			ExecutingNow = null;
			thisRobot.energyLevel += 1000;
		
			thisRobot.status = eRobotStatus.IDLE;
			Yaz(thisRobot.robotName + " MyWaker-OnWake()");
			addBehaviour(new ExecutionDelay(thisa, 10));

		}
	}

	public int LongestDistanceToOtherTasks(TaskBundle tb) {
		int maxD = 0;
		for (int i = 0; i < myTaskBundles; i++) {

			TaskBundle tbi = taskQueue[i];

			int len = (int) CalcDistance(tbi.GetTask(0), tb.GetTask(0));

			if (len > maxD) {
				maxD = len;
			}

		}
		return maxD;
	}

	public int FurthestTaskToOthers(TaskBundle tb) {
		int maxD = 0;
		int ind = -1;
		for (int i = 0; i < myTaskBundles; i++) {

			TaskBundle tbi = taskQueue[i];
			if (tb.GetTask(0).taskName.compareTo(tbi.GetTask(0).taskName) != 0) {
				int len = (int) CalcDistance(tbi.GetTask(0), tb.GetTask(0))
						+ (int) CalcTPDistance(tb.GetTask(0));

				if (len > maxD) {
					maxD = len;
					ind = i;
				}

			}
		}
		return ind;
	}

	public boolean AreThereNeighbors() {
		boolean retVal = true;

		if (expMode == eServiceArea.EXTENDEDRECTANGULAR) {
			int tasksPerHeat = 7;
			int tCount = 0;
			for (int i = 0; i < taskBundleCount; i++) {
				Task t = taskBundles[i].GetTask(0);
				//HRectangle r = thisRobot.rect;
				HRectangle r = thisRobot.outerRect;
				if (r.Contains(t)) {
					tCount++;
				}

			}

			if (tCount < tasksPerHeat) {
				retVal = false;
			}
		}
		return retVal;

	}

	public int FindMostUnavailable_Sequential(eBidValuation approach) {

		double maxLength = 0;
		int maxi = -1;

		for (int i = 0; i < myTaskBundles; i++) {

			TaskBundle tb = taskQueue[i];

			if (/*taskQueue[i].offered == false & */taskQueue[i].tradeStatus == eTradeStatus.OWNED
					/*& tb.GetTask(0).bundleHead.compareTo(lastBought) != 0*/
					& taskQueue[i].state == eTaskState.NOT_STARTED ) {
				int len = 0;
				
                
				len = Pricing(tb, approach); 
				taskQueue[i].price = len; 
				Yaz(thisRobot.robotName + " > MEVCUT GÖREVLERÝMDEN "
						+ tb.GetTask(0).taskName + " ÇIKARILIRSA KAZANCIM : "
						+ len);
				if (len > maxLength) {
					maxLength = len;
					maxi = i;
				}
			} else {

				Yaz(thisRobot.robotName + " > bundlehead: "
						+ tb.GetTask(0).bundleHead + " > offered: "
						+ tb.offered + " > tradeStatus:  " + tb.tradeStatus
						+ " > lastBought: " + lastBought + " > state : "
						+ taskQueue[i].state);
			}

		}

		// Gerek var mý?
		/*
		 * if (maxi <0) { maxi = Furthest_Sequential(approach);
		 * 
		 * 
		 * }
		 */

		return maxi;

	}

	public boolean IsInHeatBoundary(TaskBundle tb) {
	  
		//getCallerMethod();
		boolean returnValue = false; 
		if (expMode == eServiceArea.ALL_TERRAIN) {
			//int dist = (int) CalcDistance(TRADE_APPROACH, tb.GetTask(0));
			//System.out.println(thisRobot.robotName +"> IsInHeatBoundary: distance"+dist + " bidRange: "+bidRange + " expMode: "+expMode);
			//if (dist < bidRange && dist > 0)
			returnValue =  true;
			System.out.println(thisRobot.robotName +">ServiceArea is ALL_TERRAIN, ready to propose");
		}

		else if (expMode == eServiceArea.RECTANGULAR || expMode == eServiceArea.EXTENDEDRECTANGULAR) {
			Task t = tb.GetTask(0);
			 HRectangle r = thisRobot.rect;
			 System.out.println(thisRobot.robotName +"> rect "+r.x1 +"-"+r.y1 +"-"+r.x2+"-"+r.y2+" "+ " contains: " + r.Contains(t) +" expMode: "+expMode);
			 
			//HRectangle r = thisRobot.outerRect;
			if (r.Contains(t))
					returnValue =  true;
		} 
		else if (expMode == eServiceArea.CIRCULAR) 
		{
			Task t = tb.GetTask(0);
			HRectangle r = thisRobot.rect;
			/*
			 * int dist = (int) CalcDistance(t.xLoc, t.yLoc); int radius =
			 * (r.x2-r.x1)<=(r.y2-r.y1)?(r.x2-r.x1)/2:(r.y2-r.y1)/2; if (dist<=
			 * radius)
			 */
			Ellipse2D.Double circle = new Ellipse2D.Double(r.x1, r.y1,
					(int) Math.abs(r.x2 - r.x1), (int) Math.abs(r.y2 - r.y1));

			// if (dist<= radius)
			// if (t.xLoc>rect.x1 && t.xLoc <rect.x2 && t.yLoc >rect.y1 &&
			// t.yLoc <rect.y2)
			 System.out.println(thisRobot.robotName +"> circle "+r.x1 +"-"+r.y1 +"-"+r.x2+"-"+r.y2+" "+ " circle contains: " + circle.contains(t.xLoc, t.yLoc) +" expMode: "+expMode);

			if (circle.contains(t.xLoc, t.yLoc))

				// if (t.xLoc>r.x1 && t.xLoc<r.x2 && t.yLoc>r.y1 && t.yLoc<r.y2)
				returnValue =  true;
		}
		return returnValue;
	}

	public boolean IsInExtendedBoundary(TaskBundle tb) {

		boolean retVal = false; 
		if (expMode == eServiceArea.EXTENDEDRECTANGULAR || expMode == eServiceArea.ALL_TERRAIN || expMode == eServiceArea.RECTANGULAR) {
			Task t = tb.GetTask(0);
			HRectangle r = thisRobot.outerRect;

			retVal = r.Contains(t); 
				
		}else 
		{
			
		}
		return retVal;
	}

	public boolean IsInHeatBoundary(int x1, int y1) {
		Task t = new Task(x1, y1, "Task");
		TaskBundle tb = new TaskBundle();
		tb.AddTask(t);
		return IsInHeatBoundary(tb);
	}

	public boolean IsInExtendedBoundary(int x1, int y1) {
		Task t = new Task(x1, y1, "Task");
		TaskBundle tb = new TaskBundle();
		tb.AddTask(t);
		return IsInExtendedBoundary(tb);
	}

	public boolean IsInHeatBoundary(String x1, String y1) {
		//getCallerMethod();
		Task t = new Task(Integer.parseInt(x1), Integer.parseInt(y1), "Task");
		TaskBundle tb = new TaskBundle();
		tb.AddTask(t);
		return IsInHeatBoundary(tb);
	}

	

	public void CloneTaskQueue() {

		Yaz(thisRobot.robotName + "> CloneTaskQueue  ");
		int tCount = 0;
		for (int i = 0; i < myTaskBundles; i++) {
			if (taskQueue[i].state == eTaskState.NOT_STARTED)

				temptaskQueue[tCount++] = taskQueue[i];
		}
		mytempTaskBundles = tCount;

	}

	public void ExcludeTaskBundle(String bundleHead) {
		Yaz(thisRobot.robotName + "> ExcludeTaskBundle : " + bundleHead);
		int eIndex = -1;
		for (int i = 0; i < mytempTaskBundles; i++) {
			if (temptaskQueue[i].GetTask(0).bundleHead.compareTo(bundleHead) == 0)
				eIndex = i;
		}
		if (eIndex > -1) {
			for (int i = eIndex; i < mytempTaskBundles - 1; i++) {
				temptaskQueue[i] = temptaskQueue[i + 1];
			}
			mytempTaskBundles--;

		}

	}

	public void IncludeTaskBundle(TaskBundle tb) {
		Yaz(thisRobot.robotName + "> IncludeTaskBundle : "
				+ tb.GetTask(0).taskName);
		temptaskQueue[mytempTaskBundles++] = tb;

	}

	public int RRTDistance(TaskBundle tb) {
		IndoorStructure istemp = new IndoorStructure(templateSelect);
		RRT rrt = new RRT(RRTdelta, RRTmaxIter, istemp);
		return rrt.CalcRRT(thisRobot, tb.GetTask(0));

	}

	public int DStarLiteDistance(TaskBundle tb) {

		int dist = (int) dsl.DStarLiteDistance(thisRobot.xLoc, thisRobot.yLoc,
				tb.GetTask(0).xLoc, tb.GetTask(0).yLoc, thisRobot.robotName,  tb.GetTask(0).taskName);

		Yaz(thisRobot.robotName + "> Calcdstardistance BETWEEN "
				+ thisRobot.robotName + " and " + tb.GetTask(0).taskName
				+ " DIST : " + dist);
		return dist;

	}

	public int DStarLiteDistance(TaskBundle tb, Station st) {

		int dist = (int) dsl.DStarLiteDistance(st.xLoc, st.yLoc,
				tb.GetTask(0).xLoc, tb.GetTask(0).yLoc, tb.GetTask(0).taskName, st.taskName);

		Yaz(thisRobot.robotName + "> Calcdstardistance BETWEEN " + st.taskName
				+ " and " + tb.GetTask(0).taskName + " DIST : " + dist);
		return dist;

	}

	public int DStarLiteDistance(Robotum r, TaskBundle tb) {

		int dist = (int) dsl.DStarLiteDistance(r.xLoc, r.yLoc,
				tb.GetTask(0).xLoc, tb.GetTask(0).yLoc,  r.robotName, tb.GetTask(0).taskName);

		Yaz(thisRobot.robotName + "> Calcdstardistance BETWEEN " + r.robotName
				+ " and " + tb.GetTask(0).taskName + " DIST : " + dist);
		return dist;

	}

	public int DStarLiteDistance(TaskBundle tb1, TaskBundle tb2) {

		int dist = (int) dsl.DStarLiteDistance(tb1.GetTask(0).xLoc,
				tb1.GetTask(0).yLoc, tb2.GetTask(0).xLoc, tb2.GetTask(0).yLoc, tb1.GetTask(0).taskName, tb2.GetTask(0).taskName);
		Yaz(thisRobot.robotName + "> Calcdstardistance BETWEEN "
				+ tb1.GetTask(0).taskName + " and " + tb2.GetTask(0).taskName
				+ " DIST : " + dist);
		return dist;

	}

	public int CalcTPDistance(Task to) {
		Task fr = new Task(thisRobot.xLoc, thisRobot.yLoc, thisRobot.robotName);
		return CalcTPDistance(fr, to);
	}

	public int CalcRRTDistance(Task to) {
		Task fr = new Task(thisRobot.xLoc, thisRobot.yLoc, thisRobot.robotName);
		return CalcRRTDistance(fr, to);
	}

	/*
	 * public int SelectionofShortestPathCalcMethod() {
	 * 
	 * File f = new File(".");
	 * 
	 * String fileName = f.getAbsolutePath() +
	 * "\\fcl\\ShrtPatchCalcMethSel.fcl"; FIS fis = FIS.load(fileName,true); //
	 * Error while loading? if( fis == null ) {
	 * System.err.println("Can't load file: '" + fileName + "'"); return; }
	 * 
	 * // Show fis.chart();
	 * 
	 * // Set inputs fis.setVariable("TaskCount", 4);
	 * fis.setVariable("CurrentPath", 3000); fis.setVariable("Inclusion",50);
	 * 
	 * // Evaluate fis.evaluate();
	 * 
	 * // Show output variable's chart
	 * fis.getVariable("bidvalue").chartDefuzzifier(true); "
	 * 
	 * // Print ruleSet Yaz(fis);
	 * 
	 * return 0; }
	 */

	public int CalcRRTDistance(Task from, Task to) {

		IndoorStructure is_temp = new IndoorStructure(templateSelect);
		RRT rrt = new RRT(RRTdelta, RRTmaxIter, is_temp);
	
		int prop = rrt.CalcRRT(from, to);

		return (prop);

	}

	public int CalcDStarDistance(Task from, Task to) {

		int prop = (int) dsl.DStarLiteDistance(from.xLoc, from.yLoc, to.xLoc,
				to.yLoc, from.taskName, to.taskName);
		Yaz(thisRobot.robotName + "> Calcdstardistance BETWEEN "
				+ from.taskName + " and " + to.taskName + " DIST : " + prop);
		return (prop);

	}

	/*
	 * public int CalcTPDistance (Task from, Task to) {
	 * 
	 * TransitionPoint tpFrom = is_main.Closest(new TransitionPoint(from));
	 * TransitionPoint tpTo = is_main.Closest(new TransitionPoint(to)); int dist
	 * = -1; if (tpFrom != null && tpTo != null) { dist =
	 * is_main.ShortestPathLengthFF(tpFrom , tpTo );
	 * 
	 * }
	 * 
	 * if (dist<-1) dist = 99999;
	 * Yaz(thisRobot.robotName+"> CalcTPDistance BETWEEN "+from.taskName +
	 * " and "+to.taskName + " DIST : "+dist); return dist;
	 * 
	 * }
	 */
	public int CalcTPDistance(Task from, Task to) {

		WallNode wnr = new WallNode(from.xLoc, from.yLoc);
		WallNode wnt = new WallNode(to.xLoc, to.yLoc);
		Wall w = new Wall(wnr, wnt);
		int distance = 0;
         
		
		// Ýki görev arasýna engel geliyorsa durum karýþýk
		if (is_main.InterSection(w)) {
			distance = is_main.ShortestofClosestsLength(from, to);
		}
		// arada duvar yoksa direk Euclidian
		else {
			distance = (int) CalcDistance(from, to);
		}

		/*
		 * if (distance<-1) distance = 99999;
		 */
		// Yaz(thisRobot.robotName + "> CalcTPDistance BETWEEN " + from.taskName + " and " + to.taskName + " DIST : " + distance);
		return distance;

	}

	public int MSTLength(Station st, eBidValuation approach) {
		Kruskal kr = MSTCreate(st, approach);
		return kr.FastCalc();
	}

	public KruskalEdges MSTFinalEdges(Station st, eBidValuation approach) {
		Kruskal kr = MSTCreate(st, approach);
		return kr.MSTFinal();
	}

	private Kruskal MSTCreate(Station st, eBidValuation approach) {

		Kruskal kr = new Kruskal();

		for (int j = 0; j < mytempTaskBundles; j++) {
			TaskBundle tb = temptaskQueue[j];
			// Yaz(thisRobot.robotName+"> tsp -> taskname : " +
			// Integer.toString(j)+" -- "+t.taskName+" state "+
			// Integer.toString(t.state) + " t.tradeStatus " +
			// Integer.toString(t.tradeStatus));
			if (tb.tradeStatus != eTradeStatus.SOLD) {
				if (tb.state == eTaskState.NOT_STARTED) {

					for (int jj = 0; jj < tb.taskCount(); jj++)

					{
						Task tjj = tb.GetTask(jj);

						int dst = (int) CalcDistance(approach, tjj);

						kr.AddEdge("ROBOT", tjj.taskName, dst);
						kr.AddEdge(tjj.taskName, "ROBOT", 99999);

					}

				}
			}
		}

		if (st != null) {

			kr.AddEdge("ROBOT", st.taskName, 99999);
			kr.AddEdge(st.taskName, "ROBOT", 99999);
		}

		for (int j = 0; j < mytempTaskBundles; j++) {
			TaskBundle tb = temptaskQueue[j];
			// Yaz(thisRobot.robotName+"> tsp -> taskname : " +
			// Integer.toString(j)+" -- "+t.taskName+" state "+
			// Integer.toString(t.state) + " t.tradeStatus " +
			// Integer.toString(t.tradeStatus));
			if (tb.tradeStatus != eTradeStatus.SOLD && tb.state == eTaskState.NOT_STARTED) {

				for (int k = 0; k < tb.taskCount(); k++) {

					Task tk = tb.GetTask(k);

					for (int jj = 0; jj < mytempTaskBundles; jj++) {
						if (j != jj) {

							TaskBundle tbjj = temptaskQueue[jj];

							if (tbjj.tradeStatus != eTradeStatus.SOLD && tbjj.state == eTaskState.NOT_STARTED) {

								for (int kk = 0; kk < tbjj.taskCount(); kk++) {

									Task tkk = tbjj.GetTask(kk);

									int dst = (int) CalcDistance(approach, tk,
											tkk);

									kr.AddEdge(tk.taskName, tkk.taskName, dst);
									kr.AddEdge(tkk.taskName, tk.taskName, dst);

								}
							}
						}
					}

					// if (!TSPLOOPMODE&& EXECUTION_PHASE>0 && citiesToVisit>0)
					// {

					if (st != null) {

						int dst = (int) CalcDistance(approach, tk, st);
						;

						kr.AddEdge(tk.taskName, st.taskName, dst);
						kr.AddEdge(st.taskName, tk.taskName, 99999);
					}
					// }
				}

			}
		}

		return kr;

	}

	public TSPMinPath AddTasksToTSPPath(TSPMinPath pathx, Station st) {

		pathx.AddRobot(thisRobot);

		executionOrder[citiesToVisit] = new Task(thisRobot.xLoc, thisRobot.yLoc, "ROBOT");
		
		Yaz(thisRobot.robotName+"> tsp ->mytemptaskbundles "+ mytempTaskBundles);
		/*if (mytempTaskBundles<2) 
		{
			for (int j = 0; j < mytempTaskBundles+1; j++) 
			{
				Task temp = new Task (thisRobot.xLoc+j+1, thisRobot.yLoc+j+1, "temp"+j); 
				path.AddTask(temp);
				executionOrder[++citiesToVisit] = temp;
				
			}
			
			
		}
		else 
		{
		*/
		for (int j = 0; j < mytempTaskBundles; j++) 
		{
			TaskBundle tb = temptaskQueue[j];
    		 Yaz(thisRobot.robotName+"> tsp -> taskname : " +tb.GetTask(0).taskName +" -- "+" state "+ tb.state+ " tradeStatus " + tb.tradeStatus);
			 
			if (tb.tradeStatus != eTradeStatus.SOLD) 
			{
				if (tb.state == eTaskState.NOT_STARTED) 
				{
					for (int jj = 0; jj < tb.taskCount(); jj++)
					{
						Task tjj = tb.GetTask(jj);
						pathx.AddTask(tjj);
						executionOrder[++citiesToVisit] = tjj;
					}
				}
			}
		}
		int j=0; 
		if (st != null) {
			// tStation = new Task(st.xLoc, st.yLoc, "STATION");
			while (citiesToVisit<1) 
			{
				 Yaz(thisRobot.robotName+"> st != null and citiestovisit <2 case ....  citiestoVisit : " + citiesToVisit); 
				
				Task temp = new Task (st.xLoc+j+1, st.yLoc+j+1, "temp"+j); 
				pathx.AddTask(temp);
				executionOrder[++citiesToVisit] = temp;
				j++;
			}
						
			pathx.AddTask(st);
			executionOrder[++citiesToVisit] = st;

		}
		// BAÞLANGIÇ NOKTASINA DÖNÜLMESÝ
		else if (TSP_MODE == eTSPLoopMode.TSP_TOUR || TSP_MODE == eTSPLoopMode.TSP_PATH) {
			
			while (citiesToVisit<2) 
			{
				 Yaz(thisRobot.robotName+"> st == null and citiestovisit <3 case ....  citiestoVisit : " + citiesToVisit); 
				Task temp = new Task (thisRobot.xLoc+j+1, thisRobot.yLoc+j+1, "temp"+j); 
				pathx.AddTask(temp);
				executionOrder[++citiesToVisit] = temp;
				j++;
			}
		//	path.AddRobot(thisRobot);

		//	executionOrder[++citiesToVisit] = new Task(thisRobot.xLoc + 1, thisRobot.yLoc + 1, "ROBOT");

		}
		

		
		//}
		

		

		return pathx;

	}
	/*
	public PathInfo SetDistances_RobotToTasks (PathInfo pi,  eBidValuation approach)
	{
		for (int j = 0; j < mytempTaskBundles; j++) {
			TaskBundle tb = temptaskQueue[j];
			// Yaz(thisRobot.robotName+"> tsp -> taskname : " +
			// Integer.toString(j)+" -- "+t.taskName+" state "+
			// Integer.toString(t.state) + " t.tradeStatus " +
			// Integer.toString(t.tradeStatus));
			if (tb.tradeStatus != eTradeStatus.SOLD) {
				if (tb.state == eTaskState.NOT_STARTED) {

					for (int jj = 0; jj < tb.taskCount(); jj++)

					{
						Task tjj = tb.GetTask(jj);

						int dst = (int) CalcDistance(approach, tjj);
					    Yaz("Distance calculated for tsp: robot to task : " +tjj.taskName + " distance: "+dst);

						pi.path.SetDistance(0, ++pi.pointCount, dst);
						pi.path.SetDistance(pi.pointCount, 0, 99999);

					}

				}
			}
		}
		
		return pi; 
		
	}
	*/
	/*
	public PathInfo SetDistances_TasksToStations (PathInfo pi,  eBidValuation approach, Station st, Task tk)
	{
		if (st != null) {
			int dst = (int) CalcDistance(approach, tk, st);
			pi.path.SetDistance(pi.pointCount, citiesToVisit, dst);
			pi.path.SetDistance(citiesToVisit, pi.pointCount, 99999);

		}
		// BAÞLANGIÇ NOKTASINA DÖNÜLMESÝ
		else if (TSP_MODE == eTSPLoopMode.TSP_TOUR) {
			int dst = (int) CalcDistance(approach, tk, thisRobot);
			pi.path.SetDistance(pi.pointCount, citiesToVisit, dst);
			pi.path.SetDistance(citiesToVisit, pi.pointCount, 99999);

		}
		return pi; 
		
	}
	
	public PathInfo SetDistances_TasksToTasks (PathInfo pi,  eBidValuation approach, Station st)
	{
		for (int j = 0; j < mytempTaskBundles; j++) {
			TaskBundle tb = temptaskQueue[j];

			if (tb.tradeStatus != eTradeStatus.SOLD && tb.state == eTaskState.NOT_STARTED) {

				for (int k = 0; k < tb.taskCount(); k++) {

					Task tk = tb.GetTask(k);
					++pi.pointCount;
					pi.pointCount2 = 0;
					for (int jj = 0; jj < mytempTaskBundles; jj++) {
						if (j != jj) {

							TaskBundle tbjj = temptaskQueue[jj];

							if (tbjj.tradeStatus != eTradeStatus.SOLD && tbjj.state == eTaskState.NOT_STARTED) {

								for (int kk = 0; kk < tbjj.taskCount(); kk++) {

									++pi.pointCount2;
									Task tkk = tbjj.GetTask(kk);

									int dst = (int) CalcDistance(approach, tk, tkk);

									pi.path.SetDistance(pi.pointCount, pi.pointCount2, dst);
									pi.path.SetDistance(pi.pointCount2, pi.pointCount, dst);

								}
							}
						}
					}

					SetDistances_TasksToStations(pi, approach, st, tk); 

				
				}

			}
		}
		return pi; 
	}*/
/*
	public TSPMinPath SetDistancesToPath (TSPMinPath path,  Station st, eBidValuation approach) 
	{
		getCallerMethod();
		path.ResetDistances(); 
		int pointCount =0; 
		for (int j=0; j<mytempTaskBundles; j++) 
		   {
		       TaskBundle tb = temptaskQueue[j];
		       
			   if (tb.tradeStatus != eTradeStatus.SOLD )
		       {
		    	   if (tb.state == eTaskState.NOT_STARTED)
		    	   {    
		    		   
		    		   for (int jj =0; jj<tb.taskCount(); jj++)

		    		   {
		    			   Task tjj = tb.GetTask(jj); 
		    			   Yaz(thisRobot.robotName+"> tsp -> taskname : " + Integer.toString(j)+" -- "+tjj.taskName+" state "+ tb.state + " t.tradeStatus " + tb.tradeStatus);
		    			    int dst = (int) CalcDistance(approach, tjj) ; 
		    			    Yaz("Distance calculated for tsp: robot to task : " +tjj.taskName + " distance: "+dst);
		    			  
		    			    path.SetDistance(0, ++pointCount, dst);  
		    				path.SetDistance(pointCount, 0, 99999); 
		    		   
		    		   }

		    	   }
		       }
		   } 	
		
		if (st != null ) 
		{
			path.SetDistance(0, ++pointCount, 99999);  
			path.SetDistance(pointCount, 0, 99999); 
		} 
		// BAÞLANGIÇ NOKTASINA DÖNÜLMESÝ
		else if (TSP_MODE == eTSPLoopMode.TSP_TOUR) 
		{
			path.SetDistance(0, ++pointCount, 99999);  
			path.SetDistance(pointCount, 0, 99999); 
			
		}
		
		pointCount = 0; 
		int pointCount2 = 0; 
		   for (int j=0; j<mytempTaskBundles; j++) 
		   {
		       TaskBundle tb = temptaskQueue[j];
		    //   Yaz(thisRobot.robotName+"> tsp -> taskname : " + Integer.toString(j)+" -- "+t.taskName+" state "+ Integer.toString(t.state) + " t.tradeStatus " + Integer.toString(t.tradeStatus));
			   if (tb.tradeStatus != eTradeStatus.SOLD && tb.state == eTaskState.NOT_STARTED)
		       {
			      
		    		for (int k=0; k<tb.taskCount(); k++) 
		    		{
				   
				       Task tk = tb.GetTask(k); 
				       ++pointCount; 
				       pointCount2 = 0;
		    			for (int jj=0; jj<mytempTaskBundles; jj++) 
		    		    {
		    		       if (j!=jj) 
		    		       {
		    				
		    				TaskBundle tbjj = temptaskQueue[jj];
		    		        
		    			    if (tbjj.tradeStatus != eTradeStatus.SOLD && tbjj.state == eTaskState.NOT_STARTED )
		    		        {

		    	    		     
		    		    		for (int kk=0; kk<tbjj.taskCount(); kk++) 
		    		    		{
		    				   
		    		    			++pointCount2;
		    		    			Task tkk = tbjj.GetTask(kk);     
		    				        	   	    		 

		    	    			    int dst = (int) CalcDistance(approach, tk, tkk) ; 
		    		    			
		    	    			 
				    		    	path.SetDistance(pointCount, pointCount2, dst);  
				    		     	path.SetDistance(pointCount2, pointCount, dst);

		    				       
		    		    		}
		    		       }
		    		       }
		    		     }
		    			
		    			//if (!TSPLOOPMODE&& EXECUTION_PHASE>0 && citiesToVisit>0) 
		    			//{
	    			    
	    				if (st != null ) 
	    				{
	    					int dst = (int) CalcDistance(approach, tk, st) ; 
	    					path.SetDistance(pointCount, citiesToVisit, dst);  
			    		    path.SetDistance(citiesToVisit, pointCount, 99999);
	    		
	    				}
	    				// BAÞLANGIÇ NOKTASINA DÖNÜLMESÝ
	    				else if (TSP_MODE == eTSPLoopMode.TSP_TOUR) 
	    				{
	    					int dst = (int) CalcDistance(approach, tk, thisRobot) ; 
	    					path.SetDistance(pointCount, citiesToVisit, dst);  
			    		    path.SetDistance(citiesToVisit, pointCount, 99999);
	    					
	    				}
			    		//} 
		    		}

		       }
		   }
		   
		   return path; 

	}*/
/*
	public TSPMinPath SetDistancesToPath(TSPMinPath path, Station st, int approach) {

		PathInfo pi = new PathInfo(); 
		pi.path = path; 
		path.ResetDistances();
		pi.pointCount = 0;
		
		pi = SetDistances_RobotToTasks(pi, approach); 
		
		if (st != null) {
			pi.path.SetDistance(0, ++pi.pointCount, 99999);
			pi.path.SetDistance(pi.pointCount, 0, 99999);
		}
		// BAÞLANGIÇ NOKTASINA DÖNÜLMESÝ
		else if (TSP_MODE == 0) {
			pi.path.SetDistance(0, ++pi.pointCount, 99999);
			pi.path.SetDistance(pi.pointCount, 0, 99999);

		}

		pi.pointCount = 0;
		pi.pointCount2 = 0;
		pi = SetDistances_TasksToTasks(pi, approach, st); 

		return pi.path;

	}*/

	public TSPMinPath TSPCreate(Station st) {

		citiesToVisit = 0;
		TSPMinPath pathx = new TSPMinPath(is_main, TSP_MODE);

		pathx = AddTasksToTSPPath(pathx, st);
		//path = SetDistancesToPath(path, st, approach);

		return pathx;
	}

	public int WaitingTasks() {
		int waiting = 0;
		for (int i = 0; i < myTaskBundles; i++) {
			if (taskQueue[i].state == eTaskState.NOT_STARTED) {
				waiting++;
			}
		}

		return waiting;
	}

	public int CompletedTasks() {
		int completed = 0;
		for (int i = 0; i < myTaskBundles; i++) {
			if (taskQueue[i].state == eTaskState.COMPLETED) {
				completed++;
			}
		}

		return completed;
	}

	public double TSPLength(eBidValuation approach) {

		// Yaz(thisRobot.robotName + "> TSPLength : mytemptaskbundles: " + mytempTaskBundles + " approach : " + approach);
		if (mytempTaskBundles > 0) {   // görev yoksa direk 0 döndür 
			long begin = System.currentTimeMillis();
			double minLen = 10000000;
			int mini = -1;
			TSPMinPath path = null;

			if ((TSP_MODE==eTSPLoopMode.TSP_PATH || TSP_MODE==eTSPLoopMode.TSP_TOUR)  || stationCount == 0) {  // Ýstasyona dönmesi gerekmiyorsa 
				path = TSPCreate(null);
				minLen = path.TSPPathLength();
				mini = 999;

			} else { // istasyonlar varsa en uygununu aramak zorundadýr. 
				for (int i = 0; i < stationCount; i++) {
					Station st = is_main.GetStation(i);
					path = TSPCreate(st);
					double stLen = path.TSPPathLength();
					if (stLen < minLen) {
						minLen = stLen;
						mini = i;

					} // if 
				}  // for 
			} // else 

			path = null;
			long end = System.currentTimeMillis();
			if (mini > -1) {
				Yaz(thisRobot.robotName + "> TSPLength :" + minLen + " time : " + (end - begin));
				return minLen;

			} else {
				Yaz(thisRobot.robotName + "> TSPLength - Hesaplanamadý  " + " time : " + (end - begin));
				return -1;
			}
		} 
		/*else if (mytempTaskBundles>0 && mytempTaskBundles<3)
		{
			return 
		}*/
		
		else { // görev yoksa 
			Yaz(thisRobot.robotName + "> TSPLength - Görev olmadýðý için sýfýr (0)  ");
			return 0;
		}
	}

	public double TSPLengthCurrent(eBidValuation approach) {
		if (WaitingTasks() > 0) {
			CloneTaskQueue();

			return TSPLength(approach);
		} else
			return 0;
	}

	public double TSPLengthIncludingTaskBundle(TaskBundle tBundle, eBidValuation approach) {
		CloneTaskQueue();
		IncludeTaskBundle(tBundle);

		return TSPLength(approach);
	}

	public double TSPLengthExcludingTaskBundle(TaskBundle tBundle, eBidValuation approach) {

		if (WaitingTasks() > 0) {
			int tIndex = MyTaskBundleIndex_B(tBundle.GetTask(0).bundleHead);
			CloneTaskQueue();
			if (tIndex > -1) {
				ExcludeTaskBundle(tBundle.GetTask(0).bundleHead);

			}

			return TSPLength(approach);
		} else
			return 0;

	}

	public int Furthest() {

		int maxDist = 0;
		int maxi = -1;

		for (int i = 0; i < myTaskBundles; i++) {
			TaskBundle tb = taskQueue[i];
			if (tb.state == eTaskState.NOT_STARTED && tb.tradeStatus == eTradeStatus.OWNED) {
				int distance = (int) CalcTPDistance(tb.GetTask(0));
				if (distance > maxDist) {
					distance = maxDist;
					maxi = i;

				}
			}
		}

		return maxi;
	}



	public void ResetOffers() {
		for (int i = 0; i < myTaskBundles; i++)
			taskQueue[i].offered = false;

	}

	
	public void CheckTrades() {
		int tradedTaskCount = 0;
		for (int i = 0; i < myTaskBundles; i++)
			if (taskQueue[i].traded == false)
				tradedTaskCount++;
		if (tradedTaskCount == myTaskBundles)
			for (int i = 0; i < myTaskBundles; i++)
				taskQueue[i].traded = false;
	}

	public int MyClosestTaskDistanceDStar(TaskBundle worstTaskForMe) {
		//getCallerMethod();
		int minLen = 10000;
		if (WaitingTasks() > 0) {

			for (int i = 0; i < myTaskBundles; i++) {
				TaskBundle tb = taskQueue[i];
				if (tb.state == eTaskState.NOT_STARTED) {
					int dist = DStarLiteDistance(tb, worstTaskForMe);
					if (dist < minLen) {
						minLen = dist;
					}
				}
			}
		} else {
			minLen = DStarLiteDistance(thisRobot, worstTaskForMe);
		}
		return minLen;
	}
	
	public int MyClosestTaskDistanceTPM(Task t) {
		
		int minLen = 10000;
		if (WaitingTasks() > 0) {

			for (int i = 0; i < myTaskBundles; i++) {
				TaskBundle tb = taskQueue[i];
				if (tb.state == eTaskState.NOT_STARTED && tb.GetTask(0).taskName.compareTo(t.taskName)!=0 ) {
					int dist = CalcTPDistance(tb.GetTask(0), t);
					if (dist < minLen) {
						minLen = dist;
					}
				}
			}
		} else {
			
			
			minLen = 0; //CalcTPDistance(worstTaskForMe.GetTask(0));
		}
		return minLen;
	}

	public int MyClosestTaskDistanceTPM(TaskBundle worstTaskForMe) {
		return MyClosestTaskDistanceTPM (worstTaskForMe.GetTask(0)); 
	}

	public int MyClosestTaskDistanceDStar(Task t) {
	
		//getCallerMethod();
		TaskBundle tb = new TaskBundle();
		tb.AddTask(t);
		return MyClosestTaskDistanceDStar(tb);
	}
	public void PrepareRobotReceivers () 
	{
		rc1 = new String[robotCount];
		for (int i = 0; i < robotCount; i++) {

			rc1[i] = robots[i].robotName;

		}
		
	}
	
	
	public int Pricing ( TaskBundle tb,  eBidValuation approach) 
	{
		int price = -1; 
		int tpDist; 
		int closest ;
		int localCost =tb.CalculateCost();
		/*if (approach==eBidValuation.EUCLIDIAN)
			price = (int) CalcTPDistance(tb.GetTask(0));
		*/
	
		 if (approach==eBidValuation.FUZZY)
		{
			Task t = tb.GetTask(0); 
			tpDist = (int) CalcTPDistance(t);
			closest =  MyClosestTaskDistanceTPM(tb); 
			price = localCost+ tpDist + closest;
			Yaz (thisRobot.robotName+" > PRICING : "+ tb.GetTask(0).taskName +  "  Price :  "+ price + " tpDist "+tpDist + " closest :" + closest + " Waiting : " + WaitingTasks());
		}
		else if (approach==eBidValuation.DSTARLITE  || approach==eBidValuation.DSTARLITEFUZZY)
		{
			tpDist = (int)  DStarLiteDistance(tb);
			closest =  MyClosestTaskDistanceDStar(tb);
			price = localCost+ tpDist + closest;
		}
		/*else if (approach==eBidValuation.RRT)
		{
			price = localCost + RRTDistance(tb);
		}*/
		
		
	//	else if (approach==eBidValuation.TMPRRTTSP || approach==eBidValuation.TMPTSP || approach==eBidValuation.DSTARLITETSP)
		else  if (approach==eBidValuation.EUCLIDIAN)
     	{
			price = localCost + (int) TSPLengthCurrent(approach)- (int) TSPLengthExcludingTaskBundle(tb,approach);
		}
		
		else if (approach==eBidValuation.TMPRRTTSP || approach==eBidValuation.TMPTSP || approach==eBidValuation.DSTARLITETSP)
		{
			int tsplen = (int) TSPLengthCurrent(approach); 
			int tspexc = (int) TSPLengthExcludingTaskBundle(tb,approach); 
			
			//price = localCost + tsplen - tspexc + (int)  (tsplen * 0.2) ; //+ mytempTaskBundles*10;
			// price = localCost + tsplen - tspexc + (int)  (tsplen * 0.2) ; 
			price = localCost + tsplen - tspexc + (int)  (tsplen * 0.2) ; 
		}
	
		/*switch (approach) {
		case 1: // 1 : Proposal_Euclidian : DÝREK UZAKLIK
			// price = (int) CalcDistance(tb.GetTask(0).xLoc,tb.GetTask(0).yLoc);
			price = (int) CalcTPDistance(tb.GetTask(0));
			break;

		case 2:// 2: fUZZY
			Task t = tb.GetTask(0); 
			tpDist = (int) CalcTPDistance(t);
			closest =  MyClosestTaskDistanceTPM(tb); 
			price = localCost+ tpDist + closest;
			Yaz (thisRobot.robotName+" > PRICING : "+ tb.GetTask(0).taskName +  "  Price :  "+ price + " tpDist "+tpDist + " closest :" + closest + " Waiting : " + WaitingTasks());
			//price = PrepareAPriceFuzzy(t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc)); 
				break;
		case 3:
			tpDist = (int)  DStarLiteDistance(tb);
			closest =  MyClosestTaskDistanceDStar(tb);
			price = localCost+ tpDist + closest;
			//price = localCost	+ DStarLiteDistance(tb); 
			break;
			
		case 4:// 3: DStarLiteDistance
			tpDist = (int)  DStarLiteDistance(tb);
			closest =  MyClosestTaskDistanceDStar(tb);
			price = localCost+ tpDist + closest;
			
			
			break; 
	

		case 5:// 5: RRT Approach
				price = localCost + RRTDistance(tb);
		
			break;
		case 6:// 6: AP3 : TSP Approach
		case 7:		
		case 8:	
    			price = localCost + (int) TSPLengthCurrent(approach)- (int) TSPLengthExcludingTaskBundle(tb,approach);
				break;
    	}*/
			
		return price; 
	}

	public class StartAnAuctionOnce_Sequential extends OneShotBehaviour {

		private static final long serialVersionUID = -7797752489460349535L;

		public void action() {

			//getCallerMethod ();
			SearchForRobots();
			CheckTrades();
			int worstIndex;
			tradedTask = worstIndex = MyTaskBundleIndex_B(worstTaskForMe .GetTask(0).bundleHead);
			// if (worstIndex > -1 && taskQueue[worstIndex].tradeStatus == 0 )
			if (worstTaskForMe != null && IsInHeatBoundary(worstTaskForMe)) {

				PrepareRobotReceivers(); // df ten tüm robotlar listelenir 
				taskQueue[worstIndex].price = worstTaskForMe.price; //Pricing (worstTaskForMe, TRADE_APPROACH);
				Yaz(thisRobot.robotName + ">  " + worstTaskForMe.GetTask(0).taskName + " GOREVINI SATISA CIKARIYORUM. BEDELI  : " + Integer.toString((int) taskQueue[worstIndex].price) + " Hesaplama Yöntemi "+ TRADE_APPROACH);
				currentPrice = taskQueue[worstIndex].price;
				taskQueue[worstIndex].tradeStatus = eTradeStatus.OFFERED; // call for proposals
				taskQueue[worstIndex].traded = true;
				taskQueue[worstIndex].offered = true;
				taskAnnounced = worstTaskForMe.GetTask(0).taskName;
				tradeCompleted = false;
				String rcd[] = { terrain };
				String mcd[] = { "114", Integer.toString(currentPrice), "0", thisRobot.robotName, "x", worstTaskForMe.GetTask(0).taskName, "x" };
				SendACLMessage(rcd, 1, mcd, 7);   // terraine satýþa çýkarýlan görev bilgisi
				String mc1[] = { "121", thisRobot.robotName, 
						worstTaskForMe.GetTask(0).taskName,
						Integer.toString(worstTaskForMe.GetTask(0).xLoc),
						Integer.toString(worstTaskForMe.GetTask(0).yLoc),
						worstTaskForMe.GetTask(0).bundleHead,
						Integer.toString(worstTaskForMe.price) };
				Yaz(thisRobot.robotName + ">  *** 121 *** ");
				SendACLMessage(rc1, robotCount, mc1, 7);  // müzayedeciye satýþa çýkarýlan görev bilgisi
				propCount = 0;

			} else {
				String auc[] = { auctioneer };
				String mc1[] = { "117", thisRobot.robotName, "*", "*", "*" };
				SendACLMessage(auc, 1, mc1, 5);

			}

		}// action
	}
	public int Surroundings (int x, int y) 
	{
		int radius = 5; //(int) robotStep; 
		int retVal = rw*11; 
		Wall walla = new Wall (new WallNode(x-radius,y-radius),new WallNode(x+radius,y-radius));
		Wall wallb = new Wall (new WallNode(x-radius,y-radius),new WallNode(x-radius,y+radius));
		Wall wallc = new Wall (new WallNode(x-radius,y+radius),new WallNode(x+radius,y+radius));
		Wall walld = new Wall (new WallNode(x+radius,y+radius),new WallNode(x+radius,y-radius));
		
		// if (is_main.InterSection(walla) || is_main.InterSection(wallb) || is_main.InterSection(wallc) || is_main.InterSection(walld))
		if (is_main.InterSection(walla)) 
			retVal = retVal+rw; 
		if (is_main.InterSection(wallb)) 
			retVal = retVal +rw*10; 
		if (is_main.InterSection(wallc)) 
			retVal = retVal -rw; 
		if (is_main.InterSection(walld)) 
			retVal = retVal -rw*10; 
		
		return retVal; 
	}
	


	public class MoveRobot extends TickerBehaviour {
		/**
	 * 
	 */

		// private int calcX , calcY;
		private static final long serialVersionUID = -7349838116540349504L;
		

		public MoveRobot(Agent a, long interval) {
			super(a, interval);
		}

		
		protected void onTick() {

			ldr.scan(thisRobot);
			
			double sumRange = 0;
			double minn = ldr.range * 2;
			double maxx = 0;
			int mini = 0; 
			int maxi = 0; 
			globalMovementCounter++; 
			System.out.println("Hareket: " +globalMovementCounter + "  Heading : " + thisRobot.heading );
		
			for (int i=0; i<ldr.numberofSectors; i++) 
			{
				System.out.println(i  + ".  sektör -    " +  ldr.meanProximities[i]);
				
			} 
			
			

			ArrayList<Integer> availables = new ArrayList<Integer>();
			ArrayList<Integer> maybes = new ArrayList<Integer>();
					
			// versiyon 1 
			// karþý sektörlerin toplamýna bakýyordu, kesiþmede hata veriyor. 
			
			for (int i=0; i<ldr.numberofSectors; i++) 
			{

				
				sumRange=ldr.meanProximities[i]; 

				 if (minn>sumRange) 
				 {
					 minn = sumRange;
					 mini = i; 
				 }
				 if (sumRange >= (ldr.range ) - (ldr.range*0.1)) 
				 {
					 availables.add (i); 
				 }
				 
				 if (maxx<sumRange) 
				 {
					 maxx = sumRange;
					 maxi = i; 
				 }
				 
				 if (sumRange > ldr.range*0.4) 
				 {
					 maybes.add(i);
				 }
				 System.out.println("i: " + i + " sumRange: " + sumRange + " availables: " + availables.size() + " min: " + minn + " max: " + maxx);
			} 
			
			Linee ln = new Linee (new Positione (pre5x, pre5y), new Positione (thisRobot.xLoc, thisRobot.yLoc));
			/*if (globalMovementCounter%(10000/motionInterval) == 0 && ln.length() <= ldr.range ) 
			{
		    	maybes.addAll(availables); 
				int randomSelection =   (int) (Math.random() * maybes.size()); 
				int selection = maybes.get(randomSelection);
				thisRobot.heading = ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				System.out.println("Bir yere sýkýþýp kaldý, rastgele bir tanesine dönülecek. Rastgele seçilen yön :" + thisRobot.heading); 
			
			}*/
			
			if (availables.size() == ldr.numberofSectors) 
			{
				// heading won't change 
				System.out.println("Tüm çevre açýk... Yön deðiþmedi.... " + + thisRobot.heading);
				
			} 
			
		// contingency, select the maxx  
			else if (availables.size() > 0)
			{
				int randomSelection =   randomGenerator.nextInt(availables.size());  
				int selection = availables.get(randomSelection);
				int randomSelection2 =   randomGenerator.nextInt(2);  
				thisRobot.heading = randomSelection2 == 0? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ;
				System.out.println("Engeller var ama tamamen müsait yerler de var, rastgele bir tanesine dönülecek. Rastgele seçilen yön :" + thisRobot.heading); 
			}
			else if (availables.size() == 0 && maybes.size()>0)
			{
			
				//thisRobot.heading = ldr.proximityVector[maxi]  > ldr.proximityVector[maxi + ldr.numberofSectors/2]? maxi * 360 / ldr.numberofSectors :  (maxi + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				int randomSelection =   (int) (Math.random() * maybes.size()); 
				int selection = maybes.get(randomSelection);
				thisRobot.heading = selection * 360 / ldr.numberofSectors ;  // ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				System.out.println("Tüm çevrede engeller var, rastgele bir yere dönülecek. Rastgele seçilen yön :" + thisRobot.heading); 
				
			}			
			else if (availables.size() + maybes.size()>0)
			{
			 
				maybes.addAll(availables); 
				int randomSelection =   (int) (Math.random() * maybes.size()); 
				int selection = maybes.get(randomSelection);
				thisRobot.heading = selection * 360 / ldr.numberofSectors ;//ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				System.out.println("Çevrede müsait yerler var, rastgele birisine dönülecek: seçim: " + thisRobot.heading); 
			
				
			/*	while ( !(selection <= curr+ldr.numberofSectors/3 && selection >=curr-ldr.numberofSectors/3) )
						{
					selection =   (int) (Math.random() * availables.size()); 
						}; 
				*/ 
				
				//thisRobot.heading = ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
			} else 
			{
				thisRobot.heading = maxi * 360 / ldr.numberofSectors ; 
				System.out.println("Çevre çok dar, ilk bulduðu yerden kaçmaya çalýþacak. seçim: " + thisRobot.heading); 
				
			}
	
			
			System.out.println("Hareket: " +globalMovementCounter + "  Heading : " + thisRobot.heading );
		
			
			//double step = 3 * motionInterval /1000 ; // for 10 km/h
			double step = 10; 
			double radian = thisRobot.heading * 0.0174532925;
			
			
			
			
			//double tx = endPosition.x;
			//				double ty = endPosition.y;
							double rx = thisRobot.dxLoc;
							double ry = thisRobot.dyLoc;
			
							double tx = rx + ldr.range *  Math.cos(thisRobot.heading*Math.PI/180) ;
							double ty = ry + ldr.range * Math.sin(thisRobot.heading*Math.PI/180);
							
						
							
				double ydiff = ty - ry > 0.0 ? 1.0 : -1.0;
				double xdiff = tx - rx > 0.0 ? 1.0 : -1.0;
				
				
				// double xx = h * Math.cos(angle*Math.PI/180) ;
				// double yy = h * Math.sin (angle*Math.PI/180) ;
				
				double xx = step * Math.cos(radian) ;
				double yy = step * Math.sin(radian) 
						;
				
				System.out.println("tx: " + tx + " ty: "+ty + " rx: " + rx + " ry: "+ry + " xx: " + xx + " yy: "+ yy);
				Wall w = new Wall ((int) thisRobot.dxLoc, (int) thisRobot.dyLoc, (int) (thisRobot.dxLoc+xx), (int) (thisRobot.dyLoc+yy));
				if (is_main.InterSection(w))

				{
				
					System.out.print("DANGER ---- New position will be on the restricted space");
					
				}
				else 
				{
					
					thisRobot.dxLoc += xx;
					thisRobot.dyLoc += yy; 
					thisRobot.xLoc = (int) thisRobot.dxLoc; 
					thisRobot.yLoc = (int) thisRobot.dyLoc;
				}
				
				
				
				
				if (globalMovementCounter%(10000/motionInterval) == 0) 
				{
				     pre5x = thisRobot.xLoc; 
				     pre5y = thisRobot.yLoc;
				}
//				double fx = xx * xdiff;//Math.round(xx * xdiff);
//				double fy = yy * ydiff;//Math.round(yy * ydiff);

//				double instantWay = Math.sqrt(fx * fx + fy * fy);
			
			// addBehaviour(new SendLidarInfoToTerrain());
			
			
	

		} // end of on tick
		protected void onTick3() {

			ldr.scan(thisRobot);
			double sumRange = 0;
			double minn = ldr.range * 2;
			double maxx = 0;
			int mini = 0; 
			int maxi = 0; 
			globalMovementCounter++; 
			System.out.println("Hareket: " +globalMovementCounter + "  Heading : " + thisRobot.heading );
		
			/*for (int i=0; i<ldr.numberofSectors; i++) 
			{
				System.out.println(i  + ".  sektör -    " +  ldr.proximityVector[i]);
			}*/

			ArrayList<Integer> availables = new ArrayList<Integer>();
			ArrayList<Integer> maybes = new ArrayList<Integer>();
					
			// versiyon 1 
			// karþý sektörlerin toplamýna bakýyordu, kesiþmede hata veriyor. 
			
			for (int i=0; i<ldr.numberofSectors; i++) 
			{
				//if (ldr.proximityVector[i]<=0.1 ) 
				//	restricteds.add (i); 
				
				sumRange=ldr.meanProximities[i]; // + ldr.proximityVector[i+ldr.numberofSectors/2]; 
						
				 if (minn>sumRange) 
				 {
					 minn = sumRange;
					 mini = i; 
				 }
				 if (sumRange >= (ldr.range ) - (ldr.range*0.1)) 
				 {
					 availables.add (i); 
				 }
				 
				 if (sumRange>6) 
				 {
					// maxx = sumRange;
					// maxi = i; 
					 maybes.add(i);
				 }
				 System.out.println("i: " + i + " sumRange: " + sumRange + " availables: " + availables.size() + " min: " + minn + " max: " + maxx);
			} 
			Linee ln = new Linee (new Positione (pre5x, pre5y), new Positione (thisRobot.xLoc, thisRobot.yLoc));
			
			
			if (availables.size() == ldr.numberofSectors) 
			{
				// heading won't change 
				System.out.println("Tüm çevre açýk... Yön deðiþmedi.... ");
			} 
			else 
			{
		    	maybes.addAll(availables); 
				int randomSelection =   randomGenerator.nextInt(maybes.size()); 
				int selection = maybes.get(randomSelection);
				thisRobot.heading = selection * 360 / ldr.numberofSectors ; 
				//thisRobot.heading = ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				
			
			}
			
			
			System.out.println("Hareket: " +globalMovementCounter + "  Heading : " + thisRobot.heading );
		
			
			// double step = 2.78 * motionInterval/1000.0; // for 10 km/h
			double step = 10; 
			double radian = thisRobot.heading * 0.0174532925;
			
			
			
			
			//double tx = endPosition.x;
			//				double ty = endPosition.y;
							double rx = thisRobot.xLoc;
							double ry = thisRobot.yLoc;
			
							double tx = rx + ldr.range *  Math.cos(thisRobot.heading*Math.PI/180) ;
							double ty = ry + ldr.range * Math.sin(thisRobot.heading*Math.PI/180);
							
						
							
				double ydiff = ty - ry > 0.0 ? 1.0 : -1.0;
				double xdiff = tx - rx > 0.0 ? 1.0 : -1.0;
				
				
				// double xx = h * Math.cos(angle*Math.PI/180) ;
				// double yy = h * Math.sin (angle*Math.PI/180) ;
				
				double xx = step * Math.cos(radian) ;
				double yy = step * Math.sin(radian) 
						;
				
				System.out.println("tx: " + tx + " ty: "+ty + " rx: " + rx + " ry: "+ry + " xx: " + xx + " yy: "+ yy);
				Wall w = new Wall (thisRobot.xLoc, thisRobot.yLoc, thisRobot.xLoc+(int)xx, thisRobot.yLoc+(int)yy);
				if (is_main.InterSection(w))

				{
				
					System.out.print("DANGER ---- New position will be on the restricted space");
					
				}
				else 
				{
					thisRobot.xLoc += (int) xx; 
					thisRobot.yLoc += (int) yy;
				}
				
				
				
				
				if (globalMovementCounter%10 == 0) 
				{
				     pre5x = thisRobot.xLoc; 
				     pre5y = thisRobot.yLoc;
				}
//				double fx = xx * xdiff;//Math.round(xx * xdiff);
//				double fy = yy * ydiff;//Math.round(yy * ydiff);

//				double instantWay = Math.sqrt(fx * fx + fy * fy);
			
			
			
			
	

		} // end of on tick
		protected void onTick2() {

			ldr.scan(thisRobot);
			double sumRange = 0;
			double minn = ldr.range * 2;
			double maxx = 0;
			int mini = 0; 
			int maxi = 0; 
			globalMovementCounter++; 
			System.out.println("Hareket: " +globalMovementCounter + "  Heading : " + thisRobot.heading );
		
			/*for (int i=0; i<ldr.numberofSectors; i++) 
			{
				System.out.println(i  + ".  sektör -    " +  ldr.proximityVector[i]);
			}*/

			ArrayList<Integer> availables = new ArrayList<Integer>();
			ArrayList<Integer> maybes = new ArrayList<Integer>();
					
			// versiyon 1 
			// karþý sektörlerin toplamýna bakýyordu, kesiþmede hata veriyor. 
			
			for (int i=0; i<ldr.numberofSectors/2; i++) 
			{
				//if (ldr.proximityVector[i]<=0.1 ) 
				//	restricteds.add (i); 
				
				sumRange=ldr.proximityVector[i] + ldr.proximityVector[i+ldr.numberofSectors/2]; 
				//System.out.println ("MoveRobot : " + )
				 if (minn>sumRange) 
				 {
					 minn = sumRange;
					 mini = i; 
				 }
				 if (sumRange >= (ldr.range * 2) - (ldr.range*0.1)) 
				 {
					 availables.add (i); 
				 }
				 
				/* if (maxx<sumRange) 
				 {
					 maxx = sumRange;
					 maxi = i; 
					 maybes.add(i);
				 }*/
				 
				 if (sumRange > 10) 
				 {
					 maybes.add(i);
				 }
				 System.out.println("i: " + i + " sumRange: " + sumRange + " availables: " + availables.size() + " min: " + minn + " max: " + maxx);
			} 
			Linee ln = new Linee (new Positione (pre5x, pre5y), new Positione (thisRobot.xLoc, thisRobot.yLoc));
			if (globalMovementCounter%(10000/motionInterval) == 0 && ln.length() <= ldr.range ) 
			{
		    	maybes.addAll(availables); 
				int randomSelection =   (int) (Math.random() * maybes.size()); 
				int selection = maybes.get(randomSelection);
				thisRobot.heading = ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				System.out.println("Bir yere sýkýþýp kaldý, rastgele bir tanesine dönülecek. Rastgele seçilen yön :" + thisRobot.heading); 
			
			}
			
			else if (availables.size() == ldr.numberofSectors/2) 
			{
				// heading won't change 
				System.out.println("Tüm çevre açýk... Yön deðiþmedi.... " + + thisRobot.heading);
				
			} 
			
		    // Ýkinci yöntem de çalýþmadý
			/*for (int i=0; i<ldr.numberofSectors; i++) 
			{
				  sumRange=ldr.proximityVector[i];  //  + ldr.proximityVector[i+ldr.numberofSectors/2]; 
				 if (minn>sumRange) 
				 {
					 minn = sumRange;
					 mini = i; 
				 }
				 if (sumRange >= (ldr.range) - 0.01) 
				 {
					 availables.add (i); 
				 }
				 
				 if (maxx<sumRange) 
				 {
					 maxx = sumRange;
					 maxi = i; 
				 }
			} 
			if (availables.size() == ldr.numberofSectors) 
			{
				// heading won't change 
				System.out.println("Tüm çevre açýk... Yön deðiþmedi.... ");
				
			} */

			// contingency, select the maxx  
			else if (availables.size() > 2)
			{
				int randomSelection =   randomGenerator.nextInt(availables.size());  
				int selection = availables.get(randomSelection);
				int randomSelection2 =   randomGenerator.nextInt(2);  
				thisRobot.heading = randomSelection2 == 0? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ;
				System.out.println("Engeller var ama tamamen müsait yerler de var, rastgele bir tanesine dönülecek. Rastgele seçilen yön :" + thisRobot.heading); 
			}
			else if (availables.size() == 0)
			{
			
				//thisRobot.heading = ldr.proximityVector[maxi]  > ldr.proximityVector[maxi + ldr.numberofSectors/2]? maxi * 360 / ldr.numberofSectors :  (maxi + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				int randomSelection =   (int) (Math.random() * maybes.size()); 
				int selection = maybes.get(randomSelection);
				thisRobot.heading = ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				System.out.println("Tüm çevrede engeller var, rastgele bir yere dönülecek. Rastgele seçilen yön :" + thisRobot.heading); 
				
			}			
			else 
			{
			 
				maybes.addAll(availables); 
				int randomSelection =   (int) (Math.random() * maybes.size()); 
				int selection = maybes.get(randomSelection);
				thisRobot.heading = ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
				System.out.println("Çevrede müsait yerler var, rastgele birisine dönülecek: seçim: " + thisRobot.heading); 
			
				
			/*	while ( !(selection <= curr+ldr.numberofSectors/3 && selection >=curr-ldr.numberofSectors/3) )
						{
					selection =   (int) (Math.random() * availables.size()); 
						}; 
				*/ 
				
				//thisRobot.heading = ldr.proximityVector[selection]  > ldr.proximityVector[selection + ldr.numberofSectors/2]? selection * 360 / ldr.numberofSectors :  (selection + ldr.numberofSectors/2) * 360 / ldr.numberofSectors ; 
			}
	
			
			System.out.println("Hareket: " +globalMovementCounter + "  Heading : " + thisRobot.heading );
		
			
			 double step = 10 * motionInterval /1000 ; // for 10 km/h
			//double step = 6; 
			double radian = thisRobot.heading * 0.0174532925;
			
			
			
			
			//double tx = endPosition.x;
			//				double ty = endPosition.y;
							double rx = thisRobot.xLoc;
							double ry = thisRobot.yLoc;
			
							double tx = rx + ldr.range *  Math.cos(thisRobot.heading*Math.PI/180) ;
							double ty = ry + ldr.range * Math.sin(thisRobot.heading*Math.PI/180);
							
						
							
				double ydiff = ty - ry > 0.0 ? 1.0 : -1.0;
				double xdiff = tx - rx > 0.0 ? 1.0 : -1.0;
				
				
				// double xx = h * Math.cos(angle*Math.PI/180) ;
				// double yy = h * Math.sin (angle*Math.PI/180) ;
				
				double xx = step * Math.cos(radian) ;
				double yy = step * Math.sin(radian) 
						;
				
				System.out.println("tx: " + tx + " ty: "+ty + " rx: " + rx + " ry: "+ry + " xx: " + xx + " yy: "+ yy);
				Wall w = new Wall (thisRobot.xLoc, thisRobot.yLoc, thisRobot.xLoc+(int)xx, thisRobot.yLoc+(int)yy);
				if (is_main.InterSection(w))

				{
				
					System.out.print("DANGER ---- New position will be on the restricted space");
					
				}
				else 
				{
					thisRobot.xLoc += (int) xx; 
					thisRobot.yLoc += (int) yy;
				}
				
				
				
				
				if (globalMovementCounter%(10000/motionInterval) == 0) 
				{
				     pre5x = thisRobot.xLoc; 
				     pre5y = thisRobot.yLoc;
				}
//				double fx = xx * xdiff;//Math.round(xx * xdiff);
//				double fy = yy * ydiff;//Math.round(yy * ydiff);

//				double instantWay = Math.sqrt(fx * fx + fy * fy);
			
			// addBehaviour(new SendLidarInfoToTerrain());
			
			
	

		} // end of on tick
		
	} // end of Move robot

	//

	public class ProposalEvaluation_ForSequential extends OneShotBehaviour {
		/**
	 * 
	 */
		private static final long serialVersionUID = -1039387129929283969L;

		public void action() {

			//getCallerMethod ();
			Yaz(thisRobot.robotName
					+ "> Teklifler deðerlendirildi. "
					+ proposalEvaluationProcess);
			taskBuyer = "Tanýmsýz";
			taskAnnounced = taskQueue[tradedTask].GetTask(0).taskName;
			taskSold = "Tanýmsýz";
			if (ProposalsCompleted_Sequential()) {
				// addBehaviour( new EvaluateProposals_forSequential(thisa,
				// 10));

				if (EvaluateProposals_Sequential()) {

					Yaz(thisRobot.robotName
							+ "> **** SATIÞ BAÞARILI  , ONAY BEKLENÝYOR  ****  ");
				} else {
					Yaz(thisRobot.robotName + "> SATIÞ BAÞARISIZ  !!!!  ");
					I_CAN_TRADE = false; 

				}
				if (robotCount == 1)
					tradeCompleted = true;
				Yaz(thisRobot.robotName
						+ "> END OF A TRADE MESAJI 113 GÖNDERÝLÝYOR  !!!!  ");
				addBehaviour(new EndOfATrade(thisa, 1000));

			} else {
				Yaz(thisRobot.robotName
						+ "> Teklif deðerlendirmeye geçecek kadar teklif gelmedi !!! ");
				propCount++;
				tradeCompleted = true;
				if (propCount == robotCount) {
					Yaz(thisRobot.robotName
							+ "> END OF A TRADE MESAJI 113 GÖNDERÝLÝYOR  !!!!  ");
					addBehaviour(new EndOfATrade(thisa, 1000));
				}
			}

		} // action
	}

	public void SendTaskCompletionMessageToAuctioneer(String taskName, int state) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		AID aid = new AID(auctioneer, AID.ISLOCALNAME);
		msg.addReceiver(aid);
		msg.setLanguage("English");
		msg.setContent("06_" + thisRobot.robotName + "_" + taskName + "_"
				+ Integer.toString(state));
		send(msg);
	}
	/*public void SendTaskCompletionMessageToTerrain(String taskName, int state) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		AID aid = new AID(terrain, AID.ISLOCALNAME);
		msg.addReceiver(aid);
		msg.setLanguage("English");
		msg.setContent("06_" + thisRobot.robotName + "_" + taskName + "_"
				+ Integer.toString(state));
		send(msg);
	}*/
	public void SendTaskInformationToTerrain(Task t) {

		
		String rc[] = { terrain };
		String mc[] = { "06", t.taskName, Integer.toString(t.xLoc),
				Integer.toString(t.yLoc), Integer.toString(t.state.getValue()),
				Integer.toString(t.taskType), t.ownerName

		};
		SendACLMessage(rc, 1, mc, 7);
	}
	
/*public void SendTaskInformationToTerrain_finished(Task t) {

		
		String rc[] = { terrain };
		String mc[] = { "772", t.taskName, Integer.toString(t.xLoc),
				Integer.toString(t.yLoc), Integer.toString(t.state.getValue()),
				Integer.toString(t.taskType), t.ownerName

		};
		SendACLMessage(rc, 1, mc, 7);
	}*/
	public void Sleepy (int duration) 
	{
		try {
			Thread.currentThread();
			// do what you want to do before sleeping
			Thread.sleep(duration);// sleep for pickup ope.
			// do what you want to do after sleeptig
		} catch (InterruptedException ie) {
			// If this thread was intrrupted by nother thread
		}
	}

	public class TaskCompletion extends OneShotBehaviour {
	
		private static final long serialVersionUID = -3955179738292598949L;

		public void action() {

			if (Executing != null) {

				int myTBI1 = MyTaskBundleIndex(Executing.taskName);
				int TBI = TaskBundleIndex(Executing.taskName);
				Yaz(thisRobot.robotName + ">" + Executing.taskName 	+ " gorevini tamamladým..." + myTBI1);

				Executing.state = eTaskState.COMPLETED;

					if (myTBI1 >= 0) {
						taskQueue[myTBI1].state = eTaskState.COMPLETED;
						taskBundles[TBI].state = eTaskState.COMPLETED;
						Sleepy (estimatedPickUpDuration * 1000 	/ simSpeed) ; 
						
					}
					if (Executing.taskName.compareTo("STATION") == 0) {
						thisRobot.status = eRobotStatus.IDLE;
						// Send
					}
					/*
					 * else { thisRobot.status = 1; }
					 */
					SendTaskCompletionMessageToAuctioneer(Executing.taskName, Executing.state.getValue());
					//SendTaskInformationToTerrain(Executing);
					//SendTaskCompletionMessageToTerrain(Executing.taskName, Executing.state.getValue());
					++thisRobot.completedTasks;
					

					addBehaviour(new InformLocationOnce());
					Yaz(thisRobot.robotName + " TaskCompletion ");
					addBehaviour(new ExecutionDelay(thisa, 10));
	
				Executing = null;
				thisRobot.status = eRobotStatus.IDLE;
				// ListMyTasks();

			} // if executing
 
		} // action 

	}

	public void SendProspectiveGain(eBidValuation approach) {

	//	if (AreThereNeighbors()) {
		
		if (I_CAN_TRADE) 
		{
			int worstIndex = FindMostUnavailable_Sequential(approach);

			if (worstIndex > -1) {

				worstTaskForMe = myTaskBundles > 0 ? taskQueue[worstIndex]: null;

				if (worstTaskForMe != null) {

					int price = worstTaskForMe.price;

					Yaz(thisRobot.robotName + "> SENDING MOST UNAVAILABLE 111: " + Integer.toString(price) + "_" + worstTaskForMe.GetTask(0).bundleHead);
					SendSelectedTaskForSelling(Integer.toString(price), worstTaskForMe.GetTask(0).taskName, Integer.toString(price));

				} else {

					SendSelectedTaskForSelling("0", "null", "-1");
					Yaz(thisRobot.robotName + "> SENDING MOST UNAVAILABLE : WORSTTASK NULL !!!!! ");

				}
			} else {

				Yaz(thisRobot.robotName + "> SENDING MOST UNAVAILABLE : NOTASKAVAILABLE");
				SendSelectedTaskForSelling("0", "null", "-1");

			}
			
		}
		 else {

				Yaz(thisRobot.robotName + "> I AM TEMPORARILY PENALIZED TO BE A SELLER");
				SendSelectedTaskForSelling("0", "null", "-1");

			}
	/*	} else {

			Yaz(thisRobot.robotName + "> SENDING MOST UNAVAILABLE : NO NEIGHBORS TO SELL PROPERTY1");
			SendSelectedTaskForSelling("0", "null", "-1");

		}*/
		tradingPhase = false;

	}

	public void SendSelectedTaskForSelling(String price, String taskName,
			String pathLength) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		AID aid = new AID(auctioneer, AID.ISLOCALNAME);
		msg.addReceiver(aid);
		msg.setLanguage("English");
		msg.setContent("111_" + thisRobot.robotName + "_" + price + "_" 
				+ taskName + "_" + pathLength);
		send(msg);

	}

	
	public void ShowProposals_Sequential() {
		Yaz("");
		Yaz(thisRobot.robotName + "> Gelen Teklifler : ");
		Yaz(thisRobot.robotName + "> TRADED TASK : "
				+ Integer.toString(tradedTask));
		if (tradedTask > -1) {

			Yaz ("Satýsa cýkarýlan gorev :  " + taskQueue[tradedTask].GetTask(0).taskName);
			Yaz(" ");

			for (int i = 0; i < robotCount; i++) {

				System.out.print(robots[i].robotName + "  ");
				System.out.print(proposalstoTradedTask[i] + " ");
				Yaz(" ");

			}
		}
	}

	public void ShowProposals() {
		
		//getCallerMethod ();
		Yaz("");
		Yaz(thisRobot.robotName + "> Gelen Teklifler : ");
		System.out.print("        ");
		for (int j = 0; j < myTaskBundles; j++)
			System.out.print(taskQueue[j].GetTask(0).taskName + " ");
		Yaz(" ");

		for (int i = 0; i < robotCount; i++) {

			System.out.print(robots[i].robotName + "  ");
			for (int j = 0; j < myTaskBundles; j++)
				System.out.print(proposalstoMyTasks[i][j] + " ");
			Yaz(" ");
		}

		Yaz("");
		Yaz(thisRobot.robotName + "> Verilen Teklifler : ");

		System.out.print("        ");
		for (int j = 0; j < others; j++)
			System.out.print(otherTasks[j].GetTask(0).taskName + " ");
		Yaz(" ");

		for (int i = 0; i < robotCount; i++) {

			System.out.print(robots[i].robotName + "  ");
			for (int j = 0; j < others; j++)
				System.out.print(proposalstoOtherTasks[i][j] + " ");
			Yaz(" ");
		}
		Yaz(" ");

	}

	/*
	 * public void ProposalPreparation (ACLMessage msg, String temp[]) {
	 * 
	 * 
	 * int tbIndex = MyTaskBundleIndex(temp[2]); if (tbIndex < 0 &&
	 * IsInHeatBoundary(temp[3], temp[4])) { int prop = 0;
	 * //Yaz(thisRobot.robotName+"> TRADE_APPROACH_prop" +
	 * Integer.toString(TRADE_APPROACH));
	 * 
	 * 
	 * switch (TRADE_APPROACH) {
	 * 
	 * case 1: // 1 : Proposal_Euclidian : Robotun göreve uzaklýðý prop =
	 * PrepareAProposal_Euclidian (temp[2], temp[3], temp[4], temp[6]); break;
	 * 
	 * /*case 2: // 2: ProposalY: Robotun göreve en yakýn görevine uzaklýðý + en
	 * yakýn görevinin o göreve uzaklýðý = toplam mesafesi prop =
	 * PrepareAProposalY (temp[2], temp[3], temp[4], temp[5]); break; * case 3:
	 * // 3: Görevi eklesem yolum ne kadar deðiþir? prop =
	 * PrepareAProposalDSTARLITE(temp[2], temp[3], temp[4], temp[6]) ;
	 * Yaz(thisRobot
	 * .robotName+"> "+temp[1]+" robotundan gelen "+temp[6]+" deðerindeki "
	 * +temp[2]+" gorevine teklif verdim DSTARLITE : "+Integer.toString(prop));
	 * break; case 4: // 3: Görevi eklesem yolum ne kadar deðiþir? prop =
	 * PrepareAProposalDSTARLITE(temp[2], temp[3], temp[4], temp[6]) ;
	 * Yaz(thisRobot
	 * .robotName+"> "+temp[1]+" robotundan gelen "+temp[6]+" deðerindeki "
	 * +temp[2]+" gorevine teklif verdim DSTARLITE : "+Integer.toString(prop));
	 * break;
	 * 
	 * case 5: // 5: direk uzaklýk + en yakýn görevimden uzaklýk prop =
	 * PrepareAProposalRRT (temp[2], temp[3], temp[4], temp[6]);
	 * Yaz(thisRobot.robotName
	 * +"> "+temp[1]+" robotundan gelen "+temp[6]+" deðerindeki "
	 * +temp[2]+" gorevine teklif verdim RRT : "+Integer.toString(prop)); // Bu
	 * yaklaþýmda iki robot da karþýlýklý olarak birbirlerinin görevlerini satýn
	 * aldýðý için avantaj kayboluyor. break; case 6: // 6: AP3 TSP APproach
	 * prop = PrepareAProposalTSP(temp[2], temp[3], temp[4], temp[6], 6); break;
	 * case 7: // 6: AP3 TSP-rrt APproach prop = PrepareAProposalTSP(temp[2],
	 * temp[3], temp[4], temp[6], 7); break; case 8: // 6: AP3 TSP-d* APproach
	 * prop = PrepareAProposalTSP(temp[2], temp[3], temp[4], temp[6], 8); break;
	 * case 9: // 9 prop = PrepareAProposalTSP(temp[2], temp[3], temp[4],
	 * temp[6], 6) + PrepareAProposal_Euclidian(temp[2], temp[3], temp[4],
	 * temp[6]) / 2; break; default : prop = 0;
	 * 
	 * 
	 * } if (prop < -1 ) prop = 999999999; ACLMessage rrmsg = msg.createReply();
	 * rrmsg.setPerformative(ACLMessage.INFORM); rrmsg.setContent("102_" +
	 * thisRobot.robotName+"_"+ temp[2]+"_"+ Integer.toString(prop));
	 * send(rrmsg); AddUpdateTask(temp[2], temp [3],temp[4], temp[5], temp[6],
	 * false) ; AddUpdateRobot(temp[1]); System.out.print
	 * (thisRobot.robotName+"> "
	 * +temp[1]+" robotundan gelen "+temp[6]+" deðerindeki "
	 * +temp[2]+" gorevine teklif verdim : "+Integer.toString(prop));
	 * PrintWallWatch();
	 * 
	 * int tIndex = TaskBundleIndex(temp[2]); int rIndex = RobotIndex(temp[1]);
	 * 
	 * proposalstoOtherTasks[rIndex][others] = prop ; taskBundles[tIndex].owner
	 * = rIndex; taskBundles[tIndex].state = 0; taskBundles[tIndex].tradeStatus
	 * = 0;
	 * 
	 * otherTasks[others++] = taskBundles[tIndex] ;
	 * 
	 * } }
	 */
	
	// ///////////////////////////////////////////
	public void TradeIsOver(ACLMessage msg, String temp[]) {
		Yaz(thisRobot.robotName
				+ "> ### 115 ### trade sona erdi ,çizelgelemeye geç ....TRADINGPHASE : "
				+ tradingPhase);

		tradingPhase = false;
		I_CAN_TRADE = true;

		if (WaitingTasks() > 0) {
			addBehaviour(new ScheduleDelay(thisa, 10));

		} else {
			SendScheduleCompletedMessage();
		}
	}

	// /////////////////////////////////////////////
	public void SendScheduleCompletedMessage() {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		AID aid = new AID(auctioneer, AID.ISLOCALNAME);
		msg.addReceiver(aid);
		msg.setLanguage("English");

		msg.setContent("69_" + thisRobot.robotName);
		send(msg);

	}

	public void TaskCompletionAcknowledge(ACLMessage msg, String temp[]) {
		int tbi = TaskBundleIndex(temp[1]);
		if (tbi > -1) {
			taskBundles[tbi].state = eTaskState.COMPLETED;
		}
	}
	
	 /**
	  * @author Debadatta Mishra(PIKU)
	  *
	  */

	// Inner class
	// Listen and Reply Messages - Teklif Mesajlarýný dinler ve cevap verir.
	public class ListenNReply extends TickerBehaviour {

		/**
	 * 
	 */
		private  class CRandomTaskCheck extends Handler{
		     

		      public CRandomTaskCheck() {
		        
		      }

		      public void handle(ACLMessage msg, String temp[]) {
		        RandomTaskCheck (msg, temp) ;
		     }
		 }
		private  class CProposal extends Handler{ public void handle(ACLMessage msg, String temp[]) {Proposal (msg, temp) ; } }
		private  class CBuyTaskFromAuctioneer extends Handler{ public void handle(ACLMessage msg, String temp[]) {BuyTaskFromAuctioneer (msg, temp) ; } }
		private  class CCompleteSellingProcess extends Handler{ public void handle(ACLMessage msg, String temp[]) {CompleteSellingProcess (msg, temp) ; } }
		private  class CTradeCompleted extends Handler{ public void handle(ACLMessage msg, String temp[]) {TradeCompleted (msg, temp) ; } }
		private  class CSimulationStarted extends Handler{ public void handle(ACLMessage msg, String temp[]) {SimulationStarted (msg, temp) ; } }
		private  class CEndOfTour extends Handler{ public void handle(ACLMessage msg, String temp[]) {EndOfTour (msg, temp) ; } }
		private  class CTaskCompletionAcknowledge extends Handler{ public void handle(ACLMessage msg, String temp[]) {TaskCompletionAcknowledge (msg, temp) ; } }
		private  class CEndOfTrade extends Handler{ public void handle(ACLMessage msg, String temp[]) {EndOfTrade (msg, temp) ; } }
		private  class CLogToFile extends Handler{ public void handle(ACLMessage msg, String temp[]) {LogToFile (msg, temp) ; } }
		private  class CTaskAccepted extends Handler{ public void handle(ACLMessage msg, String temp[]) {TaskAccepted (msg, temp) ; } }
		private  class CTaskRejected extends Handler{ public void handle(ACLMessage msg, String temp[]) {TaskRejected (msg, temp) ; } }
		private  class CIBecomeSELLER extends Handler{ public void handle(ACLMessage msg, String temp[]) {IBecomeSELLER (msg, temp) ; } }
		private  class CTradeIsOver extends Handler{ public void handle(ACLMessage msg, String temp[]) {TradeIsOver (msg, temp) ; } }
		private  class CExecutionStart extends Handler{ public void handle(ACLMessage msg, String temp[]) {ExecutionStart (msg, temp) ; } }
		private  class CMakeABid extends Handler{ public void handle(ACLMessage msg, String temp[]) {MakeABid (msg, temp) ; } }
		private  class CIncomingBid extends Handler{ public void handle(ACLMessage msg, String temp[]) {IncomingBid (msg, temp) ; } }
		private  class CBuyTaskFromRobot extends Handler{ public void handle(ACLMessage msg, String temp[]) {BuyTaskFromRobot (msg, temp) ; } }
		private  class CReset extends Handler{ public void handle(ACLMessage msg, String temp[]) {Reset (msg, temp) ; } }
		/*
		public class AHandler extends Handler 
		{
			public void handle() 
			{
				Yaz("A handler");
			}
		}
		public class BHandler extends Handler 
		{
			public void handle() 
			{
				Yaz("B handler");
			}
			
		}*/
		public void CreateMap1() throws Exception {
			
			 handlerMap.put("0000", new CRandomTaskCheck());
			 handlerMap.put("00", new CProposal());
			 handlerMap.put("04", new CBuyTaskFromAuctioneer());
			 handlerMap.put("04RAR", new CCompleteSellingProcess());
			 handlerMap.put("04RNR", new CTradeCompleted());
			 handlerMap.put("61", new CSimulationStarted());
			 handlerMap.put("09", new CEndOfTour());
			 handlerMap.put("066", new CTaskCompletionAcknowledge());
			 handlerMap.put("10", new CEndOfTrade());
			 handlerMap.put("91", new CLogToFile());
			 handlerMap.put("103", new CTaskAccepted());
			 handlerMap.put("104", new CTaskRejected());
			 handlerMap.put("112", new CIBecomeSELLER());
			 handlerMap.put("115", new CTradeIsOver());
			 handlerMap.put("116", new CExecutionStart());
			 handlerMap.put("121", new CMakeABid());
			 handlerMap.put("122", new CIncomingBid());
			 handlerMap.put("123", new CBuyTaskFromRobot());
			 handlerMap.put("191", new CReset());
	
		}
		
		
		public void MessageParse3 (ACLMessage msg, String temp[]) 
		{
			 
		   if (messageMapFlag == false)	
			try {
				CreateMap1 ();
				messageMapFlag = true; 
			}
			catch (Exception e) 
			{
			
			}
		//	Yaz (msg.toString()) ; 
		//	 Yaz(temp[0]+" "+ temp[1]+" "+temp[2]+" "+temp[3]+" "+temp[4]+" "+ temp[5]+" "+temp[6]+" "+temp[7]+" "+temp[8]+" "+temp[9]); 
			 
			
			
		/*	 Set set = handlerMap.entrySet(); 
			// Get an iterator 
			Iterator i = set.iterator(); 
			 while(i.hasNext()) { 
				 Map.Entry me = (Map.Entry)i.next(); 
				 System.out.print(me.getKey() + ": "); 
				 Yaz(me.getValue()); 
				 } */
			 
			 Object obj = handlerMap.get(temp[0]); 
			// Yaz (obj.toString()); 
			if (obj!=null) 
			{
			 Handler h = (Handler) obj; 
			// Yaz (h.toString()); 
			 h.handle(msg, temp);
			}
			 
		}
		public class Handler 
		{
			public void handle(ACLMessage msg, String temp[]) {
				Yaz ("Handler super...")	;			
			};
		}

		//private final long serialVersionUID = 4759398232765666946L;
		public void EvaluateProposal(String temp[]) {
			//getCallerMethod ();
			int tIndex = MyTaskBundleIndex(temp[2]);
			int rIndex = RobotIndex(temp[1]);
			if (tIndex > -1 && rIndex > -1) {
				proposalstoMyTasks[rIndex][tIndex] = Integer.parseInt(temp[3]);

				System.out.print(thisRobot.robotName + "> " + temp[1]
						+ " robotundan " + temp[2]
						+ " gorevine gelen teklifi not ettim : " + temp[3]);
				PrintWallWatch();
			}

			ShowProposals();
			// addBehaviour( new ProposalEvaluation());
		}

		public void TaskAccepted(ACLMessage msg, String temp[]) {
			AddUpdateTask(temp[2], temp[3], temp[4], temp[5], temp[6], true, eTradeStatus.BOUGHT, eTaskState.NOT_STARTED);
			//taskBundles[TaskBundleIndex(temp[2])].state = eTaskState.NOT_STARTED;
			//taskBundles[TaskBundleIndex(temp[2])].tradeStatus = eTradeStatus.BOUGHT; // bought
			taskQueue[myTaskBundles++] = taskBundles[TaskBundleIndex(temp[2])];

			int otIndex = OtherTaskBundleIndex(temp[2]);
			int rIndex = RobotIndex(temp[1]);
			proposalstoOtherTasks[rIndex][otIndex] = 0;

			/*
			 * for (int i = otIndex; i<others-1; i++ ) { otherTasks[i] =
			 * otherTasks[i+1]; } others--;
			 */
			Yaz(thisRobot.robotName + "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Yaz(thisRobot.robotName + "> " + temp[1] + " robotundan " + temp[2] + " gorevini satýn aldým ve havuza ekledim ..");
			Yaz(thisRobot.robotName + "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
			thisRobot.AddBoughtTask(temp[1], temp[2]);
			String rc[] = { terrain };
			String mc[] = { "73", thisRobot.robotName, temp[1], temp[2] };
			SendACLMessage(rc, 1, mc, 4);

			PrintWallWatch();
		}

		public void TaskRejected(ACLMessage msg, String temp[]) {
			int tIndex = OtherTaskBundleIndex(temp[2]);
			int rIndex = RobotIndex(temp[1]);
			if (rIndex > -1 && tIndex > -1) {
				proposalstoOtherTasks[rIndex][tIndex] = 0;
				// addBehaviour( new ProposalEvaluation());
			}
		}

		/*
		 * public void NavigateToChargeStation (String temp[]) { /// Task bilgisi
		 * ekleme ve guncelleme ChargeUnit cu; int cIndex = ChargerIndex(temp[1]);
		 * // Task bilgisi mevcutsa if (cIndex < 0) { cu = new ChargeUnit(); cIndex
		 * = chargerCount++;
		 * 
		 * } else { cu = chargers[cIndex]; }
		 * 
		 * cu.taskName = temp[1]; cu.xLoc = Integer.parseInt(temp[2]) ; cu.yLoc =
		 * Integer.parseInt(temp[3]) ; cu.state = Integer.parseInt(temp[4]) ;
		 * cu.taskType = Integer.parseInt(temp[5]) ; cu.mineTask = true;
		 * //t.proposal = proposalValue;
		 * 
		 * chargers[cIndex] = cu;
		 * 
		 * 
		 * System.out.print
		 * (thisRobot.robotName+"> "+temp[1]+" sarj unitesine yonlendirildim ..");
		 * 
		 * PrintWallWatch(); Executing = cu; thisRobot.status = 2; }
		 */

		

		// /////////////////////////////////

		public void SimulationStarted(ACLMessage msg, String temp[]) {
			//getCallerMethod ();
			EXECUTION_MODE = eExecutionMode.values() [Integer.parseInt(temp[1])];

			Yaz(thisRobot.robotName + "> Ýþletim modu   : " + EXECUTION_MODE);
			is_main = new IndoorStructure(templateSelect);
			simSpeed = Integer.parseInt(temp[2]);

			// simSpeed * robotStep * motionInterval = 8000 olmalý
			// pist uzunluðu 3750 mt.
			// ekran çözünürlüðü 1920 *1080
			// 1 pixel yaklaþýk 4mt ye karþýlýk geliyor.

			// revised 13.05.2014
			// saniyede 1.5 pixel hareket
			/*
			 * switch (simSpeed) { case 1: robotStep = (float) 3; motionInterval =
			 * 2000; break; case 10: robotStep = (float) 3; motionInterval = 200;
			 * break; case 40 : robotStep = (float) 12; motionInterval = 200; break;
			 * }
			 */
/*
			switch (simSpeed) {
			case 1:
				robotStep = (float) 3;
				motionInterval = (int) (667.0 * (20.0 / (double) (thisRobot.speed * 1.0))) + 1;
				break;
			case 10:
				robotStep = (float) 3;
				motionInterval = (int) (67.0 * (20.0 / (double) (thisRobot.speed * 1.0))) + 1;
				break;
			case 40:
				robotStep = (float) 3;
				motionInterval = (int) (23.0 * (20.0 / (double) (thisRobot.speed * 1.0))) + 1;
				break;
			}
*/ 
			robotStep = 10;
			motionInterval = 100; 
			// saniyede 8 pixel gidiyor
			// normalde olmasý gereken (1.38 pixel)
			/*
			 * switch (simSpeed) { case 1: robotStep = (float) 8.0; motionInterval =
			 * 1000; break; case 10: robotStep = (float) 8.0; motionInterval = 100;
			 * break; case 40 : robotStep = (float) 8.0; motionInterval = 25; break;
			 * }
			 */

			addBehaviour(new MoveRobot(thisa, motionInterval));
		}

		// ////////////////////////////
		public void IBecomeSELLER(ACLMessage msg, String temp[]) {
			Yaz(thisRobot.robotName + "> ------------------------------------------------------------------------------- " 	+ temp[1]);
			Yaz(thisRobot.robotName + "> ### 112 ### SATICI OLDUM -> tradeCount : " + temp[1]);
			Yaz(thisRobot.robotName + "> ------------------------------------------------------------------------------- " 	+ temp[1]);
			ResetProposalsToTradedTask();

			addBehaviour(new StartAnAuctionOnce_Sequential());
		}

     
		
		
		/* private  class CRandomTaskCheck extends ParseClass{
		     

		      public CRandomTaskCheck() {
		        
		      }

		      public void invoke(ACLMessage msg, String temp[]) {
		        RandomTaskCheck (temp[1],temp[2],temp[3],temp[6]) ;
		     }
		 }
		 
		 private  class CProposal {
		     

		      public CProposal() {
		        
		      }

		      public void invoke(ACLMessage msg, String temp[]) {
		        Proposal (msg, temp) ;
		     }
		 }
		*/
		 public void Proposal(ACLMessage msg, String temp[]) {

				AddUpdateTask(temp[1], temp[2], temp[3], temp[4], temp[5], false, eTradeStatus.OFFERED, eTaskState.NOT_STARTED);

			}
		 
		 // String taskName, String xl, String yl,String bHead
		 public void RandomTaskCheck(ACLMessage msg, String temp[]) {
				int dist = CalcTPDistance(new Task(Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), temp[1]));
				Yaz("*******************************************************************************");
				Yaz(thisRobot.robotName + " robotundan " + temp[1] + " görevine mesafe : " + dist);
				Yaz("*******************************************************************************");

				// Negative reply 0002 because higher distance
				if (dist < 0 || dist > 1000) {
					String rcd[] = { auctioneer };

					String mcd[] = { "0002", thisRobot.robotName, temp[1] };
					SendACLMessage(rcd, 1, mcd, 3);

				} else {

					String rcd[] = { auctioneer };
					String mcd[] = { "0001", thisRobot.robotName, temp[1] };
					SendACLMessage(rcd, 1, mcd, 3);

					boolean newBundle = false;
					int myTBI = MyTaskBundleIndex_B(temp[6]);
					// / Task bilgisi ekleme ve guncelleme

					if (myTBI < 0) {
						newBundle = true;
					}

					AddUpdateTask(temp[1], temp[2], temp[3], temp[6], "0", true, eTradeStatus.OWNED, eTaskState.NOT_STARTED);

					Yaz (thisRobot.robotName + "> *** 0000 ");
					Yaz (thisRobot.robotName + "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					Yaz (thisRobot.robotName + "> MUZAYEDECI'den alýnan " + temp[1] + " gorevini havuza ekledim .." + temp[2] + "_" + temp[3] + "_" + temp[6]);
					Yaz (thisRobot.robotName + "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

					PrintWallWatch();
					Yaz (thisRobot.robotName + "> TaskBundleIndex " + Integer.toString(TaskBundleIndex(temp[1])) + " task Count :  " + Integer.toString(taskBundles[TaskBundleIndex(temp[1])].taskCount));

					if (newBundle) {
						taskQueue[myTaskBundles++] = taskBundles[TaskBundleIndex_B(temp[6])];
					} else {
						taskQueue[myTBI] = taskBundles[TaskBundleIndex_B(temp[6])];
					}

					Yaz (thisRobot.robotName + "> MyTaskBundles : " + Integer.toString(myTaskBundles));
					Yaz(thisRobot.robotName + "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

					// taskQueue[myTaskBundles++] =
					// taskBundles[TaskBundleIndex_B(temp[4])];
					Yaz(thisRobot.robotName + "> MyTaskBundleIndex "	+ Integer.toString(myTaskBundles - 1) + " My task Count :  " + Integer.toString(taskQueue[myTaskBundles - 1].taskCount));
					// ListMyTasks();
				}
			}

		 public void showHelp() {
		        Yaz("Help");
		    } 
		 
		
		/* 
		 public void MessageParse_reflection(ACLMessage msg, String temp[]) throws Exception 

		 {
			/* @SuppressWarnings("rawtypes")
			Class[] classes = new Class[temp.length +1]; 
			 classes[0] = msg.getClass(); 
			 for (int i = 0; i < temp.length; ++i) {
			            classes[i+1] = temp[i].getClass();
			 }*
			 Method m = methodMap.get(temp[0]); 
			 Yaz (m.toString()); 
			 Yaz (msg.toString()) ; 
			 Yaz(temp[0]+" "+ temp[1]+" "+temp[2]+" "+temp[3]+" "+temp[4]+" "+ temp[5]+" "+temp[6]+" "+temp[7]+" "+temp[8]+" "+temp[9]); 
			 m.invoke(null, msg, temp[0],temp[1], temp[2], temp[3],temp[4],temp[5],temp[6],temp[7],temp[8],temp[9] );
			 
		 }*/
		 
		 public void TradeCompleted (ACLMessage msg, String temp[]) 
		 {
			 tradeCompleted = true;
		 }
		 public void ExecutionStart (ACLMessage msg, String temp[]) 
		 {
			 Yaz(thisRobot.robotName + " 116 GELDÝ, eXECUTÝON BAÞLIYOR");
				addBehaviour(new ExecutionDelay(thisa, 100));
		 }
		 public void Reset(ACLMessage msg, String temp[])
		 {
			 ClearDataHolders();
				doDelete();
		 }



		 /*
		public void MessageParse(ACLMessage msg, String temp[]) throws Exception {

			
			if (temp[0].compareTo("0000") == 0)
			{
				RandomTaskCheck(msg, temp);
				//new CRandomTaskCheck().invoke(temp[1], temp[2], temp[3], temp[4]); 
			}
			// Teklif alýnýyor ve cevaplanýyor
			 else if (temp[0].compareTo("00") == 0) {

				Proposal(msg, temp);
				EXECUTION_PHASE = 1;
     		}
			/*
			 * Görevlendirme gelirse temp0 kod 04 temp1 taskName temp2 xloc
			 * temp3 yloc temp4
			 

			else if (temp[0].compareTo("04") == 0) {
				BuyTaskFromAuctioneer(msg, temp);
			} // end of 04
			else if (temp[0].compareTo("04RAR") == 0) {
				CompleteSellingProcess(msg, temp);
			} // end of 04
			else if (temp[0].compareTo("04RNR") == 0) {
				tradeCompleted = true;
			} // end of 04
			// 066 : Tamamlanan Görev Bildirimi
			else if (temp[0].compareTo("066") == 0) {
				TaskCompletionAcknowledge(msg, temp);
			} // end of 08
			else if (temp[0].compareTo("09") == 0) {
				EndOfTour(msg, temp);
			} // 09
			// Task bildirimi tamamlandý, Teklif bekleniyor.
			else if (temp[0].compareTo("10") == 0) {
				
				EndOfTrade(msg,temp);
			}
			// Start simulation
			else if (temp[0].compareTo("61") == 0) {

				SimulationStarted(msg, temp);
			}
			else if (temp[0].compareTo("91") == 0) {

				LogToFile(msg, temp);
			}
				// Satýn alma 
			else if (temp[0].compareTo("103") == 0) {
			  TaskAccepted(msg, temp); 
			  }
			// Task rejected
			else if (temp[0].compareTo("104") == 0) {
				TaskRejected(msg, temp);
			} else if (temp[0].compareTo("112") == 0) {
				IBecomeSELLER(msg, temp);

			}
			else if (temp[0].compareTo("115") == 0) {
				TradeIsOver(msg, temp);

			}
			else if (temp[0].compareTo("116") == 0) {
				ExecutionStart(msg, temp); 
			}
			// Sequential
			else if (temp[0].compareTo("121") == 0) {

				MakeABid(msg, temp);

			}
			else if (temp[0].compareTo("122") == 0) {
				IncomingBid(msg, temp);
			}
			else if (temp[0].compareTo("123") == 0) {
				BuyTaskFromRobot(msg, temp);
			}
			else if (temp[0].compareTo("191") == 0) {
				Reset(msg, temp); 
			}
		}
	*/

		public ListenNReply(Agent a, long interval) {
			super(a, interval);
			
		}
		
		public void MakeABid(ACLMessage msg, String temp[]) {
			//getCallerMethod();
			Yaz (thisRobot.robotName + "> MAKEABID " + temp[1] + " robotundan gelen " + temp[6] + " deðerindeki " + temp[2] + " gorevine teklif vereceðim ");
			// thisRobot.status = 1;
			if (MyTaskBundleIndex(temp[2]) < 0 ) {    // Benim görevim mi? 
				int prop = 0;
				
				if (IsInHeatBoundary(temp[3], temp[4]) && WaitingTasks() < thisRobot.capacityExtra)  // Task sorumluluk alaný içerisinde mi ve yeni görev için kapasite var mý? 
				{
					prop = Valuation(temp, TRADE_APPROACH); 
				}
				
				AddUpdateTask(temp[2], temp[3], temp[4], temp[5], temp[6], false,eTradeStatus.OWNERLESS,eTaskState.NOT_STARTED);
				AddUpdateRobot(temp[1]);
				
				if (prop <= 0 || prop > UPPERLIMITFORBID) {     
					prop = 99999;
				}
				
				int tIndex = TaskBundleIndex(temp[2]);
				int rIndex = RobotIndex(temp[1]);

				proposalstoOtherTasks[rIndex][others] = prop;
				taskBundles[tIndex].owner = rIndex;
				taskBundles[tIndex].state = eTaskState.NOT_STARTED;
				taskBundles[tIndex].tradeStatus = eTradeStatus.OWNED;
				otherTasks[others++] = taskBundles[tIndex];

				String rcd[] = { terrain };
				String mcd[] = { "114", temp[6], Integer.toString(prop), temp[1],  thisRobot.robotName, temp[2], "x" };
				SendACLMessage(rcd, 1, mcd, 7);  // terrain'e bilgilendirme 

				ACLMessage rrmsg = msg.createReply();
				rrmsg.setPerformative(ACLMessage.INFORM);
				rrmsg.setContent("122_" + thisRobot.robotName + "_" + temp[2] + "_" + Integer.toString(prop));
				send(rrmsg); 
				
				Yaz(thisRobot.robotName + "> " + temp[1] + " robotundan gelen " + temp[6] + " deðerindeki " + temp[2] + " gorevine teklif verdim : " + TRADE_APPROACH + " : " + Integer.toString(prop));
				PrintWallWatch();
			}
		}

		public void LogToFile(ACLMessage msg, String temp[]) {
			try {
				out = new FileOutputStream("_robotlar.txt", true);
				p = new PrintStream(out);

				// for (int k=0; k<messCount; k++)
				// p.println(messageBuff[k]);
				p.print(thisRobot.robotName);
				p.print("   ");
				p.print(thisRobot.cumulativeWay);
				p.print("   ");
				p.print(thisRobot.energyLevel);
				p.print("   ");
				p.print(thisRobot.completedTasks);
				p.print("   ");
				p.println(thisRobot.chargeCount);

				p.close();
				System.out.print(thisRobot.robotName
						+ "> Mesajlar dosyaya kaydedildi... ");
				PrintWallWatch();

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
			}
		}
		public void BuyTaskFromAuctioneer(ACLMessage msg, String temp[]) {

			ACLMessage rmsg = msg.createReply();
			rmsg.setPerformative(ACLMessage.INFORM);
			String replyContent;
			if (EXECUTION_MODE == eExecutionMode.AGILE) {
				TaskBundle tb;
				Task t;
				int tbIndex = TaskBundleIndex(temp[1]);
				int tIndex = TaskIndex(temp[1]);
				// Task bilgisi mevcutsa
				if (tbIndex < 0) {
					tb = new TaskBundle();
					t = new Task();
					tb.AddTask(t);
					tIndex = 0;
					tbIndex = taskBundleCount++;

				} else {
					tb = taskBundles[tbIndex];
					t = tb.GetTask(tIndex);
				}

				t.taskName = temp[1];
				t.xLoc = Integer.parseInt(temp[2]);
				t.yLoc = Integer.parseInt(temp[3]);
				t.state = eTaskState.PROCESSING;
				t.tradeStatus = eTradeStatus.OWNED;
				t.mineTask = true;

				// t.proposal = proposalValue;

				// tasks[tIndex] = t;
				taskBundles[tbIndex].SetTask(tIndex, t);

				System.out.print(thisRobot.robotName + "> " + temp[1] + " icin gorevlendirildim ..");

				PrintWallWatch();
				ExecutingNow = tb;
				Executing = tb.GetTask(0);
				thisRobot.status = eRobotStatus.ASSIGNED;
			} 
			else {
				Yaz(thisRobot.robotName + "> *** 04 ");
				Yaz(thisRobot.robotName + "> waiting :  " + WaitingTasks() + " status : " + thisRobot.status);
				Yaz(thisRobot.robotName + "> capacity Normal : " + thisRobot.capacityNormal + " extra : " + thisRobot.capacityExtra);
				int capacity = EXECUTION_MODE == eExecutionMode.CENTRALIZED ? thisRobot.capacityExtra : thisRobot.capacityNormal;

				if (WaitingTasks() < capacity && IsInHeatBoundary(temp[2], temp[3])) {
					boolean newBundle = false;
					int myTBI = MyTaskBundleIndex_B(temp[4]);
					// / Task bilgisi ekleme ve guncelleme

					if (myTBI < 0) {
						newBundle = true;
					}

					AddUpdateTask(temp[1], temp[2], temp[3], temp[4], temp[5], true, eTradeStatus.OWNED, eTaskState.NOT_STARTED);

					// System.out.println (thisRobot.robotName+"> *** 04 " );
					Yaz(thisRobot.robotName + "> >>>>>>>>>>>>>>>>>>>>>>>>>> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					Yaz(thisRobot.robotName + "> MUZAYEDECI'den alýnan " + temp[1] + " gorevini havuza ekledim .." + temp[2] + "_" 	+ temp[3] + "_" + temp[4] + "_" + temp[5]);
					Yaz(thisRobot.robotName + "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

					PrintWallWatch();
					Yaz (thisRobot.robotName + "> TaskBundleIndex " +Integer.toString(TaskBundleIndex(temp[1])) + " task Count :  " + Integer.toString(taskBundles[TaskBundleIndex(temp[1])].taskCount));

					if (newBundle) {
						taskQueue[myTaskBundles++] = taskBundles[TaskBundleIndex_B(temp[4])];
					} else {
						taskQueue[myTBI] = taskBundles[TaskBundleIndex_B(temp[4])];
					}

					Yaz(thisRobot.robotName + "> MyTaskBundles : " + Integer.toString(myTaskBundles));
					Yaz(thisRobot.robotName	+ "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

					// taskQueue[myTaskBundles++] =
					// taskBundles[TaskBundleIndex_B(temp[4])];
					Yaz(thisRobot.robotName + "> MyTaskBundleIndex "+ Integer.toString(myTaskBundles - 1)+ " My task Count :  "+ Integer.toString(taskQueue[myTaskBundles - 1].taskCount));
				
					replyContent = "04AR_" + thisRobot.robotName + "_" + temp[1]+ "_" + Integer.toString(WaitingTasks());
					ResetOffers();

				} else {
					replyContent = "04NR_" + thisRobot.robotName + "_" + temp[1];

				}

				thisRobot.status = eRobotStatus.ASSIGNED;
				rmsg.setContent(replyContent);
				send(rmsg);
			} 
				

			

		}

		public void IncomingBid(ACLMessage msg, String temp[]) {
			Yaz(thisRobot.robotName + "> *122* geldi -> " + temp[1] 	+ " robotundan " + temp[2] 	+ " gorevine gelen teklifi not ettim : " + temp[3]);
			tradedTask = MyTaskBundleIndex(temp[2]);
			int rIndex = RobotIndex(temp[1]);
			Yaz(thisRobot.robotName + "> " + temp[1] + " robotu " + temp[2] + " gorevi > Teklif   : " + temp[3]);
			// ListMyTasks();
			if (tradedTask > -1 && rIndex > -1) {
				proposalstoTradedTask[rIndex] = Integer.parseInt(temp[3]);

				// System.out.println
				// (thisRobot.robotName+"> "+temp[1]+" robotundan "+temp[2]+" gorevine gelen teklifi not ettim : "+temp[3]);
				// PrintWallWatch();
				ShowProposals_Sequential();
			}

			if (tradedTask > -1) {
				addBehaviour(new ProposalEvaluation_ForSequential());
			}

		}

		public void BuyTaskFromRobot(ACLMessage msg, String temp[]) {
			//if (WaitingTasks() < thisRobot.capacityExtra) {

				boolean newBundle = false;
				int myTBI = MyTaskBundleIndex_B(temp[5]);
				// / Task bilgisi ekleme ve guncelleme

				if (myTBI < 0) {
					newBundle = true;
				}

				AddUpdateTask(temp[2], temp[3], temp[4], temp[5], temp[6], true, eTradeStatus.BOUGHT, eTaskState.NOT_STARTED);

				Yaz(thisRobot.robotName + "> *** 123 ");
				System.out
						.println(thisRobot.robotName
								+ "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				Yaz(thisRobot.robotName + "> " + temp[1]
						+ " 'den alýnan " + temp[2] + " gorevini havuza ekledim .."
						+ temp[3] + "_" + temp[4] + "_" + temp[5] + "_" + temp[6]);
				System.out
						.println(thisRobot.robotName
								+ "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

				PrintWallWatch();
				System.out
						.println(thisRobot.robotName
								+ "> TaskBundleIndex "
								+ Integer.toString(TaskBundleIndex(temp[2]))
								+ " task Count :  "
								+ Integer
										.toString(taskBundles[TaskBundleIndex(temp[2])].taskCount));

				if (newBundle) {
					taskQueue[myTaskBundles++] = taskBundles[TaskBundleIndex_B(temp[5])];
				} else {
					taskQueue[myTBI] = taskBundles[TaskBundleIndex_B(temp[5])];
				}
				taskBundles[TaskBundleIndex(temp[2])].state = eTaskState.NOT_STARTED;
				taskBundles[TaskBundleIndex(temp[2])].tradeStatus = eTradeStatus.BOUGHT; // bought

				Yaz(thisRobot.robotName + "> MyTaskBundles : "
						+ Integer.toString(myTaskBundles));
				System.out
						.println(thisRobot.robotName
								+ "> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

				Yaz(thisRobot.robotName + "> MyTaskBundleIndex "
						+ Integer.toString(myTaskBundles - 1)
						+ " My task Count :  "
						+ Integer.toString(taskQueue[myTaskBundles - 1].taskCount));
				thisRobot.AddBoughtTask(temp[1], temp[2]);
				/*
				 * String rc[] = {terrain}; String mc [] = {"73",
				 * thisRobot.robotName, temp[1], temp[2] }; SendACLMessage(rc, 1,
				 * mc, 4);
				 */

				String rcd[] = { terrain };

				String mcd[] = { "114", temp[6], temp[6], temp[1],
						thisRobot.robotName, temp[2], temp[2] };

				SendACLMessage(rcd, 1, mcd, 7);

				String ac[] = { auctioneer };
				String mac[] = { "04AR", thisRobot.robotName, temp[2] };
				SendACLMessage(ac, 1, mac, 3);

				String ac1[] = { temp[1] };
				String mac1[] = { "04RAR", thisRobot.robotName, temp[2] };
				SendACLMessage(ac1, 1, mac1, 3);
				// ListMyTasks();
				ResetOffers();
				// taskListChanged = true;
			/*} /*else {
				String ac[] = { temp[1] };
				String mac[] = { "04RNR", thisRobot.robotName, temp[2] };
				SendACLMessage(ac, 1, mac, 3);
			}*/
	

		
		}

		public void EndOfTrade(ACLMessage msg, String temp[]) {
			tradingPhase = true;
			Yaz(thisRobot.robotName	+ ">*******************  10 10 10 ***************************************"	+ EXECUTION_MODE);
			// ResetTaskList();
			ResetProposalsToOtherTasks();
			if (Integer.parseInt(temp[1]) == 1) // Önceki turda satýþ baþarýlý olmuþ  
			{
				ResetOffers();
				Yaz(thisRobot.robotName + "> Previous trade has been successful, so all offers are cleared and I_CAN_TRADE");
				I_CAN_TRADE = true; 
			}
			
			// ListMyTasks();
			EXECUTION_PHASE = eExecutionPhase.AUCTIONS_COMPLETED;
			Yaz(thisRobot.robotName + "> Prospective Gain will be calculated - EXEC. MODE : " + EXECUTION_MODE + " EXEC. PHASE : " + EXECUTION_PHASE);
			SendProspectiveGain(TRADE_APPROACH);
		}

		public int Valuation (String temp[], eBidValuation approach)
		{
			//getCallerMethod();	
			Task t = new Task (Integer.parseInt(temp[3]), Integer.parseInt(temp[4]), temp[2]); 	
				return Valuation (t, approach, temp[6]) ; 
				
		}
		
		public int Valuation (Task t, eBidValuation approach, String price) 
		{
		    //getCallerMethod();	
			int proposalValue = -1; 
			//if (approach == eBidValuation.EUCLIDIAN)
			//	proposalValue = CalcTPDistance(t) < 3000 ? PrepareAProposalS(t.taskName) 	: 0;
				
			if (approach == eBidValuation.FUZZY) 
			{
				int tpDist = (int) CalcTPDistance(t);
				int closest =  MyClosestTaskDistanceTPM(t);
				proposalValue =  tpDist + closest;
			}
			else if (approach == eBidValuation.DSTARLITE) 
			{
				proposalValue = PrepareAProposalDSTARLITE(t.taskName, Integer.toString(t.xLoc),Integer.toString(t.yLoc), price);
			}
			else if (approach == eBidValuation.DSTARLITEFUZZY) 
			{
				proposalValue = PrepareAProposalDSTARLITEFuzzy(t.taskName, Integer.toString(t.xLoc),Integer.toString(t.yLoc),price);
			}
			else if (approach == eBidValuation.EUCLIDIAN) 
			{
				//proposalValue = PrepareAProposalRRT(t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), price); 	
				proposalValue = PrepareAProposalTSP(t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), price, approach);
			}
			else if (approach == eBidValuation.DSTARLITETSP || approach == eBidValuation.TMPRRTTSP || approach == eBidValuation.TMPTSP ) 
			{
				//proposalValue = PrepareAProposalTSP(t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), price, approach);
				proposalValue = PrepareAProposalTSP(t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), price, approach);
			}
			
		    
			/*
			switch (approach) {
			// Görevler ilk defa atanacaðý için 100000 ile
			// kýyaslanýyor.
			case 1:
				// 1 : Proposal_Euclidian : DÝREK UZAKLIK
				// 1000 den uzak ise teklif verme
				proposalValue = CalcTPDistance(t) < 3000 ? PrepareAProposalS(t.taskName) 	: 0;
				break;
			case 2:
				int tpDist = (int) CalcTPDistance(t);
				int closest =  MyClosestTaskDistanceTPM(t);
				proposalValue =  tpDist + closest;
				//proposalValue =  PrepareAProposalFuzzy(t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), price);
				
				break;
			case 3:
				proposalValue = PrepareAProposalDSTARLITE(t.taskName, Integer.toString(t.xLoc),Integer.toString(t.yLoc), price);
				
				break;
			case 4:
				proposalValue = PrepareAProposalDSTARLITEFuzzy(t.taskName, Integer.toString(t.xLoc),Integer.toString(t.yLoc),price);
				
				break;
			case 5:
				proposalValue = PrepareAProposalRRT(t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), price); 
				
				break;
			case 6:
			case 7:
			case 8:
				proposalValue = PrepareAProposalTSP(t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), price, approach);
			
			

				break;
		
			} // switch
			*/
			
			Yaz(thisRobot.robotName + ">  "
					+ t.taskName
					+ " GOREVINE TEKLIF VERIYORUM "+approach +" BEDELI  : "
					+ Integer.toString(proposalValue));
			
			return proposalValue; 
			
		}
		
		public boolean ValidProposal (int proposal) 
		{
			boolean retVal = true; 
			if (proposal< 0 || proposal > 5000 ) 
				retVal = false; 
			return retVal; 
		}
		public void SendDecision (int proposalValue, Task t) 
	
		{
			String decision = "affirmative";
			
			
			if (ValidProposal(proposalValue)) {
				Yaz (thisRobot.robotName + ">  Teklif verilen Gorev : " + t.taskName + " teklif : " + proposalValue);
				decision = "affirmative";
			} else {
				proposalValue = -1;
				decision = "refused";
				thisRobot.status = eRobotStatus.IDLE;
				Yaz (thisRobot.robotName + ">  Goreve teklif verilmedi : " + t.taskName);

			}
			
			String rc[] = { auctioneer };
			String mc[] = { "02", thisRobot.robotName, t.taskName, decision, Integer.toString(proposalValue) };
			SendACLMessage(rc, 1, mc, 5);
			addBehaviour(new InformLocationOnce());
			
		}
		public void EndOfTour(ACLMessage msg, String temp[]) {

			Yaz(thisRobot.robotName + "> *** 09   ***   status " + thisRobot.status );
			
			virtualTaskCount = 0;
			if (thisRobot.status == eRobotStatus.IDLE) {
				for (int i = 0; i < taskBundleCount; i++) {
					TaskBundle tb = taskBundles[i];
					Yaz(thisRobot.robotName + "> " + tb.GetTask(0).taskName + " GOREVINI TEKLIF ICIN KONTROL EDIYORUM .. state : " 	+ tb.state + " trade status : " + tb.tradeStatus);
					Task t = tb.GetTask(0);
					if (tb.state == eTaskState.NOT_STARTED) {
						Yaz(thisRobot.robotName + "> " + tb.GetTask(0).taskName + " GOREVINE TEKLÝF HAZIRLIYORUM .. state : " + tb.state + " trade status : " + tb.tradeStatus);
						

						int proposalValue = -1;
						if (IsInHeatBoundary(t.xLoc, t.yLoc)) {

   							// ALL_TERRAIN ise fiks teklif 
							proposalValue = (expMode == eServiceArea.ALL_TERRAIN) ? 5 : Valuation(t, TRADE_APPROACH, "100000"); 
							
						} // IsIn

						Yaz (thisRobot.robotName + ">  EndOfTour : " 	+ t.taskName + " teklif : " + proposalValue);
                        SendDecision(proposalValue, t); 

						taskBundles[i].proposal = proposalValue;

					}

				} // for

			} // if battery
		}

		

		protected void onTick() {

			ACLMessage msg = receive();
			if (msg != null) {
				String content = msg.getContent();
				String delimiter = "_";
				String[] temp;
				String[] temp1 = new String[10];
				temp = content.split(delimiter);
				for (int i=0; i<temp.length; i++) 
					temp1 [i] = temp[i]; 
				for (int i=temp.length; i<10; i++) 
					temp1 [i] = " * "; 
				
                Yaz ("Gelen Mesaj : "+ temp[0]);
				try {
					MessageParse3(msg, temp1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // if msg null

			else {
				block();
			}

		} // action
	} // listen n reply

	public void ClearDataHolders() {
		for (int i = 0; i < MAX_TASKS; i++) {
			taskBundles[i] = null;
			taskQueue[i] = null;
			temptaskQueue[i] = null;
			sortedtaskQueue[i] = null;

		}
		for (int i = 0; i < 10000; i++) {
			myWay[i][0] = 0;
			myWay[i][1] = 0;

		}
		for (int i = 0; i < MAX_TASKS * 100; i++) {
			executionOrder[i] = null;
		}
		for (int i = 0; i < MAX_TASKS * 10; i++) {
			executionOrder_final[i] = null;
		}

		for (int i = 0; i < MAX_ROBOTS; i++) {
			robots[i] = null;

		}

		is_main.ClearIndoorStructure();
		is_main = null;

	}

	public void PrintWallWatch() {

		mrt.PrintWallWatch();

	}

	

	public int PrepareAProposal_Euclidian(String taskName, String xl,
			String yl, String pr) {

		int returnValue = 0;

		int prop = (int) CalcTPDistance(new Task(Integer.parseInt(xl),
				Integer.parseInt(yl), "task"));
		if (prop * 1.03 < Integer.parseInt(pr))
			returnValue = prop;

		return returnValue;
	}

	public int PrepareAProposalDSTARLITE(String taskName, String xl, String yl,
			String pr) {

		//getCallerMethod();
		Task t = new Task(Integer.parseInt(xl), Integer.parseInt(yl), "tmpTask");
        System.out.println (thisRobot.robotName+"> PrepareAProposalDSTARLITE "+taskName + " x:"+xl+" y:"+yl + " price: "+pr);  
		// Station st = ClosestStation(t);
		int prop = (int) dsl.DStarLiteDistance(thisRobot.xLoc, thisRobot.yLoc,
				t.xLoc, t.yLoc, thisRobot.robotName, t.taskName); // + MyClosestTaskDistanceDStar(t);//+ (int)
									// dsl.DStarLiteDistance(st.xLoc, st.yLoc,
									// t.xLoc, t.yLoc);
		System.out.println (thisRobot.robotName+"> PrepareAProposalDSTARLITE "+taskName + " x:"+xl+" y:"+yl + " price: "+pr + " prop direct:"+prop);
		prop = prop + MyClosestTaskDistanceDStar(t);
		System.out.println (thisRobot.robotName+"> PrepareAProposalDSTARLITE "+taskName + " x:"+xl+" y:"+yl + " price: "+pr + " prop direct+closest:"+prop);
		if (prop * 1.03 < Integer.parseInt(pr))
			return (prop);
		// }
		// ver2

		return -1;
	}

	public int PrepareAProposalRRT(String taskName, String xl, String yl,
			String pr) {

		Task t = new Task(Integer.parseInt(xl), Integer.parseInt(yl), "tmpTask");

		/*
		 * System.out.println ( thisRobot.robotName+">  Task : " + t.taskName +
		 * " x : " + Integer.toString(t.xLoc)+" y : "+Integer.toString(t.yLoc));
		 * System.out.println ( thisRobot.robotName+">  RRT CUR " +
		 * Integer.toString((int) TSPLengthCurrent())); System.out.println (
		 * thisRobot.robotName+">  TSPINCT2 " + Integer.toString((int)
		 * TSPLengthIncludingTaskBundle(tb)));
		 */

		TransitionPoint tm = new TransitionPoint(t);

		TransitionPoint t1 = is_main.Closest(tm);
		TransitionPoint t2 = is_main.Closest(new TransitionPoint(thisRobot.xLoc, thisRobot.yLoc, thisRobot.robotName));
		if (t1!=null && t2!= null && is_main.ShortestPath(t1, t2) != null) {

			IndoorStructure is_temp = new IndoorStructure(templateSelect);

			RRT rrt = new RRT(RRTdelta, RRTmaxIter, is_temp);
			int prop = rrt.CalcRRT(thisRobot, t);
			if (prop * 1.03 < Integer.parseInt(pr))
				return (prop);
		}
		// ver2

		return 0;
	}

	// Yeni bir görev eklenirse yolum ne kadar uzar?
	public int PrepareAProposalTSP(String taskName, String xl, String yl,
			String pr, eBidValuation approach) {

		TaskBundle tb = new TaskBundle();

		Task t = new Task(Integer.parseInt(xl), Integer.parseInt(yl), taskName);
		tb.AddTask(t);
//if (mytempTaskBundles>2) 
//{
		int tspIncluding = (int) TSPLengthIncludingTaskBundle(tb, approach);
		int tspCurrent = (int) TSPLengthCurrent(approach);
		Yaz(thisRobot.robotName + ">  Teklif verilecek Gorev : "
				+ t.taskName + " x : " + Integer.toString(t.xLoc) + " y : "
				+ Integer.toString(t.yLoc) + " price : " + pr);
		Yaz(thisRobot.robotName + ">  TSPCURT "
				+ Integer.toString(tspCurrent));
		Yaz(thisRobot.robotName + ">  TSPINCT "
				+ Integer.toString(tspIncluding));

		//int prop = Math.abs(tspIncluding - tspCurrent) + 10;
		int prop = Math.abs(tspIncluding - tspCurrent) + (int) (tspCurrent * 0.2) + 10 ;
		// if (prop *1.1 < Integer.parseInt(pr))
		if (prop * 1.03 < Integer.parseInt(pr)) {

			return (prop);
		}
		
/*}
else 
	return (int) CalcDistance(t)+5;
		// ver2
*/
		return 0;
	}
	
	/*public int PrepareAProposalTSP2(String taskName, String xl, String yl,
			String pr, eBidValuation approach) {

		TaskBundle tb = new TaskBundle();

		Task t = new Task(Integer.parseInt(xl), Integer.parseInt(yl), taskName);
		tb.AddTask(t);
		
		if (mytempTaskBundles<2) 
		{
			return CalcDistance (approach, t, thisRobot);
		}
		else 
		{
		int tspIncluding = (int) TSPLengthIncludingTaskBundle(tb, approach);
		int tspCurrent = (int) TSPLengthCurrent(approach);
		Yaz(thisRobot.robotName + ">  Teklif verilecek Gorev : "
				+ t.taskName + " x : " + Integer.toString(t.xLoc) + " y : "
				+ Integer.toString(t.yLoc) + " price : " + pr);
		Yaz(thisRobot.robotName + ">  TSPCURT "
				+ Integer.toString(tspCurrent));
		Yaz(thisRobot.robotName + ">  TSPINCT "
				+ Integer.toString(tspIncluding));

		int prop = Math.abs(tspIncluding - tspCurrent) + (int) (tspCurrent * 0.2) + 10 ; //+ mytempTaskBundles*10;
		// if (prop *1.1 < Integer.parseInt(pr))
		if (prop * 1.03 < Integer.parseInt(pr)) {

			return (prop);
		}
		// ver2
		}
		return 0;
	}*/

public int PrepareAProposalDSTARLITEFuzzy(String taskName, String xl,
			String yl, String pr) {

		int taskCount = 0;
		int _price = Integer.parseInt(pr);
		Yaz(thisRobot.robotName + ">  EXECUTION_PHASE "
				+ EXECUTION_PHASE);
		if (EXECUTION_PHASE == eExecutionPhase.NOT_STARTED || 	EXECUTION_PHASE == eExecutionPhase.CENTRALIZED_ALLOCATIONS) {
			taskCount = virtualTaskCount;

		} else {
			taskCount = WaitingTasks();
		}

		TaskBundle tb = new TaskBundle();

		Task t = new Task(Integer.parseInt(xl), Integer.parseInt(yl), taskName);
		tb.AddTask(t);

		// Station st = ClosestStation(t);
		int dist1 = (int) dsl.DStarLiteDistance(thisRobot.xLoc, thisRobot.yLoc,	t.xLoc, t.yLoc,thisRobot.robotName, t.taskName);
		// int dist2 = (int) dsl.DStarLiteDistance(st.xLoc, st.yLoc, t.xLoc,
		// t.yLoc);
		int dist3 = MyClosestTaskDistanceDStar(tb);
		int prop = dist1 + dist3;

		double  biddecision = FuzzyDecision(_price, prop, taskCount);
		Yaz(thisRobot.robotName + ">  TASKNAME : " + taskName
				+ "  FUZZY DECISION " + biddecision);

		return (int) biddecision;

	}
	
	public int PrepareAPriceFuzzy(String taskName, String xl, String yl) {

		int taskCount = 0;
		
		Yaz(thisRobot.robotName + "> PrepareAPriceFuzzy() EXECUTION_PHASE "	+ EXECUTION_PHASE);
		Yaz(thisRobot.robotName + ">  Fuzzy pricing for :  "	+ taskName + " xl : " + xl + " yl : "+yl );
		if (EXECUTION_PHASE == eExecutionPhase.NOT_STARTED || EXECUTION_PHASE == eExecutionPhase.CENTRALIZED_ALLOCATIONS) {
			taskCount = virtualTaskCount;

		} else {
			taskCount = WaitingTasks();
		}

		TaskBundle tb = new TaskBundle();

		Task t = new Task(Integer.parseInt(xl), Integer.parseInt(yl), taskName);
		tb.AddTask(t);

	
		int tpDist = (int) CalcTPDistance(tb.GetTask(0));
		int closest =  MyClosestTaskDistanceTPM(tb);
		int prop =  tpDist + closest;
		Yaz (thisRobot.robotName+" > FUZZY PRICING : "+ tb.GetTask(0).taskName +  "  Prop :  "+ prop + " tpDist "+tpDist + " closest :" + closest);

		int biddecision = FuzzyPrice(prop, taskCount); 
		Yaz(thisRobot.robotName + ">  TASKNAME : " + taskName + "  FUZZY DECISION " + biddecision);

		return biddecision;
	}
	
public int FuzzyPrice( int prop, int taskCount) {
		
	

		int biddecision = 0;

		try {
			biddecision = (int) sf_main.PricingFuzzy(taskCount, 0, prop, prop*10);
			Yaz(thisRobot.robotName + ">  " +  " TASKCOUNT : " + taskCount + " INCLUSION  " + prop + " fuzzyPRICE : " + biddecision);
			//
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Yaz(thisRobot.robotName + ">  BIDDECISION " 	+ Integer.toString(biddecision));

		int returnVal = 0;
		int kar = 50; 

		// returnVal = (300-biddecision)/300 * _price + _price ;

		if (biddecision >= 0 & biddecision <= 100) {
			returnVal = prop + prop / 2 + kar;
		} else if (biddecision > 100 & biddecision <= 200) {
			returnVal = prop + prop / 5 + kar;
		} else if (biddecision > 200 & biddecision <= 300) {
			returnVal = prop + prop / 7 + kar;
		} else if (biddecision > 300 & biddecision <= 400) {
			returnVal = prop + prop / 10 + kar;
		} else if (biddecision > 400 & biddecision <= 500) {
			returnVal = prop + kar;
		} else {
			returnVal = 0;
		}

		if (returnVal > 0)
			virtualTaskCount--;

		return returnVal;

	}


	public int PrepareAProposalFuzzy(String taskName, String xl, String yl,String pr) {

		int taskCount = 0;
		int _price = Integer.parseInt(pr);
		Yaz(thisRobot.robotName + ">  EXECUTION_PHASE "	+ EXECUTION_PHASE);
		Yaz(thisRobot.robotName + ">  Fuzzy proposal :  "	+ taskName + " xl : " + xl + " yl : "+yl  + " pr : " + pr);
		if (EXECUTION_PHASE == eExecutionPhase.NOT_STARTED || EXECUTION_PHASE == eExecutionPhase.CENTRALIZED_ALLOCATIONS) {
			taskCount = virtualTaskCount;

		} else {
			taskCount = WaitingTasks();
		}

		TaskBundle tb = new TaskBundle();

		Task t = new Task(Integer.parseInt(xl), Integer.parseInt(yl), taskName);
		tb.AddTask(t);

	
		int tpDist = (int) CalcTPDistance(tb.GetTask(0));
		int closest =  MyClosestTaskDistanceTPM(tb);
		int prop =  tpDist + closest;
		Yaz (thisRobot.robotName+" > FUZZY VALUATION : "+ tb.GetTask(0).taskName +  "  Prop :  "+ prop + " tpDist "+tpDist + " closest :" + closest);

		double biddecision = FuzzyDecision(_price,prop, taskCount) ; 
	    if (biddecision > _price )	
		    biddecision = -1; 
		Yaz(thisRobot.robotName + ">  TASKNAME : " + taskName + "  FUZZY DECISION " + biddecision);

		
		return (int) biddecision;
	}

	public double FuzzyDecision(int _price, int prop, int taskCount) {
		
		if (_price > 10000) {
			_price = prop * 2;
		}

		int biddecision = 0;

		try {
			biddecision = (int) sf_main.BidValuatorFuzzy(taskCount, 0, prop, _price);
			Yaz(thisRobot.robotName + ">  " + " PRICE : " + _price + " TASKCOUNT : " + taskCount + " INCLUSION  " + prop + " fuzzyBIDDECISION : " + biddecision);
			//
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Yaz(thisRobot.robotName + ">  BIDDECISION " 	+ Integer.toString(biddecision));

		double returnVal = 0;
		int kar = 10; 

	
		   returnVal = ((500.0 - biddecision*1.0)/ 500.0) * _price * 0.2 + _price * 1.0 + kar ;
		 
        
		/*if (biddecision >= 0 & biddecision <= 100) {
			returnVal = prop * 2;
		} else if (biddecision > 100 & biddecision <= 200) {
			returnVal = prop + prop / 2 + kar;
		} else if (biddecision > 200 & biddecision <= 300) {
			returnVal = prop + prop / 5 + kar;
		} else if (biddecision > 300 & biddecision <= 400) {
			returnVal = prop + prop / 10 + kar;
		} else if (biddecision > 400 & biddecision <= 500) {
			returnVal = prop + kar;
		} else {
			returnVal = 0;
		} */

		if (returnVal > 0)
			virtualTaskCount++;

		return returnVal;

	}
/*
	public int PrepareAProposalS(String taskName) {

		int tbIndex = TaskBundleIndex(taskName);
		int tIndex = TaskIndex(taskName);
		int prop = -1;
		if (tbIndex > -1 && ExecutingNow == null) {
			TaskBundle tb = taskBundles[tbIndex];

			if (tb.state == eTaskState.NOT_STARTED || tb.state == eTaskState.PROCESSING ) {

				// Task tmp = new Task(thisRobot.startX, thisRobot.startY,
				// "tmp");
				prop = (int) CalcTPDistance(tb.GetTask(0));

				tb.proposal = prop;
			} else {
				Yaz(thisRobot.robotName + "> task statem"
						+ Integer.toString(tb.state.getValue()));
			}

		} else {
			Yaz(thisRobot.robotName + "> Task Index : "
					+ Integer.toString(tIndex)
					+ " - task listede yok - task null");
		}

		return prop;

	}

	public double CalcDistance(eBidValuation approach, TaskBundle from, TaskBundle to) {
		return CalcDistance(approach, from.GetTask(0), to.GetTask(0));

	}
*/
	public double CalcDistance(eBidValuation approach, Task to) {
		Task r = new Task(thisRobot.xLoc, thisRobot.yLoc, "robot");
		return CalcDistance(approach, r, to);

	}

	public double CalcDistance(eBidValuation approach, Task from, Robotum rob) {
		Task t = new Task(rob.xLoc, rob.yLoc, rob.robotName);
		return CalcDistance(approach, from, t);
	}

	public double CalcDistance(eBidValuation approach, Task from, Task to) {
		double retVal = -1;
		//Yaz ("robot->CalcDistance : "+from.taskName + " - " +to.taskName + " APPROACH: "+approach );
		if (approach== eBidValuation.TMPTSP || approach== eBidValuation.EUCLIDIAN || approach==eBidValuation.FUZZY || approach == eBidValuation.HYBRID) 
			retVal = CalcTPDistance(from, to);
		else if (approach== eBidValuation.DSTARLITE|| approach== eBidValuation.DSTARLITEFUZZY || approach==eBidValuation.DSTARLITETSP ) 
			retVal = CalcDStarDistance(from, to);
		else if (approach== eBidValuation.RRT || approach == eBidValuation.TMPRRTTSP) 
			retVal = CalcRRTDistance(from, to);
		
		//Yaz ("robot->CalcDistance : "+from.taskName + " - " +to.taskName + " dist: "+retVal);
		
		/*switch (approach) {
		case 1:
		case 2:
		case 6:
		case 9:
			retVal = CalcTPDistance(from, to);
			break;
		case 3:
		case 4:
		case 8:
			retVal = CalcDStarDistance(from, to);
			break;
		case 5:
		case 7:
			retVal = CalcRRTDistance(from, to);
			break;

		}*/ 
		return retVal;

	}

	public double CalcDistance(int xl, int yl) {

		int x_fark = thisRobot.xLoc - xl;
		int y_fark = thisRobot.yLoc - yl;

		return Math.sqrt(x_fark * x_fark + y_fark * y_fark);

	}

	public double CalcDistance(Task t) {

		int x_fark = thisRobot.xLoc - t.xLoc;
		int y_fark = thisRobot.yLoc - t.yLoc;

		return Math.sqrt(x_fark * x_fark + y_fark * y_fark);

	}

	public double CalcDistance(Task t1, Task t2) {

		int x_fark = t1.xLoc - t2.xLoc;
		int y_fark = t1.yLoc - t2.yLoc;

		return Math.sqrt(x_fark * x_fark + y_fark * y_fark);

	}

	/*
	 * private double CalcDistanceD(int xs, int ys, int xd, int yd) {
	 * 
	 * int x_fark = xs-xd ; int y_fark = ys-yd ;
	 * 
	 * return Math.sqrt( x_fark * x_fark + y_fark*y_fark ) ;
	 * 
	 * 
	 * }
	 */

	public int TaskIndex(String taskName) {
		int bundleIndex = TaskBundleIndex(taskName);
		if (bundleIndex > -1) {
			TaskBundle tb = taskBundles[bundleIndex];
			return tb.TaskIndex(taskName);
		}
		return -1;

	}

	public int MyTaskBundleIndex(String taskName) {
		for (int i = 0; i < myTaskBundles; i++) {
			TaskBundle tb = taskQueue[i];
			for (int j = 0; j < tb.taskCount(); j++) {
				Task t = tb.GetTask(j);
				String tskName = t.taskName;
				if (tskName.compareTo(taskName) == 0) {
					return i;
				}
			}
		}
		return -1;

	}

	public int MyTaskBundleIndex_B(String bundleName) {
		for (int i = 0; i < myTaskBundles; i++) {
			TaskBundle tb = taskQueue[i];
			for (int j = 0; j < tb.taskCount(); j++) {
				Task t = tb.GetTask(j);
				String bName = t.bundleHead;
				if (bName.compareTo(bundleName) == 0) {
					return i;
				}
			}
		}
		return -1;

	}

	public int OtherTaskBundleIndex(String taskName) {
		for (int i = 0; i < others; i++) {
			TaskBundle tb = otherTasks[i];
			for (int j = 0; j < tb.taskCount(); j++) {
				Task t = tb.GetTask(j);
				String tskName = t.taskName;
				if (tskName.compareTo(taskName) == 0) {
					return i;
				}
			}
		}
		return -1;

	}

	
}// agent

