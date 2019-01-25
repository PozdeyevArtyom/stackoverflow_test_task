package app.validation;

import app.entities.Filter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class FilterValidator implements ConstraintValidator<FilterConstraint, Filter> {
    private static List<String> orderValues;
    private static List<String> sortValues;
    private static Logger logger = LogManager.getLogger(FilterValidator.class);

    public FilterValidator() {
        Properties properties = new Properties();
        InputStream s = getClass().getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(s);
            String delimiter = properties.getProperty("delimiter");
            orderValues = Arrays.asList(properties.getProperty("orderTypes").split(delimiter));
            sortValues = Arrays.asList(properties.getProperty("sortTypes").split(delimiter));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public boolean isValid(Filter o, ConstraintValidatorContext constraintValidatorContext) {
        if (o.getTitle().isEmpty() &&  o.getInclude().isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Title or included tags must be specified")
                    .addConstraintViolation();
            return false;
        }

        if (o.getFrom() != null && o.getTo() != null && o.getFrom().compareTo(o.getTo()) > 0) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("\"From\" date must be lower than \"To\" date")
                    .addConstraintViolation();
            return false;
        }

        if (!sortValues.contains(o.getSort())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Unknown sort type")
                    .addConstraintViolation();
            return false;
        }

        if (!orderValues.contains(o.getOrder())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Unknown order type")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
