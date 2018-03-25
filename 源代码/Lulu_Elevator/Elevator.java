package Lulu_Elevator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import Press.PressButton;

//import javax.swing.JLabel;


class Inside extends JPanel
{
	Elevator elevator;
	JButton buttonFloor[];
	JButton openButton = new JButton("⪦⪧");
	JButton closeButton = new JButton("⪧⪦");
	Inside(Elevator ele)
	{
		setLayout(null);
		elevator=ele;
		buttonFloor=new JButton[ElevatorInfo.totalFloor];
		for (int i = 0, j = 19; i< ElevatorInfo.totalFloor; i++,j--)
		{
			buttonFloor[j]=new JButton(""+(ElevatorInfo.totalFloor-i));
			buttonFloor[j].setMargin(new Insets(1,1,1,1));
			buttonFloor[j].setFont(new Font(buttonFloor[j].getFont().getFontName(),buttonFloor[j].getFont().getStyle(),9));
			buttonFloor[j].setBounds(0, 2+i * ElevatorInfo.elevatorButtonHigh,
					ElevatorInfo.elevatorButtonWide, ElevatorInfo.elevatorButtonHigh);
			buttonFloor[j].setBackground(Color.white);
			buttonFloor[j].addActionListener(buttonFloorListener);
			buttonFloor[j].setCursor(new Cursor(Cursor.HAND_CURSOR));
			add(buttonFloor[j]);
		}

		openButton.setBounds(0, ElevatorInfo.totalFloor* ElevatorInfo.elevatorButtonHigh,
				ElevatorInfo.elevatorButtonWide/2, ElevatorInfo.elevatorButtonHigh/2);
		openButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		openButton.setMargin(new Insets(1,0,1,0));
		openButton.setFont(new Font(openButton.getFont().getFontName(), openButton.getFont().getStyle(),9));
		openButton.addActionListener(openFloorListener);

		openButton.setBackground(Color.white);

		closeButton.setBounds(ElevatorInfo.elevatorButtonWide/2, ElevatorInfo.totalFloor* ElevatorInfo.elevatorButtonHigh,
				ElevatorInfo.elevatorButtonWide/2, ElevatorInfo.elevatorButtonHigh/2);
		closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		closeButton.setMargin(new Insets(1,0,1,0));
		closeButton.setFont(new Font(closeButton.getFont().getFontName(), closeButton.getFont().getStyle(),9));
		closeButton.addActionListener(closeFloorListener);
		closeButton.setBackground(Color.white);

		add(openButton);
		add(closeButton);
	}

	ActionListener buttonFloorListener=new ActionListener()
	{
		public void actionPerformed(ActionEvent	e)
		{
			int floor=Integer.parseInt(( (JButton)e.getSource() ).getText());

			if (elevator.getFloor()==floor && elevator.isOpen)
			{
				elevator.reopen() ;
				return ;
			}
			if (elevator.getElevatorState()==0 && elevator.getFloor()==floor)
			{
				elevator.open();
				return ;
			}

			buttonFloor[floor-1].setBackground(Color.decode("#CFD8DC"));
			buttonFloor[floor-1].setBorder(BorderFactory.createLineBorder(Color.decode("#B2EBF2"), 2));
			buttonFloor[floor-1].setOpaque(true);
			if (floor== ElevatorInfo.totalFloor)
			{
				elevator.setArrival(2* ElevatorInfo.totalFloor-floor);
				return ;
			}
			if (floor==1)
			{
				elevator.setArrival(0);
				return ;
			}
			if (elevator.getFloor()<floor)
			{
				elevator.setArrival(floor-1);
			}
			else if (elevator.getFloor()>floor)
			{
				elevator.setArrival(2* ElevatorInfo.totalFloor-floor);
			}
			else if (elevator.getFloor()==floor)
			{
				if (elevator.getElevatorState()==1 || elevator.getElevatorState()==2)
					elevator.setArrival(2* ElevatorInfo.totalFloor-floor);
				else if (elevator.getElevatorState()==-1 || elevator.getElevatorState()==-2)
					elevator.setArrival(floor-1);
			}
		}
	};

	ActionListener closeFloorListener=new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (!elevator.isOpen) return ;
			elevator.close();
		}
	};

	ActionListener openFloorListener=new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (elevator.isOpen)	elevator.reopen();
			else if (elevator.getElevatorState()==0) elevator.setArrival(elevator.getFloor()-1);
			else return ;
		}
	};
}

