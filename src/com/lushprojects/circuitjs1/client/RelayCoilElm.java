/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.lushprojects.circuitjs1.client;

import com.google.gwt.user.client.ui.Button;
import com.lushprojects.circuitjs1.client.util.Locale;

// 0 = switch
// 1 = switch end 1
// 2 = switch end 2
// ...
// 3n   = coil
// 3n+1 = coil
// 3n+2 = end of coil resistor

class RelayCoilElm extends CircuitElm {
	final int FLAG_SHOW_BOX = 2;
	final int FLAG_BOTH_SIDES_COIL = 4;
	
    double inductance;
    Inductor ind;
    String label;
    double onCurrent, offCurrent;
    Point coilPosts[], coilLeads[];
    Point outline[] = newPointArray(4);
    Point extraPoints[];
    double coilCurrent, coilCurCount;
    
    // fractional position, between 0 and 1 inclusive
    double d_position;
    
    // integer position, can be 0 (off), 1 (on), 2 (in between)
    int i_position;
    
    double coilR;
    
    // time to switch in seconds, or 0 for old model where switching time was not constant
    double switchingTime;
    
    int openhs;
    boolean onState;
    final int nSwitch0 = 0;
    final int nSwitch1 = 1;
    final int nSwitch2 = 2;
    int nCoil1, nCoil2, nCoil3;
    double currentOffset1, currentOffset2;
    
