import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import shiffman.box2d.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class NoiseChain extends PApplet {

Box2DProcessing box2d;
ArrayList<Particle> particles;
Surface surface;

public void setup(){

  box2d = new Box2DProcessing(this);
  box2d.createWorld();
  box2d.setGravity(0, -20);

  particles = new ArrayList<Particle>();
  surface = new Surface();
}

public void draw(){
  if (mousePressed){
    float sz = random(2,6);
    particles.add(new Particle(mouseX,mouseY,sz));
  }
  box2d.step();

  background(255);
  surface.display();
  for (Particle p: particles){
    p.display();
  }
  for (int i = particles.size()-1; i >= 0; i--){
    Particle p = particles.get(i);
    if (p.done()){
      particles.remove(i);
    }
  }
}
class Particle{
  Body body;
  float r;

  Particle(float x, float y, float r_){
    r = r_;
    makeBody(x,y,r);
  }
  public void killBody(){
    box2d.destroyBody(body);
  }
  public boolean done(){
    Vec2 pos = box2d.getBodyPixelCoord(body);
    if (pos.y > height+r*2){
      killBody();
      return true;
    }
    return false;
  }

  public void display(){
    Vec2 pos = box2d.getBodyPixelCoord(body);
    float a = body.getAngle();
    pushMatrix();
    translate(pos.x,pos.y);
    rotate(-a);
    fill(175);
    stroke(0);
    strokeWeight(1);
    ellipse(0,0,r*2,r*2);
    line(0,0,r,0);
    popMatrix();
  }
  public void makeBody(float x, float y, float r){
    BodyDef bd = new BodyDef();
    bd.position = box2d.coordPixelsToWorld(x,y);
    bd.type = BodyType.DYNAMIC;
    body = box2d.world.createBody(bd);

    CircleShape cs = new CircleShape();
    cs.m_radius = box2d.scalarPixelsToWorld(r);

    FixtureDef fd = new FixtureDef();
    fd.shape = cs;
    // Physics Properties:
    fd.density = 1;
    fd.friction = 0.01f;
    fd.restitution = 0.3f;

    // Rig
    body.createFixture(fd);

    body.setLinearVelocity(new Vec2(random(-10f,10f),random(5f,10f)));
    body.setAngularVelocity(random(-10,10));
  }
}
class Surface {
  ArrayList<Vec2> surface;
  Surface(){
    surface = new ArrayList<Vec2>();
    ChainShape chain = new ChainShape();

    float xoff = 0.0f;
    for (float x = width+10; x > -10; x -= 5){
      float y;
      if (x > width/2){
        y =200+ (width - x)*1.1f + map(noise(xoff),0,1,-80,80);
      }
      else {
        y =200+ x*1.1f + map(noise(xoff),0,1,-40,40);
      }

      surface.add(new Vec2(x,y));

      xoff += 0.1f;

    }

    Vec2[] vertices = new Vec2[surface.size()];
    for (int i = 0; i < vertices.length; i++){
      Vec2 edge = box2d.coordPixelsToWorld(surface.get(i));
      vertices[i] = edge;
    }

    chain.createChain(vertices,vertices.length);

    BodyDef bd = new BodyDef();
    bd.position.set(0.0f,0.0f);
    Body body = box2d.createBody(bd);
    body.createFixture(chain,1);

  }

  public void display(){
    strokeWeight(2);
    stroke(0);
    noFill();
    beginShape();
    for (Vec2 v: surface){
      vertex(v.x,v.y);
    }
    endShape();
  }
}
  public void settings(){  size(600,600,P2D); }
  static public void main(String[] passedArgs){
    String[] appletArgs = new String[] { "NoiseChain" };
    if (passedArgs != null){
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
