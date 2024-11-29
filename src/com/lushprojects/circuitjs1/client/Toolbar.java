package com.lushprojects.circuitjs1.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.lushprojects.circuitjs1.client.util.Locale;

public class Toolbar extends HorizontalPanel {

    private Label modeLabel;

    final String wireIcon = "<svg width='24' height='24' viewBox='-5 0 110 50' xmlns='http://www.w3.org/2000/svg'>" +
                "<line x1='5' y1='45' x2='95' y2='5' stroke='black' stroke-width='8' />" +
                "<circle cx='5' cy='45' r='10' fill='black' />" +
                "<circle cx='95' cy='5' r='10' fill='black' />" +
            "</svg>";

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

        // Spacer to push the mode label to the right
        HorizontalPanel spacer = new HorizontalPanel();
        //spacer.style.setFlexGrow(1); // Fill remaining space
        add(spacer);

        // Create and add the mode label on the right
        modeLabel = new Label("");
        styleModeLabel(modeLabel);
        add(modeLabel);
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
                command.execute();
            }
        });

        return iconLabel;
    }

    private void styleModeLabel(Label label) {
	Style style = label.getElement().getStyle();
        style.setFontSize(16, Style.Unit.PX);
        style.setColor("#333");
        style.setPaddingRight(10, Style.Unit.PX);
    }
}

