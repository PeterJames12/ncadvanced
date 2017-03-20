package com.overseer.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.overseer.dto.RequestDTO;
import com.overseer.service.ReportService;
import com.overseer.service.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    private static final Long DEFAULT_MONTHS_STEP = 1L;
    private final RequestService requestService;
    private final ReportService reportService;

    /**
     * Gets {@link Document} pdf report.
     *
     * @return {@link Document} doc with reporting data.
     */
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = "application/pdf")
    public Document getPDF() {
        Document document = null;
        try {
            document = reportService.generateAdminPDFReport();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * Gets list of request transfer objects which created in the same period.
     *
     * @param beginDate date from
     * @param endDate   date to
     * @return return list of requestDTO from one period of time
     */
    @GetMapping("/getListCountRequestsByPeriod")
    public ResponseEntity<List<RequestDTO>> getListCountRequestsByPeriod(@RequestParam String beginDate,
                                                                         @RequestParam String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(beginDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        //Main list with RequestDTO's
        List<RequestDTO> allRequests = new ArrayList<>();

        //Round the date until next month
        LocalDate localStart = start.plusDays((start.lengthOfMonth() - start.getDayOfMonth()) + 1);

        //Receive data before the 1st day of the next month (after start date)
        allRequests.add(requestService.findCountRequestsByPeriod(start, localStart));

        //Round the date of the last month by the 1st day of this month
        LocalDate localEnd = end.minusDays(end.getDayOfMonth() - 1);
        if (!localStart.equals(localEnd)) {
            //Receive data between the 1st day of the next month and the 1st day of the last month
            List<RequestDTO> dataFromCentralDates = requestService.findListCountRequestsByPeriod(localStart, localEnd);
            LocalDate local = loadGeneralList(allRequests, dataFromCentralDates, localStart, localEnd);

            //Receive data from the 1st day of the last month
            allRequests.add(requestService.findCountRequestsByPeriod(local, end));
        }
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    /**
     * Gets general list of request transfer objects which created in the same period.
     *
     * @param generalList main collection with DTO's
     * @param hourlyList  collection with DTO's in period between the 1st day of the second month and the 1st day of the last month
     * @param localStart date from
     * @param localEnd   date to
     * @return return list of request DTO's from one period of time
     */
    private LocalDate loadGeneralList(List<RequestDTO> generalList, List<RequestDTO> hourlyList, LocalDate localStart, LocalDate localEnd) {
        //Date difference in months
        int countMonth = Period.between(localStart, localEnd).getMonths();
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

}
