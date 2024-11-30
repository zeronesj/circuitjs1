package com.lushprojects.circuitjs1.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.lushprojects.circuitjs1.client.util.Locale;

import java.util.HashMap;

public class Toolbar extends HorizontalPanel {

    private Label modeLabel;
    private HashMap<String, Label> highlightableButtons = new HashMap<>();
    private Label activeButton;  // Currently active button

    final String wireIcon = "<svg width='24' height='24' viewBox='-5 0 110 50' xmlns='http://www.w3.org/2000/svg'>" +
                "<line x1='5' y1='45' x2='95' y2='5' stroke='currentColor' stroke-width='8' />" +
                "<circle cx='5' cy='45' r='10' fill='currentColor' />" +
                "<circle cx='95' cy='5' r='10' fill='currentColor' />" +
            "</svg>";

    final String resistorIcon = "<svg width='24' height='24'> <g transform='scale(.5,.5) translate(-544,-297)'>" +
      "<path stroke='#000000' d=' M 544 320 L 552 320' stroke-width='3'/>" +
      "<path stroke='#000000' d=' M 584 320 L 592 320' stroke-width='3'/>" +
      "<g transform='matrix(1,0,0,1,552,320)'><path fill='none' stroke='currentColor' " +
      "d=' M 0 0 L 2 6 L 6 -6 L 10 6 L 14 -6 L 18 6 L 22 -6 L 26 6 L 30 -6 L 32 0' stroke-width='2'/> </g> </g> </svg>";
    final String euroResistorIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><g transform='translate(97.71,-28.71) scale(0.428571)'><path fill='none' stroke='currentColor' d=' M -224 96 L -216 96' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M -184 96 L -176 96' stroke-linecap='round' stroke-width='3' /><g transform='matrix(1,0,0,1,-216,96)'><rect fill='none' stroke='currentColor' x='0' y='-6' width='32' height='12' stroke-linecap='round' stroke-width='3' /></g><path fill='currentColor' stroke='currentColor' d=' M -221 96 A 3 3 0 1 1 -221.00026077471009 95.96044522459944 Z' /><path fill='currentColor' stroke='currentColor' d=' M -173 96 A 3 3 0 1 1 -173.00026077471009 95.96044522459944 Z' /></g></svg>";

    final String groundIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='scale(.6) translate(-826.46,-231.31) scale(1.230769)'><path fill='none' stroke='currentColor' d=' M 688 192 L 688 208' stroke-linecap='round' stroke-width='3' /> <path fill='none' stroke='currentColor' d=' M 698 208 L 678 208' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 694 213 L 682 213' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 690 218 L 686 218' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 691 192 A 3 3 0 1 1 690.9997392252899 191.96044522459943 Z' /> </g></svg>";

