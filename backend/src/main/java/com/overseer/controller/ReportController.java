package com.overseer.controller;

import com.overseer.dto.RequestDTO;
import com.overseer.service.ReportService;
import com.overseer.service.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller provides api for creating, getting reports.
 */
@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public class ReportController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Long DEFAULT_MONTHS_STEP = 1L;
    private static final int COUNT_MONTHS_IN_YEAR = 12;
    private final RequestService requestService;
    private final ReportService reportService;

    /**
     * Handle request to download an Excel document.
     */
    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public View download(@RequestParam String id) {
        Long requestId = Long.valueOf(id);
        return reportService.generateRequestPDFReport(requestId);
    }

    /**
     * Gets Document pdf report.
     *
     * @return Document doc with reporting data.
     */
    @GetMapping("/adminPDFReport")
    public View getAdminPDFReport(@RequestParam String beginDate, @RequestParam String endDate) {

        LocalDate start = LocalDate.parse(beginDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        return reportService.generateAdminPDFReport(start, end);
    }

    /**
     * Gets list of request transfer objects which created in the same period.
     *
     * @param beginDate date from
     * @param endDate   date to
     * @return return list of requestDTO from one period of time
     */
    @GetMapping("/getAllStaticticsOfCreatedRequestsByPeriod")
    public ResponseEntity<List<RequestDTO>> getAllStatisticsOfCreatedRequestsByPeriod(@RequestParam String beginDate,
                                                                                      @RequestParam String endDate) {

        LocalDate start = LocalDate.parse(beginDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        //Main list with RequestDTO's
        List<RequestDTO> allRequests = new ArrayList<>();

        //Round the date until next month
        LocalDate localStart = start.plusDays((start.lengthOfMonth() - start.getDayOfMonth()) + 1);

        //Receive data before the 1st day of the next month (after start date)
        allRequests.add(requestService.findCountRequestsByPeriod(start, localStart, "Free"));

        //Round the date of the last month by the 1st day of this month
        LocalDate localEnd = end.minusDays(end.getDayOfMonth() - 1);
        if (!(localStart.equals(localEnd))) {
            //Receive data between the 1st day of the next month and the 1st day of the last month
            List<RequestDTO> dataFromCentralDates = requestService.findListCountRequestsByPeriod(localStart, localEnd, "Free");
            LocalDate local = loadGeneralList(allRequests, dataFromCentralDates, localStart, localEnd);

            //Receive data from the 1st day of the last month
            allRequests.add(requestService.findCountRequestsByPeriod(local, end, "Free"));
        }
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    /**
     * Gets list of request transfer objects which created in the same period.
     *
     * @param beginDate date from
     * @param endDate   date to
     * @return return list of requestDTO from one period of time
     */
    @GetMapping("/getAllStaticticsOfClosedRequestsByPeriod")
    public ResponseEntity<List<RequestDTO>> getAllStatisticsOfClosedRequestsByPeriod(@RequestParam String beginDate,
                                                                                     @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(beginDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        //Main list with request transfer objects
        List<RequestDTO> allRequests = new ArrayList<>();

        //Round the date until next month
        LocalDate localStart = start.plusDays((start.lengthOfMonth() - start.getDayOfMonth()) + 1);

        //Receive data before the 1st day of the next month (after start date)
        allRequests.add(requestService.findCountRequestsByPeriod(start, localStart, "Closed"));

        //Round the date of the last month by the 1st day of this month
        LocalDate localEnd = end.minusDays(end.getDayOfMonth() - 1);
        if (!(localStart.equals(localEnd))) {
            //Receive data between the 1st day of the next month and the 1st day of the last month
            List<RequestDTO> dataFromCentralDates = requestService.findListCountRequestsByPeriod(localStart, localEnd, "Closed");
            LocalDate local = loadGeneralList(allRequests, dataFromCentralDates, localStart, localEnd);

            //Receive data from the 1st day of the last month
            allRequests.add(requestService.findCountRequestsByPeriod(local, end, "Closed"));
        }
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    /**
     * Gets general list of request transfer objects which created in the same period.
     *
     * @param generalList main collection with DTO's
     * @param hourlyList  collection with DTO's in period between the 1st day of the second month and the 1st day of the last month
     * @param localStart  date from
     * @param localEnd    date to
     * @return return list of request DTO's from one period of time
     */
    private LocalDate loadGeneralList(List<RequestDTO> generalList, List<RequestDTO> hourlyList, LocalDate localStart, LocalDate localEnd) {

        //Dates difference in months
        int countYears = Period.between(localStart, localEnd).getYears();
        int countMonth;
        if (countYears != 0) {
            countMonth = countYears * COUNT_MONTHS_IN_YEAR + Period.between(localStart, localEnd).getMonths();
        } else {
            countMonth = Period.between(localStart, localEnd).getMonths();
        }
        if (countMonth == 0) {
            return localEnd;
        }
        boolean key;
        LocalDate local = null;
        for (int i = 0; i < countMonth; i++) {
            key = false;
            for (RequestDTO r : hourlyList) {
                if (localStart.equals(r.getStartDateLimit())) {
                    generalList.add(r);
                    key = true;
                    break;
                } else {
                    key = false;
                }
            }
            local = localStart.plusMonths(DEFAULT_MONTHS_STEP);
            if (!key) {
                RequestDTO temp = new RequestDTO();
                temp.setStartDateLimit(localStart);
                temp.setCount(new Long(0));
                temp.setEndDateLimit(local);
                generalList.add(temp);
            }
            localStart = local;
        }
        return local;
    }

    /**
     * Gets list of best managers which created in the same period. Data showed with status closed.
     *
     * @param beginDate date from
     * @param endDate   date to
     * @return return list of managers from one period of time
     */
    @GetMapping("/getBestManagersWithClosedStatusByPeriod")
    public ResponseEntity<List<RequestDTO>> getBestManagersWithClosedStatusByPeriod(@RequestParam String beginDate,
                                                                                    @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(beginDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        List<RequestDTO> topManagers = requestService.findBestManagersByPeriod(start, end, "Closed");
        return new ResponseEntity<>(topManagers, HttpStatus.OK);
    }

    /**
     * Gets list of best managers which created in the same period. Data showed with status free.
     *
     * @param beginDate date from
     * @param endDate   date to
     * @return return list of managers from one period of time
     */
    @GetMapping("/getBestManagersWithFreeStatusByPeriod")
    public ResponseEntity<List<RequestDTO>> getBestManagersWithFreeStatusByPeriod(@RequestParam String beginDate,
                                                                                  @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(beginDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        List<RequestDTO> topManagers = requestService.findBestManagersByPeriod(start, end, "Free");
        return new ResponseEntity<>(topManagers, HttpStatus.OK);
    }

    /**
     * Gets list of request transfer objects which created in the same period for manager.
     *
     * @param beginDate date from.
     * @param endDate   date to.
     * @param id        manager id.
     * @return return list of requestDTO from one period of time for manager.
     */
    @GetMapping("/getManagerStatisticsOfClosedRequestsByPeriod")
    public ResponseEntity<List<RequestDTO>> getManagerStatisticsOfClosedRequestsByPeriod(@RequestParam String beginDate,
                                                                                         @RequestParam String endDate,
                                                                                         @RequestParam int id) {
        LocalDate start = LocalDate.parse(beginDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<RequestDTO> requests = new ArrayList<>();
        LocalDate localStart = start.plusDays((start.lengthOfMonth() - start.getDayOfMonth()) + 1);
        requests.add(requestService.findCountRequestsByManagerAndPeriod(start, localStart, "Closed", id));
        LocalDate localEnd = end.minusDays(end.getDayOfMonth() - 1);
        if (!(localStart.equals(localEnd))) {
            List<RequestDTO> dataFromCentralDates = requestService.findListCountRequestsByManagerAndPeriod(localStart, localEnd, "Closed", id);
            LocalDate local = loadGeneralList(requests, dataFromCentralDates, localStart, localEnd);
            requests.add(requestService.findCountRequestsByManagerAndPeriod(local, end, "Closed", id));
        }
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }
}