import java.util.*;

import math.geom2d.Point2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.line.LineSegment2D;

public class GraphVoronoi {
	
	public Vector<CellVoronoi> pavage;
	public Vector<LimitVoronoi> limites;
	public Contour contour;
    // Points du contour
    public Point2D p0 = new Point2D(0,0);
	public Point2D p1 = new Point2D(700,0);
	public Point2D p2 = new Point2D(700,400);
	public Point2D p3 = new Point2D(0,400);
	
	//On construit le graphe comme un ensemble de cellules
    public GraphVoronoi(){
        pavage = new Vector<CellVoronoi>();
        limites = new Vector<LimitVoronoi>();
        contour = new Contour();
     }
	
	//M�thode pour effacer toutes les cellules
	public void clear(){
		
		pavage.removeAllElements();
		limites.removeAllElements();
	}
	
	//M�thode pour ajouter une cellule
	public void addCell(int x, int y){
		
		//On cr�e la cellule a partir de son noyau
		Point2D p = new Point2D(x,y);
		CellVoronoi cell = new CellVoronoi(p);
				
		//On distingue le cas de la premi�re cellule du graphe
		if (pavage.size() == 0) {
			cell.addvoisin(null); //On rajoute un voisin fictif qui repr�sente l'ext�rieur
			
			//On rajoute les limites de la cellule
			addLimit(cell,null,p0,p1);
			addLimit(cell,null,p1,p2);
			addLimit(cell,null,p2,p3);
			addLimit(cell,null,p3,p0);
									
			// A virer
			for (int i = 0; i < cell.getVoisins().size(); i++) {
			System.out.println(cell.getVoisins().elementAt(i)); 
			}
			
		/*---------------------------------------------------------------------------Debug---------------------------------------------------------------------*/
			System.out.println(limites);
		}
		
		//Cas g�n�ral
		else {
			//On cherche la cellule existante la plus proche
			CellVoronoi nearest = nearestCell(p);
			
			//On trace la m�diatrice
			StraightLine2D median = StraightLine2D.createMedian(p,nearest.getkernel());
			
			//On r�cup�re les fronti�res de la cellule dans laquelle on se trouve
			Iterator<LimitVoronoi> listlimits = nearest.getlimits();
			
			/*On cherche les points d'intersection avec ces frontieres et on stocke  :
				- les fronti�res dans le vecteur intersect
				- les limites qui comportent ces points d'intersection */
			Vector<Point2D> intersect = new Vector<Point2D>();
			Vector<LimitVoronoi> limitesintersect = new Vector<LimitVoronoi>();
			
			while(listlimits.hasNext()) {
				LimitVoronoi limit = listlimits.next();
				Point2D i = StraightLine2D.getIntersection(median, limit);
				//Il faut v�rifier que le point est dans le segment
				if (limit.contains(i.getX(), i.getY())) {
					intersect.addElement(i);
					limitesintersect.addElement(limit);
				}
				
			}
			
			//Juste pour tester pour le moment on renvoie les coordonn�es d'intersection de la m�diatrice avec les bords de la cellule
			Iterator<Point2D> listinter = intersect.iterator();
			while(listinter.hasNext()) {
				Point2D next = listinter.next();
				System.out.println("Intersection: ("+next.getX()+","+next.getY()+")");
			}
			

			
			// C'est � partir de maintenant que l'on distingue les diff�rents cas de l'algorithme :
			
			// ----------------------------------------------------Cas 1 : les deux points sont situ�s sur le contour-------------------------------------------------------
			
			
			if(contour.contient(intersect.elementAt(0)) && contour.contient(intersect.elementAt(1))) {
				//On ajoute la nouvelle fronti�re
				addLimit(cell, nearest, intersect.elementAt(0), intersect.elementAt(1));
				
				// On met � jour les pr�c�dentes fronti�res de "nearest" et celles de "cell".
				for (int i=0 ; i<=1 ; i++) {
					scinder(limitesintersect.elementAt(i), intersect.elementAt(i), nearest, cell);
				}
				System.out.println(nearest.limits);
				System.out.println(cell.limits);
				
				/* On rappelle que le vecteur contenant les limites de "cell" comprend :
					- en premier la m�diatrice entre p et nearest.getkernel()
					- en second et trois�me les limites lim1 et lim 2 issues du r�sultat de la fonction scinder(...)*/
				Point2D q = cell.getlimit().elementAt(1).getFirstPoint();
				// A partir du point q de lim1, on cherche � retrouver lim2 en parcourant les limites de nearest
				while (!q.equals(cell.getlimit().elementAt(2).getFirstPoint())){
					Iterator<LimitVoronoi> listlimitsbis = nearest.getlimits();
					while(listlimitsbis.hasNext()) {
						LimitVoronoi limit = listlimitsbis.next();
						// Les limites de l'ancienne cellule ayant �t� scind�es, une seule contiendra q
						if (limit.contains(q)) {
							removeLimit(nearest, null, limit);
							addLimit(cell, null, limit);
							// On d�finit alors le nouveau point q et l'on casse la boucle.
							if(q.equals(limit.getFirstPoint())){							
								q=limit.getLastPoint();	
								System.out.println(q);
							}
							else {
								q=limit.getFirstPoint();
								System.out.println(q);
							}
							break;
						}
					}
				}
				
				System.out.println("on est dans la boucle cas 1");
			}
			
			
			// --------------------------------------------------------Cas 2 : aucun des points d'intersection n'est situ� sur le contour---------------------------------------
			
			
			// On part du point "intersect.elementAt(0)" et deux cas se pr�sentent � nouveau en appliquant l'algo :
			// - soit on retombe sur le point "intersect.elementAt(1)"
			// - soit on tombe sur un point du contour ; il faudra alors reprendre l'algo � partir du point "intersect.elementAt(1)" jusqu'� tomber sur un autre point du contour
			else if (!contour.contient(intersect.elementAt(0)) && !contour.contient(intersect.elementAt(1))){
				// On applique l'algorithme en partant du point "intersect.elementAt(0)"
				Point2D r = algorithme(p, intersect.elementAt(0), intersect.elementAt(1), nearest, cell, limitesintersect.elementAt(0));
				// Si r est �gal � "intersect.elementAt(1)", il n'y a plus rien � faire !
				// On se place maintenant dans le cas o� le point r est un point du contour ; il faut repartir du point "intersect.elementAt(1)"
				if(contour.contient(r)){
					// 1er probl�me : �ventuellement 2 limites au lieu d'une + non supression de certaines anciennes limites + ar�tes du contour
					// Le cas trait� dans la suite ne prend en compte que l'�ventualit� o� les points r et s sont situ�s sur la m�me ar�te de l'objet contour ; de plus, il peut y avoir des limites d'anciennes cellules que je n'enl�ve pas...
					// On retire la derni�re limite ajout�e � cell sinon deux limites au lieu d'une seule
					cell.removeLimit(cell.getlimit().lastElement());
					limites.remove(limites.size()-1);
					// On retire la limite provenant de la m�diatrice initiale car elle va �tre recr��e avec l'application de "algorithme(...)"
					LimitVoronoi limit = new LimitVoronoi(nearest, cell, intersect.elementAt(0), intersect.elementAt(1));
					removeLimit(nearest, cell, limit);
					Point2D s = algorithme(p, intersect.elementAt(1), intersect.elementAt(0), nearest, cell, limitesintersect.elementAt(1));
					// De la m�me mani�re, on retire la derni�re limite ajout�e � cell sinon deux limites au lieu d'une seule
					cell.removeLimit(cell.getlimit().lastElement());
					limites.remove(limites.size()-1);
					// Enfin on rajoute la limite compos�e des deux segments enlev�s au pr�alable.
					addLimit(cell, null, r, s);
				}
				System.out.println("on est dans la boucle cas 2");
			}
			
			
			// ---------------------------------------------------------Cas 3 : Un seul des points est situ� sur le contour-------------------------------------------------------
			
			
			else {
				// 2�me probl�me identique au 1er : ar�tes du contour + non suppression de certaines anciennes limites
				// Le cas trait� dans la suite ne prend en compte que l'�ventualit� o� les points intersect.elementAt(...) et s sont sur la m�me ar�te du contour.
				// On cherche � savoir quel bout du segment touche le contour pour ensuite appliquer l'algorithme
				if(contour.contient(intersect.elementAt(0))){
					scinder(limitesintersect.elementAt(0), intersect.elementAt(0), nearest, cell);
					// On retire la derni�re limite ajout�e � cell sinon deux limites au lieu d'une seule
					removeLimit(cell.getlimit().lastElement());
					Point2D s = algorithme(p, intersect.elementAt(1), intersect.elementAt(0), nearest, cell, limitesintersect.elementAt(1));
					// On retire la derni�re limite ajout�e � cell sinon deux limites au lieu d'une seule
					removeLimit(cell.getlimit().lastElement());
					// Enfin on rajoute la limite compos�e des deux segments enlev�s au pr�alable.
					addLimit(cell, null, intersect.elementAt(0), s);
				}
				// Principe identique 
				else {
					scinder(limitesintersect.elementAt(1), intersect.elementAt(1), nearest, cell);
					cell.removeLimit(cell.getlimit().lastElement());
					limites.remove(limites.size()-1);
					Point2D s = algorithme(p, intersect.elementAt(0), intersect.elementAt(1), nearest, cell, limitesintersect.elementAt(0));
					cell.removeLimit(cell.getlimit().lastElement());
					System.out.println(limites);
					addLimit(cell, null, intersect.elementAt(1), s);
				}
				System.out.println("on est dans la boucle cas 3");
			
			}
			/*---------------------------------------------------------------------------Debug---------------------------------------------------------------------*/
			System.out.println(limites);
		}
		
		//On rajoute la nouvelle cellule dans le pavage
		pavage.addElement(cell);
		
	}
	