    final String capacitorIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-323.76,-71.18) scale(0.470588)'><path fill='none' stroke='currentColor' d=' M 688 176 L 708 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 708 164 L 708 188' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 736 176 L 716 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 716 164 L 716 188' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 691 176 A 3 3 0 1 1 690.9997392252899 175.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 739 176 A 3 3 0 1 1 738.9997392252899 175.96044522459943 Z' /></g></svg>";

    final String diodeIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-323.76,-72.06) scale(0.470588)'><path fill='none' stroke='currentColor' d=' M 688 176 L 704 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 720 176 L 736 176' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 704 168 L 704 184 L 720 176 Z' /><path fill='none' stroke='currentColor' d=' M 720 168 L 720 184' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 691 176 A 3 3 0 1 1 690.9997392252899 175.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 739 176 A 3 3 0 1 1 738.9997392252899 175.96044522459943 Z' /></g></svg>";

    final String switchIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(55.79,-100.42) scale(0.421053)'><path fill='none' stroke='currentColor' d=' M -128 272 L -120 272' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M -88 272 L -80 272' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M -120 272 L -88 256' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M -125 272 A 3 3 0 1 1 -125.00026077471007 271.9604452245994 Z' /><path fill='currentColor' stroke='currentColor' d=' M -77 272 A 3 3 0 1 1 -77.00026077471007 271.9604452245994 Z' /></g></svg>";

    final String voltage2Icon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='scale(.8) translate(-122.00,-53.00) scale(0.500000)'><path fill='none' stroke='currentColor' d=' M 272 160 L 272 140' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 132 L 272 112' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 262 140 L 282 140' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 256 132 L 288 132' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 275 160 A 3 3 0 1 1 274.99973922528994 159.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 275 112 A 3 3 0 1 1 274.99973922528994 111.96044522459944 Z' /></g></svg>";

    final String transistorIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-107.73,-90.40) scale(0.533333)'><path fill='none' stroke='currentColor' d=' M 240 176 L 227 186' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 240 208 L 227 198' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 240 208 L 236 200 L 231 206 Z' /><path fill='none' stroke='currentColor' d=' M 208 192 L 224 192' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 224 176 L 227 176 L 227 208 L 224 208 Z' /><path fill='currentColor' stroke='currentColor' d=' M 211 192 A 3 3 0 1 1 210.99973922528991 191.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 243 176 A 3 3 0 1 1 242.99973922528991 175.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 243 208 A 3 3 0 1 1 242.99973922528991 207.96044522459943 Z' /></g></svg>";
    final String pnpTransistorIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-116.27,-90.40) scale(0.533333)'><path fill='none' stroke='currentColor' d=' M 256 208 L 243 198' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 256 176 L 243 186' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 245 187 L 253 184 L 248 178 Z' /><path fill='none' stroke='currentColor' d=' M 224 192 L 240 192' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 240 176 L 243 176 L 243 208 L 240 208 Z' /><path fill='currentColor' stroke='currentColor' d=' M 227 192 A 3 3 0 1 1 226.99973922528991 191.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 259 208 A 3 3 0 1 1 258.99973922528994 207.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 259 176 A 3 3 0 1 1 258.99973922528994 175.96044522459943 Z' /></g></svg>";

    final String opAmpIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-56.26,-45.81) scale(0.258065)'><path fill='none' stroke='currentColor' d=' M 224 208 L 238 208' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 224 240 L 238 240' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 290 224 L 304 224' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 238 192 L 238 256 L 290 224 Z' stroke-linecap='round' stroke-width='3' /><g><text fill='currentColor' stroke='currentColor' font-family='sans-serif' font-size='14px' font-style='normal' font-weight='normal' text-decoration='normal' x='248' y='206' text-anchor='middle' dominant-baseline='central'>-</text></g><g><text fill='currentColor' stroke='currentColor' font-family='sans-serif' font-size='14px' font-style='normal' font-weight='normal' text-decoration='normal' x='248' y='240' text-anchor='middle' dominant-baseline='central'>+</text></g><path fill='currentColor' stroke='currentColor' d=' M 227 208 A 3 3 0 1 1 226.99973922528991 207.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 227 240 A 3 3 0 1 1 226.99973922528991 239.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 307 224 A 3 3 0 1 1 306.99973922528994 223.96044522459943 Z' /></g></svg>";

    final String fetIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-68.92,-50.27) scale(0.324324)'><path fill='none' stroke='currentColor' d=' M 272 208 L 250 208' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 176 L 250 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 208 L 250 203' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 197 L 250 192' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 192 L 250 187' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 181 L 250 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 208 L 250 213' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 176 L 250 171' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 208 L 272 192' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 192 L 250 192' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 250 192 L 262 197 L 262 187 Z' /><path fill='none' stroke='currentColor' d=' M 224 192 L 244 192' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 244 184 L 244 200' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 176 L 272 160' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 208 L 272 224' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 227 192 A 3 3 0 1 1 226.99973922528991 191.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 275 160 A 3 3 0 1 1 274.99973922528994 159.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 275 224 A 3 3 0 1 1 274.99973922528994 223.96044522459943 Z' /></g></svg>";
    final String fetIcon2 = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-68.92,-50.27) scale(0.324324)'><path fill='none' stroke='currentColor' d=' M 272 176 L 272 160' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 208 L 272 224' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 208 L 250 208' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 176 L 250 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 208 L 250 203' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 197 L 250 192' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 192 L 250 187' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 181 L 250 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 208 L 250 213' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 250 176 L 250 171' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 176 L 272 192' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 272 192 L 250 192' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 272 192 L 260 187 L 260 197 Z' /><path fill='none' stroke='currentColor' d=' M 224 192 L 244 192' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 244 184 L 244 200' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 275 160 A 3 3 0 1 1 274.99973922528994 159.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 275 224 A 3 3 0 1 1 274.99973922528994 223.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 227 192 A 3 3 0 1 1 226.99973922528991 191.96044522459943 Z' /></g></svg>";

    final String inductIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><g transform='translate(-101.59,-58.18) scale(0.405680)'><path fill='none' stroke='currentColor' d=' M 256 176 L 264 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 296 176 L 304 176' stroke-linecap='round' stroke-width='3' /><g transform='matrix(1,0,0,1,264,176) scale(1,1)'><path fill='none' stroke='currentColor' d=' M 0 0 L 0 6.53144959545255e-16 A 5.333333333333333 5.333333333333333 0 0 1 10.666666666666666 0 L 10.666666666666666 0' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 10.666666666666666 0 L 10.666666666666668 6.53144959545255e-16 A 5.333333333333333 5.333333333333333 0 0 1 21.333333333333332 0 L 21.333333333333332 0' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 21.333333333333332 0 L 21.333333333333336 6.53144959545255e-16 A 5.333333333333333 5.333333333333333 0 0 1 32 0 L 32 0' stroke-linecap='round' stroke-width='3' /></g><path fill='currentColor' stroke='currentColor' d=' M 259 176 A 3 3 0 1 1 258.99973922528994 175.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 307 176 A 3 3 0 1 1 306.99973922528994 175.96044522459943 Z' /></g></svg>";

    final String railIcon = "<svg version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='24' height='24'><g><text fill='currentColor' stroke='none' font-family='sans-serif' font-size='10px' font-style='normal' font-weight='normal' text-decoration='normal' x='2' y='11' text-anchor='start' dominant-baseline='central'>+5V</text></g></svg>";

    Label resistorButton;

    public Toolbar() {
        // Set the overall style of the toolbar
	Style style = getElement().getStyle();
        style.setPadding(2, Style.Unit.PX);
        style.setBackgroundColor("#f8f8f8");
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderColor("#ccc");
        style.setDisplay(Style.Display.FLEX);
	setVerticalAlignment(ALIGN_MIDDLE);

	add(createIconButton("ccw", "Undo", new MyCommand("edit", "undo")));
	add(createIconButton("cw",  "Redo", new MyCommand("edit", "redo")));
	add(createIconButton("scissors", "Cut", new MyCommand("edit", "cut")));
	add(createIconButton("copy", "Copy", new MyCommand("edit", "copy")));
	add(createIconButton("paste", "Paste", new MyCommand("edit", "paste")));
	add(createIconButton("clone", "Duplicate", new MyCommand("edit", "duplicate")));
	add(createIconButton("search", "Find Component...", new MyCommand("edit", "search")));
	add(createIconButton("zoom-11", "Zoom 100%", new MyCommand("zoom", "zoom100")));
	add(createIconButton("zoom-in", "Zoom In", new MyCommand("zoom", "zoomin")));
	add(createIconButton("zoom-out", "Zoom Out", new MyCommand("zoom", "zoomout")));
	add(createIconButton(wireIcon, "Wire", new MyCommand("main", "WireElm")));
	add(resistorButton = createIconButton(resistorIcon, "Resistor", new MyCommand("main", "ResistorElm")));
	add(createIconButton(groundIcon, "Ground", new MyCommand("main", "GroundElm")));
	add(createIconButton(capacitorIcon, "Capacitor", new MyCommand("main", "CapacitorElm")));
	add(createIconButton(inductIcon, "Inductor", new MyCommand("main", "InductorElm")));
	add(createIconButton(diodeIcon, "Diode", new MyCommand("main", "DiodeElm")));
	add(createIconButton(voltage2Icon, "Voltage Source", new MyCommand("main", "DCVoltageElm")));
	add(createIconButton(railIcon, "Voltage Rail", new MyCommand("main", "RailElm")));
	add(createIconButton(switchIcon, "Switch", new MyCommand("main", "SwitchElm")));
	add(createIconButton(transistorIcon, "Transistor", new MyCommand("main", "NTransistorElm")));
	add(createIconButton(pnpTransistorIcon, "Transistor", new MyCommand("main", "PTransistorElm")));
	add(createIconButton(fetIcon, "MOSFET", new MyCommand("main", "NMosfetElm")));
	add(createIconButton(fetIcon2, "MOSFET", new MyCommand("main", "PMosfetElm")));
	add(createIconButton(opAmpIcon, "Op-Amp", new MyCommand("main", "OpAmpElm")));

        // Spacer to push the mode label to the right
        HorizontalPanel spacer = new HorizontalPanel();
        //spacer.style.setFlexGrow(1); // Fill remaining space
        add(spacer);

        // Create and add the mode label on the right
        modeLabel = new Label("");
        styleModeLabel(modeLabel);
        add(modeLabel);

        highlightButton("WireElm");  // Make "WireElm" the active button initially
    }

    public void setModeLabel(String text) { modeLabel.setText(Locale.LS("Mode: ") + text); }

    private Label createIconButton(String iconClass, String tooltip, MyCommand command) {
        // Create a label to hold the icon
        Label iconLabel = new Label();
        iconLabel.setText(""); // No text, just an icon
	if (iconClass.startsWith("<svg"))
	    iconLabel.getElement().setInnerHTML(iconClass);
        else
	    iconLabel.getElement().addClassName("cirjsicon-" + iconClass);
        iconLabel.setTitle(Locale.LS(tooltip));

        // Style the icon button
	Style style = iconLabel.getElement().getStyle();
        style.setFontSize(20, Style.Unit.PX);
        style.setColor("#333");
        style.setPadding(5, Style.Unit.PX);
        style.setMarginRight(5, Style.Unit.PX);
        style.setCursor(Style.Cursor.POINTER);

        // Add hover effect for the button
        iconLabel.addMouseOverHandler(event -> iconLabel.getElement().getStyle().setColor("#007bff"));
        iconLabel.addMouseOutHandler(event -> iconLabel.getElement().getStyle().setColor("#333"));

        // Add a click handler to perform the action
        iconLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
		// un-highlight
        	iconLabel.getElement().getStyle().setColor("#333");
                command.execute();
            }
        });

        // Track buttons that belong to the "main" command group
        if (command.getMenuName().equals("main"))
            highlightableButtons.put(command.getItemName(), iconLabel);

        return iconLabel;
    }

    private void styleModeLabel(Label label) {
	Style style = label.getElement().getStyle();
        style.setFontSize(16, Style.Unit.PX);
        style.setColor("#333");
        style.setPaddingRight(10, Style.Unit.PX);
    }

    public void highlightButton(String key) {
        // Deactivate the currently active button
        if (activeButton != null) {
            activeButton.getElement().getStyle().setColor("#333"); // Reset color
            activeButton.getElement().getStyle().setBackgroundColor(null);
        }

        // Activate the new button
        Label newActiveButton = highlightableButtons.get(key);
        if (newActiveButton != null) {
            newActiveButton.getElement().getStyle().setColor("#007bff"); // Active color
            newActiveButton.getElement().getStyle().setBackgroundColor("#e6f7ff");
            activeButton = newActiveButton;
        }
    }

    public void setEuroResistors(boolean euro) {
	resistorButton.getElement().setInnerHTML(euro ? euroResistorIcon : resistorIcon);
    }

}

