import math.geom2d.line.LineSegment2D;
import math.geom2d.Point2D;
import java.util.*;

public class Contour {
	
    Point2D p0 = new Point2D(0,0);
	Point2D p1 = new Point2D(700,0);
	Point2D p2 = new Point2D(700,400);
	Point2D p3 = new Point2D(0,400);
	LineSegment2D line1 = new LineSegment2D(p0,p1);
	LineSegment2D line2 = new LineSegment2D(p1,p2);
	LineSegment2D line3 = new LineSegment2D(p2,p3);
	LineSegment2D line4 = new LineSegment2D(p3,p0);
	Vector<LineSegment2D> contour= new Vector<LineSegment2D>();


    public Contour(){
    	contour.addElement(line1);
		contour.addElement(line2);
		contour.addElement(line3);
		contour.addElement(line4);
    }
    
	//Méthode pour savoir si un point est contenu dans une liste de LineSegment2D
	public boolean contient(Point2D m) {
		boolean bool;
			if (contour.elementAt(0).contains(m) || contour.elementAt(1).contains(m) || contour.elementAt(2).contains(m) || contour.elementAt(3).contains(m)){
				bool = true;				
			}
			else bool = false;
		return bool;
	}
}
