package examples;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Given a latitude and a longitude or IP address, searches for up to 20 places that can be used as a place_id when updating a status.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class SearchPlaces {
    /**
     * Usage: java twitter4j.examples.geo.examples.SearchPlaces [ip address] or [latitude] [longitude]
     *
     * @param args message
     */
    public static void main(String[] args) {
//        if (args.length < 1) {
//            System.out.println("Usage: java twitter4j.examples.geo.examples.SearchPlaces [ip address] or [latitude] [longitude]");
//            System.exit(-1);
//        }
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("")
                    .setOAuthConsumerSecret("")
                    .setOAuthAccessToken("")
                    .setOAuthAccessTokenSecret("");

            Twitter twitter = new TwitterFactory(cb.build()).getInstance();
            GeoQuery query = new GeoQuery(new GeoLocation(Double.parseDouble("41.117143"), Double.parseDouble("16.871871")));
            query.setAccuracy("10000m");

            ResponseList<Place> places = twitter.searchPlaces(query);
            if (places.size() == 0) {
                System.out.println("No location associated with the specified IP address or lat/lang");
            } else {
                for (Place place : places) {
                    System.out.println("id: " + place.getId() + " name: " + place.getFullName());
                    Place[] containedWithinArray = place.getContainedWithIn();
                    if (containedWithinArray != null && containedWithinArray.length != 0) {
                        System.out.println("  contained within:");
                        for (Place containedWithinPlace : containedWithinArray) {
                            System.out.println("  id: " + containedWithinPlace.getId() + " name: " + containedWithinPlace.getFullName());
                        }
                    }
                }
            }
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to retrieve places: " + te.getMessage());
            System.exit(-1);
        }
    }
}