class Outside extends JPanel
{
	JLabel floor[];
	Elevator elevator;
	Outside(Elevator ele)
	{
		elevator=ele;
		setLayout(null);
		floor=new JLabel[ElevatorInfo.totalFloor];
		ele.elevator.setBounds(0,
				(ElevatorInfo.totalFloor-1)*(ElevatorInfo.floorHigh+ ElevatorInfo.floorSpace)+ ElevatorInfo.floorSpace,
				ElevatorInfo.floorWide, ElevatorInfo.floorHigh);
		ele.elevator.setBackground(Color.decode("#00B8D4"));
		ele.elevator.setBorder(BorderFactory.createLineBorder(Color.decode("#18FFFF"), 4));
		ele.elevator.setOpaque(true);

		add(ele.elevator);

		for (int i = 0, j = 19; i< ElevatorInfo.totalFloor; i++,j--)
		{
			floor[j]=new JLabel(""+(ElevatorInfo.totalFloor-i));
			floor[j].setOpaque(true);
			floor[j].setBackground(Color.black);
			floor[j].setForeground(Color.cyan);
			floor[j].setHorizontalAlignment(JLabel.CENTER);
			floor[j].setBorder(BorderFactory.createLineBorder(Color.decode("#ECEFF1"), 1));
			floor[j].setBounds(0,
					i*(ElevatorInfo.floorHigh+ ElevatorInfo.floorSpace)+ ElevatorInfo.floorSpace,
					ElevatorInfo.floorWide, ElevatorInfo.floorHigh);
			add(floor[j]);
		}

	}
}


public class Elevator extends Thread
{
	JLabel elevator=new JLabel();
	private Inside inside =new Inside(this);
	private Outside outside =new Outside(this);
	private int state;
	//1 正在向上运行 并且接向上的
	//10 表示向上运行的过程中在某些楼层停止
	//2表示向上运行，接向下的
	//0 表示没有运行
	private boolean restart;
	private int floor;
	public boolean arrival[];
	private int willArrive;
	boolean isOpen;

	public Elevator()
	{
		isOpen =false;
		willArrive=1;
		state=0;
		floor=1;
		arrival =new boolean[ElevatorInfo.totalFloor*2];
		for (int i = 0; i< ElevatorInfo.totalFloor*2; i++)
			arrival[i]=false;
	}

	public int getElevatorState()
	{
		return state;
	}


	public void run()
	{
		while (true)
		{
			state= setDirection();
			if (state==1 || state==2)	up();
			else if (state==-1 || state==-2) 	down();
			else if (state==100)
			{
				arrival[floor-1]= PressButton.isPress[floor-1]=false;
				inside.buttonFloor[floor-1].setBackground(Color.white);
				PressButton.pressButton[floor-1].setBackground(Color.white);
				open();
				state=1;
			}
			else if (state==-100)
			{
				arrival[2* ElevatorInfo.totalFloor-floor]= PressButton.isPress[2* ElevatorInfo.totalFloor-floor]=false;
				inside.buttonFloor[floor].setBackground(Color.white);
				PressButton.pressButton[2* ElevatorInfo.totalFloor-floor].setBackground(Color.white);
				open();
				state=-1;
			}
		}
	}
	public void up()
	{
		for (int i = 0; i< ElevatorInfo.floorHigh+ ElevatorInfo.floorSpace; i++)
		{
			try
			{
				Thread.sleep(40);
			}
			catch (InterruptedException e)
			{
			}
			elevator.setLocation(elevator.getLocation().x,
					elevator.getLocation().y-1);
		}
		floor++;
		if (willArrive==floor &&  state==2 && !arrival[floor-1] && !PressButton.isPress[floor-1])
		{
			arrival[2* ElevatorInfo.totalFloor-floor]=false;
			inside.buttonFloor[floor-1].setBackground(Color.white);

			PressButton.isPress[2* ElevatorInfo.totalFloor-floor]=false;
			PressButton.pressButton[2* ElevatorInfo.totalFloor-floor].setBackground(Color.white);
			open();
		}
		else if (arrival[floor-1] || PressButton.isPress[floor-1] )
		{
			inside.buttonFloor[floor-1].setBackground(Color.white);
			arrival[floor-1]=false;

			PressButton.isPress[floor-1]=false;
			PressButton.pressButton[floor-1].setBackground(Color.white);
			open();
		}
	}

