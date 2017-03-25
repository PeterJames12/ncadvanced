package com.overseer.dao.impl;

import com.overseer.dao.RequestDao;
import com.overseer.dto.RequestDTO;
import com.overseer.model.PriorityStatus;
import com.overseer.model.Request;
import com.overseer.model.User;
import com.overseer.model.enums.ProgressStatus;
import com.overseer.service.impl.ProgressStatusUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * Implementation of {@link RequestDao} interface.
 * </p>
 */
@Repository
@RequiredArgsConstructor
public class RequestDaoImpl extends CrudDaoImpl<Request> implements RequestDao {

    private static final int HIGH = 1;
    private static final int NORMAL = 2;
    private static final int LOW = 3;
    private static final int DEFAULT_DAY_IN_MONTH = 1;
    private static final Long DEFAULT_MONTHS_STEP = 1L;
    private final ProgressStatusUtil progressStatusUtil;


    @Override
    public List<Request> findRequestsByReporterAndProgress(Long reporterId, String progress, int pageSize, int pageNumber) {
        Assert.notNull(reporterId, "id must not be null");
        try {
            val parameterSource = new MapSqlParameterSource("limit", pageSize);
            parameterSource.addValue("offset", pageSize * (pageNumber - 1));
            parameterSource.addValue("reporterId", reporterId);
            parameterSource.addValue("progress", progress);
            return jdbc().query(queryService().getQuery("request.select").concat(
                    queryService().getQuery("request.findByReporterAndProgress")),
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Long countRequestsByReporterAndProgress(Long reporterId, String progress) {
        val parameterSource = new MapSqlParameterSource("reporterId", reporterId);
        parameterSource.addValue("progress", progress);
        return jdbc().queryForObject(queryService().getQuery("request.countByReporterAndProgress"),
                parameterSource, Long.class);
    }

    @Override
    public Long countRequestsByReporter(Long reporterId) {
        return jdbc().queryForObject(queryService().getQuery("request.countByReporter"),
                new MapSqlParameterSource("reporterId", reporterId), Long.class);
    }

    @Override
    public Long countRequestsByAssignee(Long managerId) {
        return jdbc().queryForObject(queryService().getQuery("request.countByAssignee"),
                new MapSqlParameterSource("assigneeId", managerId), Long.class);
    }

    @Override
    public Long countInProgressRequestByAssignee(Long managerId) {
        return jdbc().queryForObject(queryService().getQuery("request.countInProgressByAssignee"),
                new MapSqlParameterSource("assigneeId", managerId), Long.class);
    }

    @Override
    public List<Request> findSubRequests(Long id) {
        Assert.notNull(id, "id must not be null");
        String subRequestsQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findSubRequests"));
        return jdbc().query(subRequestsQuery,
                new MapSqlParameterSource("id", id),
                this.getMapper());
    }

    @Override
    public List<Request> findJoinedRequests(Long id) {
        Assert.notNull(id, "id must not be null");
        String joinedRequestsQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findJoinedRequests"));
        return jdbc().query(joinedRequestsQuery,
                new MapSqlParameterSource("id", id),
                this.getMapper());
    }

    @Override
    public List<Request> findRequestsByAssignee(Long assigneeId, int pageSize, int pageNumber) {
        Assert.notNull(assigneeId, "id must not be null");
        String findByAssigneeQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findByAssignee"));
        try {
            val parameterSource = new MapSqlParameterSource("limit", pageSize);
            parameterSource.addValue("offset", pageSize * (pageNumber - 1));
            parameterSource.addValue("assigneeId", assigneeId);
            return jdbc().query(findByAssigneeQuery,
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Request> findInProgressRequestsByAssignee(Long assigneeId, int pageSize, int pageNumber) {
        Assert.notNull(assigneeId, "id must not be null");
        String findByAssigneeQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findInProgressByAssignee"));
        try {
            val parameterSource = new MapSqlParameterSource("limit", pageSize);
            parameterSource.addValue("offset", pageSize * (pageNumber - 1));
            parameterSource.addValue("assigneeId", assigneeId);
            return jdbc().query(findByAssigneeQuery,
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Request> findRequestsByReporter(Long reporterId, int pageSize, int pageNumber) {
        Assert.notNull(reporterId, "id must not be null");
        String findByReporterQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findByReporter"));
        try {
            val parameterSource = new MapSqlParameterSource("limit", pageSize);
            parameterSource.addValue("offset", pageSize * (pageNumber - 1));
            parameterSource.addValue("reporterId", reporterId);
            return jdbc().query(findByReporterQuery,
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Request> findRequestsByProgress(Long statusId, int pageSize, int pageNumber) {
        Assert.notNull(statusId, "id must not be null");
        String findByStatusQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findByStatus"));
        try {
            val parameterSource = new MapSqlParameterSource("limit", pageSize);
            parameterSource.addValue("offset", pageSize * (pageNumber - 1));
            parameterSource.addValue("progress_status_id", statusId);
            return jdbc().query(findByStatusQuery,
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Request> findRequestsByPriority(Long priorityId, int pageSize, int pageNumber) {
        Assert.notNull(priorityId, "id must not be null");
        String findByPriorityQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findByPriority"));
        try {
            val parameterSource = new MapSqlParameterSource("limit", pageSize);
            parameterSource.addValue("offset", pageSize * (pageNumber - 1));
            parameterSource.addValue("priority_status_id", priorityId);
            return jdbc().query(findByPriorityQuery,
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Request> findRequestsByPeriod(LocalDate begin, LocalDate end, int pageSize, int pageNumber) {
        Assert.notNull(begin, "begin time must not be null");
        Assert.notNull(end, "end time must not be null");
        String findByPeriodQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findByPeriod"));
        try {
            val parameterSource = new MapSqlParameterSource("limit", pageSize);
            parameterSource.addValue("offset", pageSize * (pageNumber - 1));
            parameterSource.addValue("begin", java.sql.Date.valueOf(begin));
            parameterSource.addValue("end", java.sql.Date.valueOf(end));
            return jdbc().query(findByPeriodQuery,
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public RequestDTO findCountRequestsByPeriod(LocalDate start, LocalDate end, Long progressStatusId) {
        String findByPeriodQuery = this.queryService().getQuery("request.countByStatusAndPeriod");
        try {
            val parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("begin", java.sql.Date.valueOf(start));
            parameterSource.addValue("end", java.sql.Date.valueOf(end));
            parameterSource.addValue("progress_status_id", progressStatusId);
            return jdbc().queryForObject(findByPeriodQuery,
                    parameterSource, (resultSet, i) -> {
                        RequestDTO requestDTO = new RequestDTO();
                        requestDTO.setCount(resultSet.getLong("count"));
                        requestDTO.setStartDateLimit(start);
                        requestDTO.setEndDateLimit(end);
                        return requestDTO;
                    });
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<RequestDTO> findListCountRequestsByPeriod(LocalDate start, LocalDate end, Long progressStatusId) {
        String findCountByPeriodsQuery = this.queryService().getQuery("request.countByStatusesAndPeriods");
        List<RequestDTO> list = new ArrayList<>();
        try {
            val parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("begin", java.sql.Date.valueOf(start));
            parameterSource.addValue("end", java.sql.Date.valueOf(end));
            parameterSource.addValue("progress_status_id", progressStatusId);
            return jdbc().query(findCountByPeriodsQuery,
                    parameterSource, resultSet -> {
                        while (resultSet.next()) {
                            RequestDTO requestDTO = new RequestDTO();

                            int year = resultSet.getInt("year");
                            int month = resultSet.getInt("month");
                            LocalDate localStartDate = LocalDate.of(year, month, DEFAULT_DAY_IN_MONTH);
                            requestDTO.setStartDateLimit(localStartDate);

                            LocalDate localEndDate = LocalDate.of(year, month, DEFAULT_DAY_IN_MONTH);
                            localEndDate = localEndDate.plusMonths(DEFAULT_MONTHS_STEP);
                            requestDTO.setEndDateLimit(localEndDate);

                            requestDTO.setCount(resultSet.getLong("count"));
                            list.add(requestDTO);
                        }
                        return list;
                    });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RequestDTO findCountRequestsByManagerAndPeriod(LocalDate start, LocalDate end, Long progressStatusId, int id) {
        String findCountByManagerAndPeriodQuery = this.queryService().getQuery("request.countByManagerAndPeriod");
        try {
            val parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("begin", java.sql.Date.valueOf(start));
            parameterSource.addValue("end", java.sql.Date.valueOf(end));
            parameterSource.addValue("progress_status_id", progressStatusId);
            parameterSource.addValue("assignee_id", id);
            return jdbc().queryForObject(findCountByManagerAndPeriodQuery,
                    parameterSource, (resultSet, i) -> {
                        RequestDTO request = new RequestDTO();
                        request.setStartDateLimit(start);
                        request.setEndDateLimit(end);
                        request.setCount(resultSet.getLong("count"));
                        return request;
                    });
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<RequestDTO> findListCountRequestsByManagerAndPeriod(LocalDate start, LocalDate end, Long progressStatusId, int id) {
        List<RequestDTO> data = new ArrayList<>();
        try {
            val parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("begin", java.sql.Date.valueOf(start));
            parameterSource.addValue("end", java.sql.Date.valueOf(end));
            parameterSource.addValue("progress_status_id", progressStatusId);
            parameterSource.addValue("assignee_id", id);
            return jdbc().query(this.queryService().getQuery("request.countByManagerAndPeriods"),
                    parameterSource, resultSet -> {
                        while (resultSet.next()) {
                            RequestDTO request = new RequestDTO();
                            int year = resultSet.getInt("year");
                            int month = resultSet.getInt("month");
                            request.setCount(resultSet.getLong("count"));
                            LocalDate localStartDate = LocalDate.of(year, month, DEFAULT_DAY_IN_MONTH);
                            request.setStartDateLimit(localStartDate);

                            LocalDate localEndDate = LocalDate.of(year, month, DEFAULT_DAY_IN_MONTH);
                            localEndDate = localEndDate.plusMonths(DEFAULT_MONTHS_STEP);
                            request.setEndDateLimit(localEndDate);
                            System.out.println("request" + request);
                            data.add(request);
                        }
                        return data;
                    });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RequestDTO> findListOfBestManagersByPeriod(LocalDate start, LocalDate end, Long progressStatusId) {
        List<RequestDTO> bestManagers = new ArrayList<>();
        try {
            val parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("begin", java.sql.Date.valueOf(start));
            parameterSource.addValue("end", java.sql.Date.valueOf(end));
            parameterSource.addValue("progress_status_id", progressStatusId);
            return jdbc().query(this.queryService().getQuery("request.bestManagersByPeriod"),
                    parameterSource, resultSet -> {
                        while (resultSet.next()) {
                            RequestDTO manager = new RequestDTO();
                            manager.setCount(resultSet.getLong("count"));
                            manager.setManagerFirstName(resultSet.getString("first_name"));
                            manager.setManagerLastName(resultSet.getString("last_name"));
                            bestManagers.add(manager);
                        }
                        return bestManagers;
                    });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Request> findRequestsByDate(LocalDate date) {
        Assert.notNull(date, "Date must be not null");
        return jdbc().query(queryService().getQuery("request.select")
                        .concat(queryService().getQuery("request.byDate")),
                new MapSqlParameterSource("date", date),
                getMapper());
    }

    @Override
    public List<Request> findRequestsByIds(List<Long> ids) {
        Assert.notNull(ids, "ids must not be null");
        String subRequestsQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findRequestsByIds"));
        return jdbc().query(subRequestsQuery,
                new MapSqlParameterSource("ids", ids),
                this.getMapper());
    }

    @Override
    public List<Request> findRequestsByProgressStatusesAndReporterId(List<Long> statusIds, Long reporterId) {
        Assert.notNull(reporterId, "id must not be null");
        Assert.notNull(statusIds, "list status ids must not be null");
        String findRequestsByProgressStatusAndReporterIdQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findRequestsByProgressStatusAndReporterId"));
        try {
            val parameterSource = new MapSqlParameterSource("reporterId", reporterId);
            parameterSource.addValue("progress_status_ids", statusIds);
            return jdbc().query(findRequestsByProgressStatusAndReporterIdQuery,
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public void deleteParentRequestIfItHasNoChildren(Long parentId) {
        Assert.notNull(parentId, "id must not be null");
        String deleteQuery = this.queryService().getQuery("request.deleteParentRequestIfHasNoChildren");
        this.jdbc().update(deleteQuery, new MapSqlParameterSource("id", parentId));
    }

    @Override
    public List<Long> countRequestByProgressStatus() {
        String quantityQuery = queryService().getQuery("request.countByProgressStatus");
        List<Long> progressList = new LinkedList<>();
        progressList.add(jdbc().queryForObject(quantityQuery, new MapSqlParameterSource("progress", ProgressStatus.FREE.getId()), Long.class));
        progressList.add(jdbc().queryForObject(quantityQuery, new MapSqlParameterSource("progress", ProgressStatus.JOINED.getId()), Long.class));
        progressList.add(jdbc().queryForObject(quantityQuery, new MapSqlParameterSource("progress", ProgressStatus.IN_PROGRESS.getId()), Long.class));
        return progressList;
    }

    @Override
    public List<Long> countRequestByProgressStatusForUser(Long userId) {
        String quantityQueryForUser = queryService().getQuery("request.countByProgressStatusForUser");
        List<Long> progressListForUser = new LinkedList<>();


        val parameterSourceForFree = new MapSqlParameterSource();
        parameterSourceForFree.addValue("userId", userId);
        parameterSourceForFree.addValue("progress", ProgressStatus.FREE.getId());
        progressListForUser.add(jdbc().queryForObject(quantityQueryForUser, parameterSourceForFree, Long.class));

        val parameterSourceForJoined = new MapSqlParameterSource();
        parameterSourceForJoined.addValue("userId", userId);
        parameterSourceForJoined.addValue("progress", ProgressStatus.JOINED.getId());
        progressListForUser.add(jdbc().queryForObject(quantityQueryForUser, parameterSourceForJoined, Long.class));

        val parameterSourceForInProgress = new MapSqlParameterSource();
        parameterSourceForInProgress.addValue("userId", userId);
        parameterSourceForInProgress.addValue("progress", ProgressStatus.IN_PROGRESS.getId());
        progressListForUser.add(jdbc().queryForObject(quantityQueryForUser, parameterSourceForInProgress, Long.class));

        return progressListForUser;
    }

    @Override
    public List<Long> countRequestByPriorityStatus() {
        String quantityQuery = queryService().getQuery("request.countByPriorityStatus");
        List<Long> priorityList = new LinkedList<>();
        priorityList.add(jdbc().queryForObject(quantityQuery, new MapSqlParameterSource("priority", HIGH), Long.class));
        priorityList.add(jdbc().queryForObject(quantityQuery, new MapSqlParameterSource("priority", NORMAL), Long.class));
        priorityList.add(jdbc().queryForObject(quantityQuery, new MapSqlParameterSource("priority", LOW), Long.class));
        return priorityList;
    }

    @Override
    public List<Long> countRequestByProgressStatusForSixMonths() {
        String quantityQueryForSixMonths = queryService().getQuery("request.countStatisticForSixMonths");
        String quantityQueryForSixMonthsClosed = queryService().getQuery("request.countStatisticForSixMonthsClosed");
        List<Long> statisticList = new LinkedList<>();
        statisticList.add(jdbc().queryForObject(quantityQueryForSixMonthsClosed, new MapSqlParameterSource(), Long.class));
        statisticList.add(jdbc().queryForObject(quantityQueryForSixMonths, new MapSqlParameterSource("progress", ProgressStatus.CLOSED.getId()), Long.class));
        return statisticList;
    }

    @Override
    public List<Long> countRequestByProgressStatusForSixMonthsForUser(Long userId) {
        String quantityQuery = queryService().getQuery("request.countStatisticForSixMonthsForUser");
        List<Long> userStatistic = new LinkedList<>();
        userStatistic.add(jdbc().queryForObject(quantityQuery, new MapSqlParameterSource("userId", userId), Long.class));
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("progress", ProgressStatus.CLOSED.getId());
        source.addValue("userId", userId);
        String quantityQueryClosed = queryService().getQuery("request.countStatisticForSixMonthsForUserClosed");
        userStatistic.add(jdbc().queryForObject(quantityQueryClosed, source, Long.class));
        return userStatistic;
    }

    @Override
    public Long countFree() {
        String findCountQuery = queryService().getQuery("request.countFree");
        return jdbc().queryForObject(findCountQuery, new MapSqlParameterSource(), Long.class);
    }

    @Override
    public Long countByAssignee(Long assigneeId) {
        String findCountQuery = queryService().getQuery("request.countByAssignee");
        return jdbc().queryForObject(findCountQuery, new MapSqlParameterSource("assigneeId", assigneeId), Long.class);
    }

    @Override
    public List<Request> findFreeRequests(int pageSize, int pageNumber) {
        String findByStatusQuery = this.queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findFree"));
        try {
            val parameterSource = new MapSqlParameterSource("limit", pageSize);
            parameterSource.addValue("offset", pageSize * (pageNumber - 1));
            return jdbc().query(findByStatusQuery,
                    parameterSource,
                    this.getMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    protected String getInsertQuery() {
        return queryService().getQuery("request.insert");
    }

    @Override
    protected String getFindOneQuery() {
        return queryService().getQuery("request.select")
                .concat(queryService().getQuery("request.findOne"));
    }

    @Override
    protected String getDeleteQuery() {
        return queryService().getQuery("request.delete");
    }

    @Override
    protected String getExistsQuery() {
        return queryService().getQuery("request.exists");
    }

    @Override
    protected String getFindAllQuery() {
        return queryService().getQuery("request.select").concat(queryService().getQuery("request.fetchPage"));
    }

    @Override
    protected String getCountQuery() {
        return this.queryService().getQuery("request.count");
    }

    @Override
    protected RowMapper<Request> getMapper() {
        return (resultSet, i) -> {
            User reporter = new User();
            reporter.setId(resultSet.getLong("reporter_id"));
            reporter.setFirstName(resultSet.getString("reporter_first_name"));
            reporter.setLastName(resultSet.getString("reporter_last_name"));
            reporter.setEmail(resultSet.getString("reporter_email"));

            User assignee = new User();
            assignee.setId(resultSet.getLong("assignee_id"));
            if (resultSet.getString("assignee_first_name") != null) {
                assignee.setFirstName(resultSet.getString("assignee_first_name"));
                assignee.setLastName(resultSet.getString("assignee_last_name"));
                assignee.setEmail(resultSet.getString("assignee_email"));
            }

            User lastChanger = new User();
            lastChanger.setId(resultSet.getLong("last_changer_id"));
            lastChanger.setFirstName(resultSet.getString("last_changer_first_name"));
            lastChanger.setLastName(resultSet.getString("last_changer_last_name"));

            PriorityStatus priorityStatus = new PriorityStatus();
            priorityStatus.setName(resultSet.getString("priority_name"));
            priorityStatus.setId(resultSet.getLong("priority_id"));
            priorityStatus.setValue(resultSet.getInt("priority_value"));

//            ProgressStatus progressStatus = null;
//            String progressStatusName = resultSet.getString("progress_name");
//            if (progressStatusName != null && !progressStatusName.isEmpty()) {
//                progressStatus = new ProgressStatus();
//                progressStatus.setName(progressStatusName);
//                progressStatus.setId(resultSet.getLong("progress_id"));
//                progressStatus.setValue(resultSet.getInt("progress_value"));
//            }
//
//            PriorityStatus priorityStatus = null;
//            String priorityStatusName = resultSet.getString("priority_name");
//            if (priorityStatusName != null && !priorityStatusName.isEmpty()) {
//                priorityStatus = new PriorityStatus();
//                priorityStatus.setName(priorityStatusName);
//                priorityStatus.setId(resultSet.getLong("priority_id"));
//                priorityStatus.setValue(resultSet.getInt("priority_value"));
//            }

            Long parentId = resultSet.getLong("parent_id");
            if (parentId == 0) {
                parentId = null;
            }

            ProgressStatus progressStatus = progressStatusUtil.getProgressById(resultSet.getLong("progress_id"));

            Request request = new Request();
            request.setId(resultSet.getLong("id"));
            request.setTitle(resultSet.getString("title"));
            request.setDescription(resultSet.getString("description"));
            request.setParentId(resultSet.getLong("parent_id"));
            request.setEstimateTimeInDays(resultSet.getInt("estimate_time_in_days"));
            request.setDateOfCreation(resultSet.getTimestamp("date_of_creation").toLocalDateTime());
            request.setReporter(reporter);
            request.setAssignee(assignee);
            request.setLastChanger(lastChanger);
            request.setPriorityStatus(priorityStatus);
            request.setParentId(parentId);
            request.setProgressStatus(progressStatus);
            return request;
        };
    }
}
