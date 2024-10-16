package com.codecrafter.commenting.domain.enumeration;

import com.codecrafter.commenting.domain.dto.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@Schema(description = "공급자")
public enum Provider {

	GOOGLE("google", (attributes) -> {
		MemberDto member = new MemberDto();
		member.setEmail((String) attributes.get("email"));
		return member;
	}),

	KAKAO("kakao", (attributes) -> {
		MemberDto member = new MemberDto();
		member.setEmail((String) attributes.get("email"));
		return member;
	}),

	NAVER("naver", (attributes) -> {
		MemberDto member = new MemberDto();
		member.setEmail((String) attributes.get("email"));
		return member;
	}),
	BASE("base", (attributes) -> {
		MemberDto member = new MemberDto();
		member.setEmail((String) attributes.get("email"));
		return member;
	})
	;

	private final String registrationId;
	private final Function<Map<String, Object>, MemberDto> of;

	Provider(String registrationId, Function<Map<String, Object>, MemberDto> of) {
		this.registrationId = registrationId;
		this.of = of;
	}

	public static MemberDto extract(Provider registrationId, Map<String, Object> attributes) {
		return Arrays.stream(Provider.values())
			.filter(provider -> registrationId.equals(provider))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new)
			.of.apply(attributes);
	}
}
