
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

public class UIVoronoi extends JFrame implements MouseListener, ActionListener {
	
	//On crée les deux objets principaux : le dessin et le graphe
	ViewVoronoi dessin;
	GraphVoronoi voronoi;
	
	JButton clearButton = new JButton("Effacer");

	public UIVoronoi() {
		
		//On initialise le graphe
		voronoi = new GraphVoronoi();

		//Parametres de la Fenetre
		getContentPane().setBackground(Color.WHITE);
		setResizable(false);
		setTitle("Diagramme de Voronoi");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(707, 466);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setVisible(true);
		
		//Le panel de dessin au centre
		dessin = new ViewVoronoi(voronoi);
		dessin.addMouseListener(this);
		dessin.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(dessin, BorderLayout.CENTER);
			
		//Le panel de controle avec les boutons en bas
		JPanel control = new JPanel();
		FlowLayout flowLayout = (FlowLayout) control.getLayout();
		getContentPane().add(control, BorderLayout.SOUTH);
		
		//On rajoute le bouton clear sur le control
		control.add(clearButton);
		clearButton.addActionListener(this);

	}
	
	//Methodes hérités du patron observateur ActionListener
	
    public void actionPerformed(ActionEvent evt){
    	
    	if(evt.getSource()==clearButton){
    		voronoi.clear();
    		System.out.println("Clear");
    	}
    	getContentPane().repaint();
   	}
	
	//Methodes hérités du patron observateur MouseListener
	
   	public void mousePressed(MouseEvent evt){   		
   		System.out.println("Click!"+"("+evt.getX()+","+evt.getY()+")");
   		voronoi.addCell(evt.getX(),evt.getY());
   		getContentPane().repaint();
   	}
   	public void mouseReleased(MouseEvent evt){}
   	public void mouseClicked(MouseEvent evt){}
   	public void mouseEntered(MouseEvent evt){}
   	public void mouseExited(MouseEvent evt){}
   	
   	//Main de Voronoi
   	
	public static void main(String[] args) {
		
		UIVoronoi MyUI = new UIVoronoi();

	}

}
