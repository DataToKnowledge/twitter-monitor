package examples;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by fabiofumarola on 09/06/15.
 */
public class ShowUserInfo {
    /**
     * Usage: java twitter4j.examples.user.ShowUser [screen name]
     *
     * @param args message
     */
    public static void main(String[] args) {
        try {

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("")
                    .setOAuthConsumerSecret("")
                    .setOAuthAccessToken("-")
                    .setOAuthAccessTokenSecret("");


            Twitter twitter = new TwitterFactory(cb.build()).getInstance();
            User user = twitter.showUser("baritoday");
            System.out.println(user.getId());
            if (user.getStatus() != null) {
                System.out.println("@" + user.getScreenName() + " - " + user.getStatus().getText());
            } else {
                // the user is protected
                System.out.println("@" + user.getScreenName());
            }
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to delete status: " + te.getMessage());
            System.exit(-1);
        }
    }

}
