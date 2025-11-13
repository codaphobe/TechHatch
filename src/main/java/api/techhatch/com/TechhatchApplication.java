package api.techhatch.com;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;


@SpringBootApplication
@ComponentScan(basePackages = "api.techhatch.com")
public class TechhatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechhatchApplication.class, args);
	}


    @Bean
    public ApplicationRunner testBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("==== CONTROLLERS FOUND ====");
            for (String bean : ctx.getBeanDefinitionNames()) {
                if (bean.toLowerCase().contains("service")) {
                    System.out.println(bean);
                }
            }
        };
    }

}
