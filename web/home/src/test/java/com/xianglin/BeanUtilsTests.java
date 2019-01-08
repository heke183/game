package com.xianglin;

import org.springframework.beans.BeanUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class BeanUtilsTests {

    public static void main(String[] args) {

        Student student = new Student();
        student.setAge(10);
        System.out.println(student.getCount().incrementAndGet());

        Student target = new Student();
        BeanUtils.copyProperties(student, target);
        System.out.println(target.count.get());
    }

    static class Student {

        private int age;

        private AtomicInteger count = new AtomicInteger();

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public AtomicInteger getCount() {
            return count;
        }

        public void setCount(AtomicInteger count) {
            this.count = count;
        }
    }
}
