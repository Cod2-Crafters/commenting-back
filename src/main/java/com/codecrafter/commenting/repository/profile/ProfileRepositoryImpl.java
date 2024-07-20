package com.codecrafter.commenting.repository.profile;

import com.codecrafter.commenting.domain.dto.MemberInfoDto;
import com.codecrafter.commenting.domain.enumeration.RecommendStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

public class ProfileRepositoryImpl implements ProfileRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MemberInfoDto getProfileResponse(Long memberId) {
        String jpql = "SELECT new com.codecrafter.commenting.domain.dto.MemberInfoDto(m, " +
            "(SELECT COUNT(c) FROM Conversation c WHERE c.isQuestion = false AND c.memberInfo.id = m.id), " +
            "(SELECT COUNT(r) FROM Recommend r WHERE r.recommendStatus = :recommendStatus AND r.memberInfo.id = m.id)) " +
            "FROM MemberInfo m WHERE m.id = :memberId";

        TypedQuery<MemberInfoDto> query = entityManager.createQuery(jpql, MemberInfoDto.class);
        query.setParameter("memberId", memberId);
        query.setParameter("recommendStatus", RecommendStatus.LIKES);

        return query.getSingleResult();
    }
}