	public void down()
	{
		for (int i = 0; i< ElevatorInfo.floorHigh+ ElevatorInfo.floorSpace; i++)
		{
			try
			{
				this.sleep(40);
			}
			catch (InterruptedException e)
			{
			}
			elevator.setLocation(elevator.getLocation().x,
					elevator.getLocation().y+1);
		}
		floor--;
		if (willArrive==floor && state==-2 && !arrival[2* ElevatorInfo.totalFloor-floor] && !PressButton.isPress[2* ElevatorInfo.totalFloor-floor])
		{
			arrival[floor-1]=false;
			inside.buttonFloor[floor-1].setBackground(Color.white);

			PressButton.isPress[floor-1]=false;
			PressButton.pressButton[floor-1].setBackground(Color.white);
			open();
		}
		else if (arrival[2* ElevatorInfo.totalFloor-floor] || PressButton.isPress[2* ElevatorInfo.totalFloor-floor])
		{
			arrival[2* ElevatorInfo.totalFloor-floor]=false;
			inside.buttonFloor[floor-1].setBackground(Color.white);

			PressButton.isPress[2* ElevatorInfo.totalFloor-floor]=false;
			PressButton.pressButton[2* ElevatorInfo.totalFloor-floor].setBackground(Color.white);
			open();
		}

	}



	private int setDirection()
	{
		if ((state==0) && (arrival[floor-1]))	return 100;
		if ((state==0) && (arrival[2* ElevatorInfo.totalFloor-floor])) return -100;



		if (state==0)
		{
			for (int i = 0; i< ElevatorInfo.totalFloor; i++)
			{
				if (arrival[i])
				{
					willArrive=i+1;
					if (i+1>floor)		return 1;
					else 	return -2;

				}
				if (arrival[2* ElevatorInfo.totalFloor-i-1])
				{
					willArrive=i+1;
					if (i+1>floor)	return 2;
					else return -1;
				}
			}
			search(this);
		}
		if (state==1 || state==2)	//电梯状态向上
		{
			for (int i = ElevatorInfo.totalFloor-1; i>floor-1; i--)
			{
				if (arrival[i])									//需求向上
				{
					willArrive=Math.max(i+1,willArrive);
					return 1;
				}
				if (arrival[2* ElevatorInfo.totalFloor-i-1])
				{
					willArrive=Math.max(i+1,willArrive);
					return 2;
				}
			}
		}

		if (state==-1 || state==-2)
		{
			for (int i=0; i<floor-1; i++)
			{
				if (arrival[2* ElevatorInfo.totalFloor-i-1])
				{
					willArrive=Math.min(i+1,willArrive);
					return -1;
				}
				if (arrival[i])
				{
					willArrive=Math.min(i+1,willArrive);
					return -2;
				}
			}

		}
		return 0;
	}

	static public synchronized void search(Elevator ele)	//寻找电梯的目标楼层
	{
		int i= PressButton.getIsPress();
		if (i==-1) return ;
		int floor=i+1;
		if (floor> ElevatorInfo.totalFloor) floor=2* ElevatorInfo.totalFloor-floor;
		if (PressButton.isPress[i] && PressButton.isShortest(ele, floor))
		{
			ele.arrival[i]=true;
			PressButton.isPress[i]=false;
			PressButton.taskList.removeFirst();
			return ;
		}
	}

	public void setArrival(int i)
	{
		arrival[i]=true;
	}

	public int getFloor()
	{
		return floor;
	}

	public void open()
	{
		elevator.setBackground(Color.pink);
		elevator.setOpaque(false);
		isOpen =true;


		restart=false;
		try
		{
			this.sleep(2000);
		}
		catch (InterruptedException e)
		{
			if (restart)	open();
		}
		close();
	}

	public void close()
	{
		if (isOpen && state!=-100 && state!=100) this.interrupt();
		elevator.setOpaque(true);
		elevator.setBackground(Color.decode("#00B8D4"));
		isOpen =false;
	}

	public void reopen()
	{
		restart=true;
		this.interrupt();
	}

	public void add(JFrame frame,int i)
	{
		frame.setLayout(null);

		inside.setBounds((i+1)*150, ElevatorInfo.windowMargin,
				ElevatorInfo.elevatorButtonWide*2, ElevatorInfo.elevatorButtonHigh*((ElevatorInfo.totalFloor>>1)+2));
		inside.setSize(ElevatorInfo.elevatorButtonWide,(ElevatorInfo.totalFloor+1)*ElevatorInfo.elevatorButtonHigh);
		inside.setBackground(Color.decode("#00838F"));

		outside.setBounds(150*(i+1)+ ElevatorInfo.elevatorButtonWide+2,ElevatorInfo.windowMargin,
				ElevatorInfo.floorWide,
				(ElevatorInfo.floorHigh+ ElevatorInfo.floorSpace)* ElevatorInfo.totalFloor);
		frame.add(outside);
		frame.add(inside);

	}
}