/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.jmocap.scene;

import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 *
 * @author Michael Kipp
 */

public class Tiles extends Shape3D {

        private QuadArray _plane;

        public Tiles(ArrayList coords, Color3f col) {
            _plane = new QuadArray(coords.size(), GeometryArray.COORDINATES | GeometryArray.COLOR_3);
            createGeometry(coords, col);
            createAppearance();
        }

        private void createGeometry(ArrayList coords, Color3f col) {
            int numPoints = coords.size();

            Point3f[] points = new Point3f[numPoints];
            coords.toArray(points);
            _plane.setCoordinates(0, points);

            Color3f cols[] = new Color3f[numPoints];
            for (int i = 0; i < numPoints; i++) {
                cols[i] = col;
            }
            _plane.setColors(0, cols);

            setGeometry(_plane);
        }

        private void createAppearance() {
            Appearance app = new Appearance();
            PolygonAttributes pa = new PolygonAttributes();
            // so can see the Tiles from both sides
            pa.setCullFace(PolygonAttributes.CULL_NONE);
            // render only lines
            pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
            app.setPolygonAttributes(pa);
            setAppearance(app);
        }
    }