    public RelayCoilElm(int xx, int yy) {
	super(xx, yy);
	ind = new Inductor(sim);
	inductance = .2;
	ind.setup(inductance, 0, Inductor.FLAG_BACK_EULER);
	noDiagonal = true;
	onCurrent = .02;
	offCurrent = .015;
	label = "label";
	coilR = 20;
	switchingTime = 5e-3;
	coilCurrent = coilCurCount = 0;
	flags |= FLAG_SHOW_BOX | FLAG_BOTH_SIDES_COIL;
	setupPoles();
    }
    public RelayCoilElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	label = CustomLogicModel.unescape(st.nextToken());
	inductance = new Double(st.nextToken()).doubleValue();
	coilCurrent = new Double(st.nextToken()).doubleValue();
	onCurrent = new Double(st.nextToken()).doubleValue();
	coilR = new Double(st.nextToken()).doubleValue();
	try {
	    offCurrent = onCurrent;
	    switchingTime = 0;
	    offCurrent = new Double(st.nextToken()).doubleValue();
	    switchingTime = Double.parseDouble(st.nextToken());
	    d_position = i_position = Integer.parseInt(st.nextToken());
	} catch (Exception e) {}	
	if (i_position == 1)
	    onState = true;
	// intermediate state?
	if (i_position == 2)
	    d_position = .5;
	noDiagonal = true;
	ind = new Inductor(sim);
	ind.setup(inductance, coilCurrent, Inductor.FLAG_BACK_EULER);
	setupPoles();
        allocNodes();
    }
    
    void setupPoles() {
	nCoil1 = 0;
	nCoil2 = nCoil1+1;
	nCoil3 = nCoil1+2;
    }
    
    int getDumpType() { return 425; }
    
    String dump() {
	return super.dump() + " " + CustomLogicModel.escape(label) + " " +
	    inductance + " " + coilCurrent + " " + onCurrent + " " + coilR + " " + offCurrent + " " + switchingTime + " " + i_position;
    }
    
    void draw(Graphics g) {
	int i, p;
	for (i = 0; i != 2; i++) {
	    setVoltageColor(g, volts[nCoil1+i]);
	    drawThickLine(g, coilLeads[i], coilPosts[i]);
	}
	setPowerColor(g, coilCurrent * (volts[nCoil1]-volts[nCoil2]));

	// draw rectangle
	g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	drawThickLine(g, outline[0], outline[1]);
	drawThickLine(g, outline[1], outline[2]);
	drawThickLine(g, outline[2], outline[3]);
	drawThickLine(g, outline[3], outline[0]);

	g.setColor(needsHighlight() ? selectColor : whiteColor);
	if (x == x2)
	    g.drawString(label, outline[2].x+10, (y+y2)/2+4);
	else {
            g.save();
            g.context.setTextAlign("center");
	    g.drawString(label, (x+x2)/2, outline[1].y+15);
	    g.restore();
	}
	    
	// latching
	//for (i = 0; i != 3; i++)
	    //drawThickLine(g, extraPoints[i], extraPoints[i+1]);
	// delay on
	//drawThickLine(g, extraPoints[1], extraPoints[2]);
	//drawThickLine(g, extraPoints[0], extraPoints[2]);
	//drawThickLine(g, extraPoints[1], extraPoints[3]);
	// delay off
	g.fillRect(extraPoints[0].x, extraPoints[0].y, extraPoints[2].x-extraPoints[0].x, extraPoints[2].y-extraPoints[0].y);

	coilCurCount = updateDotCount(coilCurrent, coilCurCount);
	
	if (coilCurCount != 0) {
	    drawDots(g, coilPosts[0], coilLeads[0], coilCurCount);
	    //drawDots(g, coilLeads[0], coilLeads[1], addCurCount(coilCurCount, currentOffset1));
	    drawDots(g, coilLeads[1], coilPosts[1], addCurCount(coilCurCount, currentOffset2));
	}

	drawPosts(g);
	setBbox(outline[0], outline[2], 0);
	adjustBbox(coilPosts[0], coilPosts[1]);

    }
	
    double getCurrentIntoNode(int n) {
	if (n == 0)
	    return -coilCurrent;
	return coilCurrent;
    }

    void setPoints() {
	super.setPoints();
	setupPoles();
	allocNodes();
	openhs = -dsign*16;

	// coil
	coilPosts = newPointArray(2);
	coilLeads   = newPointArray(2);

	int boxSize;
	coilPosts[0] = point1;
	coilPosts[1] = point2;
	boxSize = 32;

	// outline
	double boxWScale = Math.min(0.4, 12.0 / dn);
	interpPoint(point1, point2, coilLeads[0], 0.5-boxWScale);
	interpPoint(point1, point2, coilLeads[1], 0.5+boxWScale);
	interpPoint(point1, point2, outline[0], 0.5 - boxWScale, -boxSize * dsign);
	interpPoint(point1, point2, outline[1], 0.5 + boxWScale, -boxSize * dsign);
	interpPoint(point1, point2, outline[3], 0.5 - boxWScale, +boxSize * dsign);
	interpPoint(point1, point2, outline[2], 0.5 + boxWScale, +boxSize * dsign);
	
	currentOffset1 = distance(coilPosts[0], coilLeads[0]);
	currentOffset2 = currentOffset1 + distance(coilLeads[0], coilLeads[1]);

	extraPoints = newPointArray(4);
	/*
	// latching relay
	interpPoint(coilLeads[0], coilLeads[1], extraPoints[0], .3, 8);
	interpPoint(coilLeads[0], coilLeads[1], extraPoints[1], .3, 0);
	interpPoint(coilLeads[0], coilLeads[1], extraPoints[2], .7, 0);
	interpPoint(coilLeads[0], coilLeads[1], extraPoints[3], .7, -8);
	*/
	interpPoint(coilLeads[0], coilLeads[1], extraPoints[0], 0, -boxSize);
	interpPoint(coilLeads[0], coilLeads[1], extraPoints[1], 0, -boxSize+12);
	interpPoint(coilLeads[0], coilLeads[1], extraPoints[2], 1, -boxSize+12);
	interpPoint(coilLeads[0], coilLeads[1], extraPoints[3], 1, -boxSize);
    }
    
    Point getPost(int n) {
	return coilPosts[n];
    }
    int getPostCount() { return 2; }
    int getInternalNodeCount() { return 1; }
    void reset() {
	super.reset();
	ind.reset();
	coilCurrent = coilCurCount = 0;
	d_position = i_position = 0;

	// preserve onState because if we don't, Relay Flip-Flop gets left in a weird state on reset.
	// onState = false;
    }
    double a1, a2, a3, a4;
    void stamp() {
	// inductor from coil post 1 to internal node
	ind.stamp(nodes[nCoil1], nodes[nCoil3]);
	// resistor from internal node to coil post 2
	sim.stampResistor(nodes[nCoil3], nodes[nCoil2], coilR);
    }
    
    void startIteration() {
	ind.startIteration(volts[nCoil1]-volts[nCoil3]);
	double absCurrent = Math.abs(coilCurrent);
	int oldPos = i_position;
	
	if (onState) {
	    // on or turning on.  check if we need to turn off
	    if (absCurrent < offCurrent) {
		// turning off, set switch to intermediate position
		onState = false;
		i_position = 2;
	    } else {
		d_position += sim.timeStep/switchingTime;
		if (d_position >= 1)
		    d_position = i_position = 1;
	    }
	} else {
	    // off or turning off.  check if we need to turn on
	    if (absCurrent > onCurrent) {
		// turning on, set switch to intermediate position
		onState = true;
		i_position = 2;
	    } else {
		d_position -= sim.timeStep/switchingTime;
		if (d_position <= 0)
		    d_position = i_position = 0;
	    }
	    
	}
	int i;
	if (oldPos != i_position)
	    for (i = 0; i != sim.elmList.size(); i++) {
		Object o = sim.elmList.elementAt(i);
		if (o instanceof RelayContactElm) {
		    RelayContactElm s2 = (RelayContactElm) o;
		    if (s2.label.equals(label))
			s2.setPosition(onState, i_position, d_position);
		}
	    }
    }
    
    void doStep() {
	double voltdiff = volts[nCoil1]-volts[nCoil3];
	ind.doStep(voltdiff);
    }
    void calculateCurrent() {
	double voltdiff = volts[nCoil1]-volts[nCoil3];
	coilCurrent = ind.calculateCurrent(voltdiff);
    }
    void getInfo(String arr[]) {
	arr[0] = Locale.LS("relay");
	if (i_position == 0)
	    arr[0] += " (" + Locale.LS("off") + ")";
	else if (i_position == 1)
	    arr[0] += " (" + Locale.LS("on") + ")";
	int i;
	int ln = 1;
	arr[ln++] = Locale.LS("coil I") + " = " + getCurrentDText(coilCurrent);
	arr[ln++] = Locale.LS("coil Vd") + " = " +
	    getVoltageDText(volts[nCoil1] - volts[nCoil2]);
    }
    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("Inductance (H)", inductance, 0, 0);
	if (n == 1)
	    return new EditInfo("On Current (A)", onCurrent, 0, 0);
	if (n == 2)
	    return new EditInfo("Off Current (A)", offCurrent, 0, 0);
	if (n == 3)
	    return new EditInfo("Coil Resistance (ohms)", coilR, 0, 0);
	if (n == 4)
	    return new EditInfo("Switching Time (s)", switchingTime, 0, 0);
	if (n == 5) {
	    EditInfo ei = new EditInfo("Label", 0);
	    ei.text = label;
	    return ei;
	}
	return null;
    }
    
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0 && ei.value > 0) {
	    inductance = ei.value;
	    ind.setup(inductance, coilCurrent, Inductor.FLAG_BACK_EULER);
	}
	if (n == 1 && ei.value > 0)
	    onCurrent = ei.value;
	if (n == 2 && ei.value > 0)
	    offCurrent = ei.value;
	if (n == 3 && ei.value > 0)
	    coilR = ei.value;
	if (n == 4 && ei.value > 0)
	    switchingTime = ei.value;
	if (n == 5)
	    label = ei.textf.getText();
    }
    
    boolean getConnection(int n1, int n2) {
	return true;
    }
}
    
