package vv3ird.populatecard.gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;

import vv3ird.populatecard.control.Task;
import vv3ird.populatecard.control.TaskScheduler;

public class JTaskScheduler extends JPanel {
	private JLabel label;
	private JPanel pnQueuedTasks;

	/**
	 * Create the panel.
	 */
	public JTaskScheduler() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setSize(new Dimension(250, 350));
		setPreferredSize(new Dimension(250, 350));
		setMaximumSize(new Dimension(250, 350));
		
		Component verticalStrut_1 = Box.createVerticalStrut(5);
		add(verticalStrut_1);
		
		Box horizontalBox = Box.createHorizontalBox();
		add(horizontalBox);
		
		Component rigidArea = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox.add(rigidArea);
		
		JLabel lblActiveTask = new JLabel("Active Task: ");
		horizontalBox.add(lblActiveTask);
		
		label = new JLabel(TaskScheduler.hasActiveTask() ? TaskScheduler.getActiveTask().getDescription() : "No active task");
		horizontalBox.add(label);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox.add(horizontalGlue);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox.add(rigidArea_1);
		
		Component verticalStrut = Box.createVerticalStrut(10);
		add(verticalStrut);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		add(horizontalBox_1);
		
		Component rigidArea_2 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox_1.add(rigidArea_2);
		
		JLabel lblQueue = new JLabel("Queue:");
		horizontalBox_1.add(lblQueue);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox_1.add(horizontalGlue_1);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		add(horizontalBox_2);
		
		Component rigidArea_4 = Box.createRigidArea(new Dimension(5, 5));
		horizontalBox_2.add(rigidArea_4);
		
		JScrollPane scrollPane = new JScrollPane();
		horizontalBox_2.add(scrollPane);
		
		pnQueuedTasks = new JPanel();
		scrollPane.setViewportView(pnQueuedTasks);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		pnQueuedTasks.setLayout(new BoxLayout(pnQueuedTasks, BoxLayout.Y_AXIS));
		
		Box queuedTaskOne = Box.createHorizontalBox();
		pnQueuedTasks.add(queuedTaskOne);
		
		JLabel taskOneDesc = new JLabel("<TASK>");
		queuedTaskOne.add(taskOneDesc);
		
		Component hgOne = Box.createHorizontalGlue();
		queuedTaskOne.add(hgOne);
		
		JButton killTask1 = new JButton("Kill");
		queuedTaskOne.add(killTask1);
		
		Component rigidArea_3 = Box.createRigidArea(new Dimension(5, 5));
		horizontalBox_2.add(rigidArea_3);
		
		Component rigidArea_5 = Box.createRigidArea(new Dimension(5, 5));
		add(rigidArea_5);
		addQueuedTasks();
	}
	
	
	private void addQueuedTasks() {
		Task[] tasks = TaskScheduler.getQueue();
		pnQueuedTasks.removeAll();
		for (Task task : tasks) {
			Box queuedTaskOne = Box.createHorizontalBox();
			JLabel taskOneDesc = new JLabel(task.getDescription());
			queuedTaskOne.add(taskOneDesc);
			Component hgOne = Box.createHorizontalGlue();
			queuedTaskOne.add(hgOne);
			
			JButton killTask1 = new JButton("Remove");
			killTask1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TaskScheduler.removeTask(task);
					addQueuedTasks();
				}
			});
			queuedTaskOne.add(killTask1);
			pnQueuedTasks.add(queuedTaskOne);
		}
		pnQueuedTasks.revalidate();
		pnQueuedTasks.repaint();
	}

}