	//Methode pour ajouter une limite
	public void addLimit(CellVoronoi cell1, CellVoronoi cell2, Point2D p1, Point2D p2) {
		LimitVoronoi limit = new LimitVoronoi(cell1, cell2, p1, p2);
		if (cell1 != null) {
			cell1.addLimit(limit);
		}
		if (cell2 != null) {
			cell2.addLimit(limit);
		}
		//On rajoute la limite dans le vecteur
		limites.addElement(limit);
	}
	
	// Surcharge de la m�thode pr�c�dente
	public void addLimit(CellVoronoi cell1, CellVoronoi cell2, LimitVoronoi limit) {
		if (cell1 != null) {
			cell1.addLimit(limit);
		}
		if (cell2 != null) {
			cell2.addLimit(limit);
		}
		//On rajoute la limite dans le vecteur
		limites.addElement(limit);
	}
	
	//Methode pour retirer une limite
	public void removeLimit (CellVoronoi cell1, CellVoronoi cell2, LimitVoronoi limit) {
		if (cell1 != null) {
			cell1.removeLimit(limit);
		}
		if (cell2 != null) {
			cell2.removeLimit(limit);
		}
		limites.remove(limit);
	}
	


	//M�thode pour renvoyer la cellule la plus proche d'un point
	public CellVoronoi nearestCell(Point2D p){
		Iterator<CellVoronoi> listcells = pavage.iterator();
		CellVoronoi nearest = listcells.next();
		double distance = Point2D.getDistance(p,nearest.getkernel());
		
		while(listcells.hasNext()){
			CellVoronoi celltest = listcells.next();
			if (Point2D.getDistance(p,celltest.getkernel()) < distance) {
				nearest = celltest;
				distance = Point2D.getDistance(p,nearest.getkernel());
			}
		}
		return nearest;
	}
	
