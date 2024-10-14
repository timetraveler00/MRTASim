package MRTAStreamManager;

public class TerrainDimensionsReader extends MRTAFileReader {
	
	
	public int getTERRAIN_WIDTH() {
		return TERRAIN_WIDTH;
	}
	public void setTERRAIN_WIDTH(int tERRAIN_WIDTH) {
		TERRAIN_WIDTH = tERRAIN_WIDTH;
	}
	public int getTERRAIN_HEIGHT() {
		return TERRAIN_HEIGHT;
	}
	public void setTERRAIN_HEIGHT(int tERRAIN_HEIGHT) {
		TERRAIN_HEIGHT = tERRAIN_HEIGHT;
	}
	public int getPhysicalWidth() {
		return physicalWidth;
	}
	public void setPhysicalWidth(int physicalWidth) {
		this.physicalWidth = physicalWidth;
	}
	
	private int TERRAIN_WIDTH = 400; 
	private int TERRAIN_HEIGHT = 300 ; 
	private int physicalWidth = 400 ;
	
	public TerrainDimensionsReader(String template) {
		super();
		delimiter ="_";
		folderPath = "\\config\\";
		fileName = "dimensions.txt"; 
		templateName = template; 
	
		FromFileToArray();
		setTERRAIN_WIDTH(Integer.parseInt(this.Get(0, 0)));
		setTERRAIN_HEIGHT(Integer.parseInt(this.Get(0, 1)));
		setPhysicalWidth(Integer.parseInt(this.Get(0, 2)));
	}
	
	

}
