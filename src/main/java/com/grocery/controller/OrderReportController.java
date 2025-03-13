package com.grocery.controller;

import com.grocery.model.Orders;
import com.grocery.service.OrdersService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderReportController {

    private final OrdersService orderService;

    public OrderReportController(OrdersService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/download-report")
    public ResponseEntity<byte[]> downloadReport() {
        try {
            List<Orders> todaysOrders = orderService.getTodaysOrders();
            String csvContent = orderService.generateCsvReport(todaysOrders);

            byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);


            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=todays_orders.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
