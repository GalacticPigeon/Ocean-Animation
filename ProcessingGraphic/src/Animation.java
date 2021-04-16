
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Use processing in Eclipse.
 *
 */
public class Animation extends PApplet {

    /**
     * The argument passed to main must match the class name.
     *
     * @param args
     */
    public static void main(String[] args) {
        PApplet.main("Animation");
    }

    /**
     * Width of frame.
     */
    static final int WIDTH = 960;

    /**
     * Height of frame.
     */
    static final int HEIGHT = 540;

    /**
     * Bubble object and friends.
     */
    int NUM_BUBBLES = 100; //was 100
    float MIN_RADIUS = 10;
    float MAX_RADIUS = 30;

    /**
     * Bubble object.
     */
    Bubble[] bubbles;

    /**
     * Trash object and friends.
     */
    static final int NUM_TRASH = 5 * 2;

    /**
     * Trash object.
     */
    Trash[] trash;

    /**
     * Method used only for setting the size of the window.
     */
    @Override
    public void settings() {
        this.size(WIDTH, HEIGHT, P2D);
    }

    /**
     * identical use to setup in Processing IDE except for size().
     */
    @Override
    public void setup() {
        /**
         * Jug image.
         */
        PImage jug = this.loadImage("half_gallon_jug_small.png");
        /**
         * Straws image.
         */
        PImage straws = this.loadImage("straws_rotate.png");
        this.smooth();
        this.bubbles = new Bubble[this.NUM_BUBBLES];
        this.trash = new Trash[this.NUM_TRASH];
        for (int i = 0; i < this.NUM_BUBBLES; i++) {
            this.bubbles[i] = new Bubble(this.random(WIDTH), -this.MAX_RADIUS,
                    this.random(this.MIN_RADIUS, this.MAX_RADIUS));
        }
        for (int i = 0; i < this.NUM_TRASH; i++) {
            if (i < NUM_TRASH / 2) {
                this.trash[i] = new Trash(this.random(WIDTH),
                        -this.random(HEIGHT + 60), 40, 91, jug);
            } else {
                this.trash[i] = new Trash(this.random(WIDTH),
                        -this.random(HEIGHT + 60), 70, 36, straws);
            }
        }

    }

    /**
     * identical use to draw in Processing IDE.
     */
    @Override
    public void draw() {
        int r = 20;
        int g = 71;
        int b = 138;
        this.background(r, g, b);
        this.update();
        for (int i = 0; i < this.bubbles.length; i++) {
            this.bubbles[i].draw();
        }
        for (int i = 0; i < this.NUM_TRASH; i++) {
            this.trash[i].draw();
        }
        //println(this.mouseX + " " + this.mouseY);
    }

    /**
     * Updates circle array.
     */
    void update() {
        for (int i = 0; i < this.bubbles.length; i++) {
            this.bubbles[i].update();
        }
        for (int i = 0; i < this.NUM_TRASH; i++) {
            this.trash[i].update();
        }
        //this.noBubbleUnderTrash();
    }

    /**
     * Bubbles cannot have the same x coordinate as trash.
     */
    void noBubbleUnderTrash() {
        for (int i = 0; i < this.NUM_BUBBLES; i++) {
            for (int j = 0; j < this.NUM_TRASH; j++) {
                if (Math.abs(this.bubbles[i].x - this.trash[j].x) < 10) {
                    this.bubbles[i].y = HEIGHT - 5;
                    this.bubbles[i].x += this.trash[j].x + 1;
                }
            }
        }
    }

    /**
     * Circle class.
     *
     * @author NickPaulW
     *
     */
    class Bubble {
        float x;
        float y;
        float radius;
        float heading;
        float speed;
        float acceleration;
        PVector v = new PVector(0, 0);

        /**
         * Circle constructor.
         *
         * @param _x
         * @param _y
         * @param _radius
         */
        Bubble(float _x, float _y, float _radius) {
            this.x = _x;
            this.y = _y;
            //this.v.set(_x, _y);
            this.radius = _radius;
            //heading of half circle facing upwards
            //this.heading = Process2.this.random(PI, TWO_PI);
            //heading directly upwards
            this.heading = -HALF_PI;
            this.speed = 6; //orginally 6
        }

        /**
         * Updates circle position.
         */
        void update() {
            this.behavior1();
//            this.behavior2();
//            this.behavior3();
//            this.noPassThrough();
        }

        /**
         * Constant linear motion.
         */
        void behavior1() {
            float theta = 0.03f;
            final float amplitude = 1;
            float dx = sin(this.y * theta) * amplitude;
            float dy = (this.speed) * sin(this.heading);
            this.x += dx;
            this.y += dy;

            Random rand = new Random();
            if (this.y < 0 && rand.nextInt(1500) < 1) {
                this.y = HEIGHT;
            }
        }

        /**
         * No touching.
         */
        void behavior2() {
            //While touching another, move away from center
            //check all circles
            for (int i = 0; i < Animation.this.bubbles.length; i++) {
                //if current circle is not this
                if (Animation.this.bubbles[i] != this) {
                    //check if circle is touching other
                    if (this.touching(Animation.this.bubbles[i])) {
                        Bubble other = Animation.this.bubbles[i];
                        //calculate distance to other circle
                        float d = this.distance(other);
                        //calculate direction in x and y to other circle
                        float dx = (other.x - this.x) / d;
                        float dy = (other.y - this.y) / d;
                        //Move circle away from other
                        this.x -= this.speed * dx;
                        this.y -= this.speed * dy;
                    }
                }
            }
        }

