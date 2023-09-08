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

class RelayContactElm extends CircuitElm {
    double r_on, r_off;
    Point swposts[], swpoles[], ptSwitch;
    double switchCurrent, switchCurCount;
    String label;
    
    // fractional position, between 0 and 1 inclusive
    double d_position;
    
    // integer position, can be 0 (off), 1 (on), 2 (in between)
    int i_position;
    
    // time to switch in seconds, or 0 for old model where switching time was not constant
    double switchingTime;
    
    int poleCount;
    int openhs;
    boolean onState;
    final int nSwitch0 = 0;
    final int nSwitch1 = 1;
    final int nSwitch2 = 2;
    double currentOffset1, currentOffset2;
    
    public RelayContactElm(int xx, int yy) {
	super(xx, yy);
	noDiagonal = true;
	r_on = .05;
	r_off = 1e6;
	switchingTime = 5e-3;
    }
    public RelayContactElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	r_on = new Double(st.nextToken()).doubleValue();
	r_off = new Double(st.nextToken()).doubleValue();
	try {
	    d_position = i_position = Integer.parseInt(st.nextToken());
	} catch (Exception e) {}	
	if (i_position == 1)
	    onState = true;
	// intermediate state?
	if (i_position == 2)
	    d_position = .5;
	noDiagonal = true;
        allocNodes();
    }
    
    int getDumpType() { return 426; }
    
    String dump() {
	// escape label
	return super.dump() + " " + label + " " + r_on + " " + r_off + " " + switchingTime + " " + i_position;
    }
    
    void draw(Graphics g) {
	int i;
	for (i = 0; i != 3; i++) {
	    // draw lead
	    setVoltageColor(g, volts[nSwitch0+i]);
	    drawThickLine(g, swposts[i], swpoles[i]);
	}
	
	interpPoint(swpoles[1], swpoles[2], ptSwitch, d_position);
	//setVoltageColor(g, volts[nSwitch0]);
	g.setColor(Color.lightGray);
	drawThickLine(g, swpoles[0], ptSwitch);
	switchCurCount = updateDotCount(switchCurrent, switchCurCount);
	drawDots(g, swposts[0], swpoles[0], switchCurCount);
	
	if (i_position != 2)
	    drawDots(g, swpoles[i_position+1], swposts[i_position+1], switchCurCount);
	
	drawPosts(g);
	adjustBbox(swposts[0], swposts[1]);
    }
	
    double getCurrentIntoNode(int n) {
	if (n == 0)
	    return -switchCurrent;
	if (n == 1+i_position)
	    return switchCurrent;
	return 0;
    }

    void setPoints() {
	super.setPoints();
	allocNodes();
	openhs = -dsign*16;

	// switch
	calcLeads(32);
	swposts = new Point[3];
	swpoles = new Point[3];
	int i, j;
	for (j = 0; j != 3; j++) {
	    swposts[j] = new Point();
	    swpoles[j] = new Point();
	}
	interpPoint(lead1,  lead2, swpoles[0], 0, 0);
	interpPoint(lead1,  lead2, swpoles[1], 1, -openhs);
	interpPoint(lead1,  lead2, swpoles[2], 1, openhs);
	interpPoint(point1, point2, swposts[0], 0, 0);
	interpPoint(point1, point2, swposts[1], 1, -openhs);
	interpPoint(point1, point2, swposts[2], 1, openhs);
	ptSwitch = new Point();
    }
    
    public void setPosition(boolean onState_, int i_position_, double d_position_) {
	onState = onState_;
	i_position = i_position_;
	d_position = d_position_;
	sim.console("setpos " + onState + " " + i_position);
    }

    Point getPost(int n) {
	return swposts[n];
    }
    int getPostCount() { return 3; }
    void reset() {
	super.reset();
	switchCurrent = switchCurCount = 0;
	d_position = i_position = 0;

	// preserve onState because if we don't, Relay Flip-Flop gets left in a weird state on reset.
	// onState = false;
    }

    void stamp() {
	sim.stampNonLinear(nodes[nSwitch0]);
	sim.stampNonLinear(nodes[nSwitch1]);
	sim.stampNonLinear(nodes[nSwitch2]);
    }
    
    // we need this to be able to change the matrix for each step
    boolean nonLinear() { return true; }

    void doStep() {
	sim.stampResistor(nodes[nSwitch0], nodes[nSwitch1], i_position == 0 ? r_on : r_off);
	sim.stampResistor(nodes[nSwitch0], nodes[nSwitch2], i_position == 1 ? r_on : r_off);
    }
    void calculateCurrent() {
	// actually this isn't correct, since there is a small amount
	// of current through the switch when off
	if (i_position == 2)
	    switchCurrent = 0;
	else
	    switchCurrent = (volts[nSwitch0]-volts[nSwitch1+i_position])/r_on;
    }
    void getInfo(String arr[]) {
	arr[0] = Locale.LS("relay");
	if (i_position == 0)
	    arr[0] += " (" + Locale.LS("off") + ")";
	else if (i_position == 1)
	    arr[0] += " (" + Locale.LS("on") + ")";
	if (switchingTime == 0)
	    arr[0] += " (" + Locale.LS("old model") + ")";
	int i;
	int ln = 1;
	arr[ln++] = "I = " + getCurrentDText(switchCurrent);
    }
    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("On Resistance (ohms)", r_on, 0, 0);
	if (n == 1)
	    return new EditInfo("Off Resistance (ohms)", r_off, 0, 0);
	if (n == 2) {
	    EditInfo ei = new EditInfo("Label", 0);
	    ei.text = label;
	    return ei;
	}
	return null;
    }
    
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0 && ei.value > 0)
	    r_on = ei.value;
	if (n == 1 && ei.value > 0)
	    r_off = ei.value;
        if (n == 2)
	    label = ei.textf.getText();
    }
    
    boolean getConnection(int n1, int n2) {
	return true;
    }
}
