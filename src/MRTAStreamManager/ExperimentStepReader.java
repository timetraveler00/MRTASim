package MRTAStreamManager;

public class ExperimentStepReader extends MRTAFileReader{



	
	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	private int step = 0; 

	public ExperimentStepReader() {
		super();
		delimiter ="_";
		folderPath = "\\Experiments\\";
		fileName = "ExperimentCounter.txt"; 
		templateName = ""; 
		FromFileToArray();
		setStep(Integer.parseInt(Get(0,0)));
		
	}
	
	

}
