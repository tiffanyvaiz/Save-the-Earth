package gamedesign;


class Pos {
    private double x;
    private double y;    
    public Pos(){}
    public Pos(double xx, double yy) {
        x = xx;
        y = yy;
    }
    public void setX(double xx) {
        x = xx;
    }
    public void setY(double yy) {
        y = yy;
    }
    public double getX() {return x;}
    public double getY() {return y;}
}