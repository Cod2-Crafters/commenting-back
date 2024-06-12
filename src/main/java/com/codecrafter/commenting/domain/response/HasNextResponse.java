package com.codecrafter.commenting.domain.response;

import java.util.List;

import lombok.Getter;

@Getter
public class HasNextResponse<T> {

	private List<T> data;

	private boolean hasNext;

	private Long lastId;

	public static <T> HasNextResponse<T> of(List<T> data, boolean hasNext, Long nextCursor) {
		HasNextResponse<T> hasNextResponse = new HasNextResponse<>();
		hasNextResponse.data = data;
		hasNextResponse.hasNext = hasNext;
		hasNextResponse.lastId = nextCursor;
		return hasNextResponse;
	}

}
