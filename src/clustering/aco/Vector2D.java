package clustering.aco;

/***
 * Is a 2D vector that the ant uses to move about.
 */
class Vector2D {
    double x;
    double y;

    Vector2D(double _x, double _y) {
        x = _x;
        y = _y;
    }

    double Magnitude() {
        return Math.sqrt(x * x + y * y);
    }
}