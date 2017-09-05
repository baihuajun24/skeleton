package dao;

import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static com.google.common.base.Preconditions.checkState;
import java.util.List;
import java.util.stream.Collectors;

import static generated.Tables.RECEIPTS;
import static generated.Tables.TAGS;

public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public boolean isReceiptTagged(String tagLabel, int receiptId) {
        TagsRecord record = dsl.selectFrom(TAGS).where(TAGS.RECEIPT_ID.eq(receiptId))
                .and(TAGS.LABEL.eq(tagLabel)).fetchOne();
        return record != null;
    }

    public int insert(String tagLabel, int receiptId) {
        TagsRecord tagsRecord = dsl.insertInto(TAGS, TAGS.LABEL, TAGS.RECEIPT_ID)
                .values(tagLabel, receiptId)
                .returning(TAGS.ID, TAGS.RECEIPT_ID, TAGS.LABEL)
                .fetchOne();
        checkState(tagsRecord != null && tagsRecord.getId() != null, "Insert failed");
        return tagsRecord.getId();
    }

    public void remove(String tagLabel, int receiptId) {
        dsl.deleteFrom(TAGS).where(TAGS.LABEL.eq(tagLabel))
                .and(TAGS.RECEIPT_ID.eq(receiptId)).execute();
    }

    public List<ReceiptsRecord> receiptsRecordsWithTag(String tagLabel) {
        List<Integer> receiptIds = dsl.selectFrom(TAGS).where(TAGS.LABEL.eq(tagLabel)).fetch()
                .stream().map(x -> x.getReceiptId()).collect(Collectors.toList());
        return dsl.selectFrom(RECEIPTS).where(RECEIPTS.ID.in(receiptIds)).fetch();
    }
}
