package io.quarkus.it.hibernate.validator.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target({ ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MyCustomCdiConstraint.Validator.class)
public @interface MyCustomCdiConstraint {

    String message() default "{MyCustomCdiConstraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @ApplicationScoped
    class Validator implements ConstraintValidator<MyCustomCdiConstraint, MyOtherBean> {

        @Inject
        MyCdiBean cdiBean;

        @Override
        public boolean isValid(MyOtherBean value, ConstraintValidatorContext context) {
            return cdiBean != null;
        }
    }
}
