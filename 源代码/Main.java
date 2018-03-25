import javax.swing.*;

import Lulu_Elevator.Elevator;
import Lulu_Elevator.ElevatorInfo;
import Press.PressButton;

import java.awt.*;

class View extends JFrame
{
    public View()
    {
        super("lulu's Elevator");
        getContentPane().setBackground(Color.decode("#00838F"));

        PressButton require=new PressButton();
        require.setBounds(30,ElevatorInfo.windowMargin,
                ElevatorInfo.elevatorButtonWide*2,
                ElevatorInfo.elevatorButtonHigh* ElevatorInfo.totalFloor+   3*2);
        require.setBackground(Color.decode("#CFD8DC"));
        require.setBorder(BorderFactory.createLineBorder(Color.decode("#455A64"), 2));
        add(require);

        ElevatorInfo.elevator = new Elevator[5];
        for (int i=0; i<5; i++)
        {
            ElevatorInfo.elevator[i]=new Elevator();
            ElevatorInfo.elevator[i].add(this,i);
        }

        setSize(ElevatorInfo.windowWide,ElevatorInfo.windowHigh);
        this.setVisible(true);
        this.setResizable(false);
        for (int i=0; i<5; i++)
        {
            ElevatorInfo.elevator[i].start();
        }
    }
}



public class Main {

    public static void main(String[] args)
    {
        View frame = new View();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}

