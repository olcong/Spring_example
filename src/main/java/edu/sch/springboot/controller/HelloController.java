package edu.sch.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    static class Person{
        String name;
        int age;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setName(String name) { this.name=name;}
    }
//    개체를 통해서 넘겨주는게 젤 좋다
    @ResponseBody
    @GetMapping("spring-person")
    public Person springPerson(@RequestParam("name") String name,
                               @RequestParam("age") int age, Model model) {
        Person person = new Person();
        person.setName(name);
        person.setAge(age);
        return person;
    }

    //Get-> spring-api
    @ResponseBody
//    responsebody가 template로 빠지는 거 막아줘서 api 사용 ㄱㄴ ㅇㅇ
    @GetMapping("/spring-api")
    public String springApi(@RequestParam("name") String name,
                            @RequestParam("age") int age, Model model) {
        return "spirng-api" + name + ","+age;  //브라우저에 문자열로 바로 전송
    }
    @GetMapping("spring-mvc")
    public String springMvc(@RequestParam("name") String name,
                            @RequestParam("age") int age, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("age", age);
        return "spring-mvc";
    }


    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello~Spring~!!");
        return "hello";
    }
}
