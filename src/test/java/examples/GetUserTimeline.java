package examples;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;


/**
 * Created by fabiofumarola on 09/06/15.
 */
public class GetUserTimeline {

    /**
     * Usage: java twitter4j.examples.timeline.examples.GetUserTimeline
     *
     * @param args String[]
     */
    public static void main(String[] args) {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("")
                .setOAuthConsumerSecret("")
                .setOAuthAccessToken("")
                .setOAuthAccessTokenSecret("");

        // gets Twitter instance with default credentials
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        try {
            List<Status> statuses;
            String user = "baritoday";
            statuses = twitter.getUserTimeline(user);

            System.out.println("Showing @" + user + "'s user timeline.");
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
    }
}
