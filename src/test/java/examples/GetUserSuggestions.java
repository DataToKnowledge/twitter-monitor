package examples;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Shows suggested users in specified category.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class GetUserSuggestions {
    /**
     * Usage: java twitter4j.examples.suggestedusers.examples.GetUserSuggestions [slug]
     *
     * @param args message Notizie:notizie
     */
    public static void main(String[] args) {
//        if (args.length < 1) {
//            System.out.println("Usage: java twitter4j.examples.suggestedusers.examples.GetUserSuggestions [slug]");
//            System.exit(-1);
//        }
//        System.out.println("Showing suggested users in " + args[0] + " category.");
        try {

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("")
                    .setOAuthConsumerSecret("")
                    .setOAuthAccessToken("")
                    .setOAuthAccessTokenSecret("");

            Twitter twitter = new TwitterFactory(cb.build()).getInstance();
            ResponseList<User> users = twitter.getUserSuggestions("notizie");
            for (User user : users) {
                if (user.getStatus() != null) {
                    System.out.println("@" + user.getScreenName() + " - " + user.getStatus().getText());
                } else {
                    // the user is protected
                    System.out.println("@" + user.getScreenName());
                }
            }
            System.out.println("done.");
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get suggested users: " + te.getMessage());
            System.exit(-1);
        }
    }
}