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

    public static final String newsName = methods.getUniqueString() + " news";
    public static final String newsDescription = methods.getUniqueString() + " description";
    public static final String[] newsTags = {"tag1-" + methods.getUniqueString(), "tag2-" + methods.getUniqueString()};
    public static final String newsImage = "src/test/resources/images/news.png";
}
