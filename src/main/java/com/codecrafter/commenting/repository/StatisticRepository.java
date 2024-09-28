package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.response.StatisticsResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StatisticRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StatisticsResponse getStatistics(Long id) {
        String sql1 ="SELECT " +
                     "COUNT(CASE WHEN r.recommend_status = 'LIKES' AND c.is_question = true AND c.writer_info_id = :id THEN c.id END) AS goodQuestionCount, " +
                     "COUNT(DISTINCT CASE WHEN mst.owner_id = :id THEN mst.id END) AS receivedQuestionCount, " +
                     "COUNT(DISTINCT CASE WHEN mst.guest_id = :id THEN mst.id END) AS sentQuestionCount, " +
                     "COUNT(DISTINCT CASE WHEN c.writer_info_id = :id AND c.is_question = false THEN c.id END) AS answerCount, " +
                     "COUNT(DISTINCT unanswered.mst_id) AS unansweredQuestionCount " +
                     "FROM conversation_mst mst " +
                     "LEFT JOIN conversation c ON c.mst_id = mst.id " +
                     "LEFT JOIN recommend r ON r.conversation_id = c.id " +
                     "LEFT JOIN (" +
                     "            SELECT mst.id AS mst_id " +
                     "            FROM conversation_mst mst " +
                     "            JOIN conversation c ON c.mst_id = mst.id " +
                     "            WHERE mst.owner_id = :id AND mst.is_deleted = false AND c.is_deleted = false " +
                     "            GROUP BY mst.id " +
                     "            HAVING COUNT(c.id) = 1" +
                     ") AS unanswered ON unanswered.mst_id = mst.id " +
                     "WHERE mst.is_deleted = false AND c.is_deleted = false";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);

        return namedParameterJdbcTemplate.queryForObject(sql1, paramMap, (rs, rowNum) ->
            new StatisticsResponse(
                rs.getLong("goodQuestionCount"),
                rs.getLong("receivedQuestionCount"),
                rs.getLong("sentQuestionCount"),
                rs.getLong("answerCount"),
                rs.getLong("unansweredQuestionCount")
            )
        );
    }
}
