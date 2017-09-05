package controllers;

import api.ReceiptResponse;
import dao.TagDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/tags")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {
    final TagDao tags;

    public TagController(TagDao tags) {
        this.tags = tags;
    }

    @PUT
    public void toggleTag(@PathParam("tag") String tagName, int receiptId) {
        if (tags.ifExists(tagName, receiptId)) 
            tags.remove(tagName, receiptId); 
        else
            tags.insert(tagName, receiptId);
    }

    @GET
    public List<ReceiptResponse> getTaggedReceipts(@PathParam("tag") String tagName) {
        List<ReceiptsRecord> taggedReceipts = tags.findRptwithTag(tagName);
        return taggedReceipts.stream().map(ReceiptResponse::new).collect(toList());
    }
}
