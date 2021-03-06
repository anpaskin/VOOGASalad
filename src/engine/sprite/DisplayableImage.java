package engine.sprite;

public interface DisplayableImage extends Comparable<DisplayableImage>{
	public double getX();
	public double getY();
	public double getWidth();
	public double getHeight();
	public double getHeading();
	public String getFileName();
	public int getDrawingPriority();
}
