package study.datajpa.test;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties ("member.validation")
public class YmlTest {
    private staString nametest;

    public static void main(String[] args) {
        System.out.println("test : " + nametest);
    }
}
