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

    final String groundIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='scale(.6) translate(-826.46,-231.31) scale(1.230769)'><path fill='none' stroke='currentColor' d=' M 688 192 L 688 208' stroke-linecap='round' stroke-width='3' /> <path fill='none' stroke='currentColor' d=' M 698 208 L 678 208' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 694 213 L 682 213' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 690 218 L 686 218' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 691 192 A 3 3 0 1 1 690.9997392252899 191.96044522459943 Z' /> </g></svg>";

    final String capacitorIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-323.76,-71.18) scale(0.470588)'><path fill='none' stroke='currentColor' d=' M 688 176 L 708 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 708 164 L 708 188' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 736 176 L 716 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 716 164 L 716 188' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 691 176 A 3 3 0 1 1 690.9997392252899 175.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 739 176 A 3 3 0 1 1 738.9997392252899 175.96044522459943 Z' /></g></svg>";

    final String diodeIcon = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='24' height='24'><defs /><g transform='translate(-323.76,-72.06) scale(0.470588)'><path fill='none' stroke='currentColor' d=' M 688 176 L 704 176' stroke-linecap='round' stroke-width='3' /><path fill='none' stroke='currentColor' d=' M 720 176 L 736 176' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 704 168 L 704 184 L 720 176 Z' /><path fill='none' stroke='currentColor' d=' M 720 168 L 720 184' stroke-linecap='round' stroke-width='3' /><path fill='currentColor' stroke='currentColor' d=' M 691 176 A 3 3 0 1 1 690.9997392252899 175.96044522459943 Z' /><path fill='currentColor' stroke='currentColor' d=' M 739 176 A 3 3 0 1 1 738.9997392252899 175.96044522459943 Z' /></g></svg>";

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
	add(createIconButton(resistorIcon, "Resistor", new MyCommand("main", "ResistorElm")));
	add(createIconButton(groundIcon, "Ground", new MyCommand("main", "GroundElm")));
	add(createIconButton(capacitorIcon, "Capacitor", new MyCommand("main", "CapacitorElm")));
	add(createIconButton(diodeIcon, "Diode", new MyCommand("main", "DiodeElm")));

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

    private Widget createIconButton(String iconClass, String tooltip, MyCommand command) {
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

}

