import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import shiffman.box2d.*; 
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.joints.*; 
import org.jbox2d.collision.shapes.*; 
import org.jbox2d.collision.shapes.Shape; 
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.*; 
import org.jbox2d.dynamics.contacts.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Draw extends PApplet {










Box2DProcessing box2d;

Bridge bridge;

ArrayList<Box> boxes;

public void setup() {
  
  box2d = new Box2DProcessing(this);
  box2d.createWorld();
  bridge = new Bridge(width, width/10);
  boxes = new ArrayList<Box>();
}
public void draw() {
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
class Box {
  Body body;
  float w;
  float h;
  Box(float x, float y) {
    w = random(4, 16);
    h = random(4, 16);
    makeBody(new Vec2(x, y), w, h);
  }
  public void killBody() {
    box2d.destroyBody(body);
  }
  public boolean done() {
    Vec2 pos = box2d.getBodyPixelCoord(body);
    if (pos.y > height+w*h) {
      killBody();
      return true;
    }
    return false;
  }
  public void display() {
    Vec2 pos = box2d.getBodyPixelCoord(body);
    float a = body.getAngle();
    rectMode(CENTER);
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(-a);
    stroke(0);
    fill(127);
    strokeWeight(2);
    rect(0, 0, w, h);
    popMatrix();
  }
  public void makeBody(Vec2 center, float w_, float h_) {
    PolygonShape sd = new PolygonShape();
    float box2dW = box2d.scalarPixelsToWorld(w_/2);
    float box2dH = box2d.scalarPixelsToWorld(h_/2);
    sd.setAsBox(box2dW, box2dH);
    FixtureDef fd = new FixtureDef();
    fd.shape = sd;
    fd.density = 1;
    fd.friction = 0.3f;
    fd.restitution = 0.5f;
    BodyDef bd = new BodyDef();
    bd.type = BodyType.DYNAMIC;
    bd.position.set(box2d.coordPixelsToWorld(center));
    body = box2d.createBody(bd);
    body.createFixture(fd);
    body.setLinearVelocity(new Vec2(random(-5, 5), random(2, 5)));
    body.setAngularVelocity(random(-5, 5));
  }
}

class Bridge {
  float totalLength;
  int numPoints;
  ArrayList<Particle> particles;
  Bridge(float l, int n) {
    totalLength = l;
    numPoints = n;
    particles = new ArrayList();
    float len = totalLength / numPoints;
    for(int i=0; i < numPoints+1; i++) {
      Particle p = null;
      if (i == 0 || i == numPoints) p = new Particle(i*len,height/3,4,true);
      else p = new Particle(i*len,height/3,4,false);
      particles.add(p);
      if (i > 0) {
         DistanceJointDef djd = new DistanceJointDef();
         Particle previous = particles.get(i-1);
         djd.bodyA = previous.body;
         djd.bodyB = p.body;
         djd.length = box2d.scalarPixelsToWorld(len);
         djd.frequencyHz = 0;
	 djd.dampingRatio = 0;
         DistanceJoint dj = (DistanceJoint) box2d.world.createJoint(djd);
      }
    }
  }
  public void display() {
    for (Particle p: particles) {
      p.display();
    }
  }
}

class Particle {
  Body body;
  float r;
  Particle(float x, float y, float r_, boolean fixed) {
    r = r_;
    BodyDef bd = new BodyDef();
    if (fixed) bd.type = BodyType.STATIC;
    else bd.type = BodyType.DYNAMIC;
    bd.position = box2d.coordPixelsToWorld(x,y);
    body = box2d.world.createBody(bd);
    CircleShape cs = new CircleShape();
    cs.m_radius = box2d.scalarPixelsToWorld(r);
    FixtureDef fd = new FixtureDef();
    fd.shape = cs;
    fd.density = 1;
    fd.friction = 0.3f;
    fd.restitution = 0.5f;
    body.createFixture(fd);
  }
  public void killBody() {
    box2d.destroyBody(body);
  }
  public boolean done() {
    Vec2 pos = box2d.getBodyPixelCoord(body);
    if (pos.y > height+r*2) {
      killBody();
      return true;
    }
    return false;
  }
  public void display() {
    Vec2 pos = box2d.getBodyPixelCoord(body);
    float a = body.getAngle();
    pushMatrix();
    translate(pos.x,pos.y);
    rotate(a);
    stroke(0);
    fill(127);
    strokeWeight(2);
    ellipse(0,0,r*2,r*2);
    line(0,0,r,0);
    popMatrix();
  }
}
  public void settings() {  size(600,600,P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Draw" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