        /**
         * Bind circle to window better.
         */
        void behavior3() {
            //Change of angle
            float DELTA_ANGLE = PI;

            float mx = constrain(this.x, this.radius,
                    Animation.this.width - this.radius);
            float my = constrain(this.y, this.radius,
                    Animation.this.height - this.radius);
            if (this.x < this.radius || this.y < this.radius
                    || this.x > Animation.this.width - this.radius
                    || this.y > Animation.this.height - this.radius) {
                this.x = mx;
                this.y = my;

                //change direction
                //this.heading += Process2.this.random(-DELTA_ANGLE, DELTA_ANGLE);
            }
        }

        /**
         * Tell circles not pass through each other.
         */
        void noPassThrough() {
            //Change of angle
            float DELTA_ANGLE = TWO_PI / 18;
            //check all circles
            for (int i = 0; i < Animation.this.bubbles.length; i++) {
                //if circle is not this
                if (Animation.this.bubbles[i] != this) {
                    //check if circle is touching other
                    if (this.touching(Animation.this.bubbles[i])) {
                        //change direction
                        this.heading += Animation.this.random(-DELTA_ANGLE,
                                DELTA_ANGLE);
                    }
                }
            }
        }

        /**
         * Check if circle is touching another.
         *
         * @param other
         *            the *other* circle
         * @return true or false depending on if cirlce is touching another
         */
        boolean touching(Bubble other) {
            return this.distance(other) < (this.radius + other.radius);
        }

        /**
         * Helper function to get distance a circle is from another.
         *
         * @param other
         *            the *other* circle
         * @return distance of this from other
         */
        float distance(Bubble other) {
            return dist(this.x, this.y, other.x, other.y);
        }

        /**
         * Draws circle.
         */
        void draw() {
            Animation.this.pushStyle();
            Animation.this.blendMode(BLEND);
            Animation.this.stroke(255, 255, 255, 100f);
            Animation.this.strokeWeight(1);
            Animation.this.fill(255, 255, 255, 100f);
            Animation.this.shapeMode(CENTER);
            Animation.this.pushMatrix();
            Animation.this.translate(this.x, this.y);
            Animation.this.rotate(this.heading); // Rotate to the direction of the heading
            Animation.this.ellipse(0, 0, this.radius, this.radius);
            Animation.this.popMatrix();
            Animation.this.popStyle();
        }
    }

    class Trash {
        float x;
        float y;
        float radius;
        float heading;
        float speed;
        /**
         * Width of jug.
         */
        float w = 40;
        /**
         * Height of jug.
         */
        float h = 91f;
        private PImage img;

        /**
         * Circle constructor.
         *
         * @param _x
         * @param _y
         * @param _radius
         */
        Trash(float _x, float _y, float _w, float _h, PImage _img) {
            this.x = _x;
            this.y = _y;
            this.w = _w;
            this.h = _h;
            this.heading = -HALF_PI;
            this.speed = 2f; //orginally 0.3
            this.img = _img;
        }

        /**
         * Updates trash position.
         */
        void update() {
            this.behavior1();
            //this.behavior3();
        }

        /**
         * Constant linear motion.
         */
        void behavior1() {
            Random rand = new Random();
            float theta = 0.03f;
            if (rand.nextInt(150) < 1) {
                theta = -0.3f;
            }
            final float amplitude = 1;
            float dx = sin(this.y * theta) * amplitude;
            float dy = (this.speed) * sin(this.heading);
            this.x += dx;
            this.y += dy;

            if (this.y < 0 && rand.nextInt(150) < 1) {
                this.y = HEIGHT + this.radius + 5;
            }

        }

        /**
         * Bind circle to window better.
         */
        void behavior3() {

            float mx = constrain(this.x, this.h, Animation.this.width - this.h);
            float my = constrain(this.y, this.w,
                    Animation.this.height - this.w);
            if (this.x < this.h || this.y < this.w
                    || this.x > Animation.this.width - this.h
                    || this.y > Animation.this.height - this.w) {
                this.x = mx;
                this.y = my;
            }
        }

        /**
         * Creates a shape based on parameters.
         *
         * @param texture
         */
        void createShape(PImage texture) {
            Animation.this.beginShape();
            Animation.this.texture(texture);
            Animation.this.vertex(0, 0, 0, 0);
            Animation.this.vertex(this.w, 0, this.w, 0);
            Animation.this.vertex(this.w, this.h, this.w, this.h);
            Animation.this.vertex(0, this.h, 0, this.h);
            Animation.this.scale(0.8f);
            Animation.this.endShape(CLOSE);
        }

        /**
         * Draws circle.
         */
        void draw() {
            Animation.this.pushStyle();
            Animation.this.blendMode(BLEND);
            Animation.this.shapeMode(CENTER);
            Animation.this.noStroke();
            Animation.this.pushMatrix();
            Animation.this.translate(this.x, this.y);
            Animation.this.rotate(this.heading); // Rotate to the direction of the heading
            PImage jug = Animation.this.loadImage("half_gallon_jug_small.png");
            this.createShape(this.img);
//            PImage straws = Animation.this.loadImage("straws.png");
//            this.createShape(36, 70, straws);

            Animation.this.popMatrix();
            Animation.this.popStyle();
        }
    }
}