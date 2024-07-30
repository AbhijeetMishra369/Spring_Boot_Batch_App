package in.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.demo.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Integer>
{
	

}
