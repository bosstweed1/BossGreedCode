package game;

public class Location {
	private double x,y;
	private int levelCode;
	private boolean level;
	
	public Location(double d,double e,boolean level,int levelCode){
		this.x = d;
		this.y = e;
		this.level = level;
		this.levelCode = levelCode;
		
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public boolean inLevel(){
		return level;
	}
	
	public int getLevelCode(){
		return levelCode;
	}
	
	

}
