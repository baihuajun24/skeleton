package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.NotEmpty;
import java.*;
import static java.lang.System.out;
import java.util.ArrayList;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);

    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     **/
    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

            String merchantName = null;
            //BigDecimal amount = null;
            BigDecimal amount = new BigDecimal("0.0");

            // Your Algo Here!!
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount

            int maxy = -1;
            int miny = 100000;
            String amountString = "";
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                BoundingPoly bp = annotation.getBoundingPoly();
                int thisY = bp.getVertices(0).getY();
                String topLine = annotation.getDescription();
                if (thisY > maxy){
                    maxy = thisY;
                    amountString = topLine;
                }
                if (thisY < miny){
                    miny = thisY;
                    merchantName = topLine.split("[\n\r\t]")[0];
                }
            }
            System.out.println(amountString);
            for (String ele : amountString.split(" ")) {
                if (ele.matches("$?[0-9]*(\\.[0-9]{2})?")) {
                    System.out.println(ele);
                    if (ele.charAt(0) == '$') {
                        amount = new BigDecimal(ele.substring(1));
                    }
                    else {
                        amount = new BigDecimal(ele);
                    }
                    break;
                }
            }

            //TextAnnotation fullTextAnnotation = res.getFullTextAnnotation();
            return new ReceiptSuggestionResponse(merchantName, amount);
        }
    }
}
