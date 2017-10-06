package controllers;

import api.ReceiptResponse;
import dao.TagDao;
import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;


@Consumes(MediaType.APPLICATION_JSON)
public class TagController {
    final TagDao tags;

    public TagController(TagDao tags) {
        this.tags = tags;
    }
    @Path("/tags/{tag}")
    @PUT
    public void toggleTag(@PathParam("tag") String tagLabel, int receiptId) {
        if (tags.isReceiptTagged(tagLabel, receiptId)) {
            tags.remove(tagLabel, receiptId);
        } else {
            tags.insert(tagLabel, receiptId);
        }
    }

    @Path("/tags/{tag}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReceiptResponse> getReceiptsWithTag(@PathParam("tag") String tagLabel) {
        List<ReceiptsRecord> receiptRecords = tags.receiptsRecordsWithTag(tagLabel);
        return receiptRecords.stream().map(ReceiptResponse::new).collect(Collectors.toList());
    }

    /*@Path("/tags")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReceiptResponse> getReceiptsWithTag(@PathParam("tag") String tagLabel) {
        List<ReceiptsRecord> receiptRecords = tags.receiptsRecordsWithTag(tagLabel);
        return receiptRecords.stream().map(ReceiptResponse::new).collect(Collectors.toList());
    }

    @GET
    public List<ReceiptResponse> getReceipts() {
        List<ReceiptsRecord> receiptRecords = receipts.getAllReceipts();
        System.out.println("Receipt GET success!");
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }*/
}