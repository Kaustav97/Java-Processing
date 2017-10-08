class Surface {
  ArrayList<Vec2> surface;
  Surface() {
    surface = new ArrayList<Vec2>();
    ChainShape chain = new ChainShape();

    float xoff = 0.0;
    for (float x = width+10; x > -10; x -= 5) {
      float y;
      if (x > width/2) {
        y =200+ (width - x)*1.1 + map(noise(xoff),0,1,-80,80);
      }
      else {
        y =200+ x*1.1 + map(noise(xoff),0,1,-40,40);
      }

      surface.add(new Vec2(x,y));

      xoff += 0.1;

    }

    Vec2[] vertices = new Vec2[surface.size()];
    for (int i = 0; i < vertices.length; i++) {
      Vec2 edge = box2d.coordPixelsToWorld(surface.get(i));
      vertices[i] = edge;
    }

    chain.createChain(vertices,vertices.length);

    BodyDef bd = new BodyDef();
    bd.position.set(0.0f,0.0f);
    Body body = box2d.createBody(bd);
    body.createFixture(chain,1);

  }

  void display() {
    strokeWeight(2);
    stroke(0);
    noFill();
    beginShape();
    for (Vec2 v: surface) {
      vertex(v.x,v.y);
    }
    endShape();
  }
}