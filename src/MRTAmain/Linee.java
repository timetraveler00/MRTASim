package MRTAmain;

public class Linee {
Positione start; 
Positione end; 

public Linee (Positione st, Positione en) 
{
start = st; 
end = en; 
}

public double length() {
	double x_fark = start.x - end.x;
	
	double y_fark = start.y - end.y;

	return Math.sqrt(x_fark * x_fark + y_fark * y_fark);
}

public double angle() 
{
	double tx = end.x;
	double ty = end.y;
	double rx = start.x;
	double ry = start.y;
	double angle;
	if (tx - rx == 0) {
		if (ty - ry > 0)
			angle = 90.0;

		else if (ry - ty > 0)
			angle = 180.0;

		else
			angle = 0.0;

		// angle= //atan (abs(task->GetY()-GetY())) * 180 / PI;
	} else
		angle = Math.atan(Math.abs(ty - ry) * 1.0
				/ Math.abs(tx - rx) * 1.0)
				* 180.0 / Math.PI;
	
	return angle; 
} 


}