	//M�thode pour renvoyer le vecteur des cellules
    public Iterator<CellVoronoi> getpavage(){
        return pavage.iterator();
     }
    
	//M�thode pour renvoyer le vecteur des limites
    public Iterator<LimitVoronoi> getlimites(){
        return limites.iterator();
     }
    
    
    // M�thode pour scinder une limite en deux
    public CellVoronoi scinder(LimitVoronoi limit, Point2D intersection, CellVoronoi formercell, CellVoronoi newcell) {
    		// Pour chacune des intersections...
			
				LimitVoronoi lim1;
				LimitVoronoi lim2;
				CellVoronoi voisin;
				Point2D extremite1 = new Point2D(limit.getFirstPoint());
				Point2D extremite2 = new Point2D(limit.getLastPoint());
				
				voisin = limit.getOtherCell(formercell);
				
				//... on cherche quel bout de segment appartient � quelle cellule
				if(extremite1.getDistance(formercell.getkernel()) < extremite1.getDistance(newcell.getkernel())) {
					// ... et l'on cherche le voisin de l'ancienne fronti�re pour cr�er les deux nouvelles fronti�res
				
					lim1 = new LimitVoronoi(formercell, voisin, extremite1, intersection);
					lim2 = new LimitVoronoi(newcell, voisin, extremite2, intersection);
				}
				
				else {
					
					lim1 = new LimitVoronoi(formercell, voisin, extremite2, intersection);
					lim2 = new LimitVoronoi(newcell, voisin, extremite1, intersection);	
				}
				// NB : par construction, le point d'intersection constitue toujours le "LastPoint" des nouvelles limites cr��es.
				// On retire l'ancienne limite.
				removeLimit(formercell, voisin, limit);
				
				// Dans tous les cas, on rajoute le bout n�1 de limite � l'ancienne cellule.
				
				addLimit(formercell, voisin, lim1);
				
				// On rajoute le bout de limite n�2 � la nouvelle cellule seulement si il appartient au contour.
				if(contour.contient(intersection)){
				addLimit(newcell, voisin, lim2);
				}
				return voisin;

	}
    
