import math.geom2d.line.LineSegment2D;
import math.geom2d.Point2D;
import java.util.*;

public class LimitVoronoi extends LineSegment2D {
	
	//Variables globales
	CellVoronoi cell1, cell2;
	
	//Le constructeur de la frontière entre deux cellules
    public LimitVoronoi(CellVoronoi cell1, CellVoronoi cell2, Point2D limit1, Point2D limit2){
        	super(limit1, limit2);
            this.cell1 = cell1;
            this.cell2 = cell2;
        }
    
    public Vector<CellVoronoi> getCells() {
    	Vector<CellVoronoi> cellules = new Vector<CellVoronoi>();
    	cellules.addElement(cell1);
    	cellules.addElement(cell2);
    	return cellules;
    }
    
    public CellVoronoi getOtherCell(CellVoronoi cell) {
    	if (cell == cell1)
    		return cell2;
    	else
    		return cell1;
    }
    
    public boolean intersects (LimitVoronoi lim1){
    	return intersects((LineSegment2D)this, (LineSegment2D)lim1);
    }
}
