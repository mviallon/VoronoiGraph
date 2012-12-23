
import java.util.*;
import javax.swing.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import math.geom2d.Point2D;

@SuppressWarnings("serial")
public class ViewVoronoi extends JPanel{
	
	GraphVoronoi voronoi;
	
	public ViewVoronoi(GraphVoronoi voronoi){
		this.voronoi = voronoi;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//Méthode qui dessine les centres de cellules
		drawKernels(g);
		//Méthode qui dessine les limites
		drawLimits(g);
		}
	
	private void drawKernels(Graphics g){
		
		//Couleur rouge pour les centres des cellules	
		g.setColor(Color.red);
			
		// On crée le graphique sur lequel on va dessiner
		Graphics2D g2 = (Graphics2D) g;

		Iterator<CellVoronoi> listcells = voronoi.getpavage();
		while(listcells.hasNext()){
			Point2D p = (Point2D) listcells.next().getkernel();
			p.draw(g2);
		}
	}
	
	private void drawLimits(Graphics g){
		
		//Couleur rouge pour les centres des cellules	
		g.setColor(Color.blue);
			
		// On crée le graphique sur lequel on va dessiner
		Graphics2D g2 = (Graphics2D) g;

		Iterator<LimitVoronoi> listlimits = voronoi.getlimites();
		while(listlimits.hasNext()){
			listlimits.next().draw(g2);
		}
	}
}
