package com.thanglv.batch.config;

import com.thanglv.batch.entity.Order;
import com.thanglv.batch.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

/**
 * @author acer on 10/28/2021
 * @project spring-batch-demo
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private OrderRepository orderRepository;

    @Bean
    public ItemReader<Order> reader() {
        return new FlatFileItemReaderBuilder<Order>()
                .name("orderItemReader")
                .resource(new PathResource("D:/order.csv"))
                .delimited()
                .names(new String[] {"CustomerId", "ItemId", "ItemName", "ItemPrice", "PurchaseDate"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Order>() {{
                    setTargetType(Order.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<Order, Order> processor() {
        return  new ItemProcessor<Order, Order>() {
            @Override
            public Order process(Order order) throws Exception {
                return order;
            }
        };
    }

    @Bean
    public ItemWriter<Order> writer() {
        RepositoryItemWriter<Order> writer = new RepositoryItemWriter<>();
        writer.setRepository(orderRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Job importOrderJob() {
        return jobBuilderFactory.get("importOrderJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        System.out.println("Before Job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        System.out.println("After Job");
                    }
                })
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Order, Order>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
}
