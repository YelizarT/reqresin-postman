package dto;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {
        private String name;
        private String height;
        private String mass;
        private String hair_color;
        private String skin_color;
        private String eye_color;
        private String birth_year;
        private String gender;
        private String homeworld;
        private ArrayList<String> films;
        private ArrayList<Object> species;
        private ArrayList<Object> vehicles;
        private ArrayList<String> starships;
        private String created;
        private String edited;
        private String url;

}

