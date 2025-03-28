package org.example;


public class Constants {
    private static final Methods methods = new Methods();

    public static final String correctEmailUser = "example@mail.com";
    public static final String incorrectEmailUser = "example";
    public static final String correctPasswordUser = "examplePassword";
    public static final String incorrectPasswordUser = "eee";

    public static final String correctUserFirstname = "ExampleFirstName";
    public static final String correctUserLastname = "ExampleLastName";
    public static final String userImage = "src/test/resources/images/user.png";

    public static final String postName = methods.getUniqueString() + " post";
    public static final String postDescription = methods.getUniqueString() + " description";
    public static final String[] postTags = {"tag1-" + methods.getUniqueString(), "tag2-" + methods.getUniqueString()};
    public static final String postImage = "src/test/resources/images/news.png";
    public static final int postLimit = 1;
    public static final String postComment = methods.getUniqueString() + " comment";
}
