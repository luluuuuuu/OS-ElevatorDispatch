package Press;

import java.awt.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;


import javax.swing.*;

import Lulu_Elevator.Elevator;
import Lulu_Elevator.ElevatorInfo;

public class PressButton extends JPanel
{
	public static LinkedList<Integer> taskList = new LinkedList<Integer>();
	public static JButton [] pressButton =new JButton[ElevatorInfo.totalFloor*2];
	public static boolean [] isPress =new boolean[ElevatorInfo.totalFloor*2];
	public PressButton()
	{
		for (int i = 0; i< ElevatorInfo.totalFloor*2; i++)
			isPress[i]=false;
		
		setLayout(null);
		for (int i = 0; i< ElevatorInfo.totalFloor; i++)
		{
			pressButton[i]=new JButton((i+1)+"△");
		}
		for (int i = ElevatorInfo.totalFloor; i<2* ElevatorInfo.totalFloor; i++)
		{
			pressButton[i]=new JButton(2* ElevatorInfo.totalFloor-i+"▽");
		}
		
		for (int i = 0; i< ElevatorInfo.totalFloor*2-1; i++)
		{
			if (i== ElevatorInfo.totalFloor-1) continue;
			int j;
			if (i< ElevatorInfo.totalFloor-1) j= ElevatorInfo.totalFloor-i-1;
			else j=i-20;


			pressButton[i].setMargin(new Insets(1,1,1,1));
			pressButton[i].setFont(new Font(pressButton[i].getFont().getFontName(), pressButton[i].getFont().getStyle(),9));
			pressButton[i].setBounds(5+i/ ElevatorInfo.totalFloor* ElevatorInfo.elevatorButtonWide,
					j*(ElevatorInfo.floorHigh+ ElevatorInfo.floorSpace)+ ElevatorInfo.floorSpace
					, ElevatorInfo.floorWide/2, ElevatorInfo.elevatorButtonHigh);
			
			pressButton[i].setBackground(Color.green);
			pressButton[i].setForeground(Color.black);
			pressButton[i].addActionListener(pressButtonListener);
			add(pressButton[i]);
		}
	}
	ActionListener pressButtonListener =new ActionListener()
	{
		public void actionPerformed(ActionEvent	e)
		{
			JButton pressBtn=(JButton) e.getSource();
			int which = 0;
			for (int i = 0; i< ElevatorInfo.totalFloor*2; i++)
			{
				if (pressBtn== pressButton[i])
				{
					which=i;
					break;
				}
			}
			if (pressBtn.getBackground()==Color.lightGray) return ;
			pressBtn.setBackground(Color.lightGray);
			isPress[which]=true;
			taskList.add(new Integer(which));
		}
	};
	public static boolean isShortest(Elevator ele, int floor)
	{
		for (int i=0; i<5; i++)
		{
			if (ElevatorInfo.elevator[i].getElevatorState()==0 && Math.abs(ele.getFloor()-floor)>Math.abs(ElevatorInfo.elevator[i].getFloor()-floor)) return false;
		}
		return true;
	}
	
	public static int getIsPress()
	{
		while (true)
		{
			if (taskList.isEmpty()) return -1;
			int floor= taskList.getFirst();
			if (isPress[floor]) return floor;
			else taskList.removeFirst();
		}
	}
}
