package dao;

import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static com.google.common.base.Preconditions.checkState;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static generated.Tables.RECEIPTS;
import static generated.Tables.TAGS;

public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    // for untag
    public boolean ifExists(String tagName, int receiptID){
        TagsRecord tagsRecord = dsl.selectFrom(TAGS).where(TAGS.RECEIPT_ID.eq(receiptId))
                .and(TAGS.NAME.eq(tagName)).fetchOne();
        return tagsRecord != null;
    }

    // no need to write as a seperate funct
    public void remove(String tagName, int receiptID){
        dsl.deleteFrom(TAGS).where(TAGS.LABEL.eq(tagName))
                .and(TAGS.RECEIPT_ID.eq(receiptId)).execute();
    }

    public int insert(String tagName, int receiptID){
        TagsRecord tagsRecord = dsl.insertInto(TAGS, TAGS.RECEIPT_ID, TAGS.NAME)
                                .values(tagName, receiptID)
                                .returning(TAGS.ID)
                                .fetchOne();
        checkState(tagsRecord != null && tagsRecord.getId() != null, "Tag insert failed");
        return tagsRecord.getId();
    }

    public List<ReceiptsRecord> findRptwithTag(String tagName){
        List<Integer> RptIDs = dsl.selectFrom(TAGS).where(TAGS.TAG.eq(tagName)).fetch().map(x -> x.getReceiptId());
        return dsl.selectFrom(RECEIPTS).where(RECEIPTS.ID.in(RptIDs)).fetch();    
    }

    
}