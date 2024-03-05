package tests;

import dto.Film;
import dto.Person;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tests.BaseTest.getRequest;

public class GetPersonBySearchTest {
    //ЗАДАЧА:
    // Search for a person with the name Vader.
    //РЕШЕНИЕ:
    @Test
    public void getVaderInfo() {
        List<Person> people = getRequest("/people/?search=Vader", 200)
                .body().jsonPath().getList("results", Person.class);
        System.out.println(people.get(0).getName());

        //Check that name contains "Vader" (Each result contains name Vader)
        for (Person person : people) {
            assertTrue(person.getName().contains("Vader"));
        }
    }

    //ЗАДАЧА:
    //Using previous response (1) find which film that Darth Vader joined has the less planets.
    //РЕШЕНИЕ:
    @Test
    public void testFilmWithLeastPlanetsForDarthVader() {
        // Get the previous response that contains information about Darth Vader
        Response previousResponse = get("https://swapi.dev/api/people/?search=Vader");
        previousResponse.then().assertThat().statusCode(200);

        // Extract the film URLs from the previous response
        List<String> filmUrls = previousResponse.jsonPath().getList("results.films.flatten()");

        // Initialize variables to track the film with the least number of planets
        String filmWithLeastPlanetsUrl = null;
        int minPlanetCount = Integer.MAX_VALUE;

        // Iterate over each film URL to find the one with the least number of planets
        for (String filmUrl : filmUrls) {
            // Send a request to the film URL to retrieve information about the planets
            Response filmResponse = get(filmUrl);
            filmResponse.then().assertThat().statusCode(200);

            // Extract the count of planets from the film response
            int planetCount = filmResponse.jsonPath().getList("planets").size();

            // Update the film with the least number of planets if applicable
            if (planetCount < minPlanetCount) {
                minPlanetCount = planetCount;
                filmWithLeastPlanetsUrl = filmUrl;
            }
        }

        // Output the film with the least number of planets
        System.out.println("Film with the least number of planets for Darth Vader: " + filmWithLeastPlanetsUrl);

        // You can also assert the result if needed
        // assertEquals(filmWithLeastPlanetsUrl, "expectedFilmUrl");
    }

