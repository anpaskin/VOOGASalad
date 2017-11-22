import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import engine.BoundedImage;
import engine.utilities.collisions.BoundingGeometry;
import engine.utilities.collisions.BoundingPolygon;
import engine.utilities.collisions.RelativeBoundingGeometry;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * @author Ian Eldridge-Allegra
 *
 */
public class BoundingPolygonCreator extends Pane{

	private static final double PHANTOM_OPACITY = .5;
	private static final double LINE_WIDTH = 3;
	private List<RelativeBoundingGeometry> geometries = new ArrayList<>();
	private List<Point2D> vertices = new ArrayList<>();
	private Line phantomLine;
	private Consumer<BoundedImage> consumer;
	private String imageName;
	
	public BoundingPolygonCreator(Image image, String imageName, Consumer<BoundedImage> consumer) {
		getChildren().add(new ImageView(image));
		this.imageName = imageName;
		this.consumer = consumer;
		setup();
		requestFocus();
	}
	
	private void setup() {
		setOnMouseClicked(this::mouseClicked);
		setOnMouseMoved(this::mouseMoved);
		setOnKeyPressed(this::keyPressed);
	}
	
	private void keyPressed(KeyEvent event) {
		System.out.println("pressed");
		if(event.getCode().equals(KeyCode.S))
			consumer.accept(new BoundedImage(imageName, geometries));
	}
	
	private void mouseClicked(MouseEvent event) {
		if(event.getButton().equals(MouseButton.SECONDARY))
			rightClick(event);
		else
			leftClick(event);
	}
	
	private void leftClick(MouseEvent event) {
		Point2D clicked = new Point2D(event.getX(), event.getY());
		if(!isLegalNextPoint(clicked))
			return;
		if(!vertices.isEmpty()) {
			addLine(generateLine(lastPoint(), clicked));
		}
		vertices.add(clicked);
		changePhantomLine(new Line());
	}
	
	private boolean isLegalNextPoint(Point2D point) {
		if(vertices.size() < 2)
			return true;
		Point2D previousSide = lastPoint().subtract(vertices.get(vertices.size()-2));
		Point2D nextSide = point.subtract(lastPoint());
		if(previousSide.crossProduct(nextSide).getZ()<0)
			return false;
		Point2D finalSide = vertices.get(0).subtract(point);
		if(nextSide.crossProduct(finalSide).getZ()<0)
			return false;
		Point2D originalSide = vertices.get(1).subtract(vertices.get(0));
		return finalSide.crossProduct(originalSide).getZ()>=0;
	}
	
	private void addLine(Line l) {
		getChildren().add(l);
	}
	
	private Line generateLine(Point2D start, Point2D end) {
		Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
		line.setStrokeWidth(LINE_WIDTH);
		return line;
	}
	
	private void changePhantomLine(Line newLine) {
		if(phantomLine != null)
			getChildren().remove(phantomLine);
		phantomLine = newLine;
		if(phantomLine != null)
			getChildren().add(phantomLine);
	}
	
	private Point2D lastPoint() {
		return vertices.get(vertices.size()-1);
	}

	private void rightClick(MouseEvent event) {
		if(vertices.isEmpty())
			return;
		addLine(generateLine(lastPoint(), vertices.get(0)));
		changePhantomLine(null);
		
		generateRelativeGeometry();
	}

	private void generateRelativeGeometry() {
		BoundingGeometry poly = new BoundingPolygon(vertices);
		poly = poly.getScaled(1.0/getWidth(), 1.0/getHeight()).getTranslated(-.5, -.5);
		geometries.add(new RelativeBoundingGeometry(poly));
		vertices.clear();
		System.out.println(geometries);
	}

	private void mouseMoved(MouseEvent event) {
		if(phantomLine == null)
			return;
		Point2D location = new Point2D(event.getX(), event.getY());
		Line newPhantom = generateLine(lastPoint(), location);
		newPhantom.setOpacity(PHANTOM_OPACITY);
		if(!isLegalNextPoint(location))
			newPhantom.setStroke(Color.RED);
		changePhantomLine(newPhantom);
	}
}
