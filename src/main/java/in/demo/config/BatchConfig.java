package in.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import in.demo.entity.Customer;
import in.demo.repo.CustomerRepository;

@Configuration
@EnableBatchProcessing
public class BatchConfig 
{
	@Autowired
	private CustomerRepository custRepo;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	//item reader bean
	
	@Bean
	public FlatFileItemReader<Customer> customerReader()
	{
		//used to read the data from file
		FlatFileItemReader<Customer> itemReader=new FlatFileItemReader<Customer>();
		
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("customer-item-read");
		itemReader.setLinesToSkip(1);
		
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Customer> lineMapper() 
	{
		DefaultLineMapper<Customer>lineMapper=new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");
		
		BeanWrapperFieldSetMapper<Customer>filelSetMapper=new BeanWrapperFieldSetMapper<Customer>();
		filelSetMapper.setTargetType(Customer.class);
		
		lineMapper.setFieldSetMapper(filelSetMapper);
		lineMapper.setLineTokenizer(lineTokenizer);
		
		return lineMapper;
	}
	//item processor bean
	
	@Bean
	public CustomerProcessor customerProcessor()
	{
		return new CustomerProcessor();
	}
	
	//item writer bean(is used to write the data to Destination)
	@Bean
	public RepositoryItemWriter<Customer> customerWriter()
	{
		RepositoryItemWriter<Customer> itemWriter=new RepositoryItemWriter<Customer>();
		itemWriter.setRepository(custRepo);
		itemWriter.setMethodName("save");
		
		return itemWriter;
	}
	
	//step bean
	
	@Bean
	public Step step()
	{
		
	   return  stepBuilderFactory.get("step-1").<Customer,Customer>chunk(10).reader(customerReader())
			             .processor(customerProcessor()).writer(customerWriter()).build();
	}
	//job bean
	
	@Bean
	public Job job()
	{
		return jobBuilderFactory.get("customer-import").flow(step()).end().build();
	}
	

}
