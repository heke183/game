package com.xianglin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListEqualsRemove {

    public static void main(String[] args) {

        List<User> list = new ArrayList<>();
        User user = new User();
        user.age = 1;
        user.name = "小敏";

        User user1 = new User();
        user1.age = 1;
        user1.name = "小红";

        list.add(user);
        list.add(user1);

        System.out.println(list);

        User user2 = new User();
        user2.age = 1;
        list.remove(user2);

        System.out.println(list);

    }

    static class User {
        int age;

        String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return age == user.age;
        }

        @Override
        public int hashCode() {
            return Objects.hash(age);
        }

        @Override
        public String toString() {
            return "User{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
