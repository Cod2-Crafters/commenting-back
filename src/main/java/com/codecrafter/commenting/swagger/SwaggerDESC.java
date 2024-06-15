package com.codecrafter.commenting.swagger;

import com.codecrafter.commenting.domain.entity.base.Provider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Schema(description = "설명용 명세서(기능없음)")
@RestController
public class SwaggerDESC {
    public SwaggerDESC swg(SwaggerDESC swaggerDESC) {
        return swaggerDESC;
    }

    @Operation(summary = "공급자 종류",
            description = """
                        GOOGLE  -> 구글/구현<br>
                        KAKAO   -> 카카오/미구현<br>
                        NAVER   -> 네이버/미구현<br>
                        BASE    -> 기본
                        """)
    @GetMapping("com.codecrafter.commenting.domain.entity.base.Provider")
    public String providers() {
        return "";
    }


}
