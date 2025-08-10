package br.com.hfurlan.rinhabackend2025.repository;

import br.com.hfurlan.rinhabackend2025.bean.Payment;
import br.com.hfurlan.rinhabackend2025.bean.PaymentProcessorPriority;
import br.com.hfurlan.rinhabackend2025.bean.PaymentSummary;
import br.com.hfurlan.rinhabackend2025.bean.PaymentSummaryProcessor;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Repository
@AllArgsConstructor
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_PAYMENT_SQL = "INSERT INTO PAYMENTS (CORRELATION_ID, AMOUNT, PAYMENT_PROCESSOR, INSERTED_AT) values (?,?,?,?) ";
    private final String SUMMARY_SQL = "SELECT PAYMENT_PROCESSOR, SUM(AMOUNT) SUM_AMOUNT, COUNT(1) QTD FROM PAYMENTS WHERE INSERTED_AT BETWEEN ? AND ? GROUP BY PAYMENT_PROCESSOR";
    private final String UPDATE_PAYMENT_PROCESSOR_PRIORITY_SQL = "UPDATE PAYMENT_PROCESSOR_PRIORITY SET PAYMENT_PROCESSOR = ?, UPDATED_AT = CURRENT_TIMESTAMP";
    private final String SELECT_PAYMENT_PROCESSOR_PRIORITY_SQL = "SELECT * FROM PAYMENT_PROCESSOR_PRIORITY";

    public void insertPayment(Payment payment, int paymentProcessor, OffsetDateTime now) {
        jdbcTemplate.update(INSERT_PAYMENT_SQL, payment.getCorrelationId(), payment.getAmount(), paymentProcessor, Timestamp.valueOf(now.toLocalDateTime()));
    }

    public PaymentSummary calculateSummary(LocalDateTime from, LocalDateTime to) {
        PaymentSummary paymentSummary = new PaymentSummary();
        PaymentSummaryProcessor paymentSummaryProcessorMain = new PaymentSummaryProcessor();
        paymentSummaryProcessorMain.setTotalRequests(0);
        paymentSummaryProcessorMain.setTotalAmount(BigDecimal.ZERO);
        paymentSummary.setMain(paymentSummaryProcessorMain);

        PaymentSummaryProcessor paymentSummaryProcessorFallback = new PaymentSummaryProcessor();
        paymentSummaryProcessorFallback.setTotalRequests(0);
        paymentSummaryProcessorFallback.setTotalAmount(BigDecimal.ZERO);
        paymentSummary.setFallback(paymentSummaryProcessorFallback);

        jdbcTemplate.query(SUMMARY_SQL, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int paymentProcessor = rs.getInt("PAYMENT_PROCESSOR");
                BigDecimal sumAmount = rs.getBigDecimal("SUM_AMOUNT");
                int qtd = rs.getInt("QTD");
                if (paymentProcessor == 1) {
                    paymentSummary.getMain().setTotalRequests(qtd);
                    paymentSummary.getMain().setTotalAmount(sumAmount);
                } else {
                    paymentSummary.getFallback().setTotalRequests(qtd);
                    paymentSummary.getFallback().setTotalAmount(sumAmount);
                }
            }
        }, Timestamp.valueOf(from), Timestamp.valueOf(to));
        return paymentSummary;
    }

    public void updatePaymentProcessorPriority(int paymentProcessor) {
        jdbcTemplate.update(UPDATE_PAYMENT_PROCESSOR_PRIORITY_SQL, paymentProcessor);
    }

    public PaymentProcessorPriority findPaymentProcessorPriority() {
        return jdbcTemplate.queryForObject(SELECT_PAYMENT_PROCESSOR_PRIORITY_SQL, new RowMapper<PaymentProcessorPriority>() {
            @Override
            public PaymentProcessorPriority mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new PaymentProcessorPriority(rs.getInt("PAYMENT_PROCESSOR"), rs.getTimestamp("UPDATED_AT").toLocalDateTime());
            }
        });
    }
}