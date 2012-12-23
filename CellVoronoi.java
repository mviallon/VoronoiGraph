import java.util.Iterator;
import java.util.Vector;

import math.geom2d.Point2D;

public class CellVoronoi {

	Point2D kernel;
	Vector<CellVoronoi> voisins;
	Vector<LimitVoronoi> limits;
	
	//Le constructeur de la cellule
	public CellVoronoi(Point2D p) {
		voisins = new Vector<CellVoronoi>();
		limits = new Vector<LimitVoronoi>();
		kernel = p;
	}
	
	//Ajoute un voisin à la cellule
	public void addvoisin(CellVoronoi voisin) {
		voisins.addElement(voisin);
	}
	
	public Vector<CellVoronoi> getVoisins(){
		return voisins;
	}
	
	//Ajoute une frontière à la cellule
	public void addLimit(LimitVoronoi limit) {
		limits.addElement(limit);
	}
	
	//Enlève une frontière à la cellule
	public void removeLimit (LimitVoronoi limit) {
		limits.removeElement(limit);
	}
	
	
	//Renvoie le centre de la cellule
	public Point2D getkernel() {
		return kernel;
	}
	
	//Méthode pour renvoyer l'itérateur des limites
    public Iterator<LimitVoronoi> getlimits(){
        return limits.iterator();
     }
  
    //Méthode pour renvoyer le vecteur contenant les limites (to be checked : peut-on récuperer des éléments de l'itérateur ? Si oui, méthode à supprimer)
    public Vector<LimitVoronoi> getlimit(){
    	return limits;
    	
    }
}
