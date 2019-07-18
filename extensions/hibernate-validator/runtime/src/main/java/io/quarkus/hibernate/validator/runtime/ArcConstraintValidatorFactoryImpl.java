package io.quarkus.hibernate.validator.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.Dependent;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.hibernate.validator.internal.util.privilegedactions.NewInstance;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;

public class ArcConstraintValidatorFactoryImpl implements ConstraintValidatorFactory {

    private final Map<Object, InstanceHandle<?>> destroyableValidators = new ConcurrentHashMap<>();

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        InstanceHandle<T> handle = Arc.container().instance(key);
        if (handle.isAvailable()) {
            T instance = handle.get();
            if (handle.getBean().getScope().equals(Dependent.class)) {
                destroyableValidators.put(instance, handle);
            }
            return instance;
        }
        return run(NewInstance.action(key, "ConstraintValidator"));
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        Optional.ofNullable(destroyableValidators.remove(instance)).ifPresent(InstanceHandle::destroy);
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}