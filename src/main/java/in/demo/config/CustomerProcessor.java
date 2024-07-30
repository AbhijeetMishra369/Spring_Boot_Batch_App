package in.demo.config;

import org.springframework.batch.item.ItemProcessor;
import in.demo.entity.Customer;


public class CustomerProcessor implements ItemProcessor<Customer,Customer> 
{

	@Override
	public Customer process(Customer item) throws Exception
	{
		//Logic to process the data
		
//		if(item.getCountry().equals("india"))
//		{
//			return item;
//		}
		return item;
	}

}
