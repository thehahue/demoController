package at.bbrz.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/")
    public String index() {
        return "<b>Hello World!</b>";
    }

    @GetMapping("/hallo")
    public String hallo() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"de\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Hello World Demo</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Hello World!</h1>\n" +
                "    \n" +
                "    <p>Willkommen zu meinem kleinen HTML-Demo.</p>\n" +
                "    <p>Dies ist eine weitere Zeile Text.</p>\n" +
                "    <p>HTML steht für HyperText Markup Language.</p>\n" +
                "    \n" +
                "    <hr>\n" +
                "    \n" +
                "    <p>Viel Spaß beim Ausprobieren! \uD83D\uDE80</p>\n" +
                "</body>\n" +
                "</html>";
    }

    @GetMapping("/json")
    public String json() {
        return "{\"demo\":\"test\"}";
    }

    @GetMapping("/hund")
    public Hund hund() {
       Hund fifi = new Hund("Fifi", 5, 1);

       return fifi;
    }

    @GetMapping("/hundage/{age}")
    public Hund hundage(@PathVariable int age) {
        Hund fifi = new Hund("Fifi", age, 2);

        return fifi;
    }

}
