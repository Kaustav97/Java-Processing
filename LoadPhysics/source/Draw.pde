import shiffman.box2d.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.joints.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;

Box2DProcessing box2d;

Bridge bridge;

ArrayList<Box> boxes;

void setup() {
  size(600,600,P3D);
  box2d = new Box2DProcessing(this);
  box2d.createWorld();
  bridge = new Bridge(width, width/10);
  boxes = new ArrayList<Box>();
}
void draw() {
  background(255);
  box2d.step();
  if (mousePressed) {
    Box p = new Box(mouseX, mouseY);
    boxes.add(p);
  }
  for (Box b: boxes) {
    b.display();
  }
  for (int i = boxes.size()-1; i >= 0; i--) {
    Box b = boxes.get(i);
    if (b.done()) {
      boxes.remove(i);
    }
  }
  bridge.display();
  fill(0);
}