package projekti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

/**
 * The main class which starts the application :)
 */
@SpringBootApplication
public class MyApplication {
    /**
     * Bean to enable modern java time dialect in thymeleaf and spring.
     *
     * @return Modern Java time dialect.
     */
    @Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }

    /**
     * This is used to start the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class);
    }
}