    @Test
    public void getVaderInfoNew() {
        List<Person> people = getRequest("/people/?search=Vader", 200)
                .body().jsonPath().getList("results", Person.class);
        List<String> films = people.get(0).getFilms();
        films.forEach(x -> System.out.println(x));

        Map<String, Integer> filmPlanetQuantity = new HashMap<>();

        for (String film : films) {
            Film filmInfo = getRequest(film, 200)
                    .body().jsonPath().getObject("", Film.class);
            filmPlanetQuantity.put(filmInfo.getTitle(), filmInfo.getPlanets().size());
        }
        String minFilmName
                = Collections.min(filmPlanetQuantity.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println(minFilmName);
        System.out.println(filmPlanetQuantity.get(minFilmName));

    }

    //ЗАДАЧА:
    //Using previous responses (1) verify if Vader's starship is on film with id 1
    //РЕШЕНИЕ:
    @Test
    public void verifyVaderStarshipInFilmWithId1() {
        Response vaderResponse = get("https://swapi.dev/api/people/?search=Vader");
        List<String> vaderStarshipUrl = vaderResponse.jsonPath().getList("results[0].starships");

        Response filmResponse = get("https://swapi.dev/api/films/1");
        List<String> filmStarships = filmResponse.jsonPath().getList("starships");
        boolean isVaderShipInFilmWithId1 = false;

        for (String vaderStarship : vaderStarshipUrl
        ) {
            if (filmStarships.contains(vaderStarship)) {
                isVaderShipInFilmWithId1 = true;
                break;
            }
        }
        String resultMessage = isVaderShipInFilmWithId1 ? "да" : "нет";
        System.out.println("Корабль Вейдера присутствует в фильме с ID 1: " + resultMessage);
    }

    //ЗАДАЧА:
    //Using previous responses (1) & (2) verify if Vader's starship is on film from response (2).(в фильме с наименьшим колич. планет)
    //РЕШЕНИЕ:
    @Test
    public void verifyVaderStarshipInFilmWithLeastPlanets() {
        List<Person> people = getRequest("/people/?search=Vader", 200)
                .body().jsonPath().getList("results", Person.class);

        //people.forEach(person -> assertTrue(person.getName().contains("Vader")));

        List<String> filmUrls = new ArrayList<>();
        people.forEach(person -> filmUrls.addAll(person.getFilms()));

        String filmWithLeastPlanets = null;
        int leastPlanetFilmId = 0;
        int leastPlanetsCount = Integer.MAX_VALUE;
        String filmWithLeastPlanetsUrl = "";
        for (String filmUrl : filmUrls) {
            Response filmResponse = getRequest(filmUrl, 200);
            Film film = filmResponse.getBody().as(Film.class);
            int planetCount = film.getPlanets().size();

            if (planetCount < leastPlanetsCount) {
                leastPlanetsCount = planetCount;
                filmWithLeastPlanetsUrl = filmUrl;
                leastPlanetFilmId = film.getEpisode_id();

                System.out.println("911");
                Response filmInfoLessPlanets = getRequest(filmWithLeastPlanetsUrl, 200);
                Film filmLessPlanets = filmInfoLessPlanets.getBody().as(Film.class);
                List<String> starshipsInFilm = filmLessPlanets.getStarships();

                List<String> vaderStarships = new ArrayList<>();
                people.forEach(person -> vaderStarships.addAll(person.getStarships()));
                for (String starship : vaderStarships) {
                    if (starshipsInFilm.contains(starship)) {
                        System.out.println("Корабль Вайдера присутствует в фильме? Да");

                    } else {
                        System.out.println("Корабль Вайдера присутствует в фильме 1? Нет");
                    }

                    System.out.println("Ид фильма = " + leastPlanetFilmId);
                }
            }
        }
    }


    @Test
    public void verifyVaderStarshipInFilmNew2() {
        // Получаем предыдущий ответ, который содержит информацию о Дарт Вейдере
        Response vaderResponse = get("https://swapi.dev/api/people/?search=Vader");
        vaderResponse.then().assertThat().statusCode(200);

        // Получаем предыдущий ответ, который содержит информацию о фильмах
        Response filmsResponse = get("https://swapi.dev/api/films");
        filmsResponse.then().assertThat().statusCode(200);

        // Извлекаем URL-адреса звездолетов Вейдера и фильмов
        List<String> vaderStarshipUrls = vaderResponse.jsonPath().getList("results[0].starships");
        List<String> filmUrls = filmsResponse.jsonPath().getList("results.url");

        // Перебираем фильмы и проверяем наличие звездолета Вейдера в каждом фильме
        boolean vaderStarshipInFilm = false;
        for (String filmUrl : filmUrls) {
            // Получаем информацию о фильме
            Response filmResponse = get(filmUrl);
            filmResponse.then().assertThat().statusCode(200);

            // Извлекаем URL-адреса звездолетов из фильма
            List<String> filmStarshipUrls = filmResponse.jsonPath().getList("starships");

            // Проверяем наличие пересечения между звездолетами Вейдера и звездолетами из фильма
            if (!Collections.disjoint(vaderStarshipUrls, filmStarshipUrls)) {
                vaderStarshipInFilm = true;
                break;
            }
        }
        // Проверяем, что звездолет Вейдера присутствует хотя бы в одном фильме
        assertTrue(vaderStarshipInFilm, "Звездолет Вейдера не присутствует ни в одном фильме.");
    }

    @Test
    public void oldestCharacterSearch() {
        //ЗАДАЧА:
        //Get all people to collection (List)

        List<Person> allPeople = new ArrayList<>();

        for (int page = 1; page <= 9; page++) {
            Response response = getRequest("https://swapi.dev/api/people/?page=" + page, 200);

            List<Person> peopleOnPage = response.jsonPath().getList("results", Person.class);
            allPeople.addAll(peopleOnPage);
        }
        assertEquals(82, allPeople.size());

        Person oldestPerson = null;
        String oldestBirthYear = "999BBY";
        for (Person person : allPeople) {
            String birthYear = person.getBirth_year();
            if (birthYear.endsWith("BBY")) {
                birthYear = birthYear.replace("BBY", "").trim();

                if (birthYear.compareTo(oldestBirthYear) < 0) {
                    oldestBirthYear = birthYear;
                    oldestPerson = person;
                }
            }
        }
        System.out.println("Самый старший персонаж:");
        System.out.println("Имя: " + oldestPerson.getName());
        System.out.println("Год рождения: " + oldestPerson.getBirth_year());
    }
    @Test
    public void oldestCharacterSearchNew() {
        List<Person> allPeople = new ArrayList<>();
        Person oldestPerson = null;
        String oldestBirthYear = "999"; // Инициализация значением максимально возможного года

        for (int page = 1; page <= 9; page++) {
            Response response = getRequest("https://swapi.dev/api/people/?page=" + page, 200);

            List<Person> peopleOnPage = response.jsonPath().getList("results", Person.class);
            allPeople.addAll(peopleOnPage);
        }

        for (Person person : allPeople) {
            String birthYear = person.getBirth_year();
            if (birthYear.endsWith("BBY")) {
                birthYear = birthYear.replace("BBY", "").trim();
                // Сравниваем года рождения
                if (birthYear.compareTo(oldestBirthYear) < 0) {
                    oldestBirthYear = birthYear;
                    oldestPerson = person;
                }
            }
        }

        System.out.println("Самый старший персонаж:");
        System.out.println("Имя: " + oldestPerson.getName());
        System.out.println("Год рождения: " + oldestPerson.getBirth_year());
    }
}



