package jobs;

import lombok.Data;
import models.Customer;
import models.repositories.impl.CustomerRepository;
import org.yaml.snakeyaml.Yaml;
import play.Application;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.List;

@Singleton
public class OnStartup {

    @Inject
    public OnStartup(
            final Application application,
            final CustomerRepository customerRepository
    )
    {
        Yaml yaml = new Yaml();
        final InputStream resourceAsStream = application.classloader()
                                                        .getResourceAsStream("data/customers.yml");
        final SampleData sampleData = yaml.loadAs(resourceAsStream, SampleData.class);
        sampleData.getCustomers().forEach(customerRepository::add);
    }

    @Data
    private static class SampleData {
        public List<Customer> customers;
    }
}

