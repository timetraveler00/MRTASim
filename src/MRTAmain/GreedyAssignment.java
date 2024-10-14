package MRTAmain;



public class GreedyAssignment {

	int haassignments[][] = new int[100][100];
	public int[][] GreedyAssignment_yFirst (double fArray[][], int tourCount, int xCount, int yCount) 
	{
		// ResetProcessingTasks();
		/*Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");*/
		// ShowProposals(fArray);
		// IterativeGreedyAssignment(fArray);
		int assigned[] = new int[100];
		int assignment[][] = new int[100][100];
		for (int i = 0; i < 100; i++)
		{
			assigned[i] = -1;
			for (int j=0;j<100;j++)
			  assignment [i][j] = -1; 
		
		}	// int i=0;
		int notAssigned = 0;
		//do {
		for (int k=0; k<tourCount; k++)	
		{
			for (int j = 0; j < yCount; j++) {

				if (assigned[j] < 1)
					notAssigned++;
			}

			if (notAssigned >= xCount) {
				for (int j = 0; j < yCount; j++) {
					// i= randomGenerator.nextInt(robotCount);
					double pMax = 0.0;
					int pIndex = -1;

					
					for (int i = 0; i < xCount; i++) {
						if (fArray[i][j] > pMax
								&& assigned[j] < 1
								&& fArray[i][j] < 10000) 
						{
							pMax = fArray[i][j];
							pIndex = i;
						}

					}
					if (pIndex > -1) {

						assigned[j] = 1;
						// ResetProcessingTasks();
						//AssignATaskMessage(pIndex, j);
						assignment[pIndex][j] = 1; 
					}
            	}

			} 
			
			else // notassigned < robotcount
			{

				for (int i = 0; i < xCount; i++) 
				{		double pMax = 0.0;
					int pIndex = -1;
				
					for (int j = 0; j < yCount; j++)

					{

						if (fArray[i][j] > pMax && assigned[j] < 1) 
						{
							pMax = fArray[i][j];
							pIndex = j;
						}
					}

					if (pIndex > -1) {

						assigned[pIndex] = 1;
						// ResetProcessingTasks();
						//AssignATaskMessage(i, pIndex);
						assignment[i][pIndex] = 1; 
					}

				}

			}

		}	
	
		//} while (notAssigned>0); 
		

		return assignment; 
	}
	
	
	
	public int[][] GreedyAssignment_xFirst (double fArray[][], int tourCount, int xCount, int yCount) 
	{
		// ResetProcessingTasks();
		/*Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");
		Yaz(" *-*-*-*-*- GREEDY *-*-*-*-*-*-*-");*/
		// ShowProposals(fArray);
		// IterativeGreedyAssignment(fArray);
		int assigned_col[] = new int[100];
		int assigned_row[] = new int[100];
		// int i=0;
		
		int assignment[][] = new int[100][100];
		for (int i = 0; i < 100; i++)
		{
			assigned_col[i] = -1;
			assigned_row[i] = -1;
			for (int j=0;j<100;j++)
			  assignment [i][j] = -1; 
		
		}	// int i=0;
		int notAssigned = 0;
		//do {
		for (int k=0; k<tourCount; k++)	
		{
			for (int j = 0; j < yCount; j++) {

				if (assigned_col[j] < 1)
					notAssigned++;
			}

			if (notAssigned >= xCount) {
				for (int i = 0; i < xCount; i++) {
					// i= randomGenerator.nextInt(robotCount);
					double pMax = 0.0;
					int pIndex = -1;

					for (int j = 0; j < yCount; j++) {

						if (fArray[i][j] > pMax
								&& assigned_col[j] < 1 && assigned_row[i] <1
								&& fArray[i][j] < 10000) 
						{
							pMax = fArray[i][j];
							pIndex = j;
						}

					}
					if (pIndex > -1) {

						assigned_col [pIndex] = 1;
						assigned_row[i] = 1;
						// ResetProcessingTasks();
						//AssignATaskMessage(i, pIndex);
						assignment[i][pIndex] = 1; 
					}
            	}

			} 
			
			else // notassigned < robotcount
			{

				for (int j = 0; j < yCount; j++) {

					double pMax = 0.0;
					int pIndex = -1;
					for (int i = 0; i < xCount; i++) 
					{

						if (fArray[i][j] > pMax && assigned_col[j] < 1 && assigned_row [i] <1 )  
						{
							pMax = fArray[i][j];
							pIndex = i;
						}
					}

					if (pIndex > -1) {

						assigned_col[j] = 1;
						assigned_row[pIndex] = 1; 
						// ResetProcessingTasks();
						// AssignATaskMessage(pIndex, j);
						assignment[pIndex][j] = 1; 
					}

				}

			}

		}	
	
		//} while (notAssigned>0); 
		
		return assignment; 
		
	}
	
	
	public int[][] HungarianAssignment (double fArray[][], int tourCount,  int xCount, int yCount) 
	{
		/*System.out
		.println(" *-*-*-*-*- HUNGARIAN *-*-*-*-*-*-*-");
System.out
		.println(" *-*-*-*-*- HUNGARIAN *-*-*-*-*-*-*-");
System.out
		.println(" *-*-*-*-*- HUNGARIAN *-*-*-*-*-*-*-");
System.out
		.println(" *-*-*-*-*- HUNGARIAN *-*-*-*-*-*-*-");*/
int aSize = xCount>yCount ? xCount:yCount; 
int assignments[][] = new int[aSize][2];
int assigned[] = new int[100];
int haassigned[] = new int[100];
for (int i = 0; i < 100; i++) {
	assigned[i] = -1;
}


for (int k = 0; k < tourCount; k++) {
	for (int i = 0; i < 100; i++) {
		haassigned[i] = -1;
	}
	HungarianAlgorithm ha = new HungarianAlgorithm(fArray);
	assignments = ha.HungarianStart();

	//ResetProcessingTasks();
	return AssignTasksToRobots(assignments, fArray, assigned, ha, xCount, yCount);

/*	for (int i = 0; i < 100; i++) {
		if (haassigned[i] == 1) {
			assigned[i] = 1;
			for (int j = 0; j < xCount; j++) {
				fArray[j][i] = 0;
			}
		}
	}*/

	
}

    return haassignments; 
		
	}
	
	
	public int[][] AssignTasksToRobots(int assignmentMatrix[][], double finalArray[][], int[] preAss, HungarianAlgorithm ha, int xCount, int yCount) {

		int rIndex = -1;
		int tIndex = -1;
		int assigned[] = new int[100];
		for (int i = 0; i < 100; i++)
			assigned[i] = -1;
		if (xCount <= yCount) {
			for (int i = 0; i < xCount; i++) {

				rIndex = assignmentMatrix[i][0];
				tIndex = assignmentMatrix[i][1];
				if (preAss[tIndex] < 1  && ha.array[rIndex][tIndex]>0) {
					assigned[tIndex] = 1;
					// AssignATaskMessage(rIndex, tIndex);
					haassignments [rIndex][tIndex] = 1; 
				}
				
			}

		} else {
			for (int i = 0; i < yCount; i++) {
				rIndex = assignmentMatrix[i][1];
				tIndex = assignmentMatrix[i][0];

				if (preAss[tIndex] < 1  && ha.array[tIndex][rIndex]>0) {
					assigned[tIndex] = 1;
					//AssignATaskMessage(rIndex, tIndex);
					haassignments [rIndex][tIndex] = 1;
				}
				
			}

			

		}
	//	Yaz(" AFTER AssignTasksToRobots ()");
		return haassignments;

	}
}
