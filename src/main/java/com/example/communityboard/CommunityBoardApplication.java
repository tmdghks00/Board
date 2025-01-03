package com.example.communityboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@EnableJpaAuditing
@SpringBootApplication
public class CommunityBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityBoardApplication.class, args); // 애플리케이션 실행
    }

    @Bean // Spring 컨테이너에 Bean으로 등록
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter(); // PUT, DELETE 요청을 지원하기 위한 필터 생성
    }

}
