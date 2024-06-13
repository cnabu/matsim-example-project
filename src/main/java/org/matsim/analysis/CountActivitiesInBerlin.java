package org.matsim.analysis;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;

public class CountActivitiesInBerlin {
    public static void main(String[] args) {

        var population = PopulationUtils.readPopulation("C:\\Users\\arsal\\Downloads\\Berlin_Bezirksgrenzen_-2292027283764261881\\berlin-v5.5.3-1pct.output_plans.xml.gz");
        var features = ShapeFileReader.getAllFeatures("C:\\Users\\arsal\\Downloads\\Berlin_Bezirksgrenzen_-2292027283764261881\\Berlin_Bezirke.shp");
        var mitte = features.stream()
                .filter(feature -> feature.getAttribute("Gemeinde_n").equals("Mitte"))
                .map(feature -> (Geometry)feature.getDefaultGeometry())
                .findAny()
                .orElseThrow();

        var counter = 0;
        var transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");

        for (Person person : population.getPersons().values()) {

            var plan = person.getSelectedPlan();
            var firstElement = plan.getPlanElements().get(0);
            var activity = (Activity)firstElement;
            var activityCoord = activity.getCoord();
            var transformedActivityCoord = transformation.transform(activityCoord);
            var activityPoint = MGC.coord2Point(transformedActivityCoord);

            if (mitte.contains(activityPoint)) {
                counter++;
            }
        }

        System.out.println("There are " + counter + " home activities in Mitte.");
    }
}
