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
    double curcounts[];
    Point posts[], leads[];
    
    public ThreePhaseMotorElm(int xx, int yy) { 
	super(xx, yy); 
	inductance = .5; resistance = 1; angle = pi/2; speed = 0; K = 0.15; b= 0.05; J = 0.02; Kb = 0.15; gearRatio=1; tau=0;
        voltSources = new int[2];
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
        voltSources = new int[2];
        curcounts = new double[3];
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
	interpPoint2(point1, point2, leads[0], leads[2], 1, 32);
	interpPoint(point1, point2, leads[1], (dn-cr)/dn);
	motorCenter = point2;
	allocNodes();
    }
    int getPostCount() { return 3; }
    Point getPost(int n) { return posts[n]; }
    int getInternalNodeCount() { return 8; }
    int getVoltageSourceCount() { return 2; }
    void reset() {
	super.reset();
	speed = 0;
        coilCurSourceValues = new double[coilCount];
        coilCurrents = new double[coilCount];
    }

    final int n001_ind = 3;
    final int n002_ind = 4;
    final int n003_ind = 5;
    final int n004_ind = 6;
    final int n005_ind = 7;
    final int n006_ind = 8;
    final int n007_ind = 9;
    final int nn_ind = 10;
    
    final double Rs = .435;
    final double Rr = .816;
    final double Cj = .64;
    final double Ls = .0294;
    final double Lr = .0297;
    final double Lm = .0287;
    final double Zp = 2;
    
    final int coilCount = 5;
    
    // based on https://forum.kicad.info/t/ac-motors-simulation-1-phase-3-phase/14188/3
    
    void stamp() {
	int i;
	
	int n001 = nodes[n001_ind];
	int n002 = nodes[n002_ind];
	int n003 = nodes[n003_ind];
	int n004 = nodes[n004_ind];
	int n005 = nodes[n005_ind];
	int n006 = nodes[n006_ind];
	int n007 = nodes[n007_ind];
	int nn   = nodes[nn_ind];
	sim.console("nodes " + n001 + " " + n002 + " " + n003);

	sim.stampResistor(nodes[0], n001, Rs);
	sim.stampResistor(nodes[1], n003, Rs);
	sim.stampResistor(nodes[2], n005, Rs);
	sim.stampResistor(n004, 0, 1.5*Rr);
	sim.stampResistor(n007, 0, 1.5*Rr);
	
	double coilInductances[] = { Ls, Ls, Ls, 1.5*Lr, 1.5*Lr };
	double couplingCoefs[][] = new double[coilCount][coilCount];
        xformMatrix = new double[coilCount][coilCount];

        // see CustomTransformerElm.java
        
        // fill diagonal
        for (i = 0; i != coilCount; i++)
            xformMatrix[i][i] = coilInductances[i];
        
        couplingCoefs[0][3] = couplingCoefs[3][0] = Lm/Math.sqrt(Ls*1.5*Lr);
        couplingCoefs[1][3] = couplingCoefs[3][1] = -Lm/(2*Math.sqrt(Ls*1.5*Lr));
        couplingCoefs[1][4] = couplingCoefs[4][1] = Math.sqrt(3)*Lm/(2*Math.sqrt(Ls*1.5*Lr));
        couplingCoefs[2][3] = couplingCoefs[3][2] = -Lm/(2*Math.sqrt(Ls*1.5*Lr));
        couplingCoefs[2][4] = couplingCoefs[4][2] = -Math.sqrt(3)*Lm/(2*Math.sqrt(Ls*1.5*Lr));
                
        int j;
        // fill off-diagonal
        for (i = 0; i != coilCount; i++)
            for (j = 0; j != i; j++)
                xformMatrix[i][j] = xformMatrix[j][i] = couplingCoefs[i][j]*Math.sqrt(coilInductances[i]*coilInductances[j]);
	
        CirSim.invertMatrix(xformMatrix, coilCount);

        double ts = sim.timeStep;
        for (i = 0; i != coilCount; i++)
            for (j = 0; j != coilCount; j++) {
                // multiply in dt/2 (or dt for backward euler)
                xformMatrix[i][j] *= ts;
                int ni1 = coilNodes[i*2];
                int nj1 = coilNodes[j*2];
                int ni2 = coilNodes[i*2+1];
                int nj2 = coilNodes[j*2+1];
                if (i == j)
                    sim.stampConductance(nodes[ni1], nodes[ni2], xformMatrix[i][i]);
                else
                    sim.stampVCCurrentSource(nodes[ni1], nodes[ni2], nodes[nj1], nodes[nj2], xformMatrix[i][j]);
            }
        for (i = 0; i != 10; i++)
            sim.stampRightSide(nodes[coilNodes[i]]);
        
        sim.stampVoltageSource(n002, 0, voltSources[0]);
        sim.stampVoltageSource(n006, 0, voltSources[1]);
        
        coilCurSourceValues = new double[coilCount];
        coilCurrents = new double[coilCount];
        int nodeCount = getPostCount() + getInternalNodeCount();
        nodeCurrents = new double[nodeCount];
    }
    
    int coilNodes[] = { n001_ind, nn_ind, n003_ind, nn_ind, n005_ind, nn_ind, n002_ind, n004_ind, n006_ind, n007_ind };
    double coilCurrents[];
    double coilCurSourceValues[];
    double xformMatrix[][];
    double nodeCurrents[];
    int voltSources[];
    
    void setVoltageSource(int n, int v) { voltSources[n] = v; }
    
    void startIteration() {
        int i;
        for (i = 0; i != coilCount; i++) {
            double val = coilCurrents[i];
            coilCurSourceValues[i] = val;
        }
        
        double torque = Zp * Math.sqrt(3)/2 * Lm * (coilCurrents[1]-coilCurrents[2]) * coilCurrents[3] - Math.sqrt(3) * coilCurrents[0] * coilCurrents[4];
	speed += sim.timeStep * (torque - b * speed);
        angle= angle + speed*sim.timeStep;

	int n002 = nodes[n002_ind];
	int n006 = nodes[n006_ind];
        sim.updateVoltageSource(n002, 0, voltSources[0], -Zp*speed*(Lm*Math.sqrt(3)/2 * (coilCurrents[1]-coilCurrents[2]) + 1.5*Lr*coilCurrents[4]));
        sim.updateVoltageSource(n006, 0, voltSources[1], Zp*speed*(3/2.*Lm*coilCurrents[0] + 1.5*Lr*coilCurrents[3]));
    }
    
    void doStep() {
        int i;
        for (i = 0; i != coilCount; i++) {
            int n1 = coilNodes[i*2];
            int n2 = coilNodes[i*2+1];
            sim.stampCurrentSource(nodes[n1], nodes[n2], coilCurSourceValues[i]);
        }
    }
    
    void calculateCurrent() {
        int i;
        int nodeCount = getPostCount() + getInternalNodeCount();
        for (i = 0; i != nodeCount; i++)
            nodeCurrents[i] = 0;
        for (i = 0; i != coilCount; i++) {
            double val = coilCurSourceValues[i];
            if (xformMatrix != null) {
                int j;
                for (j = 0; j != coilCount; j++) {
                    int n1 = coilNodes[j*2];
                    int n2 = coilNodes[j*2+1];
                    double voltdiff = volts[n1]-volts[n2];
                    val += voltdiff*xformMatrix[i][j];
                }
            }
            coilCurrents[i] = val;
            int ni = coilNodes[i];
            nodeCurrents[ni] += val;
            nodeCurrents[ni+1] -= val;
        }
    }

    boolean hasGroundConnection(int n1) {
	return false;
    }
    
    boolean getConnection(int n1, int n2) {
    	return true;
    }
    
    int cr = 37;
    
    void draw(Graphics g) {

	int hs = 8;
	setBbox(point1, point2, cr);
	
	int i;
	for (i = 0; i != 3; i++) {
	    setVoltageColor(g, volts[i]);
	    drawThickLine(g, posts[i], leads[i]);
	    curcounts[i] = updateDotCount(coilCurrents[i], curcounts[i]);
	    drawDots(g, posts[i], leads[i], curcounts[i]);
	}
	
	//getCurrent();
	setPowerColor(g, true);
	Color cc = new Color((int) (165), (int) (165), (int) (165));
	g.setColor(cc);
	g.fillOval(motorCenter.x-(cr), motorCenter.y-(cr), (cr)*2, (cr)*2);
	cc = new Color((int) (10), (int) (10), (int) (10));

	g.setColor(cc);
	double angleAux = Math.round(angle*300.0)/300.0;
	g.fillOval(motorCenter.x-(int)(cr/2.2), motorCenter.y-(int)(cr/2.2), (int)(2*cr/2.2), (int)(2*cr/2.2));

	g.setColor(cc);
	double q = .28*1.7 * 36/dn * 37/27;
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
