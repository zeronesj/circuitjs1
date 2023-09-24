package com.lushprojects.circuitjs1.client;

import com.lushprojects.circuitjs1.client.util.Locale;

// based on https://ctms.engin.umich.edu/CTMS/index.php?example=MotorPosition&section=SystemModeling


class ThreePhaseMotorElm extends CircuitElm {

    Inductor ind, indInertia;
    // Electrical parameters
    double resistance, inductance;
    // Electro-mechanical parameters
    double K, Kb, J, b, gearRatio, tau; //tau reserved for static friction parameterization  
    public double angle;
    public double speed;

    double coilCurrent;
    double inertiaCurrent;
    int[] voltSources = new int[2];
    Point posts[], leads[];
    
    public ThreePhaseMotorElm(int xx, int yy) { 
	super(xx, yy); 
	inductance = .5; resistance = 1; angle = pi/2; speed = 0; K = 0.15; b= 0.05; J = 0.02; Kb = 0.15; gearRatio=1; tau=0;

    }
    public ThreePhaseMotorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	angle = pi/2;speed = 0;
	//read:
	// inductance; resistance, K, Kb, J, b, gearRatio, tau
	inductance = new Double(st.nextToken()).doubleValue();
	resistance = new Double(st.nextToken()).doubleValue(); 
	K = 		new Double(st.nextToken()).doubleValue();
	Kb = 		new Double(st.nextToken()).doubleValue();
	J = 		new Double(st.nextToken()).doubleValue();
	b = 		new Double(st.nextToken()).doubleValue();
	gearRatio = new Double(st.nextToken()).doubleValue();
	tau = 		new Double(st.nextToken()).doubleValue();
    }
    int getDumpType() { return 427; }
    String dump() {
	// dump: inductance; resistance, K, Kb, J, b, gearRatio, tau
	return super.dump() + " " +  inductance + " " + resistance + " " + K + " " +  Kb + " " + J + " " + b + " " + gearRatio + " " + tau;
    }
    public double getAngle(){ return(angle);}

    Point motorCenter;

    void setPoints() {
	super.setPoints();
	posts = newPointArray(3);
	leads = newPointArray(3);
	posts[1] = point1;
	interpPoint2(point1, point2, posts[0], posts[2], 0, 32);
	interpPoint2(point1, point2, leads[0], leads[2], 16/dn, 32);
	interpPoint(point1, point2, leads[1], 16/dn);
	motorCenter = point2;
	allocNodes();
    }
    int getPostCount() { return 3; }
    Point getPost(int n) { return posts[n]; }
    int getInternalNodeCount() { return 4; }
    void reset() {
	super.reset();
    }

    final int phids_ind = 3;
    final int phidr_ind = 4;
    final int phiqs_ind = 5;
    final int phiqr_ind = 6;
    
    final double Rs = .435;
    final double Rr = .816;
    
    void stamp() {
	int i;
	int phids = nodes[phids_ind];
	int phidr = nodes[phidr_ind];
	int phiqs = nodes[phiqs_ind];
	int phiqr = nodes[phiqr_ind];
	
	// https://core.ac.uk/download/pdf/80147763.pdf
	// dphids/dt = vds - A phids + B phidr
	// expand dphids/dt as (phids(t+1)-phids(t))/dt
	// expand vds as sqrt(2/3) (va - .5 vb - .5 vc)
	double m23 = Math.sqrt(2/3.);
	double m32 = Math.sqrt(3)/2;
	double ts = 1/sim.timeStep;
	double Lr = 1;
	double Ls = 1;
	double Lm = .99;
	double wr = speed*3;

	double den = 1/(Ls*Lr-Lm*Lm);
	
	// stationary reference frame so we = 0
	sim.stampMatrix(phids, nodes[0], m23*ts);
	sim.stampMatrix(phids, nodes[1], -.5 * m23*ts);
	sim.stampMatrix(phids, nodes[2], -.5 * m23*ts);
	sim.stampMatrix(phids, phids, - ts * Rs*Lr*den);
	sim.stampMatrix(phids, phidr, ts*Rs*Lm*den);
	
//	sim.stampMatrix(phiqs, nodes[0], 0);
	sim.stampMatrix(phiqs, nodes[1], m23* m32 * ts);
	sim.stampMatrix(phiqs, nodes[2], m23* m32 * ts);
	sim.stampMatrix(phiqs, phiqs, - ts * Rs*Lr*den);
	sim.stampMatrix(phiqs, phiqr, ts*Rs*Lm*den);

	sim.stampMatrix(phidr, phidr, - ts* Rr*Ls*den);
	sim.stampMatrix(phidr, phids, ts * Rr*Lm*den);
	
	sim.stampMatrix(phiqr, phiqr, - ts * Rr*Ls*den);
	sim.stampMatrix(phiqr, phiqs, ts*Rr*Lm*den);

	// ia, ib, ic
	sim.stampMatrix(nodes[0], phids,  m23 * Lr*den);
	sim.stampMatrix(nodes[0], phidr, -m23 * Lm*den);
	sim.stampMatrix(nodes[1], phids, -.5*  m23 * Lr*den);
	sim.stampMatrix(nodes[1], phidr, -.5* -m23 * Lm*den);
	sim.stampMatrix(nodes[2], phids, -.5*  m23 * Lr*den);
	sim.stampMatrix(nodes[2], phidr, -.5* -m23 * Lm*den);
	
	sim.stampMatrix(nodes[1], phiqs, m32*  m23 * Lr*den);
	sim.stampMatrix(nodes[1], phiqr, m32* -m23 * Lm*den);
	sim.stampMatrix(nodes[2], phiqs, -m32*  m23 * Lr*den);
	sim.stampMatrix(nodes[2], phiqr, -m32* -m23 * Lm*den);
    }
    
    void startIteration() {
	sim.console("phids = " + volts[3] + ", phidr = " + volts[4]);
	
	double wr = speed*3; // 3=number of poles
	
	double ts = 1/sim.timeStep;
	double Lr = 1;
	double Ls = 1;
	double Lm = .99;

	double den = 1/(Ls*Lr-Lm*Lm);

	double phids = volts[phids_ind];
	double phiqs = volts[phiqs_ind];
	double ids = Lr * den * volts[phids_ind] - Lm * den * volts[phidr_ind];
	double iqs = Lr * den * volts[phiqs_ind] - Lm * den * volts[phiqr_ind];
	double idr = Ls * den * volts[phidr_ind] - Lm * den * volts[phids_ind];
	double iqr = Ls * den * volts[phiqr_ind] - Lm * den * volts[phiqs_ind];
	
	int poles = 3;
	double torque = 3*poles/4. * Lm * (iqs*idr-ids*iqr);
	double J = .089;
	speed += poles*torque*sim.timeStep/(2*J);
	sim.console("torque = " + torque + " speed = " + speed + " " + sim.t);
	if (Math.abs(torque) > 1e10)
	    sim.stop("bad torque", this);
//	sim.console("phids = " + phids + ", phiqs = " + phiqs + ", ids = " + ids + ", iqs = " + iqs + ", idr = " + idr + ", iqr = " + iqr);
    }

    boolean hasGroundConnection(int n1) {
	return false;
    }
    
    boolean getConnection(int n1, int n2) {
    	return true;
    }
    
    void doStep() {
	int phids = nodes[phids_ind];
	int phidr = nodes[phidr_ind];
	int phiqs = nodes[phiqs_ind];
	int phiqr = nodes[phiqr_ind];
	sim.stampRightSide(phidr, volts[phidr_ind]);
	sim.stampRightSide(phids, volts[phids_ind]);
	sim.stampRightSide(phiqs, volts[phiqs_ind]);
	sim.stampRightSide(phiqr, volts[phiqr_ind]);
	
	double ts = 1/sim.timeStep;
	double wr = speed*3;

	// time-varying part of matrix
	sim.stampMatrix(phidr_ind, phiqr_ind, -wr*ts);
	sim.stampMatrix(phiqr_ind, phidr_ind, -wr*ts);
    }
    
    void calculateCurrent() {
//	inds[0].calculateCurrent(volts[5]-volts[6]);
//	inds[1].calculateCurrent(volts[6]-volts[7]);
//	inds[2].calculateCurrent(volts[10]-volts[11]);
//	inds[3].calculateCurrent(volts[11]-volts[12]);
    }
