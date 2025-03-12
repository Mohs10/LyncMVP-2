package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerProfileStatDTO {
    private String userId;
    private int noOfQueries;
    private List<InquiryDTO> inquiryDTOS = new ArrayList<>();
    private int noOfOrders;
    private List<OrderDTO> orderDTOs = new ArrayList<>();
 }
