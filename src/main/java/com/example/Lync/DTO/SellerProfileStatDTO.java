package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerProfileStatDTO {
    private String userId;
    private int noOfQueriesReceived;
    private List<SellerReceiveInquiryDTO> inquiryDTOS = new ArrayList<>();
    private int noOfOrders;
    private List<OrderDTO> orderDTOs = new ArrayList<>();
    private int noOfSampleOrders;
    private int noOfSampleApproved;
    private int noOfSampleRejected;

}