//    public double getCurrent() { current = (volts[2]-volts[3])/resistance; return current; }

    void setCurrent(int vn, double c) {
	if (vn == voltSources[0])
	    current = c;
    }
    
    void draw(Graphics g) {

	int cr = 27;
	int hs = 8;
	setBbox(point1, point2, cr);
	
	int i;
	for (i = 0; i != 3; i++) {
	    setVoltageColor(g, volts[i]);
	    drawThickLine(g, posts[i], leads[i]);
	}
	
	//getCurrent();
	doDots(g);
	setPowerColor(g, true);
	Color cc = new Color((int) (165), (int) (165), (int) (165));
	g.setColor(cc);
	g.fillOval(motorCenter.x-(cr), motorCenter.y-(cr), (cr)*2, (cr)*2);
	cc = new Color((int) (10), (int) (10), (int) (10));

	g.setColor(cc);
	double angleAux = Math.round(angle*300.0)/300.0;
	g.fillOval(motorCenter.x-(int)(cr/2.2), motorCenter.y-(int)(cr/2.2), (int)(2*cr/2.2), (int)(2*cr/2.2));

	g.setColor(cc);
	double q = .28*1.7 * 36/dn;
	interpPointFix(point1, point2, ps1, 1 + q*Math.cos(angleAux*gearRatio), q*Math.sin(angleAux*gearRatio));
	interpPointFix(point1, point2, ps2, 1 - q*Math.cos(angleAux*gearRatio), -q*Math.sin(angleAux*gearRatio));

	drawThickerLine(g, ps1, ps2);
	interpPointFix(point1, point2, ps1, 1 + q*Math.cos(angleAux*gearRatio+pi/3), q*Math.sin(angleAux*gearRatio+pi/3));
	interpPointFix(point1, point2, ps2, 1 - q*Math.cos(angleAux*gearRatio+pi/3), -q*Math.sin(angleAux*gearRatio+pi/3));

	drawThickerLine(g, ps1, ps2);

	interpPointFix(point1, point2, ps1, 1 + q*Math.cos(angleAux*gearRatio+2*pi/3), q*Math.sin(angleAux*gearRatio+2*pi/3));
	interpPointFix(point1, point2, ps2, 1 - q*Math.cos(angleAux*gearRatio+2*pi/3), -q*Math.sin(angleAux*gearRatio+2*pi/3));

	drawThickerLine(g, ps1, ps2);

	drawPosts(g);
    }
    static void drawThickerLine(Graphics g, Point pa, Point pb) {
	g.setLineWidth(6.0);
	g.drawLine(pa.x, pa.y, pb.x, pb.y);
	g.setLineWidth(1.0);
    }

    void interpPointFix(Point a, Point b, Point c, double f, double g) {
	int gx = b.y-a.y;
	int gy = a.x-b.x;
	c.x = (int) Math.round(a.x*(1-f)+b.x*f+g*gx);
	c.y = (int) Math.round(a.y*(1-f)+b.y*f+g*gy);
    }


    void getInfo(String arr[]) {
	arr[0] = "3-Phase Motor";
	getBasicInfo(arr);
	arr[3] = Locale.LS("speed") + " = " + getUnitText(60*Math.abs(speed)/(2*Math.PI), Locale.LS("RPM"));
	arr[4] = "L = " + getUnitText(inductance, "H");
	arr[5] = "R = " + getUnitText(resistance, Locale.ohmString);
	arr[6] = "P = " + getUnitText(getPower(), "W");
    }
    public EditInfo getEditInfo(int n) {

	if (n == 0)
	    return new EditInfo("Armature inductance (H)", inductance, 0, 0);
	if (n == 1)
	    return new EditInfo("Armature Resistance (ohms)", resistance, 0, 0);
	if (n == 2)
	    return new EditInfo("Torque constant (Nm/A)", K, 0, 0);
	if (n == 3)
	    return new EditInfo("Back emf constant (Vs/rad)", Kb, 0, 0);
	if (n == 4)
	    return new EditInfo("Moment of inertia (Kg.m^2)", J, 0, 0);
	if (n == 5)
	    return new EditInfo("Friction coefficient (Nms/rad)", b, 0, 0);
	if (n == 6)
	    return new EditInfo("Gear Ratio", gearRatio, 0, 0);
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {

	if (ei.value > 0 & n==0) {
            inductance = ei.value;
            ind.setup(inductance, current, Inductor.FLAG_BACK_EULER);
        }
	if (ei.value > 0 & n==1)
	    resistance = ei.value;
	if (ei.value > 0 & n==2)
	    K = ei.value;
	if (ei.value > 0 & n==3)
	    Kb = ei.value;
	if (ei.value > 0 & n==4) {
            J = ei.value;
            indInertia.setup(J, inertiaCurrent, Inductor.FLAG_BACK_EULER);
        }
	if (ei.value > 0 & n==5)
	    b = ei.value;
	if (ei.value > 0 & n==6)
	    gearRatio = ei.value;
    }
}