    // M�thode pour appliquer l'algorithme (boucle pour scinder et passer au prochain point d'intersection)
    public Point2D algorithme (Point2D p, Point2D intersection1, Point2D intersection2, CellVoronoi anciennecellule, CellVoronoi cell, LimitVoronoi anciennelimite) {
    	// C'est ce point qui permettra de savoir si l'on a boucl� ou non
    	Point2D pointancrage = intersection2;
    	do {
			CellVoronoi voisin = scinder(anciennelimite, intersection1, anciennecellule, cell);
			addLimit(cell, anciennecellule, intersection1, intersection2);
			StraightLine2D mediatrice = StraightLine2D.createMedian(p,voisin.getkernel());
			Iterator<LimitVoronoi> listlimitsbis = voisin.getlimits();
			while(listlimitsbis.hasNext()) {
				LimitVoronoi limit = listlimitsbis.next();
				Point2D i = StraightLine2D.getIntersection(mediatrice, limit);
				//Il faut v�rifier que le point est dans le segment et que le nouveau point d'intersection n'est pas l'ancien (c'est-�-dire q)
				if (limit.contains(i.getX(), i.getY()) && !i.equals(intersection2)) {
					intersection2 = intersection1;
					intersection1 = i;
					anciennelimite = limit;
					
					break;
				}
				
			}
			anciennecellule=voisin;
		}
		while (!intersection1.equals(pointancrage) && !contour.contient(intersection1));
    	// On doit appliquer la m�thode scinder(...) une derni�re fois 
    	addLimit(cell, anciennecellule, intersection1, intersection2);
    	scinder(anciennelimite, intersection1, anciennecellule, cell);
    	
     	return intersection1;
    }
}
