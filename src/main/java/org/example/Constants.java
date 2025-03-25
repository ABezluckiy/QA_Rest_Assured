package org.example;

import static org.example.Methods.getUniqueString;

public class Constants {
    public static final String correctEmailUser = "example@mail.com";
    public static final String incorrectEmailUser = "example";
    public static final String correctPasswordUser = "examplePassword";
    public static final String incorrectPasswordUser = "eee";

    public static final String correctUserFirstname = "ExampleFirstName";
    public static final String correctUserLastname = "ExampleLastName";
    public static final String userImage = "src/test/resources/images/user.png";

    public static final String newsName = getUniqueString() + " news";
    public static final String newsDescription = getUniqueString() + " description";
    public static final String newsTags = "tag1-" + getUniqueString() + ",tag2-" + getUniqueString();
    public static final String newsImage = "src/test/resources/images/news.png";
}
