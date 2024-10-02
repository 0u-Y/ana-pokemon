package kr.anacnu.pokemonbe.jwt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderExample {

    /**
     * 테스트용으로 인코딩하는 함수입니다. 추후 삭제하면 될 것 같습니다.
     * @param args
     */
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("202302576");
        System.out.println(encodedPassword);
    }
}
