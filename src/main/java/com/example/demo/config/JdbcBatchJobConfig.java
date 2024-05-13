package com.example.demo.config;

import com.example.demo.domain.Customer;
import com.example.demo.domain.Customer2;
import java.util.HashMap;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MariaDBPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JdbcBatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job jdbcBatchJob() {
        return new JobBuilder("jdbcJob", jobRepository)
                .start(jdbcBatchStep())
                .build();
    }

    @Bean
    public Step jdbcBatchStep() {
        return new StepBuilder("jdbcBatchStep", jobRepository)
                .<Customer, Customer2>chunk(10, transactionManager)
                .reader(jdbcItemReader())
                .processor(jdbcItemProcessor())
                .writer(jdbcItemWriter())
                .build();
    }

    /**
     * ItemReader
     */
    @Bean
    public JdbcPagingItemReader<Customer> jdbcItemReader() {
        HashMap<String, Order> sortMap = new HashMap<>();
        sortMap.put("id", Order.ASCENDING);

        MariaDBPagingQueryProvider queryProvider = new MariaDBPagingQueryProvider();
        queryProvider.setSelectClause("id, first_name, last_name");
        queryProvider.setFromClause("customer");
        queryProvider.setSortKeys(sortMap);

        return new JdbcPagingItemReaderBuilder<Customer>()
                .dataSource(dataSource)
                .name("jdbcItemReader")
                .queryProvider(queryProvider)
                .fetchSize(10)
                .rowMapper((rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ))
                .build();
    }

    @Bean
    public ItemProcessor<Customer, Customer2> jdbcItemProcessor() {
        return item -> new Customer2(item.getFirstName() + item.getLastName());
    }

    @Bean
    public JdbcBatchItemWriter<Customer2> jdbcItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer2>()
                .dataSource(dataSource)
                .sql("INSERT INTO customer2(full_name, created_at) VALUES (:fullName, :createdAt)")
                .beanMapped()
                .build();
    }
}
