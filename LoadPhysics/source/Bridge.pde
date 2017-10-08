
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
  void display() {
    for (Particle p: particles) {
      p.display();
    }
  }
}