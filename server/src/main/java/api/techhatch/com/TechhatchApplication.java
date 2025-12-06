package api.techhatch.com;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "api.techhatch.com")
public class TechhatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechhatchApplication.class, args);
	}

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

//For testing beans load
//    @Bean
//    public ApplicationRunner testBeans(ApplicationContext ctx) {
//        return args -> {
//            System.out.println("==== CONTROLLERS FOUND ====");
//            for (String bean : ctx.getBeanDefinitionNames()) {
//                if (bean.toLowerCase().contains("service")) {
//                    System.out.println(bean);
//                }
//            }
//        };
//    }

}
