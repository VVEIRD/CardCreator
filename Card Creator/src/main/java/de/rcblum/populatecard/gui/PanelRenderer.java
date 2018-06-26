package de.rcblum.populatecard.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

class PanelRenderer implements ListCellRenderer<JPanel> {

    @Override
    public Component getListCellRendererComponent(JList<? extends JPanel> list, JPanel value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel renderer = (JPanel) value;
        renderer.setBackground(isSelected ? list.getBackground(): list.getBackground());
        return renderer;
    }

}