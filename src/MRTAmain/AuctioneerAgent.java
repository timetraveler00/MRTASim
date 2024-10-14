package MRTAmain;

import jade.core.Agent;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.*;

import Enums.eExecutionMode;
import Enums.eExecutionPhase;
import Enums.eInitialTaskAssignment;
import Enums.eRobotStatus;
import Enums.eServiceArea;
import Enums.eSimState;
import Enums.eTaskState;
import Enums.eTradeStatus;
import Enums.eTradingProgress;
import MRTAStreamManager.TaskReader;

public class AuctioneerAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 432064575589139439L;
	String myName = "MUZAYEDECI";
	String terrain = "ARENA";

	ACLMessage bestOffer = null;
	public int robotCount = 0;
	int ten_seconds = 5000;
	int two_minutes = 120000;
	int twenty_seconds = 20000;
	ListRobots rare_List = new ListRobots(this, two_minutes);
	ListRobots intensive_List = new ListRobots(this, twenty_seconds);
	StartAnAuctionT starta = new StartAnAuctionT(this, 60000);
	InformTasks inform_tasks = new InformTasks(this, 300000);
	ScheduleTasksFromFile stfr = new ScheduleTasksFromFile(this, 1000);
	ExecutionTimeCalculator etc = new ExecutionTimeCalculator(this, 1000);
	
	String simSetupSelection = ""; 
	long executionTickCount = 0;

	boolean assignmentFlag = true;
	int PROPOSAL_DEFAULT = -1;
	int PROPOSAL_CEIL = 5000;
	private eSimState SIM_STATE = eSimState.NOT_STARTED;
	FileOutputStream out;
	PrintStream p;
	int messCount = 0;
	private eExecutionMode EXECUTION_MODE = eExecutionMode.CENTRALIZED;
	eExecutionPhase EXECUTION_PHASE = eExecutionPhase.NOT_STARTED;
	int AUCTIONS_MADE = 0;
	String templateSelect;
	/*
	 * EXECUTION_MODE 0 : DYNAMIC OPTIMAL 1 : OPTIMAL + TRADE
	 */
	public int seedValue = 0;
	public Random randomGenerator;
	boolean tradeProcessFlag = false;
	

	int cumulativePrices = 0;
	/*
	 * SIM_STATE 0: Not started yet 1: On Process 2: Paused 3: Stopped
	 */
	public int taskStateTicker[] = new int[100];
	public int emInterval = 10;

	public int traderOrder[] = new int[100];

	EvaluateMessages evalProp = new EvaluateMessages(this, emInterval);

	// EvaluateMessages evalProp = new EvaluateMessages ();

	ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
	final MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
			MessageTemplate.MatchConversationId(msg.getConversationId()));

	Robotum robots[] = new Robotum[100];
	public int cumulativePaths[] = new int[100];
	// public int pricesperTrade[] = new int [100];

	// int totalPaths [][] = new int [2][100];
	int proposals[][] = new int[100][100];
	boolean proOK[][] = new boolean[100][100];

	public int TERRAIN_WIDTH = 290;
	public int TERRAIN_HEIGHT = 290;

	int taskBundleCount = 0;
	int simTaskCount = 0;

	int oldtCount = 0;
	TaskBundle taskBundles[] = new TaskBundle[100];

	int chargerCount = 0;

	String nearest[] = new String[3];
	public int tradeCount = -1;
	public int tempTradeCount = -1;

	// MyWaker mw = new MyWaker(this, 10000);
	Agent thisa = this;
	eInitialTaskAssignment initTaskAssgnmt;
	long time0 = 0, time1 = 0, time2 = 0;
	long iaTime = 0, tTime = 0, schTime = 0;
	long timeAuctionStart = 0, timeTradeStart = 0, timeSchedulingStart = 0, timeExecutionStart = 0;
	public int autoMode = 0;
	IOLogger iol;

	public eServiceArea expMode = eServiceArea.EXTENDEDRECTANGULAR;
	String approaches[] = { "Euclidian", "Fuzzy", "DStarLite", "DStarLiteFuzzy", "RRT", "TPM - TSP" };

	Trade[] trades = new Trade[100];
	public int robotCapacity = 0;

	private MRTATime mrt = new MRTATime();
	String rc1[];

	Map<String, Object> handlerMap = new HashMap<String, Object>();
	boolean messageMapFlag = false;

	public void Yaz(String st) {

		iol.Yaz(st);

	}

	public void getCalleMethod() {

		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		System.out.println("");
		System.out.println("AuctioneerAgent-> " + ste[2].getMethodName() + "--" + ste[3].getMethodName() + "--"
				+ ste[4].getMethodName() + "--" + ste[5].getMethodName() + "--");
		System.out.println("");

	}

	protected void setup() {
		// getCalleMethod () ;

		Object[] args = getArguments();
		if (args != null) {

			myName = args[0].toString();
			templateSelect = args[5].toString();
			seedValue = Integer.parseInt(args[1].toString());
			randomGenerator = new Random(seedValue);
			simTaskCount = Integer.parseInt(args[2].toString());
			taskBundleCount = simTaskCount;
			TERRAIN_WIDTH = Integer.parseInt(args[3].toString());
			TERRAIN_HEIGHT = Integer.parseInt(args[4].toString());
			initTaskAssgnmt = eInitialTaskAssignment.values()[Integer.parseInt(args[6].toString())];
			robotCapacity = Integer.parseInt(args[7].toString());
			terrain = args[8].toString();
			autoMode = Integer.parseInt(args[9].toString());
			expMode = eServiceArea.values()[Integer.parseInt(args[10].toString())];
			simSetupSelection = args[11].toString(); 
		}
		iol = new IOLogger("AuctioneerAgent", autoMode);
		Yaz(templateSelect);
		Yaz("auto mode " + autoMode);

		if (templateSelect.compareTo("Empty") == 0) {

			addBehaviour(new GenerateRandomTasks());
		} else {
			Yaz("generate");
			addBehaviour(new GenerateTasksFromFile());

		}

		// Görevler terrain'e bildiriliyor.
		addBehaviour(new InformTasksOnce());

		addBehaviour(evalProp);
		if (simSetupSelection.compareTo("RL")==0) 
			// newborn robot 
			addBehaviour(new NewBornRobotStart());
		else 
		    addBehaviour(starta);
		// addBehaviour(etc);

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
		// getCalleMethod ();
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			DFService.search(this, dfd);

			AID[] robots = searchDF("ROBOT");
			// System.out.print("\nROBOTS: ");
			if (robots != null) {
				for (int i = 0; i < robots.length; i++) {
					AddUpdateRobot(robots[i].getLocalName());
				}
				// System.out.print( robots[i].getLocalName() + ", ");
				// Yaz();
			}

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}

	// MMMMMMMMMMMMMMMMMMMM AGENT METHODS MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM

	public AuctioneerAgent() {
		System.out.print(myName + ">CONSTRUCTOR ... ");
		PrintWallWatch();

	}

	public void ListTasks() {

		for (int i = 0; i < taskBundleCount; i++) {
			TaskBundle tb = taskBundles[i];
			Yaz(myName + ">> TASK BUNDLE " + Integer.toString(i));
			for (int j = 0; j < tb.taskCount(); j++) {
				Task t = tb.GetTask(j);
				Yaz(myName + ">    " + t.taskName + " state : " + t.state + " ");
			}

		}

	}

	public void SendTaskInformationToTerrain(Task t) {

		// getCalleMethod ();
		if (t.owner == -1) {
			t.ownerName = "NotAssigned";

		} else {
			t.ownerName = robots[t.owner].robotName;
		}

		String rc[] = { terrain };
		String mc[] = { "72", t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc),
				Integer.toString(t.state.getValue()), Integer.toString(t.taskType), t.ownerName

		};
		SendACLMessage(rc, 1, mc, 7);
	}

	public void ResetProposals() {

		for (int i = 0; i < robotCount; i++) {

			for (int j = 0; j < taskBundleCount; j++) {
				proposals[i][j] = PROPOSAL_CEIL;
				proOK[i][j] = false;
			}
		}

	}

	public int RobotIndex(String robotName) {
		for (int i = 0; i < robotCount; i++) {
			String rName = robots[i].robotName;
			if (rName.compareTo(robotName) == 0) {
				return i;
			}
		}
		return -1;
	}

	public boolean IsPropsCompleted() {

		// getCalleMethod ();
		boolean returnValue = true;
		for (int i = 0; i < robotCount; i++) {

			for (int j = 0; j < taskBundleCount; j++) {

				if (proOK[i][j] == false /* && taskBundles[j].state<2 */) {

					returnValue = false;

				}
			}
		}
		return returnValue;
	}

	public int MaxProposal() {
		int maxP = 0;
		for (int i = 0; i < robotCount; i++) {

			for (int j = 0; j < taskBundleCount; j++) {
				if (maxP < proposals[i][j])
					maxP = proposals[i][j];
			}
		}

		return maxP;
	}

	public double[][] PrepareArray() {

		// getCalleMethod ();
		int propMax = MaxProposal() + 5;
		// System.out.println(" ");
		// System.out.println(" prepare array ");
		double finalArray[][] = new double[robotCount][taskBundleCount];
		for (int i = 0; i < robotCount; i++) {
			// System.out.println("robot index: "+i);
			for (int j = 0; j < taskBundleCount; j++) {
				// System.out.println(" task index: "+j);
				// System.out.println(" proposals[robot:"+i+"][task:"+j+"]:
				// "+proposals[i][j]);
				// System.out.println(" taskBundles[j].state:
				// "+taskBundles[j].state);
				// System.out.println(" propMax "+propMax);

				finalArray[i][j] = 0;
				if (proposals[i][j] > PROPOSAL_DEFAULT && (taskBundles[j].state == eTaskState.NOT_STARTED
						|| taskBundles[j].state == eTaskState.PROCESSING
						|| taskBundles[j].state == eTaskState.NOT_ASSIGNED)) {

					finalArray[i][j] = propMax - proposals[i][j];

				}
				// System.out.println(" finalArray[i][j] "+finalArray[i][j]);

			}
		}

		// System.out.println(" END OF prepare array ");
		// System.out.println(" ");
		return finalArray;
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

	public void PrintWallWatch() {

		mrt.PrintWallWatch();

	}

	public void PrintBlackBoard() {

		System.out.print("            ");
		for (int i = 0; i < taskBundleCount; i++) {
			TaskBundle tb = taskBundles[i];
			if (tb.state == eTaskState.NOT_STARTED || tb.state == eTaskState.PROCESSING) {

				System.out.print(tb.GetTask(0).taskName + "   ");

			}
		}
		Yaz("");

		ProposalMatrix();
		ProposalAvailabilityMatrix();

	}

	public void ProposalMatrix() {
		// getCalleMethod ();
		for (int i = 0; i < robotCount; i++) {
			System.out.print(robots[i].robotName + "    ");
			for (int j = 0; j < taskBundleCount; j++) {
				TaskBundle tb = taskBundles[j];
				if (tb.state == eTaskState.NOT_STARTED || tb.state == eTaskState.PROCESSING)
					System.out.print(Integer.toString(proposals[i][j]) + "   ");
			}
			Yaz("");
		}
		Yaz("");

	}

	public void ProposalAvailabilityMatrix() {
		// getCalleMethod ();
		for (int i = 0; i < robotCount; i++) {
			System.out.print(robots[i].robotName + "    ");
			for (int j = 0; j < taskBundleCount; j++) {
				TaskBundle tb = taskBundles[j];
				if (tb.state == eTaskState.NOT_STARTED || tb.state == eTaskState.PROCESSING) {
					if (proOK[i][j] == true)
						System.out.print("OK" + "   ");
					else if (proOK[i][j] == false)
						System.out.print("W" + "   ");
				}
			}
			Yaz("");
		}

	}

	public void RandomAssignmentMade(String temp[]) {
		taskBundles[TaskBundleIndex(temp[2])].owner = RobotIndex(temp[1]);
		Task t = taskBundles[TaskBundleIndex(temp[2])].GetTask(temp[2]);
		t.state = eTaskState.NOT_STARTED;
		t.tradeStatus = eTradeStatus.OWNED;
		t.owner = RobotIndex(temp[1]);
		t.ownerName = temp[1];
		SendTaskInformationToTerrain(t);

		Yaz(" robot : " + temp[1] + " task : " + temp[2] + " taskbundleindex : " + TaskBundleIndex(temp[2])
				+ " robotindex : " + RobotIndex(temp[1]));
		// taskBundles [TaskBundleIndex(temp[2])].GetTask(0).owner =
		// RobotIndex(temp[1]);
		if (AllTasksAssigned())
			InitializationofTradingMessage(1);
		else
			MakeRandomAssignments(temp);
		;
	}

	double CalcDistance(int x1, int y1, int x2, int y2) {
		int x_fark = x1 - x2;
		int y_fark = y1 - y2;

		return Math.sqrt(x_fark * x_fark + y_fark * y_fark);

	}

	double CalcDistance(Task t1, Task t2) {
		int x_fark = t1.xLoc - t2.xLoc;
		int y_fark = t1.yLoc - t2.yLoc;

		return Math.sqrt(x_fark * x_fark + y_fark * y_fark);

	}

	public boolean ApprovedTrader(int rIndex) {
		Yaz(myName + "> ApprovedTrader ");
		boolean returnValue = true;
		for (int i = 0; i < tradeCount; i++) {
			Trade trade = trades[i];
			Yaz("Trade : " + i + " Satýcý : " + trade.Seller + " GÖREV : " + trade.TBundle + " ALICI : " + trade.Buyer
					+ " durum : " + trade.status + " deðer : " + trade.price);

		}

		Yaz("SATICI sorgulama ??????????? : " + robots[rIndex].robotName);
		if (tradeCount > 0 && rIndex > -1) {
			Trade trade = trades[tradeCount];
			int tIndex = TaskBundleIndex(robots[rIndex].tradedTask[tempTradeCount]);

			Yaz("SATICI sorgulama ??????????? : " + robots[rIndex].robotName + " > " + trade.TBundle + " > "
					+ robots[rIndex].tradedTask[tempTradeCount] + " tIndex : " + tIndex);

			if (trade.Seller.compareTo(robots[rIndex].robotName) == 0 && trade.status == eTradingProgress.FAILURE
					&& tIndex > -1 && trade.TBundle.compareTo(robots[rIndex].tradedTask[tempTradeCount]) == 0) {

				Yaz("SATICI UYGUNSUZLUÐU : " + robots[rIndex].robotName + " SON SATIÞI BAÞARISIZ ... " + trade.TBundle);
				returnValue = false;
			}

			if (robots[rIndex].tradedTask[tempTradeCount].compareTo("null") == 0) {
				Yaz("SATICI UYGUNSUZLUÐU : " + robots[rIndex].robotName + " SATACAK UYGUN GÖREVÝ YOK ... "
						+ trade.TBundle);
				returnValue = false;
			}
		}

		return returnValue;

	}

	public boolean CheckTraders() {
		boolean healthFlag = false;
		for (int i = 0; i < robotCount; i++) {
			int tIndex = TaskBundleIndex(robots[i].tradedTask[tempTradeCount]);
			if (tIndex > -1) {
				healthFlag = true;
			}

		}
		return healthFlag;

	}

	public boolean AnyCandidates() {
		boolean healthFlag = false;
		for (int j = 0; j < robotCount; j++) {
			int price = robots[j].priceperTrade[tradeCount + 1];
			if (price > 0 && price < 5000)
				healthFlag = true;
		}

		return healthFlag;
	}

	/*
	 * public void DefineTrader112() { Yaz(myName+"> DefineTrader....."); if
	 * (CheckTraders() && AnyCandidates()) { Yaz(myName+"> CheckTraders ");
	 * SortTraders_Price();
	 * 
	 * for (int i = 0; i < robotCount; i++) Yaz("Trade order : " + i + "-" +
	 * robots[traderOrder[i]].robotName);
	 * 
	 * if (robots[traderOrder[0]].priceperTrade[tradeCount+1]<=0) {
	 * 
	 * System.out
	 * .println("SATILACAK GÖREV BULUNAMADIÐINDAN TRADE SONLANDIRILDI.....");
	 * EndOfTrading(); } else {
	 * 
	 * 
	 * int i = 0;
	 * 
	 * 
	 * while (!ApprovedTrader(traderOrder[i])) { i++; } if (i < robotCount) {
	 * AnnounceTrader(traderOrder[i]); } else { System.out
	 * .println("UYGUN SATICI BULUNAMADIÐINDAN TRADE SONLANDIRILDI.....");
	 * EndOfTrading(); }
	 * 
	 * } /* Yaz(
	 * "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
	 * ); Yaz(myName+"> tradeCount : " +Integer.toString(tradeCount) +
	 * " robotCount : "+Integer.toString(robotCount) ); Yaz(
	 * "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
	 * ); //* } else { System.out
	 * .println("UYGUN SATICI BULUNAMADIÐINDAN TRADE SONLANDIRILDI.....");
	 * EndOfTrading();
	 * 
	 * }
	 * 
	 * }
	 */

	public void DefineTrader() {
		Yaz(myName + "> DefineTrader.....");
		if (CheckTraders() && AnyCandidates()) {
			Yaz(myName + "> CheckTraders ");
			SortTraders_Price();

			for (int i = 0; i < robotCount; i++)
				Yaz("Trade order : " + i + "-" + robots[traderOrder[i]].robotName);

			if (robots[traderOrder[0]].priceperTrade[tradeCount + 1] <= 0) {

				System.out.println("SATILACAK GÖREV BULUNAMADIÐINDAN TRADE SONLANDIRILDI.....");
				EndOfTrading();
			} else {

				int i = 0;

				while (!ApprovedTrader(traderOrder[i])) {
					i++;
				}
				if (i < robotCount) {
					AnnounceTrader(traderOrder[i]);
				} else {
					System.out.println("UYGUN SATICI BULUNAMADIÐINDAN TRADE SONLANDIRILDI.....");
					EndOfTrading();
				}

			}
			/*
			 * Yaz(
			 * "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
			 * ); Yaz(myName+"> tradeCount : " +Integer.toString(tradeCount) +
			 * " robotCount : "+Integer.toString(robotCount) ); Yaz(
			 * "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
			 * );
			 */
		} else {
			System.out.println("UYGUN SATICI BULUNAMADIÐINDAN TRADE SONLANDIRILDI.....");
			EndOfTrading();

		}

	}

	public void PathLengthArrived(String temp[]) {
		Yaz(myName + "> PathLengthArrived  111 " + temp[1] + " teklif : " + temp[2] + " görev : " + temp[3]
				+ " pathLength :" + temp[4]);
		int rIndex = RobotIndex(temp[1]);
		if (rIndex > -1) {
			robots[rIndex].setPrice(tempTradeCount, Integer.parseInt(temp[2]));
			robots[rIndex].tradedTask[tempTradeCount] = temp[3];
			robots[rIndex].pathLength(tempTradeCount, Integer.parseInt(temp[4]));
		}

		EvaluatePrices();

	}

	public void SortTraders_Price() {
		Yaz(myName + "> SortTraders_Price ");
		for (int i = 0; i < 100; i++)
			traderOrder[i] = -1;

		int prevMax = 1000000;
		Yaz("SATICI SEÇÝMÝ : TEKLÝF LÝSTESÝ");
		for (int j = 0; j < robotCount; j++) {

			Yaz(robots[j].robotName + " price:  " + robots[j].priceperTrade[tradeCount + 1]);
		}
		for (int i = 0; i < robotCount; i++) {
			int maxProfit = -1;
			int maxi = -1;
			for (int j = 0; j < robotCount; j++) {
				int price = robots[j].priceperTrade[tradeCount + 1];
				if (price > maxProfit && price < prevMax && price > 0) {
					maxProfit = price;
					maxi = j;
				}

			}

			if (maxi > -1) {
				prevMax = maxProfit;
				traderOrder[i] = maxi;
			}

			else

				traderOrder[i] = (tradeCount + 1 + i) % robotCount;

		}

		Yaz("SATICI SEÇÝMÝ : SONUÇ SIRALAMASI ");
		for (int j = 0; j < robotCount; j++) {
			Yaz(j + ". robot : " + robots[traderOrder[j]].robotName);

		}
	}

	public boolean TradeStability() {

		Yaz(myName + "> Trade Stability :  " + tradeCount);
		int cter = tradeCount;
		int counter = 0;
		while (trades[cter].status == eTradingProgress.FAILURE && cter > 0) {
			cter--;
			counter++;

		}

		if (counter > taskBundleCount) {
			return true;
		}

		return false;
	}

	public void AnnounceTrader(int rIndex)

	{
		Yaz(myName + "> AnnounceTrader " + robots[rIndex].robotName);
		// if (! tradeProcessFlag )
		{
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			AID aid = new AID(robots[rIndex].robotName, AID.ISLOCALNAME);
			msg.addReceiver(aid);
			msg.setLanguage("English");
			msg.setContent("112_" + Integer.toString(tradeCount));
			send(msg);
			// tradeProcessFlag = true;
			tradeCount++;
			trades[tradeCount] = new Trade();
			trades[tradeCount].status = eTradingProgress.ON_PROGRESS;
			trades[tradeCount].Seller = robots[rIndex].robotName;

			for (int i = 0; i < robotCount; i++)
				robots[i].lastTraded = false;
			robots[rIndex].lastTraded = true;
		}

	}

	public boolean CheckPricesCompleted() {
		Yaz(myName + "> CheckPricesCompleted ");
		boolean Completed = true;
		cumulativePrices = 0;
		for (int i = 0; i < robotCount; i++) {
			Yaz("checkprices : " + robots[i].robotName + " -> " + robots[i].priceperTrade[tradeCount + 1]);

			if (robots[i].priceperTrade[tempTradeCount] < -100000) {
				Completed = false;
			} else {
				// cumulativePrices += robots[i].priceperTrade[tradeCount];
				cumulativePrices += robots[i].localPathLength[tempTradeCount];
			}

		}

		Yaz("checkprices");
		return Completed;
	}

	public void EvaluatePrices() {
		Yaz(myName + "> EvaluatePrices ");
		// for (int i=0; i<robotCount; i++)
		// Yaz("Robot i : "+i +
		// " localPathLength : "+robots[i].localPathLength[tradeCount]);
		if (CheckPricesCompleted()) {
			DefineTrader();
			/*
			 * String rcd[] = { terrain };
			 * 
			 * String mcd[] = { "116", Integer.toString(tradeCount),
			 * Integer.toString(cumulativePrices) }; SendACLMessage(rcd, 1, mcd,
			 * 3);
			 */

		} else {
			Yaz(myName + "> Satýcý adayý robotlardan gelen teklifler eksik ! ");
		}

	}

	/*
	 * public void LogToFile() { try { out = new
	 * FileOutputStream("auctioneer.txt"); p = new PrintStream(out);
	 * 
	 * for (int k = 0; k < messCount; k++) p.println(messageBuff[k]);
	 * 
	 * p.close(); System.out .print(myName+">" +
	 * " Mesajlar dosyaya kaydedildi... "); PrintWallWatch();
	 * 
	 * } catch (FileNotFoundException e1) { // TODO Auto-generated catch block
	 * e1.printStackTrace(); } /* catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); }
	 *
	 * }
	 */

	// 09.10.2014 Kullanýlmayacak
	public void ResetProcessingTasks() {

		// getCalleMethod ();
		// Ýþlerlikte olan süreçlerin durumunun güncellenmesi
		for (int k = 0; k < taskBundleCount; k++) {
			if (taskBundles[k].state == eTaskState.PROCESSING) {
				taskBundles[k].state = eTaskState.NOT_STARTED;

				for (int i = 0; i < taskBundles[k].taskCount(); i++) {
					Task t = taskBundles[k].GetTask(i);
					SendTaskInformationToTerrain(t);

					if (t.owner > -1) {

						ACLMessage tmsg05 = new ACLMessage(ACLMessage.INFORM);
						AID aid05 = new AID(robots[t.owner].robotName, AID.ISLOCALNAME);
						tmsg05.addReceiver(aid05);
						tmsg05.setLanguage("English");
						tmsg05.setContent("05_" + t.taskName);
						send(tmsg05);

						t.owner = -1;
					}
				}

			}

		}
		Yaz(" AFTER ResetProcessingTasks ()");

	}

	public int[] AssignTasksToRobots(int assignmentMatrix[][],

			double finalArray[][], int[] preAss, HungarianAlgorithm ha) {
		// getCalleMethod ();
		int rIndex = -1;
		int tIndex = -1;
		int assigned[] = new int[100];
		for (int i = 0; i < 100; i++)
			assigned[i] = -1;
		if (robotCount <= taskBundleCount) {
			for (int i = 0; i < robotCount; i++) {

				rIndex = assignmentMatrix[i][0];
				tIndex = assignmentMatrix[i][1];
				if (preAss[tIndex] < 1 && taskBundles[tIndex].state == eTaskState.NOT_STARTED
						&& ha.array[rIndex][tIndex] > 0) {
					assigned[tIndex] = 1;
					AssignATaskMessage(rIndex, tIndex);
				}

			}

		} else {
			for (int i = 0; i < taskBundleCount; i++) {
				rIndex = assignmentMatrix[i][1];
				tIndex = assignmentMatrix[i][0];

				System.out.println("rIndex: " + rIndex + " tIndex: " + tIndex);

				if (preAss[tIndex] < 1 && taskBundles[tIndex].state == eTaskState.NOT_STARTED
						&& ha.array[rIndex][tIndex] > 0) {
					assigned[tIndex] = 1;
					AssignATaskMessage(rIndex, tIndex);
				}

			}

		}
		Yaz(" AFTER AssignTasksToRobots ()");
		return assigned;

	}

	public void SendACLMessage(String rcv[], int rCount, String content[], int pCount) {

		String msgContent = "";

		for (int i = 0; i < pCount; i++) {
			msgContent = msgContent + content[i];
			msgContent = msgContent + "_";
		}

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

		for (int i = 0; i < rCount; i++) {

			msg.addReceiver(new AID(rcv[i], AID.ISLOCALNAME));

		}

		msg.setLanguage("English");
		msg.setContent(msgContent);
		send(msg);
	}

	public void SendACLMessage(String rname, String content[], int pCount) {

		String msgContent = "";

		for (int i = 0; i < pCount; i++) {
			msgContent = msgContent + content[i];
			msgContent = msgContent + "_";
		}

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

		msg.addReceiver(new AID(rname, AID.ISLOCALNAME));

		msg.setLanguage("English");
		msg.setContent(msgContent);
		send(msg);
	}

	public void StartSimulation(String temp[]) {
		// getCalleMethod ();
		time0 = Calendar.getInstance().getTimeInMillis();
		SIM_STATE = eSimState.STARTED; // execution started
		EXECUTION_MODE = eExecutionMode.values()[Integer.parseInt(temp[1])]; // dynamic
																				// or
																				// trade-based?

		// SEQUENTÝAL
		// if (EXECUTION_MODE == 3)
		// addBehaviour(new TaskClustering());

		ACLMessage imsg = new ACLMessage(ACLMessage.INFORM);
		for (int j = 0; j < robotCount; j++) {
			imsg.addReceiver(new AID(robots[j].robotName, AID.ISLOCALNAME));
		}

		imsg.setLanguage("English");
		imsg.setContent("61_" + Integer.toString(EXECUTION_MODE.getValue()) + "_" + temp[3]);
		send(imsg);
		Yaz(myName + "> Ýþletim Modu " + EXECUTION_MODE + " " + Integer.toString(robotCount)
				+ " adet robota gonderildi. ");

		PrintWallWatch();
		addBehaviour(stfr);
		addBehaviour(new InformTasksOnce());
		EXECUTION_PHASE = eExecutionPhase.CENTRALIZED_ALLOCATIONS;
		Yaz(myName + "> Ýþletim Fazý " + EXECUTION_PHASE);
		SendEXECUTIONPHASE();
		Yaz("*********************************************************");
		Yaz(initTaskAssgnmt.toString());
		Yaz("*********************************************************");

		Yaz("task bundle count" + Integer.toString(taskBundleCount));

		if (initTaskAssgnmt == eInitialTaskAssignment.RANDOM) {

			MakeRandomAssignments(temp);

			if (AllTasksAssigned())
				InitializationofTradingMessage(1);

		} else {

			addBehaviour(new StartAnAuctionOnce());
		}
	}

	public void AddUpdateRobot(String rName) {
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

		// System.out.print(thisRobot.robotName+"> "+r.robotName+" robotu
		// listeye eklendi.");
		PrintWallWatch();

	}

	public void AddUpdateRobot(String temp[]) {

		Robotum r;
		int rIndex = RobotIndex(temp[1]);
		// Robot bilgisi mevcutsa
		if (rIndex < 0) {
			r = new Robotum();
			rIndex = robotCount;
			robots[robotCount++] = r;
		} else {
			r = robots[rIndex];
		}

		r.robotName = temp[1];
		r.xLoc = Integer.parseInt(temp[2]);
		r.yLoc = Integer.parseInt(temp[3]);
		r.status = eRobotStatus.values()[Integer.parseInt(temp[4])];
		robots[rIndex] = r;

		/*
		 * BU FONKSIYONDA PARAMETRELERÝN GEÇERLÝLÝK KONTROLÜ YAPILMAMIÞTIR.
		 * GEÇERLÝ GÖNDERÝLDÝÐÝ VARSAYILMIÞTIR.
		 */

	}

	public boolean AllTasksAssigned() {
		Yaz("taskBundleCount:" + Integer.toString(taskBundleCount));
		for (int i = 0; i < taskBundleCount; i++) {

			if (taskBundles[i] != null && taskBundles[i].owner == -1) {
				Yaz("333333333333333333333333333333333");
				Yaz("taskbundle : " + taskBundles[i].GetTask(0).bundleHead + " owner: "
						+ Integer.toString(taskBundles[i].owner));
				return false;
			}
		}

		return true;
	}

	public void MakeRandomAssignments(String temp[]) {

		if (!AllTasksAssigned()) {
			int i = 0;
			while (taskBundles[i].owner >= 0) {
				i++;
			}
			TaskBundle tb = taskBundles[i];
			int rndRobot = randomGenerator.nextInt(robotCount);
			String rc[] = { robots[rndRobot].robotName };
			for (int j = 0; j < tb.taskCount; j++) {
				Task t = tb.GetTask(j);

				String mc[] = { "0000", t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc),
						Integer.toString(t.state.getValue()), Integer.toString(t.taskType), t.bundleHead

				};

				SendACLMessage(rc, 1, mc, 7);
			}

		}

	}

	public void SendEXECUTIONPHASE() {
		String rcd[] = { terrain };

		String mcd[] = { "120", Integer.toString(EXECUTION_PHASE.getValue()), Integer.toString(AUCTIONS_MADE) };
		SendACLMessage(rcd, 1, mcd, 3);

	}

	public void InitializationofTradingMessage(int sold) {
		Yaz(myName + "> InitializationofTradingMessage.....");

		// getCalleMethod ();
		if (EXECUTION_MODE == eExecutionMode.CENTRALIZED || robotCount == 1) {
			Yaz(myName
					+ "> InitializationofTradingMessage > MERKEZÝ ATAMALAR SONA ERDÝ, TÝCARET YAPILMADAN ÇÝZELGELEMEYE GEÇÝLECEK  ");
			EXECUTION_PHASE = eExecutionPhase.TRADES_COMPLETED;
			EndOfTrading();
		}

		else if (EXECUTION_MODE.getValue() > eExecutionMode.CENTRALIZED.getValue()) {

			PrepareRobotReceivers();
			String mc_end[] = { "10", Integer.toString(sold) };
			emInterval = 100;
			SendACLMessage(rc1, robotCount, mc_end, 2);
			tempTradeCount++;

			EXECUTION_PHASE = eExecutionPhase.TRADES;

			tradeProcessFlag = true;

		}
		SendEXECUTIONPHASE();
	}

	public void EndOfTourMessage() {
		String rc_end[] = new String[robotCount];
		for (int j = 0; j < robotCount; j++) {
			rc_end[j] = robots[j].robotName;
		}
		String mc_end[] = { "09" };
		SendACLMessage(rc_end, robotCount, mc_end, 1);
	}

	public void TaskCompleted(String temp[]) {

		int rIndex = RobotIndex(temp[1]);
		int tBundleIndex = TaskBundleIndex(temp[2]);
		if (rIndex > -1) {
			if (tBundleIndex > -1) {
				// Tamamlamýs"
				if (Integer.parseInt(temp[3]) == 2) {

					Yaz(myName + ">" + temp[1] + "robotu " + temp[2] + " gorevini tamamladý, tebrik ederiz...");
					PrintWallWatch();

					for (int i = 0; i < taskBundles[tBundleIndex].taskCount(); i++) {
						Task t = taskBundles[tBundleIndex].GetTask(i);
						t.state = eTaskState.COMPLETED;
						SendTaskInformationToTerrain(t);
					}
					taskBundles[tBundleIndex].state = eTaskState.COMPLETED;
					robots[rIndex].status = eRobotStatus.IDLE;

					// Bütün robotlara bildirilmeli
					String rc_end[] = new String[robotCount];
					for (int j = 0; j < robotCount; j++) {
						rc_end[j] = robots[j].robotName;
					}
					String mc_end[] = { "066", temp[2] };
					SendACLMessage(rc_end, robotCount, mc_end, 2);
					// ////////////////////////////////////////////////////

					if (EXECUTION_MODE == eExecutionMode.AGILE)
						addBehaviour(new StartAnAuctionOnce());

				}

			}
			/*
			 * else if (temp[2].compareTo("STATION")==0) { robots[rIndex].status
			 * = 0; Yaz(myName+">" + temp[1] + "robotu " + temp[2] +
			 * " gorevini tamamladý, tebrik ederiz...");
			 * 
			 * }
			 */

		}
	}

	public void EndOfTradeBySellerRobot(String temp[]) {
		Yaz(myName + "> EndOfTradeBySellerRobot ### 113 ###   tradeCount  " + Integer.toString(tradeCount)
				+ " TRADEPROCESS : " + tradeProcessFlag + " tradestability :" + TradeStability());

		String rcd[] = { terrain };

		String mcd[] = { "114", Integer.toString(tradeCount), Integer.toString(cumulativePaths[tradeCount]), temp[1],
				temp[2], temp[3], temp[4] };
		SendACLMessage(rcd, 1, mcd, 7);
		Yaz("------------------------------------------------------------------------------------------------------------------");
		Yaz(myName + "> ### 113 ### END OF A TRADE            " + Integer.toString(tradeCount));
		Yaz("------------------------------------------------------------------------------------------------------------------");

		trades[tradeCount].Seller = temp[1];
		trades[tradeCount].TBundle = temp[3];
		int SoldAnything = 0;

		if (TaskBundleIndex(temp[4]) < 0) {
			trades[tradeCount].status = eTradingProgress.FAILURE;
			trades[tradeCount].Buyer = "";
		} else {
			trades[tradeCount].status = eTradingProgress.SUCCESS;
			trades[tradeCount].Buyer = temp[2];
			SoldAnything = 1;
		}

		if (tradeProcessFlag && TradeStability() == true) {

			Yaz("TRADE DOYUMA ULAÞTIÐINDAN SONLANDIRILDI....");
			EndOfTrading();

		} else if (tradeProcessFlag) {

			tradeProcessFlag = false;
			Yaz(myName + "> YENÝ BÝR TRADE BAÞLATILACAK....");
			InitializationofTradingMessage(SoldAnything);

		}
	}

	public void AssignATaskMessage(int rIndex, int tbIndex) {
		TaskBundle tb = taskBundles[tbIndex];
		taskBundles[tbIndex].state = eTaskState.ASSIGNED;

		String rc_end[] = { robots[rIndex].robotName };

		for (int i = 0; i < tb.taskCount; i++) {
			Task t = tb.GetTask(i);
			String mc_end[] = { "04", t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), t.bundleHead,
					Integer.toString(proposals[rIndex][tbIndex]) };
			SendACLMessage(rc_end, 1, mc_end, 6);

		}

	}

	public void AddRunTimeTask(String temp[]) {
		String taskName = temp[1];
		int xLoc = Integer.parseInt(temp[2]);
		int yLoc = Integer.parseInt(temp[3]);
		assignmentFlag = true;

		if (xLoc > 0 && xLoc < TERRAIN_WIDTH && yLoc > 0 && yLoc < TERRAIN_HEIGHT) {
			TaskBundle tb = new TaskBundle();
			Task newTask = new Task();
			newTask.taskName = taskName;
			newTask.xLoc = xLoc;
			newTask.yLoc = yLoc;
			newTask.state = eTaskState.NOT_ASSIGNED;
			newTask.bundleHead = taskName;
			tb.state = eTaskState.NOT_ASSIGNED;
			tb.tradeStatus = eTradeStatus.OWNERLESS;

			tb.AddTask(newTask);
			taskBundles[taskBundleCount++] = tb;
			System.out
					.print(myName + "> " + Integer.toString(taskBundleCount) + ". gorev eklendi : " + newTask.taskName);
			PrintWallWatch();

			addBehaviour(new InformTasksOnce());
			addBehaviour(new StartAnAuctionOnce());

		}
	}

	public void ResetRobotTradeInfo() {
		// getCalleMethod ();
		for (int j = 0; j < robotCount; j++) {
			robots[j].TradeReset();

		}
		tradeCount = -1;
		tempTradeCount = -1;
		for (int i = 0; i < 100; i++) {
			traderOrder[i] = -1;
			trades[i] = null;
		}
	}

	public void SendScheduleMessage(String robotname) {
		// getCalleMethod ();
		String rc_end[] = { robotname };
		String mc_end[] = { "115" };
		SendACLMessage(rc_end, 1, mc_end, 1);
		timeSchedulingStart = Calendar.getInstance().getTimeInMillis();

	}

	public void EndOfTrading() {
		// getCalleMethod ();
		EXECUTION_PHASE = eExecutionPhase.TRADES_COMPLETED;
		SendEXECUTIONPHASE();
		// getCalleMethod ();
		for (int j = 0; j < robotCount; j++) {

			robots[j].scheduleCreated = false;

		}

		SendScheduleMessage(robots[0].robotName);

		String rc_end1[] = { terrain };
		String mc_end1[] = { "115T" };
		SendACLMessage(rc_end1, 1, mc_end1, 1);
		System.out.println(
				"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println(myName + "> ### 113 ### END OF WORK           tradeCount  " + Integer.toString(tradeCount));
		System.out.println(
				"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		tradeProcessFlag = false;
		ResetRobotTradeInfo();
		long timex = Calendar.getInstance().getTimeInMillis();
		tTime += (timex - timeTradeStart);
		SendDurationMessage();
		assignmentFlag = true;
		Yaz(myName + "> EndOfTrading - assignment = true ");

	}

	public void StartExecution() {
		// getCalleMethod ();
		EXECUTION_PHASE = eExecutionPhase.SCHEDULING_EXECUTING;

		SendEXECUTIONPHASE();

		String rc_end[] = new String[robotCount];
		for (int j = 0; j < robotCount; j++) {
			rc_end[j] = robots[j].robotName;
		}

		String mc_end[] = { "116" };
		SendACLMessage(rc_end, robotCount, mc_end, 1);
		Yaz(myName + "> Robotlara iþletime geç mesajý gönderildi : 116");
	}

	/*
	 * String rc_end2[] = { "SimManagerAgent" }; String mc_end2[] = { "115G" };
	 * SendACLMessage(rc_end2, 1, mc_end2, 1); /
	 * 
	 * String rc_end1[] = { terrain }; String mc_end1[] = { "115T" };
	 * SendACLMessage(rc_end1, 1, mc_end1, 1); System.out .println(
	 * "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
	 * ); System.out
	 * .println(myName+"> ### 113 ### END OF WORK           tradeCount  " +
	 * Integer.toString(tradeCount)); System.out .println(
	 * "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
	 * ); tradeProcessFlag = false; ResetRobotTradeInfo();
	 * 
	 * long timex = Calendar.getInstance().getTimeInMillis(); //
	 * Yaz("time2 :  "+ time2); tTime += (timex - timeTradeStart);
	 * timeSchedulingStart = timex; // timePrev = timex;
	 * 
	 * SendDurationMessage();
	 * 
	 * assignmentFlag = true; Yaz(myName+"> EndOfTrading - assignment = true ");
	 * 
	 * }
	 */

	// IIIIIIIIIIIIIIIIIIII INNER CLASSES IIIIIIIIIIIIIIIIIIIIIIIIIII
	public void ShowProposals(double fArray[][]) {

		// getCalleMethod ();
		System.out.println(
				" ???????????????????????????????????????????????????????????????????????????????????????????????????         ");
		// System.out.print(" ");
		/*
		 * for (int j = 0; j < taskBundleCount; j++) { System.out.print("      "
		 * + taskBundles[j].GetTask(0).taskName + "                " + "\t\t");
		 * 
		 * } Yaz("          ");
		 */
		for (int i = 0; i < robotCount; i++) {

			// System.out.print(robots[i].robotName + " ");
			for (int j = 0; j < taskBundleCount; j++) {
				System.out.print("(" + robots[i].robotName + " , " + taskBundles[j].GetTask(0).taskName + ") ");
				System.out.format(" %8d - ", proposals[i][j]);
				System.out.print(" " + proOK[i][j] + " \t\t");

			}
			Yaz("");
		}
		System.out.println(
				" ???????????????????????????????????????????????????????????????????????????????????????????????????         ");

	}

	public void GreedyAssignment_robotfirst(double fArray[][]) {
		// getCalleMethod ();
		// ResetProcessingTasks();
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		// ShowProposals(fArray);
		// IterativeGreedyAssignment(fArray);
		int assigned[] = new int[100];
		for (int i = 0; i < 100; i++)
			assigned[i] = -1;
		// int i=0;
		int notAssigned = 0;
		// do {
		for (int k = 0; k < robotCapacity; k++) {
			for (int j = 0; j < taskBundleCount; j++) {

				if (assigned[j] < 1)
					notAssigned++;
			}

			if (notAssigned >= robotCount) {
				for (int i = 0; i < robotCount; i++) {
					// i= randomGenerator.nextInt(robotCount);
					double pMax = 0.0;
					int pIndex = -1;

					for (int j = 0; j < taskBundleCount; j++) {

						if (fArray[i][j] > pMax && assigned[j] < 1 && fArray[i][j] < 10000) {
							pMax = fArray[i][j];
							pIndex = j;

						}

					}
					if (pIndex > -1) {

						assigned[pIndex] = 1;
						// ResetProcessingTasks();
						AssignATaskMessage(i, pIndex);
					}
				}

			}

			else // notassigned < robotcount
			{

				for (int j = 0; j < taskBundleCount; j++) {

					double pMax = 0.0;
					int pIndex = -1;
					for (int i = 0; i < robotCount; i++) {

						if (fArray[i][j] > pMax && assigned[j] < 1) {
							pMax = fArray[i][j];
							pIndex = i;
						}
					}

					if (pIndex > -1) {

						assigned[j] = 1;
						// ResetProcessingTasks();
						AssignATaskMessage(pIndex, j);
					}

				}

			}

		}

		// } while (notAssigned>0);

		addBehaviour(new TradeStarterWaker(thisa, 1500));
	}

	public void GreedyAssignment_taskfirst(double fArray[][]) {
		// getCalleMethod ();
		// ResetProcessingTasks();
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		// ShowProposals(fArray);
		// IterativeGreedyAssignment(fArray);
		int assigned[] = new int[100];
		for (int i = 0; i < 100; i++)
			assigned[i] = -1;
		// int i=0;
		int notAssigned = 0;
		// do {
		for (int k = 0; k < robotCapacity; k++) {
			for (int j = 0; j < taskBundleCount; j++) {

				if (assigned[j] < 1)
					notAssigned++;
			}

			if (notAssigned >= robotCount) {
				for (int j = 0; j < taskBundleCount; j++) {
					// i= randomGenerator.nextInt(robotCount);
					double pMax = 0.0;
					int pIndex = -1;

					for (int i = 0; i < robotCount; i++) {
						if (fArray[i][j] > pMax && assigned[j] < 1 && fArray[i][j] < 10000) {
							pMax = fArray[i][j];
							pIndex = i;
						}

					}
					if (pIndex > -1) {

						assigned[j] = 1;
						// ResetProcessingTasks();
						AssignATaskMessage(pIndex, j);
					}
				}

			}

			else // notassigned < robotcount
			{

				for (int i = 0; i < robotCount; i++) {
					double pMax = 0.0;
					int pIndex = -1;

					for (int j = 0; j < taskBundleCount; j++)

					{

						if (fArray[i][j] > pMax && assigned[j] < 1) {
							pMax = fArray[i][j];
							pIndex = j;
						}
					}

					if (pIndex > -1) {

						assigned[pIndex] = 1;
						// ResetProcessingTasks();
						AssignATaskMessage(i, pIndex);
					}

				}

			}

		}

		// } while (notAssigned>0);

		addBehaviour(new TradeStarterWaker(thisa, 1500));
	}

	public void HungarianAssignment(double fArray[][]) {

		// getCalleMethod ();
		System.out.println(" *-*-*-*-*- HUNGARIAN *-*-*-*-*-*-*-");
		System.out.println(" *-*-*-*-*- HUNGARIAN *-*-*-*-*-*-*-");
		System.out.println(" *-*-*-*-*- HUNGARIAN *-*-*-*-*-*-*-");
		System.out.println(" *-*-*-*-*- HUNGARIAN *-*-*-*-*-*-*-");

		int assignments[][] = new int[robotCount][2];
		int assigned[] = new int[100];
		int haassigned[] = new int[100];
		for (int i = 0; i < 100; i++) {
			assigned[i] = -1;
		}

		for (int k = 0; k < robotCapacity; k++) {
			for (int i = 0; i < 100; i++) {
				haassigned[i] = -1;
			}
			HungarianAlgorithm ha = new HungarianAlgorithm(fArray);
			assignments = ha.HungarianStart();

			// ResetProcessingTasks();
			haassigned = AssignTasksToRobots(assignments, fArray, assigned, ha);

			for (int i = 0; i < 100; i++) {
				if (haassigned[i] == 1) {
					assigned[i] = 1;
					for (int j = 0; j < robotCount; j++) {
						fArray[j][i] = 0;
					}
				}
			}
		}

		addBehaviour(new TradeStarterWaker(thisa, 2000));

	}

	public class TaskAllocation extends OneShotBehaviour {

		private static final long serialVersionUID = 1L;

		public void action() {
			// getCalleMethod ();
			Yaz(myName + "> TASK ALLOCATION Start");
			double fArray[][] = PrepareArray();
			ShowProposals(fArray);

			if (IsPropsCompleted()) {

				int assignments[][] = new int[100][100];
				Yaz(myName + "> TASK ALLOCATION Teklifler tam, atamaya geçiliyor ..." + initTaskAssgnmt);

				if (initTaskAssgnmt == eInitialTaskAssignment.GREEDY) {

					// GreedyAssignment_taskfirst(fArray);
					GreedyAssignment ga = new GreedyAssignment();
					assignments = ga.GreedyAssignment_yFirst(fArray, robotCapacity, robotCount, taskBundleCount);
					for (int i = 0; i < 100; i++) {
						for (int j = 0; j < 100; j++)
							if (assignments[i][j] == 1) {
								AssignATaskMessage(i, j);
							}
					}
					addBehaviour(new TradeStarterWaker(thisa, 1500));

				}

				else if (initTaskAssgnmt == eInitialTaskAssignment.OPTIMAL) {

					HungarianAssignment(fArray);

				}

				long timex = Calendar.getInstance().getTimeInMillis();
				iaTime += (timex - timeAuctionStart);
				timeTradeStart = timex;
				SendDurationMessage();

				ResetProposals();
				Yaz(" AFTER TaskAllocation");

			} // PROPS COMPLETED
			else {
				Yaz(myName + "> TASK ALLOCATION : Teklifler eksik ! ");
			}
		} // ACTION
	}

	public class MyWaker extends WakerBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyWaker(Agent a, long interval) {
			super(a, interval);
		}

		protected void handleElapsedTimeout() {

			addBehaviour(new StartAnAuctionOnce());

		}
	}

	public class ScheduleCheckerWaker extends WakerBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ScheduleCheckerWaker(Agent a, long interval) {
			super(a, interval);
		}

		protected void handleElapsedTimeout() {

			CheckRobotSchedules();

		}
	}

	public class ResetWaker extends WakerBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ResetWaker(Agent a, long interval) {
			super(a, interval);
		}

		protected void handleElapsedTimeout() {

			String rcd[] = { terrain };

			String mcd[] = { "136" };
			SendACLMessage(rcd, 1, mcd, 1);

		}
	}

	public class TradeStarterWaker extends WakerBehaviour {

		private static final long serialVersionUID = 1L;

		public TradeStarterWaker(Agent a, long interval) {
			super(a, interval);
		}

		protected void handleElapsedTimeout() {

			InitializationofTradingMessage(0);

		}
	}

	public class EvaluatePathLenghts extends WakerBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1270389378712645309L;

		public EvaluatePathLenghts(Agent a, long interval) {
			super(a, interval);
		}

		protected void handleElapsedTimeout() {

			DefineTrader();

		}
	}

	public boolean AllRobotsWaiting() {
		int waiting = 0;

		// getCalleMethod ();
		for (int i = 0; i < robotCount; i++) {
			if (robots[i].status == eRobotStatus.IDLE)
				waiting++;
		}
		System.out.println("waiting : " + waiting + " EXECUTION_PHASE " + EXECUTION_PHASE);
		if (waiting == robotCount && (EXECUTION_PHASE == eExecutionPhase.SCHEDULING_EXECUTING
				|| EXECUTION_PHASE == eExecutionPhase.CENTRALIZED_ALLOCATIONS))
			return true;
		return false;
	}

	public class StartAnAuctionT extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3225434026157830637L;

		public StartAnAuctionT(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {

			if (AllRobotsWaiting() == true) {
				EXECUTION_PHASE = eExecutionPhase.CENTRALIZED_ALLOCATIONS;
				SendEXECUTIONPHASE();
				addBehaviour(new StartAnAuctionOnce());

			} else {
				System.out.println(
						myName + "> MUZAYEDE YAPILAMIYOR, ÇÜNKÜ BEKLETEN ROBOTLAR VAR !...  " + EXECUTION_PHASE);
				for (int i = 0; i < robotCount; i++) {
					Yaz(myName + "> " + robots[i].robotName + " - " + robots[i].status);
				}

			}
		}
	}

	public class ExecutionTimeCalculator extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7977045996912772105L;

		public ExecutionTimeCalculator(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {

			long actualExecution = executionTickCount;
			for (int i = 0; i < robotCount; i++) {
				if (robots[i].status == eRobotStatus.TRAVERSING) {

					if (actualExecution == executionTickCount) {
						executionTickCount++;

						SendDurationMessage();

					}
					break;
				}

			}

		}
	}

	public class ScheduleTasksFromFile extends TickerBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ScheduleTasksFromFile(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {
			long timex = Calendar.getInstance().getTimeInMillis();
			long diff = timex - time0;

			boolean changeFlag = false;
			for (int i = 0; i < taskBundleCount; i++) {
				TaskBundle tb = taskBundles[i];
				// Yaz("Diff : "+diff+" crTime : "+tb.GetTask(0).creationTime);
				if (tb.state == eTaskState.NOT_ASSIGNED && tb.GetTask(0).creationTime < (int) (diff)) {
					Yaz(tb.GetTask(0).taskName + " state 0 ");
					taskBundles[i].state = eTaskState.NOT_STARTED;
					for (int j = 0; j < tb.taskCount; j++) {
						Task t = tb.GetTask(j);
						t.state = eTaskState.NOT_STARTED;
						taskBundles[i].SetTask(j, t);
						SendTaskInformationToTerrain(t);

					}
					changeFlag = true;
				}

			}

			if (changeFlag)
				addBehaviour(new StartAnAuctionOnce());
		}
	}

	public class GenerateTasksFromFile extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {
			/*
			 * FileReader fr = new FileReader(); int tCount =
			 * fr.ReadTasks(templateSelect); for (int i=0; i<tCount; i++) {
			 * taskBundles [i] = new TaskBundle();
			 * taskBundles[i].AddTask(fr.tasks[i]);
			 * 
			 * } taskBundleCount = tCount; Yaz("generate : " +
			 * Integer.toString(taskBundleCount));
			 */
			// FileReader fr1 = new FileReader();
			// fr1.ReadTasks(templateSelect);
			TaskReader tr = new TaskReader(templateSelect);
			System.out.println("task count :" + tr.TaskCount());
			System.out.println("taskbundlecount :" + taskBundleCount);

			for (int i = 0; i < taskBundleCount; i++) {
				taskBundles[i] = new TaskBundle();
				taskBundles[i].state = eTaskState.NOT_ASSIGNED;
				taskBundles[i].AddTask(tr.GetTask(i));

			}

			// assignmentFlag = true;
			Yaz(myName + "> From file - assignment = true ");
			// taskBundleCount = tCount;
			Yaz("generate : " + Integer.toString(taskBundleCount));
		}
	}

	public class GenerateRandomTasks extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {

			for (int i = 0; i < taskBundleCount; i++) {

				taskBundles[i] = new TaskBundle();
				Task t = new Task(randomGenerator.nextInt(TERRAIN_WIDTH - 10),
						randomGenerator.nextInt(TERRAIN_HEIGHT - 10), myName + "T" + Integer.toString(i + 1));
				taskBundles[i].AddTask(t);
			}

		}
	}

	public class TaskClustering extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {

			int NeighborhoodCondition = 20;

			// Transferring closer task between bundles
			for (int i = 0; i < taskBundleCount - 1; i++) {
				TaskBundle tbi = taskBundles[i];
				for (int j = 0; j < tbi.taskCount; j++) {
					Task tj = tbi.GetTask(j);

					for (int ii = i + 1; ii < taskBundleCount; ii++) {

						TaskBundle tbii = taskBundles[ii];
						for (int jj = 0; jj < tbii.taskCount; jj++) {
							Task tjj = tbii.GetTask(jj);
							int dist = (int) CalcDistance(tj, tjj);

							if (dist <= NeighborhoodCondition) {
								tjj.bundleHead = tj.bundleHead;
								taskBundles[i].AddTask(tjj);
								taskBundles[ii].RemoveTask(tjj);

							}

						}

					}

				}
			}

			// Detecting empty task bundles
			int removalList[] = new int[100];
			int ct = 0;
			for (int i = 0; i < taskBundleCount; i++) {
				TaskBundle tbi = taskBundles[i];
				if (tbi.taskCount == 0) {
					removalList[ct++] = i;
				}
			}

			// Eliminating empty task bundles
			while (ct > 0) {
				for (int i = removalList[ct - 1]; i < taskBundleCount - 1; i++) {
					taskBundles[i] = taskBundles[i + 1];
				}
				taskBundleCount--;
				ct--;

			}

		}

	}

	public boolean MissionCompleted() {
		for (int i = 0; i < taskBundleCount; i++)

		{
			for (int j = 0; j < taskBundles[i].taskCount; j++)
				if (taskBundles[i].GetTask(j).state != eTaskState.COMPLETED)
					return false;
		}
		return true;
	}

	public void PrepareRobotReceivers() {
		rc1 = new String[robotCount];
		for (int i = 0; i < robotCount; i++) {

			rc1[i] = robots[i].robotName;

		}

	}

	public boolean ReadyForAuction() {
		return assignmentFlag && SIM_STATE == eSimState.STARTED
				&& (EXECUTION_PHASE == eExecutionPhase.SCHEDULING_EXECUTING
						|| EXECUTION_PHASE == eExecutionPhase.CENTRALIZED_ALLOCATIONS);
	}

	public void ResetDecision() {
		if (MissionCompleted() && autoMode == 1) {
			addBehaviour(new ResetWaker(thisa, 10000));
		}

	}

	public RobotSet RobotControl(TaskBundle tsk, int taskIndex) {
		RobotSet rs = new RobotSet(robotCount);
		for (int j = 0; j < robotCount; j++) {
			if (robots[j].status == eRobotStatus.IDLE || robots[j].status == eRobotStatus.AUCTIONING) {

				rs.SetRobotName(robots[j].robotName);
				robots[j].status = eRobotStatus.AUCTIONING;

			} else {
				proOK[j][taskIndex] = true;
			}
		}
		return rs;
	}

	public boolean SendAnnouncement(RobotSet rs, Task t) {

		boolean returnVal = false;
		if (rs.GetRobotCount() > 0) {

			String mc[] = { "00", t.taskName, Integer.toString(t.xLoc), Integer.toString(t.yLoc), t.bundleHead, "1" };
			SendACLMessage(rs.GetRobotList(), rs.GetRobotCount(), mc, 6);
			System.out.print(myName + "> " + t.taskName + " gorevi " + Integer.toString(rs.GetRobotCount())
					+ " adet robota gonderildi.");
			PrintWallWatch();
			returnVal = true;

		}
		return returnVal;
		/*
		 * boolean returnVal = false; for (int i=0; i< robotCount; i++) {
		 * 
		 * 
		 * //if (robots[i].status==eRobotStatus.IDLE) { String mc[] = { "00",
		 * t.taskName,Integer.toString(t.xLoc),Integer.toString(t.yLoc),
		 * t.bundleHead, "1" }; // SendACLMessage(rs.GetRobotList(),
		 * rs.GetRobotCount() , mc, 6); SendACLMessage(robots[i].robotName, mc,
		 * 6);
		 * 
		 * PrintWallWatch(); // robots[i].status=eRobotStatus.WORKING; }
		 * returnVal = true; } if (returnVal) System.out.print(myName+"> " +
		 * t.taskName+ " gorevi " + Integer.toString(rs.GetRobotCount() )+
		 * " adet robota gonderildi."); else System.out.print(myName+"> " +
		 * t.taskName+ " gorevini gönderecek boþta robo bulunamadý!  ");
		 * 
		 * return returnVal;
		 */
	}

	public void AuctionStarted(boolean messSent) {
		if (messSent) {
			AUCTIONS_MADE++;
			EXECUTION_PHASE = eExecutionPhase.CENTRALIZED_ALLOCATIONS;
			SendEXECUTIONPHASE();
			EndOfTourMessage();
		}
	}

	public void BidsOKForTask(int ti) {
		for (int j = 0; j < robotCount; j++) {
			proOK[j][ti] = true;
		}
	}
	
	public class NewBornRobotStart extends OneShotBehaviour {
		/**
		 * 
		 */
		

		public void action() {
			StartExecution();
		}
		
	}

	// ****************** INNER CLASS *******************************
	public class StartAnAuctionOnce extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {

			SearchForRobots();
			timeAuctionStart = Calendar.getInstance().getTimeInMillis();

			Yaz("**********************************");
			Yaz("**********************************");
			Yaz(myName + "> START AN AUCTION ONCE");
			Yaz(myName + "> START AN AUCTION ONCE");
			Yaz("********************************** SIM_STATE  : " + SIM_STATE);
			Yaz("********************************** assignment  : " + assignmentFlag);
			Yaz("********************************** EXECUTION_PHASE : " + EXECUTION_PHASE);
			boolean messSent = false;
			if (ReadyForAuction()) {
				ResetDecision();
				ResetProposals();
				ListTasks();
				for (int i = 0; i < taskBundleCount; i++) {

					TaskBundle tsk = taskBundles[i];
					if (tsk.state == eTaskState.NOT_STARTED && tsk.taskCount > 0) {
						Yaz(myName + "> NAME : " + tsk.GetTask(0).taskName + " State : " + tsk.state);
						messSent = SendAnnouncement(RobotControl(tsk, i), tsk.GetTask(0));
					} // task state
					else {
						BidsOKForTask(i);
					}
				}

				AuctionStarted(messSent);

			}

			// if assignment

			Yaz(" AFTER StartAnAuctionOnce");
			// ListTasks ();

		}// action
	}

	public class InformTasksOnce extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3705827485742095300L;

		public void action() {

			// getCalleMethod ();
			Yaz(Integer.toString(taskBundleCount));
			for (int i = 0; i < taskBundleCount; i++) {
				TaskBundle tsk = taskBundles[i];
				Yaz(Integer.toString(tsk.taskCount()));
				for (int j = 0; j < tsk.taskCount(); j++) {
					Task t = tsk.GetTask(j);
					t.state = tsk.state;
					SendTaskInformationToTerrain(t);
				}

			}

		}

	}

	public class InformTasks extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7173014039873837188L;

		public InformTasks(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {
			addBehaviour(new InformTasksOnce());

		}
	}

	// ****************** INNER CLASS *******************************

	public class ListRobots extends TickerBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5314268285735255765L;

		// public void action() {
		public ListRobots(Agent a, long interval) {
			super(a, interval);
		}

		protected void onTick() {

			if (SIM_STATE == eSimState.STARTED) {
				System.out.print("Sistemdeki robotlar     ");

				PrintWallWatch();
				PrintBlackBoard();
			}

		}

	}

	public void AffirmativeReplyCame(String temp[]) {
		// Görev kabul edildi
		int rIndex = RobotIndex(temp[1]);
		int tIndex = TaskBundleIndex(temp[2]);
		if (rIndex > -1 && tIndex > -1) {

			robots[rIndex].status = eRobotStatus.ASSIGNED;

			taskBundles[tIndex].owner = rIndex;
			taskBundles[tIndex].state = eTaskState.CONFIRMED;

			for (int i = 0; i < taskBundles[tIndex].taskCount; i++) {
				Task t = taskBundles[tIndex].GetTask(i);
				t.owner = rIndex;
				t.state = eTaskState.CONFIRMED;
				SendTaskInformationToTerrain(t);
				taskBundles[tIndex].SetTask(i, t);

			}

		}
	}

	// Görev kabul edilmediðinde
	public void NegativeReplyCame(String temp[]) {
		int rIndex = RobotIndex(temp[1]);
		int tIndex = TaskBundleIndex(temp[2]);
		if (rIndex > -1 && tIndex > -1) {

			taskBundles[tIndex].owner = -1;
			taskBundles[tIndex].state = eTaskState.NOT_STARTED;
			for (int i = 0; i < taskBundles[tIndex].taskCount; i++) {
				Task t = taskBundles[tIndex].GetTask(i);
				t.owner = -1;
				t.state = eTaskState.NOT_STARTED;
				SendTaskInformationToTerrain(t);
				taskBundles[tIndex].SetTask(i, t);

			}

		}
		// assignmentFlag = true;
		Yaz(myName + "> 04NR - assignment = true ");
	}

	public void BidCame(String temp[]) {
		// temp1 robotname
		// temp2 taskName
		// temp3 affirmative/refused
		// temp4 proposal
		int robotIndex = RobotIndex(temp[1]);
		int taskBundleIndex = TaskBundleIndex(temp[2]);
		Yaz(myName + "> BID CAME " + temp[1] + " " + temp[2] + " " + temp[3] + " " + temp[4] + " " + robotIndex + " "
				+ taskBundleIndex);
		if (robotIndex >= 0 && taskBundleIndex >= 0) {
			proOK[robotIndex][taskBundleIndex] = true;
			proposals[robotIndex][taskBundleIndex] = PROPOSAL_DEFAULT;

			if (temp[3].compareTo("affirmative") == 0) {
				int proposal = Integer.parseInt(temp[4]);

				if (proposal >= 0) {
					proposals[robotIndex][taskBundleIndex] = proposal;
					Yaz(myName + "> " + temp[1] + " " + temp[2] + " icin teklif verdi  :   " + temp[4] + "  ");
				}

			} //
			else {
				Yaz(myName + "> " + temp[1] + " robotu " + temp[2] + " gorevi icin teklif vermeyi reddetti !  ");
			}
		} else {
			Yaz(myName + "> " + temp[1] + " robotu ya da  " + temp[2]
					+ " gorevi  **************  T A N I M S I Z  *******!  ");
		}

		// Mesaj gereksiz olduðu için göndermekten vazgeçildi.
		// repmsg.setContent(replyMessage);
		// send(repmsg);
		// double fArray[][] = PrepareArray();
		// ShowProposals(fArray);

		// Geçici olarak kapatýldý
		addBehaviour(new TaskAllocation());
		// addBehaviour( new TaskAllocationWaker(thisa, 10));
	}

	//////////////////////////////////////////////////////////////////////////////
	public void TaskStateUpdate(String temp[]) {
		//
		int rrIndex = RobotIndex(temp[1]);
		int ttBundleIndex = TaskBundleIndex(temp[2]);
		Yaz("66 geldi, task state : " + temp[1] + " " + temp[2] + " " + temp[3]);
		if (rrIndex > -1) {
			if (ttBundleIndex > -1) {

				Yaz(myName + ">" + temp[1] + "robotu " + temp[2] + "gorevine devam ediyor...");
				PrintWallWatch();

				for (int i = 0; i < taskBundles[ttBundleIndex].taskCount(); i++) {
					Task t = taskBundles[ttBundleIndex].GetTask(i);

					t.state = eTaskState.values()[Integer.parseInt(temp[3])];
					SendTaskInformationToTerrain(t);
				}
				// }

			}

		} else {
			Yaz("Robot müzayedecide kayýtlý deðil !!! ");
		}
	}

	////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////

	// ****************** INNER CLASS *******************************

	public void SendDurationMessage() {
		String rcd[] = { terrain };
		String mcd[] = { "118", Integer.toString((int) (iaTime)), Integer.toString((int) (tTime)),
				Integer.toString((int) (schTime)), Integer.toString((int) (executionTickCount)) };
		SendACLMessage(rcd, 1, mcd, 5);
		// ResetScheduleSituations() ;
	}

	public void ResetScheduleSituations() {
		for (int i = 0; i < robotCount; i++) {
			robots[i].scheduleCreated = false;
		}
	}

	public void CheckRobotExecutions() {

		for (int i = 0; i < robotCount; i++) {
			if (robots[i].atTheStation == false) {
				Yaz(myName + "> Ýstasyona ulaþmamýþ bir robot var : " + robots[i].robotName);

				return;
			}

		}

		StartExecution();

	}

	public void CheckRobotSchedules() {

		for (int i = 0; i < robotCount; i++) {

			if (robots[i].scheduleCreated == false) {
				Yaz(myName + "> Çizelgesini yapamamýþ bir robot var : " + robots[i].robotName);
				SendScheduleMessage(robots[i].robotName);
				// robots [i].scheduleCreated = true;

				return;
			}
			/*
			 * else { robots[i].status = 1; }
			 */

		}

		StartExecution();

	}

	public void SchedulingCompletedMessageArrived(String temp[]) {
		Yaz(myName + "> 69 mesajý geldi, bir robot çizelgelemeyi tamamladý : " + temp[1]);
		int rIndex = RobotIndex(temp[1]);
		if (rIndex > -1) {
			robots[rIndex].scheduleCreated = true;

			long timex = Calendar.getInstance().getTimeInMillis();

			// Follow Me'de çizelgelemenin manüel olarak doðru yapýldýðý ve 10
			// saniyede bittiði varsayýlýyor.
			if (expMode == eServiceArea.ALL_TERRAIN) {
				robots[rIndex].schTime += 10000;
			} else {
				robots[rIndex].schTime += (int) (timex - timeSchedulingStart);
			}
			schTime += robots[rIndex].schTime;
			// timeSchedulingStart = timex;
			SendDurationMessage();

			CheckRobotSchedules();
			// addBehaviour (new ScheduleCheckerWaker(thisa, 500));

		}

	}

	public class EvaluateMessages extends TickerBehaviour {

		/**
		 * 
		 */
		public class Handler {
			public void handle(String temp[]) {
				Yaz("Handler super...");
			};
		}

		public void CreateMap1() throws Exception {

			handlerMap.put("01", new CAddUpdateRobot());
			handlerMap.put("04AR", new CAffirmativeReplyCame());
			handlerMap.put("04NR", new CNegativeReplyCame());
			handlerMap.put("02", new CBidCame());
			handlerMap.put("06", new CTaskCompleted());
			handlerMap.put("66", new CTaskStateUpdate());
			handlerMap.put("69", new CSchedulingCompletedMessageArrived());
			handlerMap.put("81", new CAddRunTimeTask());
			handlerMap.put("111", new CPathLengthArrived());
			handlerMap.put("113", new CEndOfTradeBySellerRobot());
			handlerMap.put("117", new CEndOfTradeBySellerRobot());
			handlerMap.put("61", new CStartSimulation());
			handlerMap.put("0002", new CMakeRandomAssignments());
			handlerMap.put("0001", new CRandomAssignmentMade());
			handlerMap.put("191", new CReset());
			handlerMap.put("62", new CSetSimState2());
			handlerMap.put("63", new CSetSimState3());

		}

		private static final long serialVersionUID = -1372996987729114754L;

		private class CAddUpdateRobot extends Handler {
			public void handle(String temp[]) {
				AddUpdateRobot(temp);
			}
		}

		private class CAffirmativeReplyCame extends Handler {
			public void handle(String temp[]) {
				AffirmativeReplyCame(temp);
			}
		}

		private class CNegativeReplyCame extends Handler {
			public void handle(String temp[]) {
				NegativeReplyCame(temp);
			}
		}

		private class CBidCame extends Handler {
			public void handle(String temp[]) {
				BidCame(temp);
			}
		}

		private class CTaskCompleted extends Handler {
			public void handle(String temp[]) {
				TaskCompleted(temp);
			}
		}

		private class CTaskStateUpdate extends Handler {
			public void handle(String temp[]) {
				TaskStateUpdate(temp);
			}
		}

		private class CSchedulingCompletedMessageArrived extends Handler {
			public void handle(String temp[]) {
				SchedulingCompletedMessageArrived(temp);
			}
		}

		private class CAddRunTimeTask extends Handler {
			public void handle(String temp[]) {
				AddRunTimeTask(temp);
			}
		}

		private class CPathLengthArrived extends Handler {
			public void handle(String temp[]) {
				PathLengthArrived(temp);
			}
		}

		private class CEndOfTradeBySellerRobot extends Handler {
			public void handle(String temp[]) {
				EndOfTradeBySellerRobot(temp);
			}
		}

		private class CStartSimulation extends Handler {
			public void handle(String temp[]) {
				StartSimulation(temp);
			}
		}

		private class CMakeRandomAssignments extends Handler {
			public void handle(String temp[]) {
				MakeRandomAssignments(temp);
			}
		}

		private class CRandomAssignmentMade extends Handler {
			public void handle(String temp[]) {
				RandomAssignmentMade(temp);
			}
		}

		private class CReset extends Handler {
			public void handle(String temp[]) {
				Reset(temp);
			}
		}

		private class CSetSimState2 extends Handler {
			public void handle(String temp[]) {
				SetSimState2(temp);
			}
		}

		private class CSetSimState3 extends Handler {
			public void handle(String temp[]) {
				SetSimState3(temp);
			}
		}

		public void MessageParse3(String temp[]) {

			if (messageMapFlag == false)
				try {
					CreateMap1();
					messageMapFlag = true;
				} catch (Exception e) {

				}
			// Yaz (msg.toString()) ;
			// Yaz(temp[0]+" "+ temp[1]+" "+temp[2]+" "+temp[3]+" "+temp[4]+" "+
			// temp[5]+" "+temp[6]+" "+temp[7]+" "+temp[8]+" "+temp[9]);

			/*
			 * Set set = handlerMap.entrySet(); // Get an iterator Iterator i =
			 * set.iterator(); while(i.hasNext()) { Map.Entry me =
			 * (Map.Entry)i.next(); System.out.print(me.getKey() + ": ");
			 * Yaz(me.getValue()); }
			 */

			Object obj = handlerMap.get(temp[0]);
			if (obj != null) {
				// Yaz (obj.toString());
				Handler h = (Handler) obj;
				// Yaz (h.toString());
				h.handle(temp);
			}
		}

		public EvaluateMessages(Agent a, long interval) {
			super(a, interval);
		}

		public void MessageParse(String temp[], ACLMessage repmsg) {
			if (temp[0].compareTo("01") == 0) {

				AddUpdateRobot(temp);
			}
			// Görev kabul edildiðinde
			else if (temp[0].compareTo("04AR") == 0) {

				AffirmativeReplyCame(temp);

			}
			// Görev Kabul Edilmediðinde
			else if (temp[0].compareTo("04NR") == 0) {

				Yaz(myName + "> 04NR - robot : " + temp[1] + " task : " + temp[2]);
				NegativeReplyCame(temp);
			}

			// Müzayedeciye teklif geldiðinde
			else if (temp[0].compareTo("02") == 0) {

				// Yaz(myName+"> Bid Came");
				BidCame(temp);

			} // end of compare 02

			/*
			 * Bir robot gorevi hakkýnda durum bilgisi veriyor temp0 kod temp1
			 * robotName temp2 taskName temp3 state
			 */
			else if (temp[0].compareTo("06") == 0) {

				TaskCompleted(temp);

			}
			// Robot Request for Updating Task State
			else if (temp[0].compareTo("66") == 0) {

				TaskStateUpdate(temp);
			} // end of 66
				// 69 : robot çizelge hesabýný tamamladýðýnda
			else if (temp[0].compareTo("69") == 0) {

				SchedulingCompletedMessageArrived(temp);
			} // end of 66

			// 81: Yeni task
			else if (temp[0].compareTo("81") == 0) {

				AddRunTimeTask(temp);

			} // end of 81

			else if (temp[0].compareTo("111") == 0) {

				PathLengthArrived(temp);
			} else if (temp[0].compareTo("113") == 0) {

				EndOfTradeBySellerRobot(temp);
			} else if (temp[0].compareTo("117") == 0) {

				EndOfTradeBySellerRobot(temp);
			}

			// SIM BASLAT
			else if (temp[0].compareTo("61") == 0) {

				StartSimulation(temp);
			}

			// Random atama kabul edilmezse (eriþim yoksa)
			else if (temp[0].compareTo("0002") == 0) {

				MakeRandomAssignments(temp);
			}

			// Random atama kabul edilirse
			else if (temp[0].compareTo("0001") == 0) {

				RandomAssignmentMade(temp);
			}

			else if (temp[0].compareTo("191") == 0) {

				Reset(temp);
			}
			// SIM DURAKLAT
			else if (temp[0].compareTo("62") == 0) {

				SetSimState2(temp);
			}
			// SIM DURDUR
			else if (temp[0].compareTo("63") == 0) {

				SetSimState3(temp);
			}
			//

			else {
				Yaz("Tanýmlanamayan mesaj " + msg.getContent());
			}

			/*
			 * if (p!= null) { p.println(msg.toString());
			 * 
			 * } try { outw.write(msg.toString()); } catch (IOException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
			// messageBuff[messCount++] = msg.getContent();

		}

		public void Reset(String temp[]) {
			doDelete();
		}

		public void SetSimState2(String temp[]) {
			SIM_STATE = eSimState.PAUSED;
		}

		public void SetSimState3(String temp[]) {
			SIM_STATE = eSimState.STOPPED;
		}

		protected void onTick() {

			ACLMessage msg = receive();

			if (msg != null) {

				// Mesajýn baþlýðýna göre sýnýflandýrma
				String content = msg.getContent();
				String delimiter = "_";
				String[] temp;
				temp = content.split(delimiter);
				String[] temp1 = new String[10];
				temp = content.split(delimiter);
				for (int i = 0; i < temp.length; i++)
					temp1[i] = temp[i];
				for (int i = temp.length; i < 10; i++)
					temp1[i] = " * ";

				// Yaz ("Gelen Mesaj : "+ temp[0]);
				try {
					MessageParse3(temp1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * Robot tanýmlama mesajý ise 01_robotName_locX_locY
				 */

			} // END OF IF MSG != NULL
			else {
				block();
			}

			// } // sým state ==1

		} // END OF ON action

	} // END OF BEHAVIOUR CLASS EVALUATE MESSAGES

} // END OF AGENT
