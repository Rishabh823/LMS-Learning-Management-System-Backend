package com.cipherinfratech.lms;

//import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CipherLmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CipherLmsApplication.class, args);
	}


//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(CipherLmsApplication.class);
//    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

//    @Bean
//    ModelMapper modelMapper() {
//        return new ModelMapper();
//    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
