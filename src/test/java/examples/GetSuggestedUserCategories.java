package examples;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Shows authenticated user's suggested user categories.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class GetSuggestedUserCategories {
    /**
     * Usage: java twitter4j.examples.suggestedusers.examples.GetSuggestedUserCategories
     *
     * @param args message
     */
    public static void main(String[] args) {
        try {

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("")
                    .setOAuthConsumerSecret("")
                    .setOAuthAccessToken("")
                    .setOAuthAccessTokenSecret("");

            Twitter twitter = new TwitterFactory(cb.build()).getInstance();
            System.out.println("Showing suggested user categories.");
            ResponseList<Category> categories = twitter.getSuggestedUserCategories();
            for (Category category : categories) {
                System.out.println(category.getName() + ":" + category.getSlug());
            }
            System.out.println("done.");
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get suggested categories: " + te.getMessage());
            System.exit(-1);
        }
    }
}